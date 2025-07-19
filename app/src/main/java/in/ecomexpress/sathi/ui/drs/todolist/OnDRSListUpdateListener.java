package in.ecomexpress.sathi.ui.drs.todolist;

import java.util.List;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;

public interface OnDRSListUpdateListener {

    void updateList(List<CommonDRSListItem> listItemList);

}