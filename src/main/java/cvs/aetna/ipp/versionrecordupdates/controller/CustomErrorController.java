package cvs.aetna.ipp.versionrecordupdates.controller;

import cvs.aetna.ipp.versionrecordupdates.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomErrorController.class);
    
    private final ErrorAttributes errorAttributes;
    
    /**
     * Constructor that initializes the controller with error attributes
     * 
     * @param errorAttributes Spring's error attributes holder
     */
    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }
    
    /**
     * Handles all errors that are forwarded to the /error endpoint
     * Standardizes error responses across the application
     * Provides detailed logging for 404 errors to help diagnose missing endpoints
     * 
     * @param request The HTTP request that resulted in an error
     * @return A standardized error response with appropriate HTTP status code
     */
    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
        WebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> errorInfo = errorAttributes.getErrorAttributes(webRequest, 
                ErrorAttributeOptions.defaults());
        
        HttpStatus status = HttpStatus.valueOf((Integer) errorInfo.get("status"));
        String errorMessage = (String) errorInfo.get("error");
        
        if (errorInfo.containsKey("message") && errorInfo.get("message") != null) {
            errorMessage += ": " + errorInfo.get("message");
        }
        
        // Log detailed information about 404 errors
        if (status == HttpStatus.NOT_FOUND) {
            LOGGER.error("404 NOT FOUND: Request path: {}, Query string: {}, Client IP: {}, User agent: {}",
                        request.getRequestURI(),
                        request.getQueryString(),
                        request.getRemoteAddr(),
                        request.getHeader("User-Agent"));
        }
        
        return new ResponseEntity<>(new ErrorResponse(errorMessage), status);
    }
}
