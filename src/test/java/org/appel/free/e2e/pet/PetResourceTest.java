package org.appel.free.e2e.pet;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import org.appel.free.pet.Pet;
import org.appel.free.pet.PetRecord;
import org.appel.free.pet.PetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.appel.free.e2e.pet.PetUtils.getPetId;
import static org.appel.free.e2e.pet.PetUtils.getPetRecord;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

@QuarkusTest
class PetResourceTest {

    public static final String BASE_PATH = "/api/v1/pet/";
    @Inject
    PetRepository petRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        Pet entity = Pet.fromRecord(getPetId(), getPetRecord());
        petRepository.persist(entity);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        petRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("Should return pet by its Id")
    void getPetById() {

        PetRecord record = getPetRecord();
        given()
                .when().get(BASE_PATH + getPetId())
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
    @DisplayName("Should return 204 if no pet with that Id is found")
    void notFoundPet() {
        given()
                .when().get(BASE_PATH + UUID.randomUUID())
                .then()
                .statusCode(204);
    }

    @Test
    @TestTransaction
    @DisplayName("Should correctly update pet and its data")
    void putPet() {
        PetRecord record = getPetRecord();
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(record)
                .when()
                .put(BASE_PATH + getPetId())
                .then()
                .statusCode(200)
                .header("Location", endsWith(BASE_PATH + getPetId()))
                .body("name", equalTo(record.name()))
                .body("species", equalTo(record.species()))
                .body("breed", equalTo(record.breed()))
                .body("conditions", equalTo(record.conditions()))
                .body("color", equalTo(record.color()))
                .body("birthdate", equalTo(record.birthdate().toString()))
                .body("weight", equalTo(record.weight()));
    }

    @Test
    @Transactional
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
                .header("Location", matchesPattern(".*/api/v1/pet/[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}"))
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
                .delete(BASE_PATH + getPetId())
                .then()
                .statusCode(204);
    }
}