package in.ecomexpress.sathi.di.component;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import in.ecomexpress.sathi.SathiApplication;

@Singleton
@Component
public interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(SathiApplication app);
}
