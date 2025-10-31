package br.com.desafio.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Testes itens de carrinho na API")
public class CarrinhosTest extends BaseTest {

    private String obterToken() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"fulano@qa.com\", \"password\": \"teste\" }")
                .when()
                .post("/login");
        return response.jsonPath().getString("authorization");
    }

    @Test
    @DisplayName("Deve criar carrinho com produto válido")
    void testCriacaoCarrinhoValido() {
        String token = obterToken();

        given()
                .header("Authorization", token)
                .when()
                .delete("/carrinhos/concluir-compra");

        String produtoId = "BeeJh5lz3k6kSIzA";
        String payload = "{ \"produtos\": [{ \"idProduto\": \"" + produtoId + "\", \"quantidade\": 1 }] }";

        String response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(payload)
                .when()
                .post("/carrinhos")
                .then()
                .statusCode(201)
                .body("message", containsString("Cadastro realizado com sucesso"))
                .body("_id", notNullValue())
                .extract().asString();

        System.out.println("Resposta do carrinho válido: " + response);
    }

    @Test
    @DisplayName("Não deve criar carrinho com produto inexistente")
    void testCriacaoCarrinhoProdutoInexistente() {
        String token = obterToken();

        given()
                .header("Authorization", token)
                .when()
                .delete("/carrinhos/concluir-compra");

        String produtoId = String.valueOf(UUID.randomUUID());
        String payload = "{ \"produtos\": [{ \"idProduto\": \"" + produtoId + "\", \"quantidade\": 1 }] }";

        String response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body(payload)
                .when()
                .post("/carrinhos")
                .then()
                .statusCode(400)
                .body("message", containsString("Produto não encontrado"))
                .extract().asString();

        System.out.println("Resposta do carrinho com produto inexistente: " + response);
    }
}