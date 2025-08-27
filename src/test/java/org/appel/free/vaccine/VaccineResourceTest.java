package org.appel.free.vaccine;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

@QuarkusTest
public class VaccineResourceTest {

    public static final String PET_ID = "83b50874-115f-4051-b4ab-92dce0513acb";
    public static final String BASE_PATH = "/api/v1/vaccine/";

    @Test
    @DisplayName("Should correctly save, retrieve and soft delete a vaccine and its information")
    public void deleteVaccine() {

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

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get(BASE_PATH + "1")
                .then()
                .statusCode(200)
                .body("petId", equalTo(PET_ID))
                .body("date", equalTo(LocalDate.now().toString()))
                .body("type", equalTo("type"))
                .body("expirationDate", equalTo(LocalDate.now().plusYears(4).toString()));

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
