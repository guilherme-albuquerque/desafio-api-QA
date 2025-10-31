package br.com.desafio.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import java.util.UUID;

@DisplayName("Testes de cadastro de usuarios")
public class UsuariosTest extends BaseTest {

    @Test
    @DisplayName("Deve cadastrar o usuario com sucesso")
    void testCadastroUsuarioValido() {
        String response = given()
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"API QA\", \"email\": \"api" + UUID.randomUUID() + "@test.com\", \"password\": \"123456\", \"administrador\": \"true\" }")
                .when()
                .post("/usuarios")
                .then()
                .statusCode(201)
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", notNullValue())
                .extract().asString();

        System.out.println("Resposta do cadastro válido: " + response);
    }

    @Test
    @DisplayName("Não deve cadastrar o usuario com email existente")
    void testCadastroUsuarioEmailExistente() {
        String email = "api" + UUID.randomUUID() + "@test.com";
        String payload = "{ \"nome\": \"API QA\", \"email\": \"" + email + "\", \"password\": \"123456\", \"administrador\": \"true\" }";

        // Cadastro o primeiro email
        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(201);

        // Segundo cadastro com o mesmo email
        String response = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(400)
                .body("message", equalTo("Este email já está sendo usado"))
                .extract().asString();

        System.out.println("Resposta do cadastro com email existente: " + response);
    }

}