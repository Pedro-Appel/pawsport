package org.appel.free.vaccine;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VaccineRepository implements PanacheRepository<Vaccine> {
}
