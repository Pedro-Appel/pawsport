package org.appel.free.vaccine;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import static org.jboss.resteasy.reactive.RestResponse.Status.NOT_FOUND;
import static org.jboss.resteasy.reactive.RestResponse.Status.NO_CONTENT;

@ApplicationScoped
public class VaccineService {

    private final VaccineRepository repository;

    public VaccineService(VaccineRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Uni<VaccineRecord> createVaccine(VaccineRecord vaccineRecord) {
        Vaccine entity = Vaccine.fromRecord(vaccineRecord);
        Uni<Vaccine> persist = repository.persist(entity);
        Log.infof("Registered vaccine with id: %s, for pet with id: %s", entity.id, vaccineRecord.petId());
        return persist.map(Vaccine::toRecord);
    }

    public Uni<VaccineRecord> retrieve(long id) {
        return repository.find("where id = ?1 and active = true", id)
                .singleResult()
                .onItem().ifNotNull().transform(Vaccine::toRecord)
                .onItem().ifNull().fail();
    }

    @Transactional
    public Uni<Response> delete(long id) {
        Log.infof("Deleting Vaccine with id: %s", id);
        return repository.update("active = false where id = ?1", id)
                .map(deleted -> deleted > 0
                        ? Response.status(NO_CONTENT).build()
                        : Response.status(NOT_FOUND).build());
    }
}
