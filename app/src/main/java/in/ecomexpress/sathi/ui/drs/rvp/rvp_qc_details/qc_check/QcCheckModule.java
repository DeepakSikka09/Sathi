package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check;

import android.app.Application;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

/**
 * Created by Ashish Patel on 8/6/2018.
 */
@InstallIn(SingletonComponent.class)
@Module
public class QcCheckModule {
    @Provides
    QcCheckViewModel provideQcCheckViewModel(IDataManager dataManager,
                                             ISchedulerProvider schedulerProvider, Application sathiApplication) {
        return new QcCheckViewModel(dataManager, schedulerProvider ,sathiApplication);
    }
}
