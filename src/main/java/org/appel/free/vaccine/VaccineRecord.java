package org.appel.free.vaccine;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.appel.free.shared.ErrorMessages.*;

public record VaccineRecord(
        @Null(message = CONSTRAINT_NULL_MSG_ERROR)
        Long vaccineId,

        @NotNull(message = CONSTRAINT_NOT_NULL_MSG_ERROR)
        UUID petId,

        @NotNull(message = CONSTRAINT_NOT_NULL_MSG_ERROR)
        UUID vetId,

        @Size(max = 36, message = CONSTRAINT_BIGGER_THAN_36_MSG_ERROR_CONDITION)
        @NotBlank(message = CONSTRAINT_NOT_NULL_MSG_ERROR)
        String type,

        @PastOrPresent
        LocalDate date,

        @Future
        LocalDate expirationDate
) {
        @Override
        public String toString() {
                return "VaccineRecord{" +
                        "vaccineId=" + vaccineId +
                        ", petId=" + petId +
                        ", vetId=" + vetId +
                        ", type='" + type + '\'' +
                        ", date=" + date +
                        ", expirationDate=" + expirationDate +
                        '}';
        }
}
