package pl.software2.awsblocks;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {CommonModule.class, RoutesModule.class})
public interface AppComponent {
    void inject(AppHandler appHandler);
}
