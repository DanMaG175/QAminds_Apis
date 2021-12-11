import io.restassured.RestAssured;
import org.junit.Test;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

public class e_comerce {
    //variables


    @Test
    public void t01_obtener_categorias(){
        RestAssured.baseURI = String.format(https://webapi.segundamano.mx/nga/api/v1/public/categories/insert?lang=es)
        //RestAssured.baseURI = String.format(https://webapi.segundamano.mx/nga/api/v1/public/categories/insert?lang=es)
        //hacer el request y guardar un response
        Response response = given()
                .log()
                .all()
                .get()
//en este ejemplo implime responso directo
//System.out.println(“Nombre etiqueta: ” + response.getStatusCode());
        /* si se van a revisar mas cosas dentro del body se pasa a un avariable y se convierte a string*/
        String body_response = response.getBody().asString(); // variable
        String headers_response = response.getHeaders().toString();
        String body_p = response.prettyPrint();
        System.out.println(“Nombre etiqueta: ” + body_response); // uso de variable
        System.out.println(“Headers response:  ” + headers_response);
        System.out.println(“Body bonito ” + body_p);
    }
    @Test
    public void t02_get_token(){
        RestAssured.baseURI = String.format(https://webapi.segundamano.mx/nga/api/v1/public/categories/insert?lang=es)
    }
}
