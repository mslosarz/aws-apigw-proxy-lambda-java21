package pl.software2.awsblocks;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.software2.awsblocks.routes.ApiGatewayResponseProducer;
import pl.software2.awsblocks.routes.RouteHandler;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class AppHandler implements RequestStreamHandler {
    static{
        java.security.Security.setProperty("networkaddress.cache.ttl" , "0");
    }
    private static final AppComponent component = DaggerAppComponent.create();

    @Inject
    ObjectMapper objectMapper;
    @Inject
    ApiGatewayResponseProducer responseProducer;
    @Inject
    Set<RouteHandler> routes;

    public AppHandler() {
        component.inject(this);
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        var event = objectMapper.readValue(inputStream, APIGatewayV2HTTPEvent.class);

        APIGatewayV2HTTPResponse response = routes.stream()
                .filter(route -> route.supports(event))
                .findFirst()
                .map(routeHandler -> routeHandler.handle(event))
                .orElse(responseProducer.notFound(event.getRawPath()));

        objectMapper.writeValue(outputStream, response);
    }
}