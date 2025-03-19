package pl.software2.awsblocks;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import pl.software2.awsblocks.routes.Echo;
import pl.software2.awsblocks.routes.GetListOfStrings;
import pl.software2.awsblocks.routes.RouteHandler;

@Module
public class RoutesModule {
    @Provides
    @IntoSet
    static RouteHandler getListOfStrings(GetListOfStrings routeHandler) {
        return routeHandler;
    }

    @Provides
    @IntoSet
    static RouteHandler echo(Echo routeHandler) {
        return routeHandler;
    }
}
