package in.ecomexpress.sathi.ui.drs.todolist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.utils.GlobalConstant;

@SuppressLint("UseSparseArrays")
public class ExpListViewAdapterWithCheckbox extends BaseExpandableListAdapter {

    private final Context _context;
    ToDoListViewModel toDoListViewModel;
    List<CommonDRSListItem> commonDRSListItemList;
    private final HashMap<String, List<String>> _listDataChild;
    private final ArrayList<String> _listDataHeader;
    private final HashMap<Integer, boolean[]> mChildCheckStates;

    public ExpListViewAdapterWithCheckbox(Context context, ArrayList<String> listDataGroup, HashMap<String, List<String>> listDataChild, ToDoListViewModel toDoListViewModel, List<CommonDRSListItem> mcommonDRSListItemList) {
        this._context = context;
        this._listDataHeader = listDataGroup;
        this._listDataChild = listDataChild;
        this.toDoListViewModel = toDoListViewModel;
        this.commonDRSListItemList = mcommonDRSListItemList;
        mChildCheckStates = new HashMap<>();
    }

    @Override
    public int getGroupCount() {
        return _listDataHeader.size();
    }

    /*
     * This defaults to "public object getGroup" if you auto import the methods
     * I've always make a point to change it from "object" to whatever item
     * I passed through the constructor
    */
    @Override
    public String getGroup(int groupPosition) {
        return _listDataHeader.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String groupText = getGroup(groupPosition);
        GroupViewHolder groupViewHolder;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandableview, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.mGroupText = convertView.findViewById(R.id.lblListHeader);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.mGroupText.setText(groupText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(this._listDataChild.get(this._listDataHeader.get(groupPosition))).size();
    }

    /*
     * This defaults to "public object getChild" if you auto import the methods
     * I've always make a point to change it from "object" to whatever item
     * I passed through the constructor
    */
    @Override
    public String getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(_listDataChild.get(_listDataHeader.get(groupPosition))).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;
        String childText = getChild(mGroupPosition, mChildPosition);
        ChildViewHolder childViewHolder;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_item, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.mChildText = convertView.findViewById(R.id.lblListItem);
            childViewHolder.mCheckBox = convertView.findViewById(R.id.ischeck);
            convertView.setTag(R.layout.expandable_list_item, childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag(R.layout.expandable_list_item);
        }
        childViewHolder.mChildText.setText(childText);
        if (groupPosition == 0) {
            switch (childText) {
                case GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD:
                    if (toDoListViewModel.forwardTotalCount.get() == 0) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.dashboardbg);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                    break;
                case GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RTS:
                    //Check if rts shipment is present
                    if (toDoListViewModel.rtsTotalCount.get() == 0) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.dashboardbg);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                    break;
                case GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RVP:
                    //Check if rvp shipment is present
                    if (toDoListViewModel.rvpTotalCount.get() == 0) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.dashboardbg);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                    break;
                case GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_EDS:
                    //Check if eds shipment is present
                    if (toDoListViewModel.edsTotalCount.get() == 0) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.dashboardbg);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                    break;
            }
        }

        //Check for Status
        else if (groupPosition == 1) {
            switch (childText) {
                case GlobalConstant.ShipmentStatusConstants.ASSIGNED:
                    //Check if pending shipment is present
                    if (!toDoListViewModel.is_pending.get()) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.dashboardbg);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                    break;
                case GlobalConstant.ShipmentStatusConstants.DELIVERED:
                    //Check if successful shipment is present
                    if (!toDoListViewModel.is_successful.get()) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.dashboardbg);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                    break;
                case GlobalConstant.ShipmentStatusConstants.UNDELIVERED:
                    //Check if unsuccessful shipment is present
                    if (!toDoListViewModel.is_unsuccessful.get()) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.dashboardbg);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                    break;
            }
        }

        //Check for REMARKS
        else if (groupPosition == 2) {
            switch (childText) {
                case GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL:
                    //Check if remark CONSIGNEE_NOT_PICKING_CALL is present
                    if (!toDoListViewModel.remark_one.get()) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.dashboardbg);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                    break;
                case GlobalConstant.RemarksTypeConstants.CONSIGNEE_REQUESTED_TO_CALL_LATER:
                    //Check if remark CONSIGNEE_REQUESTED_TO_CALL_LATER is present
                    if (!toDoListViewModel.remark_two.get()) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.dashboardbg);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                    break;
                case GlobalConstant.RemarksTypeConstants.SAME_DAY_RESCHEDULE:
                    //Check if remark SAME_DAY_RESCHEDULE is present
                    if (!toDoListViewModel.remark_three.get()) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.dashboardbg);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                    break;
                case GlobalConstant.RemarksTypeConstants.CONSIGNEE_CALLED:
                    //Check if remark CONSIGNEE_CALLED is present
                    if (!toDoListViewModel.remark_four.get()) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.dashboardbg);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                    break;
                case GlobalConstant.RemarksTypeConstants.NO_REMARKS:
                    //Check if remark NO_REMARKS is present
                    if (!toDoListViewModel.remark_none.get()) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.dashboardbg);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                    break;
            }
        }

        /*
         * You have to set the onCheckChangedListener to null
		 * before restoring check states because each call to
		 * "setChecked" is accompanied by a call to the
		 * onCheckChangedListener
		*/
        childViewHolder.mCheckBox.setOnCheckedChangeListener(null);

        if (mChildCheckStates.containsKey(mGroupPosition)) {
            /*
             * if the hashmap mChildCheckStates<Integer, Boolean[]> contains
			 * the value of the parent view (group) of this child (aka, the key),
			 * then retrive the boolean array getChecked[]
			*/
            boolean[] getChecked = mChildCheckStates.get(mGroupPosition);

            // set the check state of this position's checkbox based on the
            // boolean value of getChecked[position]
            assert getChecked != null;
            childViewHolder.mCheckBox.setChecked(getChecked[mChildPosition]);
        } else {

			/*
             * if the hashmap mChildCheckStates<Integer, Boolean[]> does not
			 * contain the value of the parent view (group) of this child (aka, the key),
			 * (aka, the key), then initialize getChecked[] as a new boolean array
			 *  and set it's size to the total number of children associated with
			 *  the parent group
			*/
            boolean[] getChecked = new boolean[getChildrenCount(mGroupPosition)];

            // add getChecked[] to the mChildCheckStates hashmap using mGroupPosition as the key
            mChildCheckStates.put(mGroupPosition, getChecked);

            // set the check state of this position's checkbox based on the
            // boolean value of getChecked[position]
            childViewHolder.mCheckBox.setChecked(false);
        }

        childViewHolder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean[] getChecked = mChildCheckStates.get(mGroupPosition);
            assert getChecked != null;
            getChecked[mChildPosition] = isChecked;
            mChildCheckStates.put(mGroupPosition, getChecked);
        });
        return convertView;
    }

    public HashMap<Integer, boolean[]> getmyChildCheckStates() {
        return mChildCheckStates;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public static final class GroupViewHolder {
        TextView mGroupText;
    }

    public static final class ChildViewHolder {
        TextView mChildText;
        CheckBox mCheckBox;
    }
}