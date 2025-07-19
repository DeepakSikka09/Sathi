package in.ecomexpress.sathi.ui.dashboard.drs.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.ui.drs.todolist.DRSListAdapter;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListViewModel;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class DRSListFragment extends Fragment {

    private static int lastScrolledPosition = 0;
    private final String TAG = DRSListFragment.class.getSimpleName();
    private DRSListRemarkListener listRemarkListener;
    private ListListener listListener;
    public RecyclerView mRecyclerView;
    
    private final DRSListAdapter mDrsListAdapter = new DRSListAdapter(new DRSListAdapter.RowClickActionsListener() {
        @Override
        public void onRemarksClicked(int position){
            if (listRemarkListener != null) {
                listRemarkListener.addRemarks(position);
            }
        }
    });
    
    ItemTouchHelper.Callback itemTouchCallback= new RecyclerItemTouchCallback(mDrsListAdapter, mDrsListAdapter);
    List<CommonDRSListItem> mycommonDRSListItemList;
    private String filterString;
    private ToDoListActivity toDoListActivity;

    public static DRSListFragment newInstance() {
        lastScrolledPosition = 0;
        return new DRSListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drs_list, null);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mDrsListAdapter.setRecyclerView(mRecyclerView);
        setItemTouchListener();
        return view;
    }

    private void setItemTouchListener(){
        if(mRecyclerView != null){
            ItemTouchHelper itemTouchhelper = new ItemTouchHelper(itemTouchCallback);
            itemTouchhelper.attachToRecyclerView(mRecyclerView);
        }
    }

    public void setListRemarkListener(DRSListRemarkListener listener) {
        this.listRemarkListener = listener;
    }

    public void setListListener(ListListener listener) {
        this.listListener = listener;
    }

    @Override
    public void onResume() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mDrsListAdapter);
        notifyDataSetChanged();
        scrollRecyclerToLastScrolledPosition();
        mDrsListAdapter.resetClickVariables();
        super.onResume();
    }

    private void savedRecyclerLastScrolledPosition() {
        try {
            lastScrolledPosition = ((LinearLayoutManager) Objects.requireNonNull(mRecyclerView.getLayoutManager())).findFirstCompletelyVisibleItemPosition();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void scrollRecyclerToLastScrolledPosition() {
        try {
            Objects.requireNonNull(mRecyclerView.getLayoutManager()).scrollToPosition(lastScrolledPosition);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void onPause() {
        savedRecyclerLastScrolledPosition();
        super.onPause();
    }

    public void setData(List<CommonDRSListItem> commonDRSListItems, ToDoListViewModel toDoListViewModel) {
        try {
            if (!commonDRSListItems.isEmpty()) {
                this.mycommonDRSListItemList = commonDRSListItems;
                mDrsListAdapter.setData(this.mycommonDRSListItemList, toDoListViewModel, toDoListActivity);
                if (filterString != null && !filterString.isEmpty()) {
                    mDrsListAdapter.getFilter().filter(filterString);
                } else {
                    mDrsListAdapter.notifyDataSetChanged();
                }
            }
            if (listListener != null) {
                listListener.handledrsview(commonDRSListItems);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void setFilterString(String filterString) {
        this.filterString = filterString;
    }

    public void cancelCheckboxes() {
        mDrsListAdapter.cancelCheckboxes();
    }

    public void clearsmscheck() {
        mDrsListAdapter.clearSmsCheck();
    }

    public List<CommonDRSListItem> getFlterShipments() {
        return mDrsListAdapter.filterShipments;
    }

    public void filter(String scannedData) {
        mDrsListAdapter.getFilter().filter(scannedData);
    }

    public void getCallBridgeConfig(ToDoListActivity activity) {
        mDrsListAdapter.getCallBridgeConfig(activity);
    }

    public void refreshrecyclerview() {
        mDrsListAdapter.refreshRecyclerView();
    }

    public void handleCheckboxes() {
        mDrsListAdapter.handleCheckboxes();
    }

    public void handleCheckCheckboxes(boolean b){
        mDrsListAdapter.handleCheckCheckboxes(b);
    }

    public void notifyDataSetChanged() {
        mDrsListAdapter.notifyDataSetChanged();
    }

    public void setRemarks(String remarks) {
        mDrsListAdapter.setRemarks(remarks);
    }

    public void setActivityContext(ToDoListActivity toDoListActivity) {
        this.toDoListActivity = toDoListActivity;
        this.filterString = "";
    }

    public interface DRSListRemarkListener {
        void addRemarks(int position);
    }

    public interface ListListener {
        void handledrsview(List<CommonDRSListItem> commonDRSListItems);
    }
}