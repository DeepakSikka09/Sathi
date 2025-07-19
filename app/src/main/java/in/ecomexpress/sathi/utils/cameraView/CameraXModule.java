package in.ecomexpress.sathi.utils.cameraView;


import android.app.Application;


import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
@InstallIn(SingletonComponent.class)
@Module
public class CameraXModule {
    @Provides
    CameraXViewModel providerDashBoardMapViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new CameraXViewModel(dataManager, schedulerProvider ,sathiApplication);
    }


}
