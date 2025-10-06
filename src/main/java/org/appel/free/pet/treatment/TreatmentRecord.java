package org.appel.free.pet.treatment;

import java.time.LocalDate;
import java.util.UUID;

public record TreatmentRecord(
        Long id,
        String type,
        LocalDate startDate,
        LocalDate endDate,
        UUID vetId,
        UUID petId
) {}
