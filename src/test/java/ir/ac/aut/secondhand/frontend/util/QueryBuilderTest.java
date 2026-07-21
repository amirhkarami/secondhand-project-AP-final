package ir.ac.aut.secondhand.frontend.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryBuilderTest {
    @Test
    void buildsEncodedQuery() {
        String result = new QueryBuilder("/api/products/search")
                .add("keyword", "used laptop")
                .add("minPrice", 100)
                .build();
        assertEquals("/api/products/search?keyword=used+laptop&minPrice=100", result);
    }
}
