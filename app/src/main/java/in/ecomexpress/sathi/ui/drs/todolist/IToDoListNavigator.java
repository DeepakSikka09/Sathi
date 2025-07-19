package in.ecomexpress.sathi.ui.drs.todolist;

import android.app.Activity;
import android.content.Context;
import android.widget.ExpandableListView;
import java.util.LinkedHashMap;
import java.util.List;
import in.ecomexpress.sathi.databinding.ItemEdsListViewBinding;
import in.ecomexpress.sathi.databinding.ItemForwardListViewBinding;
import in.ecomexpress.sathi.databinding.ItemRvpListViewBinding;
import in.ecomexpress.sathi.databinding.ItemRvpMpsListViewBinding;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CallbridgeConfiguration;

public interface IToDoListNavigator {

    void onHandleError(ErrorResponse errorDetails);

    void onBackClick();

    void onApplyClick();

    void onFilterClick();

    void onClearFilterClick();

    void onScanClick();

    void hideLayout(String fwd);

    void showLayout(String fwd);

    void applyLocationBasedSort(List<CommonDRSListItem> listItems);

    void setDataToDRSFragment(List<CommonDRSListItem> listItems);

    void notifyAdapter();

    void showError(String error);

    void handleDRSCheckboxes();

    void callConfig(CallbridgeConfiguration mymasterDataReasonCodeResponse);

    void ExpandableList();

    void updateExpandableAdapter();

    void onSendClick();

    void onCancelClick();

    void toolbarVisibility(List<CommonDRSListItem> commonDRSListItems);

    void showErrorMessage(boolean status);

    void smsResponseThrowable(boolean status);

    void onErrorMsg(String message);

    void mResultReceiver1(String strBarcodeScan);

    void onSwitchCallBridgeClick();

    void setDrsData(List<CommonDRSListItem> commonDRSListItemList);

    void setCount();

    void OnCheckBoxClick();

    void mapListtoogle();

    void onExpandableData(ExpandableListView expListView ,boolean flag, List<String> header, LinkedHashMap<String, List<String>> childList, List<Boolean> option, LinkedHashMap<String, Boolean> childList_optional_flag);

    void setResetSequenceVisible(Boolean isVisible );

    void onResetSequenceClick();

    Context getActivityContext();

    void setForwardRemark(Remark remark, ItemForwardListViewBinding mBinding);

    void setEDSRemark(Remark remark, ItemEdsListViewBinding mBinding);

    void setRVPRemark(Remark remark, ItemRvpListViewBinding mBinding);

    void UpdateDRSAdapter();

    void setRVPMPSRemark(Remark remark, ItemRvpMpsListViewBinding mBinding);

    Activity getActivityReference();
}