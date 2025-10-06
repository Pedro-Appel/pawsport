package org.appel.free.pet;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import org.appel.free.pet.treatment.Treatment;
import org.appel.free.pet.treatment.TreatmentRecord;
import org.appel.free.pet.treatment.TreatmentRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PetService {

    private final PetRepository petRepository;
    private final TreatmentRepository treatmentRepository;

    public PetService(@Default PetRepository petRepository, TreatmentRepository treatmentRepository) {
        this.petRepository = petRepository;
        this.treatmentRepository = treatmentRepository;
    }

    public PetRecord findById(UUID petId) {
        return petRepository.find("where id = ?1 and active = true", petId)
                .firstResultOptional()
                .map(Pet::toRecord)
                .orElse(null);
    }

    @Transactional
    public PetRecord create(PetRecord record) {
        Pet pet = Pet.fromRecord(record);
        petRepository.persist(pet);
        return pet.toRecord();
    }

    @Transactional
    public PetRecord update(UUID key, PetRecord record) {
        Optional<Pet> petOptional = petRepository.find("where id = ?1 and active = true", key).firstResultOptional();
        if (petOptional.isEmpty()) {
            throw new EntityNotFoundException("Pet not found");
        }
        Pet pet = Pet.fromRecord(key, record);
        petRepository.update(
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
                key);
        return pet.toRecord();
    }

    @Transactional
    public void delete(UUID uuid) {
        petRepository.update("active = false where  id = ?1", uuid);
    }

    @Transactional
    public TreatmentRecord addTreatment(UUID petId, @Valid TreatmentRecord record) {
        Optional<Pet> petOptional = petRepository.find("where id = ?1 and active = true", petId).firstResultOptional();
        if (petOptional.isEmpty()) {
            throw new EntityNotFoundException("Pet not found");
        }
        Treatment treatment = Treatment.fromRecord(record);
        treatmentRepository.persist(treatment);
        return treatment.toRecord();
    }

    public List<TreatmentRecord> listPetTreatments(UUID petId, int limit, int page) {
        PanacheQuery<Treatment> pageableQuery = treatmentRepository.find("where petId = ?1", Sort.by("startDate").descending(), petId.toString());
        List<TreatmentRecord> records = pageableQuery.page(Page.of(page, limit))
                .stream()
                .map(Treatment::toRecord)
                .toList();
        if(records.isEmpty())
            throw new NotFoundException("No records found");
        return records;
    }

    public TreatmentRecord findTreatmentDetails(Long treatmentId) {
        return treatmentRepository.findByIdOptional(treatmentId)
                .map(Treatment::toRecord)
                .orElse(null);
    }

    @Transactional
    public void deleteTreatment(long treatmentId) {
        treatmentRepository.delete("where id = ?1 and active = true", treatmentId);
    }
}
