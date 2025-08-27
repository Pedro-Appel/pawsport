package org.appel.free.pet;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

@QuarkusTest
class PetResourceTest {

    public static final String BASE_PATH = "/api/v1/pet/";

    @Test
    @DisplayName("Should return pet by its Id")
    void getPetById() {

        PetRecord record = getPetRecord();
        ValidatableResponse validatableResponse = given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(record)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(201);

        String locationHeader = validatableResponse.extract().header("Location");
        String savedId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
        given()
                .when().get(BASE_PATH + savedId)
                .then()
                .statusCode(200)
                .body("name", equalTo(record.name()))
                .body("species", equalTo(record.species()))
                .body("breed", equalTo(record.breed()))
                .body("conditions", equalTo(record.conditions()))
                .body("color", equalTo(record.color()))
                .body("birthdate", equalTo(record.birthdate().toString()))
                .body("weight", equalTo(record.weight()));
    }

    @Test
    @DisplayName("Should return 204 if no pet with that Id")
    void noPetWithId() {
        given()
                .when().get(BASE_PATH + UUID.randomUUID())
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Should correctly update pet and its data")
    void putPet() {
        PetRecord record = getPetRecord();
        UUID uuid = UUID.randomUUID();
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(record)
                .when()
                .put(BASE_PATH + uuid)
                .then()
                .statusCode(200)
                .header("Location", endsWith(BASE_PATH + uuid))
                .body("name", equalTo(record.name()))
                .body("species", equalTo(record.species()))
                .body("breed", equalTo(record.breed()))
                .body("conditions", equalTo(record.conditions()))
                .body("color", equalTo(record.color()))
                .body("birthdate", equalTo(record.birthdate().toString()))
                .body("weight", equalTo(record.weight()));
    }

    @Test
    @DisplayName("Should correctly save a pet and its data")
    void postPet() {
        PetRecord record = getPetRecord();
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(record)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(201)
                .header("Location", matchesPattern(".*/api/v1/pet/[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}")) //83b50874-115f-4051-b4ab-92dce0513acb
                .body("name", equalTo(record.name()))
                .body("species", equalTo(record.species()))
                .body("breed", equalTo(record.breed()))
                .body("conditions", equalTo(record.conditions()))
                .body("color", equalTo(record.color()))
                .body("birthdate", equalTo(record.birthdate().toString()))
                .body("weight", equalTo(record.weight()));
    }

    @Test
    @DisplayName("Should correctly soft delete a pet")
    void deletePet() {
        PetRecord record = getPetRecord();
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(record)
                .when()
                .delete(BASE_PATH + UUID.randomUUID())
                .then()
                .statusCode(204);
    }

    private PetRecord getPetRecord() {
        return new PetRecord(
                UUID.randomUUID(),
                "name",
                "species",
                "breed",
                "conditions",
                LocalDate.now().minusYears(10),
                10.1f,
                "color"
        );
    }
}