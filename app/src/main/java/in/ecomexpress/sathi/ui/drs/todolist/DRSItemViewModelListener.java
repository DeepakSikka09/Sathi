package in.ecomexpress.sathi.ui.drs.todolist;

import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;

public interface DRSItemViewModelListener {

    void onItemClick(CommonDRSListItem commonDRSListItem);

    void onMapClick(CommonDRSListItem commonDRSListItem);

    void onCallClick(CommonDRSListItem mCommonDRSListItem);

    void onIndicatorClick(CommonDRSListItem mCommonDRSListItem);

    void onRemarksAdded(CommonDRSListItem mCommonDRSListItem);

    void onTrayClick(CommonDRSListItem mCommonDRSListItem);
}