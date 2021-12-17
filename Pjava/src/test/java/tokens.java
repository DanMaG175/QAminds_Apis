import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)


public class tokens {
    static private String url_base = "webapi.segundamano.mx";
    static private String access_token;
    static private String account_id;
    static private String uuid;

    //static private String article0;

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
