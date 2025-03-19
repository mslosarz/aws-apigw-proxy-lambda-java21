package pl.software2.awsblocks.routes;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.junit.jupiter.api.Test;
import pl.software2.awsblocks.exceptions.BadRequestException;
import pl.software2.awsblocks.exceptions.NotFoundException;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AbstractRouteHandlerTest {
    private final ApiGatewayResponseProducer producer = mock(ApiGatewayResponseProducer.class);
    private final APIGatewayV2HTTPEvent request = mock(APIGatewayV2HTTPEvent.class);
    private final APIGatewayV2HTTPResponse response = mock(APIGatewayV2HTTPResponse.class);

    @Test
    public void shouldProduceOkResponse() {
        // given
        when(producer.ok(anyString())).thenReturn(response);
        String result = "result";

        // when
        var handle = new MockRouteHandler(producer, () -> result).handle(request);

        // then
        assertThat(handle).isEqualTo(response);
        verify(producer).ok(eq(result));
    }

    @Test
    public void shouldProduceNotFound() {
        // given
        when(producer.notFound(anyString(), anyString())).thenReturn(response);
        when(request.getRawPath()).thenReturn("/some/path");

        // when
        var handle = new MockRouteHandler(producer, () -> {
            throw new NotFoundException("Exception message");
        }).handle(request);

        // then
        assertThat(handle).isEqualTo(response);
        verify(producer).notFound("/some/path", "Exception message");
    }

    @Test
    public void shouldProduceBadRequest() {
        // given
        when(producer.badRequest(anyString())).thenReturn(response);

        // when
        var handle = new MockRouteHandler(producer, () -> {
            throw new BadRequestException("Exception message");
        }).handle(request);

        // then
        assertThat(handle).isEqualTo(response);
        verify(producer).badRequest("Exception message");
    }

    @Test
    public void shouldProduceInternalServerError() {
        // given
        when(producer.internalServerError(anyString())).thenReturn(response);

        // when
        var handle = new MockRouteHandler(producer, () -> {
            throw new RuntimeException("Exception message");
        }).handle(request);

        // then
        assertThat(handle).isEqualTo(response);
        verify(producer).internalServerError("Exception message");
    }

    static class MockRouteHandler extends AbstractRouteHandler<String> {
        private final Supplier<String> result;

        public MockRouteHandler(ApiGatewayResponseProducer producer, Supplier<String> result) {
            super(producer);
            this.result = result;
        }

        @Override
        public boolean supports(APIGatewayV2HTTPEvent request) {
            return false;
        }

        @Override
        protected String handleRequest(APIGatewayV2HTTPEvent request) {
            return result.get();
        }
    }
}