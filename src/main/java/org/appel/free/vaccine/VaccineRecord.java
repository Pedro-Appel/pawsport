package org.appel.free.vaccine;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.appel.free.ErrorMessages.*;

public record VaccineRecord(
        long vaccineId,

        @NotNull(message = CONSTRAINT_NULL_MSG_ERROR)
        UUID petId,

        @NotNull(message = CONSTRAINT_NULL_MSG_ERROR)
        UUID vetId,

        @Size(max = 36, message = CONSTRAINT_BIGGER_THAN_36_MSG_ERROR_CONDITION)
        @NotBlank(message = CONSTRAINT_NULL_MSG_ERROR)
        String type,

        @PastOrPresent
        LocalDate date,

        @Future
        LocalDate expirationDate
) {
}
