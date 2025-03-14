package pl.software2.awsblocks;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;


class AppHandlerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AppHandler appHandler = new AppHandler();
    private final Context context = Mockito.mock(Context.class);

    @Test
    public void basicTest() {
        // given
        var rq = load("rq.json", APIGatewayV2HTTPEvent.class);

        // when
        var response = callLambda(appHandler, rq, context, APIGatewayV2HTTPEvent.class);

        // then
        assertThat(response).isNotNull();
    }

    private <RS> RS load(String fileName, Class<RS> clazz) {
        try (var steam = getClass().getClassLoader().getResourceAsStream(fileName)) {
            return objectMapper.readValue(steam, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <RS> Response<RS> callLambda(AppHandler lambda, APIGatewayV2HTTPEvent request, Context context,
                                         Class<RS> responseModel) {
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(
                objectMapper.writeValueAsBytes(request))) {
            final var outputStream = new ByteArrayOutputStream();
            lambda.handleRequest(inputStream, outputStream, context);
            outputStream.close();
            final var apiGwProxyResponse = objectMapper.readValue(outputStream.toString(StandardCharsets.UTF_8), APIGatewayV2HTTPResponse.class);
            if (apiGwProxyResponse.getIsBase64Encoded()) {
                return new Response<>(apiGwProxyResponse, null);
            }
            return new Response<>(apiGwProxyResponse,
                    objectMapper.readValue(apiGwProxyResponse.getBody(), responseModel));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    record Response<RS>(APIGatewayV2HTTPResponse rawResponse, RS model) {
    }
}