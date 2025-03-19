package pl.software2.awsblocks.routes;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import pl.software2.awsblocks.exceptions.BadRequestException;
import pl.software2.awsblocks.exceptions.NotFoundException;

public abstract class AbstractRouteHandler<T> implements RouteHandler {
    protected final ApiGatewayResponseProducer producer;

    public AbstractRouteHandler(ApiGatewayResponseProducer producer) {
        this.producer = producer;
    }

    @Override
    public abstract boolean supports(APIGatewayV2HTTPEvent request);

    protected abstract T handleRequest(APIGatewayV2HTTPEvent request);

    @Override
    public APIGatewayV2HTTPResponse handle(APIGatewayV2HTTPEvent request) {
        try {
            return producer.ok(handleRequest(request));
        } catch (BadRequestException e) {
            return producer.badRequest(e.getMessage());
        } catch (NotFoundException e) {
            return producer.notFound(request.getRawPath(), e.getMessage());
        } catch (Exception e) {
            return producer.internalServerError(e.getMessage());
        }
    }
}
