package br.com.desafio.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Testes de cadastro de produtos na API")
public class ProdutosTest extends BaseTest {

    private String obterToken() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"fulano@qa.com\", \"password\": \"teste\" }")
                .when()
                .post("/login");
        return response.jsonPath().getString("authorization");
    }

    @Test
    @DisplayName("Deve cadastrar produto com dados válidos e retornar sucesso")
    void testCadastroProdutoValido() {
        String token = obterToken();
        String nomeProduto = "Produto QA " + UUID.randomUUID();
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body("{ \"nome\": \"" + nomeProduto + "\", \"preco\": 200, \"descricao\": \"Produto de teste\", \"quantidade\": 10 }")
                .when()
                .post("/produtos");

        System.out.println("Corpo da resposta: " + response.asString());

        response.then()
                .assertThat()
                .statusCode(anyOf(is(201), is(200)))
                .body("$", hasKey("_id"));
    }


    @Test
    @DisplayName("Não deve cadastrar produto sem campos obrigatórios e deve retornar erro")
    void testCadastroProdutoCamposObrigatoriosAusentes() {
        String token = obterToken();
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .body("{ \"nome\": \"\", \"descricao\": \"\" }") // Remove campos nulos
                .when()
                .post("/produtos");

        response.then()
                .assertThat()
                .statusCode(400);

        // Exibe o corpo para depuração
        System.out.println("Corpo da resposta erro: " + response.asString());

        // Tenta validar mensagem de erro, se existir
        if (response.jsonPath().get("message") != null) {
            response.then().body("message", containsString("campo"));
        }
    }
}
