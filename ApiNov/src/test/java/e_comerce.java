import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import com.jayway.jsonpath.JsonPath;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class e_comerce {

    //variables
    static private String base_url = "webapi.segundamano.mx";
    static private String access_token;
    static private String account_id;
    static private String uuid;

    @Test
    public void t01_obtener_categorias(){
        //Configurar URI
        RestAssured.baseURI = String.format("https://%s/nga/api/v1/public/categories/insert",base_url);
        //hacer el request y guardarlo en response

        Response response = given()
                .log()
                .all()
                .queryParam("lang","es")
                .get();

        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();
        //String body_p = response.prettyPrint();
        System.out.println("Body response: " + body_response);
        System.out.println("Headers response: " + headers_response);
        //System.out.println("Body response: " + body_p);

        assertEquals(200,response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("categories"));

    }

    @Test
    public void t02_obtener_token(){
        //Configurar URI
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",base_url);
        String token_basic = "dmVudGFzNDgxMTgzQG1haWxpbmF0b3IuY29tOjQ4MTE4NA==";

        Response resp = given()
                .log().all()
                .queryParam("lang","es")
                .header("Authorization","Basic " + token_basic)
                .post();

        String body_response = resp.getBody().asString();
        String headers_response = resp.getHeaders().toString();
        //String body_p = response.prettyPrint();
        System.out.println("Body response: " + body_response);
        System.out.println("Headers response: " + headers_response);
        //System.out.println("Body response: " + body_p);

        assertEquals(200, resp.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("access_token"));
    }

    @Test
    public void t03_obtener_token_con_Basic_Auth_email_pass(){
        //Configurar URI
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",base_url);
        //String token_basic = "dmVudGFzNDgxMTgzQG1haWxpbmF0b3IuY29tOjQ4MTE4NA== ";

        Response resp = given()
                .log().all()
                .queryParam("lang","es")
                .auth().preemptive().basic("ventas481183@mailinator.com", "481184")
                //.header("Authorization","Basic " + token_basic)
                .post();

        String body_response = resp.getBody().asString();
        String headers_response = resp.getHeaders().toString();
        //String body_p = response.prettyPrint();
        System.out.println("Body response: " + body_response);


        assertEquals(200, resp.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("access_token"));

        access_token = JsonPath.read(body_response,"$.access_token");
        System.out.println("token: " + access_token);

        System.out.println("account_id: " + JsonPath.read(body_response,"$.account.account_id"));
        System.out.println("uuid: " + JsonPath.read(body_response,"$.account.uuid"));

        account_id = JsonPath.read(body_response,"$.account.account_id");
        uuid = JsonPath.read(body_response,"$.account.uuid");

        //console.log("Token: ", responseJson.access_token)
        //console.log("account_id: ",responseJson.account.account_id)
        //console.log("uuid: ", responseJson.account.uuid)
    }
    @Test
    public void t04_editar_datos_usuario(){
        //Configurar URI

        RestAssured.baseURI = String.format("https://%s/nga/api/v1/%s",base_url,account_id);

        String body2 = "{\"account\":{\"name\":\"P5 intellij\",\"phone\":\"5589562374\",\"professional\":false}}";

        Response resp = given()
                .log().all()
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .header("Content-Type","application/json")
                .header("Origin","https://www.segundamano.mx")
                .body(body2)
                .patch();

        String body_response = resp.getBody().asString();
        String headers_response = resp.getHeaders().toString();
        //String body_p = response.prettyPrint();
        System.out.println("Body response: " + body_response);
        System.out.println("Headers response: " + headers_response);
        //System.out.println("Body response: " + body_p);

        assertEquals(200, resp.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("account"));
    }
}