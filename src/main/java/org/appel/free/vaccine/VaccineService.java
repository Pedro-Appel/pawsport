package org.appel.free.vaccine;

import io.quarkus.logging.Log;
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
        Log.infof("Registered vaccine with id: %s, for pet with id: %s", entity.id, vaccineRecord.petId());
        return entity.toRecord();
    }

    public VaccineRecord retrieve(long id) {
        return repository.find("where id = ?1 and active = true", id)
                .firstResultOptional()
                .map(Vaccine::toRecord)
                .orElse(null);
    }

    @Transactional
    public void delete(long id) {
        Log.infof("Deleting Vaccine with id: %s", id);
        repository.update("active = false where id = ?1", id);
    }
}
