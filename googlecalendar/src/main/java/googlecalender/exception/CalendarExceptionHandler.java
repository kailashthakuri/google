package googlecalender.exception;

import googlecalender.dto.ResponseDTO;
import googlecalender.dto.ErrorDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// todo
@ControllerAdvice
public class CalendarExceptionHandler {
    private final static Log logger = LogFactory.getLog(CalendarExceptionHandler.class);


    @ExceptionHandler(CalendarException.class)
    protected ResponseEntity<Object> handleCalendarException(CalendarException ex) {
        logger.error("Error : {}", ex);
        return buildErrorResponse(ex.getErrorDTO(), HttpStatus.valueOf(ex.getErrorDTO().getStatus()));
    }

    @ExceptionHandler(Throwable.class)
    protected ResponseEntity<Object> handleAllOtherException(Throwable ex) {
        ErrorDTO errorDTO = new ErrorDTO();
        logger.error("Error : {}", ex);
        errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDTO.setTitle("Internal Server Error");
        errorDTO.setDetail(ex.getMessage());
        return buildErrorResponse(errorDTO, HttpStatus.valueOf(errorDTO.getStatus()));
    }

    private ResponseEntity<Object> buildErrorResponse(ErrorDTO errorDTO, HttpStatus status) {
        return new ResponseEntity<Object>(errorDTO, status);
    }

    private ResponseEntity<ResponseDTO> buildErrorResponse(ResponseDTO responseDTO, HttpStatus status) {
        return new ResponseEntity<ResponseDTO>(responseDTO, status);
    }
}
