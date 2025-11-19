package org.appel.free.pet;


import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.appel.free.shared.ErrorMessages.*;

public record PetRecord(

        @Null(message = CONSTRAINT_NULL_MSG_ERROR)
        UUID uuid,

        @Size(max = 36, message = CONSTRAINT_BIGGER_THAN_36_MSG_ERROR_CONDITION)
        @NotBlank(message = CONSTRAINT_NOT_NULL_MSG_ERROR)
        String name,

        @Size(max = 36, message = CONSTRAINT_BIGGER_THAN_36_MSG_ERROR_CONDITION)
        @NotBlank(message = CONSTRAINT_NOT_NULL_MSG_ERROR)
        String species,

        @Size(max = 36, message = CONSTRAINT_BIGGER_THAN_36_MSG_ERROR_CONDITION)
        @NotBlank(message = CONSTRAINT_NOT_NULL_MSG_ERROR)
        String breed,

        @Size(max = 255, message = CONSTRAINT_BIGGER_THAN_255_MSG_ERROR_CONDITION)
        String conditions,

        @NotNull(message = CONSTRAINT_NOT_NULL_MSG_ERROR) @Past(message = CONSTRAINT_PAST_MSG_ERROR) LocalDate birthdate,
        @Positive(message = CONSTRAINT_POSITIVE_GT_ZERO_MSG_ERROR)
        float weight,

        @Size(max = 36, message = CONSTRAINT_BIGGER_THAN_36_MSG_ERROR_CONDITION)
        @NotBlank(message = CONSTRAINT_NOT_NULL_MSG_ERROR)
        String color
) {
}