package org.appel.free.vaccine;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Path("/api/v1/vaccine/")
public class VaccineResource {

    private final VaccineService vaccineService;

    public VaccineResource(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVaccine(@RequestBody @Valid VaccineRecord vaccineRecord) {
        return Response.status(Response.Status.CREATED)
                .entity(vaccineService.createVaccine(vaccineRecord))
                .build();
    }

    @GET
    @Path("/{vaccineId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> fetchVaccine(@PathParam("vaccineId") long id) {
        return vaccineService.retrieve(id)
                .onItem().transform(v -> Response.ok(v).build());
    }

    @DELETE
    @Path("/{vaccineId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> deleteVaccine(@PathParam("vaccineId") long id) {
        return vaccineService.delete(id);
    }
}
