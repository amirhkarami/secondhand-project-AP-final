package ir.ac.aut.secondhand.frontend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FavoriteDto {
    private Long id;

    @JsonAlias({"product", "advertisement"})
    private ProductDto product;

    public FavoriteDto() {
    }

    public FavoriteDto(Long id, ProductDto product) {
        this.id = id;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }
}
