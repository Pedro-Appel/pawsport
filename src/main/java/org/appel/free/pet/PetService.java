package org.appel.free.pet;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.appel.free.exception.ExceptionResponse;
import org.appel.free.pet.treatment.Treatment;
import org.appel.free.pet.treatment.TreatmentRecord;
import org.appel.free.pet.treatment.TreatmentRepository;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.appel.free.shared.Constants.PETS_API_PATH;
import static org.jboss.resteasy.reactive.RestResponse.Status.*;

@ApplicationScoped
public class PetService {

    private final PetRepository petRepository;
    private final TreatmentRepository treatmentRepository;

    public PetService(@Default PetRepository petRepository, TreatmentRepository treatmentRepository) {
        this.petRepository = petRepository;
        this.treatmentRepository = treatmentRepository;
    }

    public Uni<PetRecord> findById(UUID petId) {
        return Panache.withTransaction(() -> petRepository.find("where id = ?1 and active = true", petId).singleResult())
                .onItem()
                    .transform(Pet::toRecord)
                    .invoke(p -> Log.debugf("Retrieved pet %s", p.uuid()))
                .onFailure()
                    .recoverWithUni(e -> {
                        Log.errorf("Fail searching for '%s', with exception '%s'", petId, e.getMessage());
                        return Uni.createFrom().nullItem();
                    });
    }

    public Uni<Response> create(PetRecord record) {
        Pet pet = Pet.fromRecord(record);
        return Panache.withTransaction(pet::persist)
                .replaceWith(Response.created(URI.create(PETS_API_PATH + pet.toRecord().uuid())).build())
                .onFailure()
                    .recoverWithUni(f -> {
                        Log.errorf("Fail persisting pet with exception '%s'", f.getMessage());
                        return Uni.createFrom().item(Response.status(INTERNAL_SERVER_ERROR).entity(new ExceptionResponse(f)).build());
                    });
    }

    public Uni<Response> update(UUID key, PetRecord record) {
        Pet pet = Pet.fromRecord(key, record);
        return Panache.withTransaction(() -> Pet.find("where id = ?1 and active = true", key).singleResult())
                .onItem()
                    .ifNotNull()
                        .transformToUni(_ ->  Panache.withTransaction(() -> Pet.update(
                                        "name = ?1, " +
                                                "species = ?2, " +
                                                "breed = ?3, " +
                                                "conditions = ?4, " +
                                                "birthdate = ?5, " +
                                                "weight = ?6, " +
                                                "color = ?7 " +
                                                "where id = ?8",
                                        record.name(),
                                        record.species(),
                                        record.breed(),
                                        record.conditions(),
                                        record.birthdate(),
                                        record.weight(),
                                        record.color(),
                                        key)).onItem()
                        .transform(i -> i > 0
                            ? Response.ok(pet.toRecord()).build()
                            : Response.notModified().build()))
                .onFailure()
                    .recoverWithUni(treatMissingPet());
    }

    public Uni<Response> delete(UUID uuid) {
        return Panache.withTransaction(() -> Pet.update("active = false where  id = ?1 and active = true", uuid))
                .map(deleted -> deleted > 0
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }

    public Uni<Response> addTreatment(UUID petId, @Valid TreatmentRecord record) {
        Treatment treatment = Treatment.fromRecord(record);
        return Panache.withTransaction(() -> Pet.find("where id = ?1 and active = true", petId).singleResult())
                .onItem()
                    .ifNotNull()
                    .transformToUni(_ -> Panache.withTransaction(treatment::persist))
                .replaceWith(Response.created(URI.create(PETS_API_PATH + "/%s".formatted(petId) + "/%d".formatted(treatment.toRecord().id()))).build())
                .onFailure()
                    .recoverWithUni(treatMissingPet());
    }

    private static Function<Throwable, Uni<? extends Response>> treatMissingPet() {
        return f -> {
            Log.errorf("Fail updating pet with exception '%s'", f.getMessage());
            return switch (f) {
                case NoResultException _ ->
                        Uni.createFrom().item(Response.status(NOT_FOUND).entity(new ExceptionResponse("Pet not found")).build());
                default ->
                        Uni.createFrom().item(Response.status(INTERNAL_SERVER_ERROR).entity(new ExceptionResponse(f)).build());
            };
        };
    }

    public Uni<List<TreatmentRecord>> listPetTreatments(UUID petId, int limit, int page) {
        PanacheQuery<Treatment> pageableQuery = treatmentRepository.find("where petId = ?1", Sort.by("startDate").descending(), petId.toString());
        return pageableQuery.page(Page.of(page, limit))
                .list()
                .onItem()
                .ifNull()
                .failWith(new NotFoundException("No records found"))
                .map((l) -> l
                        .stream()
                        .map(Treatment::toRecord)
                        .toList());
    }

    public Uni<TreatmentRecord> findTreatmentDetails(Long treatmentId) {
        return treatmentRepository.findById(treatmentId)
                .onItem().ifNotNull().transform(Treatment::toRecord)
                .replaceIfNullWith(() -> null);
    }

    @Transactional
    public Uni<Response> deleteTreatment(long treatmentId) {
        return treatmentRepository.delete("where id = ?1 and active = true", treatmentId)
                .map(deleted -> deleted > 0
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }
}
