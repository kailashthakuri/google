package googlecalender.exception;

import googlecalender.dto.ErrorDTO;

import java.time.Instant;

public class CalendarException extends RuntimeException {
    private Long timeStamp;
    private ErrorDTO errorDTO;

    public CalendarException(ErrorDTO errorDTO) {
        super(errorDTO.getDetail());
        this.timeStamp = Instant.now().toEpochMilli();
        this.errorDTO = errorDTO;
    }

    public ErrorDTO getErrorDTO() {
        return errorDTO;
    }

    public void setErrorDTO(ErrorDTO errorDTO) {
        this.errorDTO = errorDTO;
    }
}
