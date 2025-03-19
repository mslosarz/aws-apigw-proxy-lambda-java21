package pl.software2.awsblocks.routes;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.software2.awsblocks.model.Error;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class ApiGatewayResponseProducer {
    private final ObjectMapper objectMapper;

    @Inject
    public ApiGatewayResponseProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> APIGatewayV2HTTPResponse ok(T body) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withHeaders(Map.of("Content-Type", "application/json", "Content-Encoding", "gzip"))
                .withBody(Base64.getEncoder().encodeToString(gzip(body)))
                .withIsBase64Encoded(true)
                .build();
    }

    public APIGatewayV2HTTPResponse notFound(String path, String message) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(404)
                .withHeaders(Map.of("Content-Type", "application/json", "Content-Encoding", "gzip"))
                .withBody(Base64.getEncoder().encodeToString(gzip(Error.notFound(path + " " + message))))
                .withIsBase64Encoded(true)
                .build();
    }

    public APIGatewayV2HTTPResponse notFound(String path) {
        return notFound(path, "was not found");
    }

    public APIGatewayV2HTTPResponse badRequest(String message) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(400)
                .withHeaders(Map.of("Content-Type", "application/json", "Content-Encoding", "gzip"))
                .withBody(Base64.getEncoder().encodeToString(gzip(Error.badRequest(message))))
                .withIsBase64Encoded(true)
                .build();
    }

    public APIGatewayV2HTTPResponse internalServerError(String message) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(500)
                .withHeaders(Map.of("Content-Type", "application/json", "Content-Encoding", "gzip"))
                .withBody(Base64.getEncoder().encodeToString(gzip(Error.internalServerError(message))))
                .withIsBase64Encoded(true)
                .build();
    }

    private byte[] gzip(Object event) {
        var outputStream = new ByteArrayOutputStream();
        try(GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
            gzip.write(objectMapper.writeValueAsBytes(event));
            gzip.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();
    }
}
