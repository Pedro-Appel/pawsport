package org.appel.free.vaccine;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VaccineRepository implements PanacheRepository<Vaccine> {
}
