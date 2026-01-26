package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Pet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPet {

    private static final String BASE_URL = "http://5.181.109.28:9090/api/v3";

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("marat")
    public void testDeleteNonexistentPet() {
        Response response = step("Отправка DELETE запроса на удаление несуществующего Pet", () ->
                given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .when()
                .delete(BASE_URL + "/pet/9999"));

        String responseBody = response.getBody().asString();

        step("Проверка статус-код ответа == 200", ()->
                assertEquals(200, response.getStatusCode(),
                "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));

        step("Проверка текст ответа 'Pet deleted'", ()->
                assertEquals("Pet deleted", responseBody,
                "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));
    }

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("marat")
    public void testUpdateNonexistentPet() {
        Pet pet = new Pet();
        pet.setId(9999);
        pet.setName("Non-existent Pet");
        pet.setStatus("available");

        Response response = step("Отправка PUT запроса на обновление несуществующего Pet", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .put(BASE_URL + "/pet"));

        String responseBody = response.getBody().asString();

        step("Проверка статус-код ответа == 404", ()->
                assertEquals(404, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));

        step("Проверка текст ответа 'Pet not found'", ()->
                assertEquals("Pet not found", responseBody,
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));
    }

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("marat")
    public void testGetNonexistentPet() {
        Response response = step("Отправка GET запроса на несуществующего Pet", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .when()
                        .get(BASE_URL + "/pet/9999"));

        String responseBody = response.getBody().asString();

        step("Проверка статус-код ответа == 404", () ->
                assertEquals(404, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));

        step("Проверка текст ответа 'Pet not found'", () ->
                assertEquals("Pet not found", responseBody,
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));
    }

   @ParameterizedTest(name = "Добавление питомца со статусом: {1}")
    @CsvSource({
            "200, Kiwi, available",
            "201, Buddy, pending",
            "202, Snoopy, sold",
            "203, Bob, unknown"
    })
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("marat")
    public void testAddNewPet( int id, String name, String status) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setStatus(status);

        Response response = step("Отправить POST запрос на добавление питомца", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .post(BASE_URL + "/pet"));

        String responseBody = response.getBody().asString();

        step("Проверить, что статус-код ответа == 200", () ->
                assertEquals(200, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody));

        step("Проверка параметров созданного питомца", () -> {
            Pet createPet = response.as(Pet.class);
            assertEquals(pet.getId(), createPet.getId(), "id питомца не совпадают с ожидаемым");
            assertEquals(pet.getName(), createPet.getName(), "Имя питомца не совпадает с ожидаемым");
            assertEquals(pet.getStatus(), createPet.getStatus(), "Статус питомца не совпадает с ожидаемым");
        });
    }
}
