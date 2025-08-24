package org.appel.free.vaccine;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Vaccine extends PanacheEntity {

    private String petId;
    private String vetId;
    private String type;
    private LocalDate date;
    private LocalDate expirationDate;

    public Vaccine() {
    }

    private Vaccine(String petId, String vetId, String vaccineName, LocalDate date, LocalDate expirationDate) {
        this.petId = petId;
        this.type = vaccineName;
        this.vetId = vetId;
        this.date = date;
        this.expirationDate = expirationDate;
    }

    public static Vaccine fromRecord(VaccineRecord vaccineRecord) {
        return new Vaccine(
                vaccineRecord.petId().toString(),
                vaccineRecord.vetId().toString(),
                vaccineRecord.type(),
                vaccineRecord.date(),
                vaccineRecord.expirationDate()
        );
    }

    public VaccineRecord toRecord() {
        return new VaccineRecord(id, UUID.fromString(petId), UUID.fromString(vetId), type, date, expirationDate);
    }
}
