package cvs.aetna.ipp.versionrecordupdates.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JacksonConfig {

    /**
     * Creates a Jackson module for handling MongoDB ObjectId serialization/deserialization
     * Converts ObjectId to String when sending JSON responses
     * Converts String to ObjectId when receiving JSON requests
     * This allows seamless integration between MongoDB's ObjectId and JSON representation
     * 
     * @return A Jackson module for ObjectId handling
     */
    @Bean
    public com.fasterxml.jackson.databind.Module objectIdModule() {
        SimpleModule module = new SimpleModule("ObjectIdModule");
        
        // Serializer: Convert ObjectId to String when sending response
        module.addSerializer(ObjectId.class, new JsonSerializer<ObjectId>() {
            @Override
            public void serialize(ObjectId objectId, JsonGenerator jsonGenerator, 
                                 SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(objectId.toString());
            }
        });
        
        // Deserializer: Convert String to ObjectId when receiving request
        module.addDeserializer(ObjectId.class, new JsonDeserializer<ObjectId>() {
            @Override
            public ObjectId deserialize(JsonParser jsonParser, 
                                      DeserializationContext deserializationContext) throws IOException {
                String id = jsonParser.getText();
                return id.length() > 0 ? new ObjectId(id) : null;
            }
        });
        
        return module;
    }
}
