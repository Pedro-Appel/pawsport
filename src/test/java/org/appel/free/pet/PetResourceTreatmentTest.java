package org.appel.free.pet;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import org.appel.free.pet.treatment.Treatment;
import org.appel.free.pet.treatment.TreatmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.appel.free.pet.PetUtils.*;

@QuarkusTest
class PetResourceTreatmentTest {

    public static final String BASE_PATH = "/api/v1/pet";
    public static final String TREATMENT_PATH = "/treatment";

    @Inject
    TreatmentRepository treatmentRepository;
    @Inject
    PetRepository petRepository;
    @Inject
    EntityManager entityManager;

    @BeforeEach
    @Transactional
    void setUp() {
        Pet entity = Pet.fromRecord(getPetId(), getPetRecord());
        Treatment treatment = Treatment.fromRecord(getTreatmentRecord());
        petRepository.persist(entity);
        treatmentRepository.persist(treatment);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        petRepository.deleteAll();
        treatmentRepository.deleteAll();
        entityManager.createNativeQuery("ALTER SEQUENCE treatment_seq RESTART WITH 1").executeUpdate();
    }

    @Test
    @TestTransaction
    @DisplayName("Correctly add pet treatment")
    void test() {

        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(getTreatmentRecord())
                .post(BASE_PATH + "/" + getPetId() + TREATMENT_PATH)
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Correctly throw 404 when creating a treatment to a non existing pet")
    void test6() {
        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(getTreatmentRecord())
                .post(BASE_PATH + "/" + UUID.randomUUID() + TREATMENT_PATH)
                .then()
                .statusCode(404);
    }

    @Test
    @Transactional
    @DisplayName("Correctly fetch all pet treatments")
    void test2() {
        Treatment treatment = Treatment.fromRecord(getTreatmentRecord());
        treatmentRepository.persist(treatment);
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .get(BASE_PATH + "/" + getPetId() + TREATMENT_PATH)
                .then()
                .statusCode(200);
    }


    @Test
    @DisplayName("No existing pet treatments")
    void test3() {
        RestAssured
                .given()
                .get(BASE_PATH + "/" + UUID.randomUUID() + TREATMENT_PATH)
                .then()
                .statusCode(204);
    }

    @Test
    @Transactional
    @DisplayName("Correctly fetch treatment details")
    void test4() {

        Treatment first = treatmentRepository.findAll().list().getFirst();
        RestAssured
                .given()
                .get(BASE_PATH + "/" + getPetId() + TREATMENT_PATH + "/" + first.toRecord().id())
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Pet treatments not found")
    void test5() {
        RestAssured
                .given()
                .get(BASE_PATH + "/" + getPetId() + TREATMENT_PATH + "/" + 99999L)
                .then()
                .statusCode(404);
    }
}