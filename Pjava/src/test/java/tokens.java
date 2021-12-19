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


public class tokens {
    static private String url_base = "webapi.segundamano.mx";
    static private String access_token;
    static private String account_id;
    static private String uuid;
    static private String idAddress;

    //static private String idAddress;
    //static private String article0;
//función para crear un token
    private String obtenerToken() {
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts", url_base);
        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .auth().preemptive().basic("danmgapi@mailinator.com", "DanApiqa")
                .post();
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response);

        access_token = JsonPath.read(body_response, "$.access_token");
        account_id = JsonPath.read(body_response, "$.account.account_id");
        uuid = JsonPath.read(body_response, "$.account.uuid");
        String uuidAccess = uuid + ":" + access_token;
        String uuidToken = Base64.getEncoder().encodeToString(uuidAccess.getBytes());
        System.out.println("accessToken: " + access_token);
        System.out.println("uuid: " + uuid);
        return uuidToken;
    }
    @Test
    public void t10_obtener_credenciales(){
        //https://{{url_base}}/nga/api/v1/api/hal/{{uuid}}/realtime/credentials
        String newToken = obtenerToken();
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
}
