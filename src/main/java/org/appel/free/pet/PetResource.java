package org.appel.free.pet;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.appel.free.pet.treatment.TreatmentRecord;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

@Path("/api/v1/pet")
public class PetResource {

    private final PetService service;

    public PetResource(PetService service) {
        this.service = service;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPet(@RequestBody @Valid PetRecord record) {
        PetRecord saved = service.create(record);
        return Response.status(Response.Status.CREATED)
                .location(URI.create("/api/v1/pet/" + saved.uuid()))
                .entity(saved)
                .build();
    }

    @GET
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    public PetRecord getPetInformation(@PathParam("petId") String petId) {
        return service.findById(UUID.fromString(petId));
    }

    @PUT
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePet(@PathParam("petId") String petId, @RequestBody @Valid PetRecord petRecord) {
        PetRecord update = service.update(UUID.fromString(petId), petRecord);
        return Response.status(Response.Status.OK)
                .location(URI.create("/api/v1/pet/" + update.uuid()))
                .entity(update)
                .build();
    }

    @DELETE
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deletePet(@PathParam("petId") String petId) {
        service.delete(UUID.fromString(petId));
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{petId}/treatment")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTreatment(@PathParam("petId") UUID petId, @RequestBody @Valid TreatmentRecord record) {
        try {
            TreatmentRecord saved = service.addTreatment(petId, record);
            return Response.status(Response.Status.CREATED)
                    .location(URI.create("/api/v1/pet/" + saved.id()))
                    .entity(saved)
                    .build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{petId}/treatment")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listPetTreatments(@PathParam("petId") UUID petId, @QueryParam("limit") @DefaultValue("10") @Min(1) @Max(50) int limit, @DefaultValue("0") @Min(0) @QueryParam("_offset") int offset) {
        try {
            return Response.ok(service.listPetTreatments(petId, limit, offset)).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @GET
    @Path("/{petId}/treatment/{treatmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTreatmentDetail(@PathParam("petId") UUID petId, @PathParam("treatmentId") Long treatmentId) {
        TreatmentRecord treatmentDetails = service.findTreatmentDetails(treatmentId);
        return Objects.isNull(treatmentDetails) ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(treatmentDetails).build();
    }

    @DELETE
    @Path("/{petId}/treatment/{treatmentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteTreatment(@PathParam("petId") UUID petId, @PathParam("treatmentId") long treatmentId) {
        service.deleteTreatment(treatmentId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
