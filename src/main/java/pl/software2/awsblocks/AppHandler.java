package pl.software2.awsblocks;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class AppHandler implements RequestStreamHandler {
    static{
        java.security.Security.setProperty("networkaddress.cache.ttl" , "0");
    }
    private static final AppComponent component = DaggerAppComponent.create();

    @Inject
    ObjectMapper objectMapper;

    public AppHandler() {
        component.inject(this);
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        var event = objectMapper.readValue(inputStream, APIGatewayV2HTTPEvent.class);
        var response = APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withHeaders(Map.of("Content-Type", "application/json", "Content-Encoding", "gzip"))
                .withBody(Base64.getEncoder().encodeToString(gzip(event)))
                .withIsBase64Encoded(true)
                .build();
        objectMapper.writeValue(outputStream, response);

    }

    private byte[] gzip(APIGatewayV2HTTPEvent event) throws IOException {
        var outputStream = new ByteArrayOutputStream();
        try(GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
            gzip.write(objectMapper.writeValueAsBytes(event));
            gzip.flush();
        }
        return outputStream.toByteArray();
    }
}