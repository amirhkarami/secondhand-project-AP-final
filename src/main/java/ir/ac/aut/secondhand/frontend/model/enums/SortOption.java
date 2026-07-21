package ir.ac.aut.secondhand.frontend.model.enums;

public enum SortOption {
    NEWEST("newest", "Newest"),
    PRICE_ASC("price_asc", "Lowest price"),
    PRICE_DESC("price_desc", "Highest price");

    private final String apiValue;
    private final String displayName;

    SortOption(String apiValue, String displayName) {
        this.apiValue = apiValue;
        this.displayName = displayName;
    }

    public String getApiValue() {
        return apiValue;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
