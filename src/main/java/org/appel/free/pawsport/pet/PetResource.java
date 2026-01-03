package org.appel.free.pawsport.pet;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.appel.free.pawsport.pet.treatment.TreatmentRecord;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.util.UUID;

import static org.appel.free.pawsport.shared.Constants.PETS_API_PATH;

@Path(PETS_API_PATH)
public class PetResource {

    private final PetService service;

    public PetResource(PetService service) {
        this.service = service;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> createPet(@RequestBody @Valid PetRecord record) {
        return service.create(record);
    }

    @GET
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getPetInformation(@PathParam("petId") String petId) {
        return service.findById(UUID.fromString(petId));
    }

    @PUT
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> updatePet(@PathParam("petId") String petId, @RequestBody @Valid PetRecord petRecord) {
        return service.update(UUID.fromString(petId), petRecord);
    }

    @DELETE
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> deletePet(@PathParam("petId") String petId) {
        return service.delete(UUID.fromString(petId));
    }

    @POST
    @Path("/{petId}/treatments")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> createTreatment(@PathParam("petId") UUID petId, @RequestBody @Valid TreatmentRecord record) {
        return service.addTreatment(petId, record);
    }

    @GET
    @Path("/{petId}/treatments")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> listPetTreatments(@PathParam("petId") UUID petId,
                                           @QueryParam("_limit") @DefaultValue("10") @Min(1) @Max(50) int limit,
                                           @QueryParam("_offset") @DefaultValue("0") @Min(0) int offset) {
        return service.listPetTreatments(petId, limit, offset);
    }

    @GET
    @Path("/{petId}/treatments/{treatmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getTreatmentDetail(@PathParam("petId") UUID petId, @PathParam("treatmentId") long treatmentId) {
        return service.findTreatmentDetails(treatmentId);
    }

    @DELETE
    @Path("/{petId}/treatments/{treatmentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> deleteTreatment(@PathParam("petId") UUID petId, @PathParam("treatmentId") long treatmentId) {
        return service.deleteTreatment(treatmentId);
    }
}
