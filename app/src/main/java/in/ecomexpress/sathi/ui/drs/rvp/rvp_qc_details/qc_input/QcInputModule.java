package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_input;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

/**
 * Created by Ashish Patel on 8/8/2018.
 */
@InstallIn(SingletonComponent.class)
@Module
public class QcInputModule {
    @Provides
    QcInputViewModel provideQcCheckViewModel(IDataManager dataManager,
                                             ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new QcInputViewModel(dataManager, schedulerProvider ,sathiApplication);
    }
    
}
