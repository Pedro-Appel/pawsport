package org.appel.free.pet.treatment;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TreatmentRepository implements PanacheRepository<Treatment> {
}
