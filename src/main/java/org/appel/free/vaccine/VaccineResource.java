package org.appel.free.vaccine;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Path("/api/v1/vaccines/")
public class VaccineResource {

    private final VaccineService vaccineService;

    public VaccineResource(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> createVaccine(@RequestBody @Valid VaccineRecord vaccineRecord) {
        return vaccineService.createVaccine(vaccineRecord);
    }

    @GET
    @Path("/{vaccineId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> fetchVaccine(@PathParam("vaccineId") long vaccineId) {
        Log.debugf("Fetching vaccine with id: [ %s ]",vaccineId);
        return vaccineService.retrieve(vaccineId);
    }

    @DELETE
    @Path("/{vaccineId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> deleteVaccine(@PathParam("vaccineId") Long id) {
        return vaccineService.delete(id);
    }
}
