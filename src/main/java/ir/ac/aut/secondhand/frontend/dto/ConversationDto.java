package ir.ac.aut.secondhand.frontend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationDto {
    private Long Id;
    private ProductDto product;
    private UserDto buyer;
    private UserDto seller;

    @JsonAlias({"conversationId","id"})
    private Long id;

    @JsonAlias({"productId", "advertisementId"})
    private Long productId;

    @JsonAlias({"productTitle", "advertisementTitle"})
    private String productTitle;

    @JsonAlias({"otherUsername", "participantUsername"})
    private String otherUsername;

    private String lastMessage;
    private LocalDateTime lastMessageAt;

    public ConversationDto() {
    }

    public ConversationDto(Long id, ProductDto product, UserDto buyer, UserDto seller,
                           Long productId, String productTitle, String otherUsername,
                           String lastMessage, LocalDateTime lastMessageAt) {
        this.id = id;
        this.product = product;
        this.buyer = buyer;
        this.seller = seller;
        this.productId = productId;
        this.productTitle = productTitle;
        this.otherUsername = otherUsername;
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
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

    public UserDto getBuyer() {
        return buyer;
    }

    public void setBuyer(UserDto buyer) {
        this.buyer = buyer;
    }

    public UserDto getSeller() {
        return seller;
    }

    public void setSeller(UserDto seller) {
        this.seller = seller;
    }

    public Long getProductId() {
        if (productId != null) {
            return productId;
        }
        return product == null ? null : product.getId();
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        if (productTitle != null && !productTitle.isBlank()) {
            return productTitle;
        }
        return product == null ? null : product.getTitle();
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getOtherUsername() {
        return otherUsername;
    }

    public void setOtherUsername(String otherUsername) {
        this.otherUsername = otherUsername;
    }

    public String resolveOtherUsername(String currentUsername) {
        if (otherUsername != null && !otherUsername.isBlank()) {
            return otherUsername;
        }
        if (buyer != null && buyer.getUsername() != null
                && !buyer.getUsername().equals(currentUsername)) {
            return buyer.getUsername();
        }
        if (seller != null && seller.getUsername() != null
                && !seller.getUsername().equals(currentUsername)) {
            return seller.getUsername();
        }
        return null;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessageText(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @JsonSetter("lastMessage")
    public void readLastMessage(JsonNode node) {
        if (node == null || node.isNull()) {
            this.lastMessage = null;
        } else if (node.isTextual()) {
            this.lastMessage = node.asText();
        } else if (node.hasNonNull("content")) {
            this.lastMessage = node.get("content").asText();
        } else {
            this.lastMessage = node.toString();
        }
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    @Override
    public String toString() {
        String title = getProductTitle();
        return title == null ? "Conversation #" + id : title;
    }
}
