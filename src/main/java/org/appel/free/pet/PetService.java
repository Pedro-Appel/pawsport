package org.appel.free.pet;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class PetService {

    private final PetRepository petRepository;

    public PetService(@Default PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public PetRecord findById(UUID petId) {
        PanacheQuery<Pet> petPanacheQuery = petRepository.find("where id = ?1", petId);
        return petPanacheQuery.singleResultOptional()
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
}
