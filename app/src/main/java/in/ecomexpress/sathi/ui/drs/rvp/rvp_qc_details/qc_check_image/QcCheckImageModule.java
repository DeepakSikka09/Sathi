package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check_image;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
@InstallIn(SingletonComponent.class)
@Module
public class QcCheckImageModule {
    @Provides
    QcCheckImageViewModel provideQcCheckImageViewModel(IDataManager dataManager,
                                                       ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new QcCheckImageViewModel(dataManager, schedulerProvider ,sathiApplication);
    }

}
