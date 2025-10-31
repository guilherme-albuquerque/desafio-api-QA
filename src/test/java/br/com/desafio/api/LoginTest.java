package br.com.desafio.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import java.util.UUID;

@DisplayName("Testes de autenticação da API de Login")
public class LoginTest extends BaseTest {

    @Test
    @DisplayName("Deve realizar login com credenciais válidas e retornar token de autorização")
    void TestLoginComCredenciaisValidas() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"fulano@qa.com\", \"password\": \"teste\" }")
                .when()
                .post("/login")
                .then()
                .assertThat()
                .statusCode(200)
                .body("message", equalTo("Login realizado com sucesso"))
                .body("authorization", not(isEmptyOrNullString()));
    }

    @Test
    @DisplayName("Não deve permitir login com senha inválida e deve retornar mensagem de erro")
    void TestLoginComSenhaInvalida() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"teste@qa.com\", \"password\": \"senhaErrada\" }")
                .when()
                .post("/login")
                .then()
                .assertThat()
                .statusCode(401)
                .body("message", containsString("Email e/ou senha inválidos"));
    }
}