package org.appel.free.pet;

import java.util.UUID;

public record PetRecord(
        UUID uuid,
        String name,
        String species,
        String breed,
        String conditions,
        String birthdate,
        float weight,
        String color
) {
}
