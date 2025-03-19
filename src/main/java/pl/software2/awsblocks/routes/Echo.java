package pl.software2.awsblocks.routes;


import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

import javax.inject.Inject;

public class Echo extends AbstractRouteHandler<APIGatewayV2HTTPEvent> {

    @Inject
    public Echo(ApiGatewayResponseProducer producer) {
        super(producer);
    }

    @Override
    public boolean supports(APIGatewayV2HTTPEvent request) {
        var http = request.getRequestContext().getHttp();
        return http.getPath().startsWith("/echo");
    }

    @Override
    protected APIGatewayV2HTTPEvent handleRequest(APIGatewayV2HTTPEvent request) {
        return request;
    }
}
