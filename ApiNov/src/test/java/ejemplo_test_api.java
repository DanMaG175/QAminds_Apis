import io.restassured.RestAssured;
import org.junit.Test;
import io.restassured.response.Response;
import static org.junit.Assert.*;
import static io.restassured.RestAssured.*;

public class ejemplo_test_api {

    @Test
    public void api_Covid_test(){
        RestAssured.baseURI = String.format("https://api.quarantine.country/api/v1/summary/latest");
        Response response = given()
                .headers("Accept", "application/json")
                .get();
        String body_response = response.getBody().asString();

        System.out.println("Body response: " + body_response);
        assertEquals(200,response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("total_cases"));
    }
}

