package org.appel.free.e2e.pet;

import org.appel.free.pet.PetRecord;
import org.appel.free.pet.treatment.TreatmentRecord;

import java.time.LocalDate;
import java.util.UUID;

public class PetUtils {

    static UUID getPetId() {
        return UUID.fromString("19d66ad4-a771-4d2e-966a-d4a7719d2e11");
    }

    static PetRecord getPetRecord() {
        return new PetRecord(
                getPetId(),
                "name",
                "species",
                "breed",
                "conditions",
                LocalDate.now().minusYears(10),
                10.1f,
                "color"
        );
    }

    static TreatmentRecord getTreatmentRecord() {
        return new TreatmentRecord(
                null,
                "Treatment Type",
                LocalDate.now(),
                LocalDate.now().plusYears(10),
                getPetId(),
                getPetId()
        );
    }
}
