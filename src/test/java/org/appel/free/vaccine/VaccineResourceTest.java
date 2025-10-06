package org.appel.free.vaccine;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

@QuarkusTest
public class VaccineResourceTest {
    private static final String PET_ID = "19d66ad4-a771-4d2e-966a-d4a7719d2e11";
    private static final String BASE_PATH = "/api/v1/vaccine/";

    @Inject
    VaccineRepository vaccineRepository;
    @Inject
    EntityManager entityManager;

    @BeforeEach
    @Transactional
    void setUp() {
        Vaccine entity = Vaccine.fromRecord(getVaccineRecord());
        vaccineRepository.persist(entity);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        vaccineRepository.deleteAll();
        entityManager.createNativeQuery("ALTER SEQUENCE vaccine_seq RESTART WITH 1").executeUpdate();

    }

    @Test
    @DisplayName("Should correctly save a vaccine and its information")
    public void saveVaccine() {
        VaccineRecord vaccineRecord = getVaccineRecord();
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(vaccineRecord)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(201)
                .header("Location", matchesPattern(".*/api/v1/vaccine/[0-9]*"))
                .body("petId", equalTo(PET_ID))
                .body("date", equalTo(LocalDate.now().toString()))
                .body("type", equalTo("type"))
                .body("expirationDate", equalTo(LocalDate.now().plusYears(4).toString()));

    }
    @Test
    @DisplayName("Should correctly retrieve a vaccine and its information")
    public void fetchVaccine() {
        Vaccine first = vaccineRepository.findAll().list().getFirst();
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get(BASE_PATH + first.toRecord().vaccineId())
                .then()
                .statusCode(200)
                .body("petId", equalTo(PET_ID))
                .body("date", equalTo(LocalDate.now().toString()))
                .body("type", equalTo("type"))
                .body("expirationDate", equalTo(LocalDate.now().plusYears(4).toString()));

    }

    @Test
    @DisplayName("Should correctly soft delete a vaccine and its information")
    public void deleteVaccine() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .delete(BASE_PATH + "1")
                .then()
                .statusCode(204);

    }

    private static VaccineRecord getVaccineRecord() {
        return new VaccineRecord(0, UUID.fromString(PET_ID), UUID.randomUUID(), "type", LocalDate.now(), LocalDate.now().plusYears(4));
    }
}
