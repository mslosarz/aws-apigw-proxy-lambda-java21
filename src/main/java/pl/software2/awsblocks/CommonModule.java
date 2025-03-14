package pl.software2.awsblocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class CommonModule {
    @Provides
    @Singleton
    static ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }
}
