package pl.software2.awsblocks.routes;


import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

import javax.inject.Inject;
import java.util.List;

public class GetListOfStrings extends AbstractRouteHandler<List<String>> {

    @Inject
    public GetListOfStrings(ApiGatewayResponseProducer producer) {
        super(producer);
    }

    @Override
    public boolean supports(APIGatewayV2HTTPEvent request) {
        var http = request.getRequestContext().getHttp();
        return http.getMethod().equalsIgnoreCase("GET") &&
                http.getPath().startsWith("/test");
    }

    @Override
    protected List<String> handleRequest(APIGatewayV2HTTPEvent request) {
        return List.of("test", "test2", "test3");
    }
}
