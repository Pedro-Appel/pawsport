package org.appel.free.pawsport.pet.treatment;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.appel.free.pawsport.shared.ErrorMessages.*;

public record TreatmentRecord(
        @Null(message = CONSTRAINT_NULL_MSG_ERROR)
        Long id,
        @Size(max = 255, message = CONSTRAINT_BIGGER_THAN_255_MSG_ERROR_CONDITION)
        @NotBlank(message = CONSTRAINT_NOT_NULL_MSG_ERROR)
        String type,
        @PastOrPresent
        LocalDate startDate,
        @Future
        LocalDate endDate,
        @NotNull(message = CONSTRAINT_NOT_NULL_MSG_ERROR)
        UUID vetId,
        @NotNull(message = CONSTRAINT_NOT_NULL_MSG_ERROR)
        UUID petId
) {}
