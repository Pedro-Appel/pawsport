package org.appel.free.e2e.vaccine;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.appel.free.vaccine.Vaccine;
import org.appel.free.vaccine.VaccineRecord;
import org.appel.free.vaccine.VaccineRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

@SuppressWarnings("ALL")
@QuarkusTest
public class VaccineResourceTest {
    private static final String PET_ID = "19d66ad4-a771-4d2e-966a-d4a7719d2e11";
    private static final String BASE_PATH = "/api/v1/vaccine/";

    @Inject
    VaccineRepository vaccineRepository;
    @Inject
    EntityManager entityManager;

    @Test
    @Order(1)
    @DisplayName("Should correctly save a vaccine and its information")
    public void saveVaccine() {
        Panache.withTransaction(Vaccine::deleteAll).await().atMost(Duration.of(1, ChronoUnit.SECONDS));
        entityManager.createNativeQuery("ALTER SEQUENCE vaccine_seq RESTART WITH 1").executeUpdate();
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
    @Order(2)
    @DisplayName("Should correctly retrieve a vaccine and its information")
    public void fetchVaccine() {

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get(BASE_PATH + 1)
                .then()
                .statusCode(200)
                .body("petId", equalTo(PET_ID))
                .body("date", equalTo(LocalDate.now().toString()))
                .body("type", equalTo("type"))
                .body("expirationDate", equalTo(LocalDate.now().plusYears(4).toString()));

    }

    @Test
    @Order(3)
    @DisplayName("Should correctly soft delete a vaccine and its information")
    public void deleteVaccine() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .delete(BASE_PATH + 1)
                .then()
                .statusCode(204);

    }

    private static VaccineRecord getVaccineRecord() {
        return new VaccineRecord(0, UUID.fromString(PET_ID), UUID.randomUUID(), "type", LocalDate.now(), LocalDate.now().plusYears(4));
    }
}
