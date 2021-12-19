import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import java.util.Base64;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class eCommerce {

    static private String url_base = "webapi.segundamano.mx";
    static private String access_token;
    static private String account_id;
    static private String uuid;
    static private String idAddress;

    //función para crear un token
    private String obtenerToken(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);
        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .auth().preemptive().basic("danmgapi@mailinator.com", "DanApiqa")
                .post();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);

        access_token = JsonPath.read(body_response,"$.access_token");
        //account_id = JsonPath.read(body_response,"$.account.account_id");
        uuid = JsonPath.read(body_response,"$.account.uuid");
        String uuidAccess = uuid + ":" + access_token;
        String uuidToken = Base64.getEncoder().encodeToString(uuidAccess.getBytes());

        return uuidToken;
    }

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
    @Test
    public void t10_obtener_credenciales(){

        //https://{{url_base}}/nga/api/v1/api/hal/{{uuid}}/realtime/credentials
        //String newToken = obtenerToken();
        RestAssured.baseURI = String.format("https://%s/nga/api/v1/api/hal/%s/realtime/credentials",url_base,uuid);
        Response response = given()
                .log().all()
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .header("Content-Type","application/json; charset=utf-8")
                .post();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(body_response.contains("{\"_links\":{\"self\":{\"href\":" +
                "\"http://segundamanomx.messaging.advgo.net/api/hal/"+uuid+"/realtime/credentials\"}},\"realTimeUser\":" +
                "{\"id\":\"" + uuid + "\",\"xmppJid\":\"" + uuid + "_segundamanomx@xmpp.messaging.advgo.net"));
        assertTrue(body_response.contains("\"realTimeUser\":{\"id\":\""+ uuid +"\",\"xmppJid\":" +
                "\""+ uuid + "_segundamanomx@xmpp.messaging.advgo.net\","));
    }
    @Test
    public void t11_crear_direccion_nueva(){
        //https://{{url_base}}/addresses/v1/create
        RestAssured.baseURI = String.format("https://%s/addresses/v1/create",url_base);
        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .header("Content-Type","application/x-www-form-urlencoded")
                .formParam("contact","Prueba IntelliJ")
                .formParam("phone","5563249875")
                .formParam("rfc","FAFI881231JKL")
                .formParam("zipCode","12345")
                .formParam("exteriorInfo","Calle Intellli 2021")
                .formParam("interiorInfo","D12")
                .formParam("region","3")
                .formParam("municipality","36")
                .formParam("area","34854")
                .formParam("alias","Desde IntelliJ")
                .post();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);

        assertEquals(201, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("addressID\""));
        idAddress = JsonPath.read(body_response,"$.addressID");
        System.out.println("address: " + idAddress);
    }
    @Test
    public void t12_ver_direcciones(){
        RestAssured.baseURI = String.format("https://%s/addresses/v1/get",url_base);
        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .get();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);
        System.out.println("address: " + idAddress);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("addresses"));
        assertTrue(body_response.contains(idAddress));
    }
    @Test
    public void t13_borrar_direcciones(){
    //https://{{url_base}}/addresses/v1/delete/{{idAddress}}
    RestAssured.baseURI = String.format("https://%s/addresses/v1/delete/%s",url_base,idAddress);
    Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .delete();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("{\"message\":\"" + idAddress + " deleted correctly\"}"));
    }
    @Test
    public void t14_ver_direcciones_metodo_no_permitido(){
        RestAssured.baseURI = String.format("https://%s/addresses/v1/get",url_base);
        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .patch();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);

        assertEquals(405, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("Method Not Allowed"));
    }
    @Test
    public void t15_crear_anuncio_nuevo(){
    //https://{{url_base}}/v2/accounts/{{uuid}}/up
        String newToken = obtenerToken();
        RestAssured.baseURI = String.format("https://%s/v2/accounts/%s/up", url_base, uuid);
        String bodyRequest = "{\"category\":\"8082\",\"subject\":\"PC laptop Reparacion\",\"body\":\"Se revisa y repara todo tipo de equipo\",\"" +
                "price\":\"500\",\"region\":\"17\",\"municipality\":\"699\",\"area\":\"177439\",\"phone_hidden\":\"false\",\"" +
                "show_phone\":\"true\",\"contact_phone\":\"8855123456\"}";
        Response response = given()
                .log().all()
                .header("Authorization", "Basic " + newToken)
                .header("Accept", "application/json, text/plain, *")
                .header("Content-Type", "application/json")
                .header("X-Source", "PHOENIX_DESKTOP")
                .body(bodyRequest)
                .post();
        String body_response = response.getBody().asString();
        System.out.println("Request: " + bodyRequest);
        System.out.println("Body response: " + body_response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("ad_id"));
    }
    @Test
    public void t16_ver_anuncios_pendientes(){
        //https://{{url_base}}/nga/api/v1/{{account_id}}/klfst?status=pending
        //String newToken = obtenerToken();
        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s/klfst?status=pending", url_base, account_id);
        Response response = given()
                .log().all()
                .header("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .queryParam("status", "pending")
                .get();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);
        System.out.println("accessToken: " + access_token);
        System.out.println("account: " + account_id);
        System.out.println("uuid: " + uuid);
        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("ad_id"));
    }
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
    }
    @Test
    public void t19_obtener_articulo_favorito(){
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
    @Test
    public void t20_cambiar_password(){
        //String newToken = obtenerToken();
        RestAssured.baseURI = String.format("https://%s/nga/api/v1/%s",url_base,account_id);
        String bodyRequest = "{\"account\":{\"password\":\"DanApiqa\"}}";
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
}

