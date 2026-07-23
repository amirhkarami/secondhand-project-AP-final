package ir.ac.aut.secondhand.frontend.util;

import ir.ac.aut.secondhand.frontend.client.ApiClient;
import ir.ac.aut.secondhand.frontend.config.AppConfig;
import ir.ac.aut.secondhand.frontend.model.enums.UserType;
import ir.ac.aut.secondhand.frontend.service.ProductService;
import ir.ac.aut.secondhand.frontend.session.UserSession;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class QueryBuilderTest {
    @Test
    void buildsEncodedQuery() {
        String result = new QueryBuilder("/api/products/search").add("keyword", "used laptop").add("minPrice", 100).build();
        assertEquals("/api/products/search?keyword=used+laptop&minPrice=100", result);
    }

    @Test
    void positivePriceShouldBeAccepted(){
        long price = ValidationUtils.parsePositiveLong("5000000", "Price");
        assertEquals(5000000, price);
    }

    @Test
    void rejectsNegativePrice(){
        assertThrows(IllegalArgumentException.class, () -> ValidationUtils.parsePositiveLong("-10","Price"));
    }

    @Test
    void loginCreatesSession(){
        UserSession session = UserSession.getInstance();
        session.start("token", "ali",1, UserType.USER);
        assertTrue(session.isAuthenticated());
        assertEquals("ali", session.username().get());
    }
    @Test
    void shouldBuildSearchUrl(){
        String url =new QueryBuilder("/api/products/search").add("keyword","car").add("minPrice",1000).build();
        assertEquals(
                "/api/products/search?keyword=car&minPrice=1000", url);
    }
}
