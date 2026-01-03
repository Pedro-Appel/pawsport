package org.appel.free.pawsport.pet;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Pet extends PanacheEntityBase {

    @Id
    private UUID id;
    private String name;
    private String species;
    private String breed;
    private LocalDate birthdate;
    private float weight;
    private String color;
    private String conditions;
    boolean active;

    public Pet() {
    }

    private Pet(UUID id, String name, String species, String breed, String conditions, LocalDate birthdate, float weight, String color) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.conditions = conditions;
        this.birthdate = birthdate;
        this.weight = weight;
        this.color = color;
        active = true;
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

    static Uni<Pet> findActiveById(UUID id){
        Parameters parameters = Parameters
                .with("id", id)
                .and("active", true);
        return find("id = :id and active = :active", parameters).firstResult();
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", active=" + active +
                '}';
    }
}
