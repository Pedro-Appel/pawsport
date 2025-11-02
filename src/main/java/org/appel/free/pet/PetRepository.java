package org.appel.free.pet;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class PetRepository implements PanacheRepositoryBase<Pet, UUID> {

}
