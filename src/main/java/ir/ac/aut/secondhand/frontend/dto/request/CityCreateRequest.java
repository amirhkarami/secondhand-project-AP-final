package ir.ac.aut.secondhand.frontend.dto.request;

public class CityCreateRequest {
    private String name;

    public CityCreateRequest() {
    }

    public CityCreateRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
