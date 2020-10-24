package googlecalender.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {
    private Object data;
    private ErrorDTO error;

    public ResponseDTO() {
    }

    public ResponseDTO(ErrorDTO error) {
        this.error = error;
    }

    public ResponseDTO(Object data) {
        this.data = data;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
