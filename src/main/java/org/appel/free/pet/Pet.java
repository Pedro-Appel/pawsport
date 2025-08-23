package org.appel.free.pet;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class Pet {

    @Id
    private UUID id;
    private String name;
    private String species;
    private String breed;
    private String conditions;
    private String birthdate;
    private float weight;
    private String color;

    public Pet() {
    }

    private Pet(UUID id, String name, String species, String breed, String conditions, String birthdate, float weight, String color) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.conditions = conditions;
        this.birthdate = birthdate;
        this.weight = weight;
        this.color = color;
    }

    public UUID getId() {
        return id;
    }

    private void setId(UUID id) {
        this.id = id;
    }

    public static Pet fromRecord(PetRecord record) {
        return new Pet(
                UUID.randomUUID(),
                record.name(),
                record.species(),
                record.breed(),
                record.conditions(),
                record.birthdate(),
                record.weight(),
                record.color()
        );
    }

    public static Pet fromRecord(UUID key, PetRecord record) {
        Pet pet = fromRecord(record);
        pet.setId(key);
        return pet;
    }

    public PetRecord toRecord() {
        return new PetRecord(this.id, this.name, this.species, this.breed, this.conditions, this.birthdate, this.weight, this.color);
    }
}
