package cvs.aetna.ipp.versionrecordupdates.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

    /**
     * Creates a request logging filter that logs all incoming HTTP requests
     * Logs both the request details and response status for monitoring and debugging
     * The filter executes once per request, regardless of forwarding/includes
     * 
     * @return A servlet filter that performs request logging
     */
    @Bean
    public Filter requestLoggingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                String method = request.getMethod();
                String requestURI = request.getRequestURI();
                String queryString = request.getQueryString();
                
                LOGGER.info("Received request: {} {} {}", 
                           method, 
                           requestURI, 
                           queryString != null ? "?" + queryString : "");
                
                try {
                    filterChain.doFilter(request, response);
                } finally {
                    LOGGER.info("Completed request: {} {} with status {}", 
                               method, 
                               requestURI, 
                               response.getStatus());
                }
            }
        };
    }
}
