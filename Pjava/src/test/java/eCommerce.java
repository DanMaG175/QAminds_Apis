import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class eCommerce {

    static private String url_base = "webapi.segundamano.mx";
    static private String access_token;
    static private String account_id;
    static private String uuid;

    static private String article0;

    @Test
    public void t01_obtener_categorias(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1/public/categories/insert?lang=es",url_base);
        Response response = given()
                .log()
                .all()
                .queryParam("lang","es")
                .get();
        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();
        System.out.println("Body response: " + body_response);
        System.out.println("Headers response: " + headers_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("categories"));
    }
    @Test
    public void t02_filtrado_smartphone_cdmx(){
        //https://{{url_base}}/listing/{{encFiltro}}
        String encFiltro = "eyJyZWdpb24iOiIxMSIsImNhdGVnb3J5X2x2MCI6IjUwMDAiLCJjYXRlZ29yeV9sdjEiOiI1MDQwIiwiY2F0ZWdvcnlfbHYyIjoiNTA0MSIsInByaWNlIjoiMjAwMCwzNTAwIiwibGltIjoiMjgifQ==";
        RestAssured.baseURI = String.format("https://%s/listing/%s",url_base,encFiltro);
        Response response = given()
                .log()
                .all()
                .post();
        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();
        System.out.println("Body response: " + body_response);
        System.out.println("Headers response: " + headers_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("list_ads"));
    }
    /* + + + +  Falta crear email random + + + + + + */
    @Test
    public void t04_obtener_access_token_con_Basic_Auth_email_pass(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);
        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .auth().preemptive().basic("danmgapi@mailinator.com", "DanApiqa")
                .post();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("access_token"));

        access_token = JsonPath.read(body_response,"$.access_token");
        account_id = JsonPath.read(body_response,"$.account.account_id");
        uuid = JsonPath.read(body_response,"$.account.uuid");
        System.out.println("token: " + access_token);
        System.out.println("account_id: " + JsonPath.read(body_response,"$.account.account_id"));
        System.out.println("uuid: " + JsonPath.read(body_response,"$.account.uuid"));
    }
    @Test
    public void t05_editar_datos_usuario(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1/%s",url_base,account_id);
        String bodyRequest = "{\"account\":{\"name\":\"Project intelliJ\",\"phone\":\"5589562374\",\"professional\":false}}";
        Response response = given()
                .log().all()
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .header("Content-Type","application/json")
                .header("Origin","https://www.segundamano.mx")
                .body(bodyRequest)
                .patch();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("account"));
    }
    @Test
    public void t06_bad_request_datos_usuario_sin_account_en_body(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1/%s",url_base,account_id);
        String bodyRequest = "{{\"name\":\"Project intelliJ\",\"phone\":\"5589562374\",\"professional\":false}}";
        Response response = given()
                .log().all()
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .header("Content-Type","application/json")
                .header("Origin","https://www.segundamano.mx")
                .body(bodyRequest)
                .patch();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);

        assertEquals(400, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("MISSING_PARAMETERS"));
    }
    @Test
    public void t07_bad_request_datos_usuario_telefono_en_blanco_en_body(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1/%s",url_base,account_id);
        String bodyRequest = "{\"account\":{\"name\":\"Project intelliJ\",\"phone\":\"\",\"professional\":false}}";
        Response response = given()
                .log().all()
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .header("Content-Type","application/json")
                .header("Origin","https://www.segundamano.mx")
                .body(bodyRequest)
                .patch();
        String body_response = response.getBody().asString();
        System.out.println("Request: " + bodyRequest);
        System.out.println("Body response: " + body_response);

        assertEquals(400, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("ERROR_PHONE_TOO_SHORT"));
    }
    @Test
    public void t08_obtener_balance_monedas(){
        //https://{{url_base}}/nga/api/v1/api/users/{{uuid}}/counter?lang=es
        RestAssured.baseURI = String.format("https://%s/nga/api/v1/api/users/%s/counter",url_base,uuid);
        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .get();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("{\"unread\":0}"));
    }
    @Test
    public void t09_unauthorized_obtener_balance_monedas(){
        //https://{{url_base}}/nga/api/v1/api/users/{{uuid}}/counter?lang=es
        RestAssured.baseURI = String.format("https://%s/nga/api/v1/api/users/%s/counter",url_base,uuid);
        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .get();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);

        assertEquals(401, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("{\"error\":{\"code\":\"UNAUTHORIZED\"}}"));
    }
    /* + + + + + Están mandando código 415
    415 Unsupported Media Type (en-US) El formato multimedia de los datos solicitados no está soportado por el servidor,
     por lo cual el servidor rechaza la solicitud. + + + + + +
    @Test
    public void t10_obtener_credenciales(){
        //https://{{url_base}}/nga/api/v1/api/hal/{{uuid}}/realtime/credentials
        RestAssured.baseURI = String.format("https://%s/nga/api/v1/api/hal/%s/realtime/credentials",url_base,uuid);
        Response response = given()
                .log().all()
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .post();
        //String body_response = response.getBody().asString();
        //System.out.println("Body response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());

    }*/
    /*
    * Pruebas de crud dirección
    * pruebas de crud anuncio
    * */
    @Test
    public void t17_recomendar_precio_auto(){
        //https://{{url_base}}/price-recommender/v1/public/recommend/
        RestAssured.baseURI = String.format("https://%s/price-recommender/v1/public/recommend/",url_base);
        String bodyRequest = "{\"category\":\"2021\",\"price\":\"0\",\"carbrand\":\"115\",\"carmodel\":\"11509\",\"regdate\":\"2020\",\"mileage\":\"100000\",\"gearbox\":\"1\",\"fuel\":\"1\"}";
        Response response = given()
                .log().all()
                .body(bodyRequest)
                //.header("Content-Type","application/json")
                //.header("origin","https://www.segundamano.mx")
               // .header("accept","application/json, text/plain, */*")
                .post();
        String body_response = response.getBody().asString();
        System.out.println("Request: " + bodyRequest);
        System.out.println("Body response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("recommended"));
    }
    @Test
    public void t18_agregar_articulo_a_favorito(){
        //https://{{url_base}}/favorites/v1/private/accounts/{{uuid}}
        RestAssured.baseURI = String.format("https://%s/favorites/v1/private/accounts/%s",url_base,uuid);
        String bodyRequest = "{\"list_ids\":[937457787,937457786]}";
        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .body(bodyRequest)

                .post();
        /*Falta la parte del token*/
        String body_response = response.getBody().asString();
        System.out.println("Request: " + bodyRequest);
        System.out.println("Body response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("{\"data\":{\"added\":"));

        article0 = JsonPath.read(body_response,"$.added[0]");
    }
    @Test
    public void t19_obtener_articulo_a_favorito(){
        //https://{{url_base}}/favorites/v1/private/accounts/{{uuid}}
        RestAssured.baseURI = String.format("https://%s/favorites/v1/private/accounts/%s",url_base,uuid);
        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .get();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("{\"data\":{\"name\":\"Favoritos\",\"list_ids\":"));
    }
}
