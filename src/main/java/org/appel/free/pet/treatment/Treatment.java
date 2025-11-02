package org.appel.free.pet.treatment;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Treatment extends PanacheEntity {

    String type;

    LocalDate startDate;

    LocalDate endDate;

    String vetId;

    String petId;

    public Treatment() {
    }

    private Treatment(String type, LocalDate startDate, LocalDate endDate, UUID vetId, UUID petId) {
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.vetId = vetId.toString();
        this.petId = petId.toString();
    }

    public static Treatment fromRecord(@Valid TreatmentRecord record) {
        return new Treatment(
                record.type(),
                record.startDate(),
                record.endDate(),
                record.vetId(),
                record.petId()
        );
    }

    public TreatmentRecord toRecord() {
        return new TreatmentRecord(
                this.id,
                this.type,
                this.startDate,
                this.endDate,
                UUID.fromString(this.vetId),
                UUID.fromString(this.petId)
        );
    }
}
