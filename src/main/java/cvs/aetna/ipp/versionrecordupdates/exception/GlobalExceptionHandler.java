package cvs.aetna.ipp.versionrecordupdates.exception;

import com.mongodb.MongoWriteException;
import cvs.aetna.ipp.versionrecordupdates.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handles all unhandled exceptions
     * Provides a generic error response for unexpected errors
     * 
     * @param ex The exception that was thrown
     * @return HTTP 500 with error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        LOGGER.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred: " + ex.getMessage()));
    }
    
    /**
     * Handles cases where a requested resource is not found
     * 
     * @param ex The NoSuchElementException that was thrown
     * @return HTTP 404 with error message
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NoSuchElementException ex) {
        LOGGER.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Resource not found: " + ex.getMessage()));
    }
    
    /**
     * Handles malformed request bodies and invalid parameter types
     * 
     * @param ex The exception that was thrown during request processing
     * @return HTTP 400 with error message
     */
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception ex) {
        LOGGER.error("Bad request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Invalid request: " + ex.getMessage()));
    }
    
    /**
     * Handles validation errors and other illegal argument cases
     * 
     * @param ex The IllegalArgumentException that was thrown
     * @return HTTP 400 with error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        LOGGER.error("Invalid argument: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }
    
    /**
     * Handles MongoDB specific write errors
     * Provides special handling for duplicate key errors (code 11000)
     * 
     * @param ex The MongoWriteException that was thrown
     * @return HTTP 409 for duplicate keys, HTTP 400 for other MongoDB errors
     */
    @ExceptionHandler(MongoWriteException.class)
    public ResponseEntity<ErrorResponse> handleMongoWriteException(MongoWriteException ex) {
        LOGGER.error("MongoDB write error: {}", ex.getMessage(), ex);
        
        String errorMessage = ex.getMessage();
        int errorCode = ex.getCode();
        
        // Handle duplicate key error (code 11000)
        if (errorCode == 11000) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("duplicate key error"));
        }
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("MongoDB write error: " + errorMessage));
    }
}
