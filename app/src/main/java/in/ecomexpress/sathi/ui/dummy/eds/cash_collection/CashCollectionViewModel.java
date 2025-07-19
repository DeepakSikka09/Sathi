package in.ecomexpress.sathi.ui.dummy.eds.cash_collection;

import android.app.Application;

import androidx.databinding.ObservableField;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

/**
 * Created by dhananjayk on 11-11-2018.
 */

@HiltViewModel
public class CashCollectionViewModel extends BaseViewModel<ICashCollectionNavigation> {
    public final ObservableField<String> inputData = new ObservableField<>("");
    public final ObservableField<Boolean> isVisible = new ObservableField<>(false);
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();
    public ObservableField<String> activityName = new ObservableField<>("ActivityName Not Defined");
    public ObservableField<String> activityQuestion = new ObservableField<>("Not Defined");
    public ObservableField<String> instructions = new ObservableField<>("No Instruction");
    //public ObservableField<String> roundoffAmount = new ObservableField<>("The amount that you need to return is:- \u20B9");
    public ObservableField<String> amount = new ObservableField<>("");
    int value;


    @Inject
    public CashCollectionViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider , sathiApplication);
    }

    public void setCheckStatus(String checkStatus) {
        inputData.set(checkStatus);
        getisVisible();
    }

    public ObservableField<Boolean> getisVisible() {
        if (inputData.get().equalsIgnoreCase("YES")) {
            isVisible.set(true);
        } else {
            amount.set("");
            isVisible.set(false);
        }
        return isVisible;

    }

    public ObservableField<String> getActivityName() {
        try {
            if (masterActivityData.get() != null && masterActivityData.get().getActivityName() != null) {
                activityName.set(masterActivityData.get().getActivityName() + " ( \u20B9 " + edsActivityWizard.get().getActualValue() + " )");
                // activityName.replace("a","<font color='#c5c5c5'>a</font>");
            }
        } catch (Exception e) {
            getNavigator().errorMsg(e.getMessage());
        }
        return activityName;
    }

    public ObservableField<String> getActivityQuestion() {
        try {
            if (masterActivityData.get() != null && masterActivityData.get().getActivityName() != null)
                activityQuestion.set(masterActivityData.get().getActivityQuestion());
        } catch (Exception e) {
            getNavigator().errorMsg(e.getMessage());
        }

        return activityQuestion;
    }

    /*  public ObservableField<String> getRoundoffAmount() {

          return roundoffAmount;
      }

      public ObservableField<String> getAmount() {

          int value;
          Log.d("amount",amount.get());
          if (edsActivityWizard.get() != null && !amount.get().isEmpty()) {
              if (Integer.parseInt(edsActivityWizard.get().getActualValue()) > Integer.parseInt(amount.get())) {
                  if (!amount.get().equals("")) {
                      value = Integer.parseInt(edsActivityWizard.get().getActualValue()) - Integer.parseInt(amount.get());
                      roundoffAmount.set("Amount collected is :- \u20B9 " + value + "less than total amount\"");
                  }
              } else if (Integer.parseInt(edsActivityWizard.get().getActualValue()) < Integer.parseInt(amount.get()) || Integer.parseInt(edsActivityWizard.get().getActualValue()) == Integer.parseInt(amount.get())) {
                  value = Integer.parseInt(amount.get()) - Integer.parseInt(edsActivityWizard.get().getActualValue());
                  roundoffAmount.set("The amount that you need to return is: \u20B9 " + value);
              } else if (amount.get().equals("")) {
                  roundoffAmount.set("The amount that you need to return is: \u20B9 0");
              }
          }


          return amount;
      }
  */
  /*public ObservableField<String> getInstruction() {
          if (masterActivityData.get().getInstructions() != null) {
              instructions.set(masterActivityData.get().getInstructions());
          }


      return instructions;
  }*/
    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData) {
        try {
            this.edsActivityWizard.set(edsActivityWizard);
            this.masterActivityData.set(masterActivityData);
            getActivityName();
            getActivityQuestion();
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().errorMsg(e.getMessage());
        }

    }
}
