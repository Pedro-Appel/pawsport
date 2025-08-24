package org.appel.free.vaccine;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.net.URI;

@Path("/api/v1/vaccine/")
public class VaccineResource {

    private final VaccineService vaccineService;

    public VaccineResource(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVaccine(@RequestBody VaccineRecord vaccineRecord) {
        VaccineRecord vaccine = vaccineService.createVaccine(vaccineRecord);
        return Response.status(Response.Status.CREATED)
                .header("Location", URI.create("/api/v1/vaccine/" + vaccine.vaccineId()))
                .entity(vaccine)
                .build();
    }

    @GET
    @Path("/{vaccineId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVaccine(@PathParam("vaccineId") long id) {
        VaccineRecord vaccine = vaccineService.retrieve(id);
        return Response.status(Response.Status.OK)
                .entity(vaccine)
                .build();
    }
}
