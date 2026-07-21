package ir.ac.aut.secondhand.frontend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ir.ac.aut.secondhand.frontend.model.enums.ProductStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto {
    private Long id;
    private String title;
    private String description;
    private long price;
    private ProductStatus status = ProductStatus.UNKNOWN;
    private String rejectReason;
    private String imagePath;

    @JsonAlias({"owner", "seller", "user"})
    private UserDto owner;

    @JsonAlias({"ownerUsername", "sellerUsername"})
    private String ownerUsername;

    @JsonAlias({"ownerId", "sellerId", "userId"})
    private Integer ownerId;

    @JsonAlias({"category", "productCategory"})
    private CategoryDto category;

    @JsonAlias({"categoryId", "categoryID"})
    private Long categoryId;

    @JsonAlias({"city", "location"})
    private CityDto city;

    @JsonAlias({"cityId", "cityID"})
    private Integer cityId;

    @JsonAlias({"review", "adReview"})
    private AdReviewDto review;

    @JsonAlias({"images", "productImages", "imageList"})
    private List<ProductImageDto> images = new ArrayList<>();

    @JsonAlias({"createdAt", "creationDate", "postedAt"})
    private LocalDateTime createdAt;

    @JsonAlias({"averageRating", "sellerAverageRating"})
    private Double averageRating;

    @JsonAlias({"categoryName"})
    private String categoryName;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @JsonAlias({"cityName"})
    private String cityName;

    public ProductDto() {

    }

    public ProductDto(Long id, String title, String description, long price,
                      ProductStatus status, String rejectReason, UserDto owner,
                      String ownerUsername, Integer ownerId,
                      CategoryDto category, Long categoryId,
                      CityDto city, Integer cityId, AdReviewDto review,
                      List<ProductImageDto> images, LocalDateTime createdAt,
                      Double averageRating, String cityName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.status = status;
        this.rejectReason = rejectReason;
        this.owner = owner;
        this.ownerUsername = ownerUsername;
        this.ownerId = ownerId;
        this.category = category;
        this.categoryId = categoryId;
        this.city = city;
        this.cityId = cityId;
        this.review = review;
        this.images = images == null ? new ArrayList<>() : new ArrayList<>(images);
        this.createdAt = createdAt;
        this.averageRating = averageRating;
        this.cityName = cityName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status == null ? ProductStatus.UNKNOWN : status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public String getOwnerUsername() {
        if (ownerUsername != null && !ownerUsername.isBlank()) {
            return ownerUsername;
        }
        return owner == null ? null : owner.getUsername();
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public Integer getOwnerId() {
        if (ownerId != null) {
            return ownerId;
        }
        return owner == null ? null : owner.getId();
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public Long getCategoryId() {
        if (categoryId != null) {
            return categoryId;
        }
        return category == null ? null : category.getId();
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public CityDto getCity() {
        return city;
    }

    public void setCity(CityDto city) {
        this.city = city;
    }

    public Integer getCityId() {
        if (cityId != null) {
            return cityId;
        }
        return city == null ? null : city.getId();
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public AdReviewDto getReview() {
        return review;
    }

    public void setReview(AdReviewDto review) {
        this.review = review;
    }

    public List<ProductImageDto> getImages() {
        return images;
    }

    public void setImages(List<ProductImageDto> images) {
        this.images = images == null ? new ArrayList<>() : new ArrayList<>(images);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return title == null ? "Untitled product" : title;
    }
}
