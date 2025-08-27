package org.appel.free.pet;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.net.URI;
import java.util.UUID;

@Path("/api/v1/pet")
public class PetResource {

    private final PetService petService;

    public PetResource(PetService petService) {
        this.petService = petService;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPet(@RequestBody @Valid PetRecord record) {
        PetRecord saved = petService.create(record);
        return Response.status(Response.Status.CREATED)
                .location(URI.create("/api/v1/pet/" + saved.uuid()))
                .entity(saved)
                .build();
    }

    @GET
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    public PetRecord getPetInformation(@PathParam("petId") String petId) {
        return petService.findById(UUID.fromString(petId));
    }

    @PUT
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePet(@PathParam("petId") String petId, @RequestBody @Valid PetRecord petRecord) {
        PetRecord update = petService.update(UUID.fromString(petId), petRecord);
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
        petService.delete(UUID.fromString(petId));
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
