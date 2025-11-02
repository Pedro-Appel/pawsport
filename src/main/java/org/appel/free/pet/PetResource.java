package org.appel.free.pet;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.appel.free.pet.treatment.TreatmentRecord;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.reactive.RestResponse;

import java.net.URI;
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
    public RestResponse<Uni<PetRecord>> createPet(@RequestBody @Valid PetRecord record) {
        return RestResponse.status(Response.Status.CREATED, service.create(record));
    }

    @GET
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<PetRecord> getPetInformation(@PathParam("petId") String petId) {
        return service.findById(UUID.fromString(petId));
    }

    @PUT
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> updatePet(@PathParam("petId") String petId, @RequestBody @Valid PetRecord petRecord) {
        return service.update(UUID.fromString(petId), petRecord)
                .onItem().transform(record -> Response.ok(record).build());
    }

    @DELETE
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> deletePet(@PathParam("petId") String petId) {
        return service.delete(UUID.fromString(petId));
    }

    @POST
    @Path("/{petId}/treatment")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> createTreatment(@PathParam("petId") UUID petId, @RequestBody @Valid TreatmentRecord record) {
        return service.addTreatment(petId, record)
                .map(e -> RestResponse.created(URI.create("/api/v1/pet/" + e.id())).toResponse());
    }

    @GET
    @Path("/{petId}/treatment")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> listPetTreatments(@PathParam("petId") UUID petId, @QueryParam("limit") @DefaultValue("10") @Min(1) @Max(50) int limit, @DefaultValue("0") @Min(0) @QueryParam("_offset") int offset) {
        return service.listPetTreatments(petId, limit, offset)
                .map(resp -> Response.status(Response.Status.OK).entity(resp).build());
    }
    /*
    } catch (EntityNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND);
    } catch (NotFoundException e) {
        return Response.status(Response.Status.NO_CONTENT);
    }
     */

    @GET
    @Path("/{petId}/treatment/{treatmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getTreatmentDetail(@PathParam("petId") UUID petId, @PathParam("treatmentId") Long treatmentId) {
        return service.findTreatmentDetails(treatmentId)
                .onItem().ifNotNull().transform((treatmentRecord) -> Response.ok(treatmentRecord).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{petId}/treatment/{treatmentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> deleteTreatment(@PathParam("petId") UUID petId, @PathParam("treatmentId") long treatmentId) {
        return service.deleteTreatment(treatmentId);
    }
}
