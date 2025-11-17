package org.appel.free.pet;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.persistence.NoResultException;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import org.appel.free.exception.ExceptionResponse;
import org.appel.free.pet.treatment.Treatment;
import org.appel.free.pet.treatment.TreatmentRecord;

import java.net.URI;
import java.util.UUID;
import java.util.function.Function;

import static org.appel.free.shared.Constants.PETS_API_PATH;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.INTERNAL_SERVER_ERROR;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NOT_FOUND;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NO_CONTENT;

@ApplicationScoped
public class PetService {

    private final PetRepository petRepository;

    public PetService(@Default PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Uni<PetRecord> findById(UUID petId) {
        return Panache.withTransaction(() -> petRepository.find("where id = ?1 and active = true", petId).singleResult())
                .log("Looking for pet with id: [ %s ]".formatted(petId))
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
                .log("Saving pet")
                .replaceWith(Response.created(URI.create(PETS_API_PATH + pet.toRecord().uuid())).build())
                .onFailure()
                    .recoverWithUni(f -> {
                        Log.errorf("Fail persisting pet with exception '%s'", f.getMessage());
                        return Uni.createFrom().item(Response.status(INTERNAL_SERVER_ERROR).entity(new ExceptionResponse(f)).build());
                    });
    }

    public Uni<Response> update(UUID petId, PetRecord record) {
        Pet pet = Pet.fromRecord(petId, record);
        return Panache.withTransaction(() -> Pet.find("where id = ?1 and active = true", petId).singleResult())
                .log("Updating pet with id: [ %s ]".formatted(petId))
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
                                        petId)).onItem()
                        .transform(i -> i > 0
                            ? Response.accepted(pet.toRecord()).build()
                            : Response.notModified().build()))
                .onFailure()
                    .recoverWithUni(treatMissingPet());
    }

    public Uni<Response> delete(UUID petId) {
        return Panache.withTransaction(() -> Pet.update("active = false where  id = ?1 and active = true", petId))
                .log("Deleting pet with id: [ %s ]".formatted(petId))
                .map(deleted -> deleted > 0
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }

    public Uni<Response> addTreatment(UUID petId, @Valid TreatmentRecord record) {
        Treatment treatment = Treatment.fromRecord(record);
        return Panache.withTransaction(() -> Pet.find("where id = ?1 and active = true", petId).singleResult())
                .log("Saving treatment to pet with id: [ %s ]".formatted(petId))
                .onItem()
                    .ifNotNull()
                    .transformToUni(_ -> Panache.withTransaction(treatment::persist))
                .replaceWith(Response.created(URI.create(PETS_API_PATH + "/%s".formatted(petId) + "/%d".formatted(treatment.toRecord().id()))).build())
                .onFailure()
                    .recoverWithUni(treatMissingPet());
    }

    private static Function<Throwable, Uni<? extends Response>> treatMissingPet() {
        return f -> {
            Log.errorf("Fail updating pet with exception: [ %s ]", f.getMessage());
            return switch (f) {
                case NoResultException _ ->
                        Uni.createFrom().item(Response.status(NOT_FOUND).entity(new ExceptionResponse("Pet not found")).build());
                default ->
                        Uni.createFrom().item(Response.status(INTERNAL_SERVER_ERROR).entity(new ExceptionResponse(f)).build());
            };
        };
    }

    public Uni<Response> listPetTreatments(UUID petId, int limit, int page) {
        return Panache.withSession(() -> Treatment.listByPetId(petId.toString(), page, limit)
                .log("Trying to list treatments for pet with id: [ %s ]".formatted(petId))
                .onItem()
                    .invoke(i -> Log.infof("Found [ %d ] treatment(s) for page [ %d ] and with limit: [ %d ]", i.size(), page, limit))
                .onItem()
                    .transform(treatments -> treatments.stream().map(Treatment::toRecord).toList())
                .onItem()
                    .transform(treatments -> treatments.isEmpty()
                                ? Response.noContent().build()
                                : Response.ok(treatments).build()));
    }

    public Uni<Response> findTreatmentDetails(long treatmentId) {
        return Panache.withSession(() -> Treatment.findById(treatmentId))
                .log("Looking for treatment detail with with id: [ %s ]".formatted(treatmentId))
                .onItem()
                    .castTo(Treatment.class)
                .onItem()
                        .transform(i -> Response.ok(i.toRecord()).build())
                .replaceIfNullWith(Response.noContent().build());
    }

    public Uni<Response> deleteTreatment(long treatmentId) {
        return Panache.withTransaction(() -> Treatment.deleteById(treatmentId))
                .log("Deleting treatment with id: [ %s ]".formatted(treatmentId))
                .map(deleted -> deleted
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }
}
