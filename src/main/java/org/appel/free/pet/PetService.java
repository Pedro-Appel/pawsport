package org.appel.free.pet;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.appel.free.pet.treatment.Treatment;
import org.appel.free.pet.treatment.TreatmentRecord;
import org.appel.free.pet.treatment.TreatmentRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.jboss.resteasy.reactive.RestResponse.Status.NOT_FOUND;
import static org.jboss.resteasy.reactive.RestResponse.Status.NO_CONTENT;

@ApplicationScoped
public class PetService {

    private final PetRepository petRepository;
    private final TreatmentRepository treatmentRepository;

    public PetService(@Default PetRepository petRepository, TreatmentRepository treatmentRepository) {
        this.petRepository = petRepository;
        this.treatmentRepository = treatmentRepository;
    }

    public Uni<PetRecord> findById(UUID petId) {
        return petRepository.find("where id = ?1 and active = true", petId)
                .firstResult()
                .map(Pet::toRecord);
    }

    @Transactional
    public Uni<PetRecord> create(PetRecord record) {
        Pet pet = Pet.fromRecord(record);
        Uni<Pet> persist = petRepository.persist(pet);
        return persist.map(Pet::toRecord);
    }

    @Transactional
    public Uni<PetRecord> update(UUID key, PetRecord record) {
        Pet pet = Pet.fromRecord(key, record);
        return petRepository.find("where id = ?1 and active = true", key).singleResult()
                .onItem().ifNull().failWith(new EntityNotFoundException("Pet not found"))
                .onItem().ifNotNull().transformToUni((i) -> petRepository.update(
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
                        key)
                ).onItem().transform(i -> i > 0 ? pet.toRecord() : null);
    }

    @Transactional
    public Uni<Response> delete(UUID uuid) {
        return petRepository.update("active = false where  id = ?1", uuid)
                .map(deleted -> deleted > 0
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }

    @Transactional
    public Uni<TreatmentRecord> addTreatment(UUID petId, @Valid TreatmentRecord record) {
        Treatment treatment = Treatment.fromRecord(record);
        return petRepository.find("where id = ?1 and active = true", petId).count()
                .onItem().ifNull().failWith(() -> new EntityNotFoundException("Pet not found"))
                .onItem().ifNotNull().transformToUni((i) -> treatmentRepository.persist(treatment))
                .map(Treatment::toRecord);
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
