package org.appel.free.vaccine;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.net.URI;

import static org.jboss.resteasy.reactive.RestResponse.Status.*;

@ApplicationScoped
public class VaccineService {

    public Uni<Response> createVaccine(VaccineRecord vaccineRecord) {
        Vaccine entity = Vaccine.fromRecord(vaccineRecord);
        return Panache.withTransaction(entity::persist)
                .log("Registering vaccine with id: %s, for pet with id: %s".formatted(entity.id, vaccineRecord.petId()))
                .onFailure()
                    .recoverWithNull().replaceWith(Response.status(INTERNAL_SERVER_ERROR).build())
                .onItem()
                    .transform(_ -> Response.created(URI.create(String.valueOf(entity.id))).build());
    }

    @WithSession
    public Uni<VaccineRecord> retrieve(long id) {
        return Vaccine.findActiveById(id)
                .onItem()
                .transform(Vaccine::toRecord);
    }

    @WithTransaction
    public Uni<Response> delete(long id) {
        return Vaccine.update("active = false where id = ?1", id)
                .log("Deleting Vaccine with id: %s".formatted(id))
                .map(deleted -> deleted > 0
                        ? Response.status(NO_CONTENT).build()
                        : Response.status(NOT_FOUND).build());
    }
}
