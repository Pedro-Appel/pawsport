package org.appel.free.pet.treatment;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
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

    public static Uni<List<Treatment>> listByPetId(String petId, int page, int size){
        return find("petId = :petId", Sort.by("startDate").descending(), Parameters.with("petId", petId))
                .page(Page.of(page, size))
                .list();
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
