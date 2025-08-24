package org.appel.free.vaccine;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class VaccineService {

    private final VaccineRepository repository;

    public VaccineService(VaccineRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VaccineRecord createVaccine(VaccineRecord vaccineRecord) {
        Vaccine entity = Vaccine.fromRecord(vaccineRecord);
        repository.persist(entity);
        return entity.toRecord();
    }

    public VaccineRecord retrieve(long id) {
        return repository.findById(id).toRecord();
    }
}
