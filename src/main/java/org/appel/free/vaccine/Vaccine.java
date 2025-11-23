package org.appel.free.vaccine;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
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
    boolean active;

    public Vaccine() {
    }

    private Vaccine(String petId, String vetId, String vaccineName, LocalDate date, LocalDate expirationDate) {
        this.petId = petId;
        this.type = vaccineName;
        this.vetId = vetId;
        this.date = date;
        this.expirationDate = expirationDate;
        this.active = true;
    }

    static Uni<Vaccine> findActiveById(long id) {
        Parameters parameters = Parameters
                .with("vaccineId", id)
                .and("active", true);
        return find("id = :vaccineId and active = :active", parameters).firstResult();
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
