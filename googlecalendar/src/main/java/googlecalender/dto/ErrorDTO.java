package googlecalender.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDTO {
    private String title;
    private String detail;
    private Integer status;

    public ErrorDTO() {
    }

    public ErrorDTO(String detail, Integer code, String statusMessage) {
        this.detail = detail;
        this.status = code;
        this.title = statusMessage;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
