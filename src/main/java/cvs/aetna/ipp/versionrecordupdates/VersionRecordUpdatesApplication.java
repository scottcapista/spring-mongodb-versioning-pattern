package cvs.aetna.ipp.versionrecordupdates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableMongoRepositories(basePackages = "cvs.aetna.ipp.versionrecordupdates")
public class VersionRecordUpdatesApplication {
    /**
     * Main entry point for the application
     * Launches the Spring Boot application with MongoDB support
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(VersionRecordUpdatesApplication.class, args);
    }
}