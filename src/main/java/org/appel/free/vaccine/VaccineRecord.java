package org.appel.free.vaccine;

import java.time.LocalDate;
import java.util.UUID;

public record VaccineRecord(
        long vaccineId,
        UUID petId,
        UUID vetId,
        String type,
        LocalDate date,
        LocalDate expirationDate
) {
}
