package googlecalender.service;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDTO {
    private String detailMessage;
    private int code;
    private String statusMessage;

    public ErrorDTO(String detailMessage, int code, String statusMessage) {
        this.detailMessage = detailMessage;
        this.code = code;
        this.statusMessage = statusMessage;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
