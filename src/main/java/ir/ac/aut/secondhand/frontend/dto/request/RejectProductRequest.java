package ir.ac.aut.secondhand.frontend.dto.request;

public class RejectProductRequest {
    private String reason;

    public RejectProductRequest() {
    }

    public RejectProductRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
