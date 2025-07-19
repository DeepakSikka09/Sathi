package in.ecomexpress.sathi.ui.drs.todolist;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;

public class ExpandListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = ExpandListAdapter.class.getSimpleName();
    public static final Integer SHIPMENT_TYPE = 0,SHIPMENT_STATUS = 1,SHIPMENT_REMARKS = 2;
    private final Context _context;
    private final List<String> _listDataHeader;
    ToDoListViewModel toDoListViewModel;
    List<CommonDRSListItem> commonDRSListItemList;
    private final LinkedHashMap<String, List<String>> _listDataChild;
    LinkedHashMap<String, Boolean> shipmentType;
    LinkedHashMap<String, Boolean> shipmentStatus;
    LinkedHashMap<String, Boolean> shipmentRemarksType;
    private boolean[] selected;

    public ExpandListAdapter(Context context, List<String> listDataHeader, LinkedHashMap<String, List<String>> listChildData, ToDoListViewModel toDoListViewModel, List<CommonDRSListItem> mcommonDRSListItemList) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.toDoListViewModel = toDoListViewModel;
        this.commonDRSListItemList = mcommonDRSListItemList;
    }

    public boolean[] shipmentType() {
        selected = new boolean[shipmentType.size()];
        selected[0] = Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD));
        selected[1] = Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RTS));
        selected[2] = Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RVP));
        selected[3] = Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_EDS));
        return selected;
    }

    public boolean[] shipmentStatus() {
        selected = new boolean[shipmentStatus.size()];
        selected[0] = Boolean.TRUE.equals(shipmentStatus.get(GlobalConstant.ShipmentStatusConstants.ASSIGNED));
        selected[1] = Boolean.TRUE.equals(shipmentStatus.get(GlobalConstant.ShipmentStatusConstants.DELIVERED));
        selected[2] = Boolean.TRUE.equals(shipmentStatus.get(GlobalConstant.ShipmentStatusConstants.UNDELIVERED));
        return selected;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(this._listDataChild.get(this._listDataHeader.get(groupPosition))).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_list_item, null);
        }
        TextView txtListChild = convertView.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        LinearLayout view = convertView.findViewById(R.id.childview);
        CheckBox cb = convertView.findViewById(R.id.ischeck);
        cb.setTag(childText);
        if (groupPosition == 0) {
            if (childText.equals(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)) {
                if (toDoListViewModel.forwardTotalCount.get() == 0) {
                    view.setBackgroundResource(R.color.gray_ecom);
                    shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD, false);
                    cb.setEnabled(Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)));
                    txtListChild.setEnabled(Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)));
                    view.setEnabled(Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)));
                    cb.setChecked(Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)));
                } else {
                    if (Constants.is_filter_applied.equals(Constants.with_filter)) {
                        if (Constants.is_fwd) {
                            view.setBackgroundResource(R.color.dashboardbg);
                            shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD, true);
                            cb.setEnabled(Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)));
                            txtListChild.setEnabled(Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)));
                            view.setEnabled(Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)));
                            cb.setChecked(Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)));
                        } else {
                            view.setBackgroundResource(R.color.dashboardbg);
                            shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD, false);
                            cb.setEnabled(Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)));
                            txtListChild.setEnabled(Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)));
                            view.setEnabled(Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)));
                            cb.setChecked(Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)));
                        }
                    } else {
                        shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD, false);
                        view.setBackgroundResource(R.color.dashboardbg);
                        cb.setEnabled(true);
                        txtListChild.setEnabled(true);
                        view.setEnabled(true);
                        cb.setChecked(Boolean.TRUE.equals(shipmentType.get(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_FWD)));
                    }
                }
            }
            if (childText.equals(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RTS)) {
                if (toDoListViewModel.rtsTotalCount.get() == 0) {
                    view.setBackgroundResource(R.color.gray_ecom);
                    cb.setEnabled(false);
                    txtListChild.setEnabled(false);
                    shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RTS, false);
                    view.setEnabled(false);
                } else {
                    if (Constants.is_filter_applied.equals(Constants.with_filter)) {
                        if (Constants.is_rts) {
                            view.setBackgroundResource(R.color.dashboardbg);
                            cb.setEnabled(true);
                            shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RTS, true);
                            txtListChild.setEnabled(true);
                            view.setEnabled(true);
                        } else {
                            view.setBackgroundResource(R.color.dashboardbg);
                            shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RTS, false);
                        }
                    } else {
                        view.setBackgroundResource(R.color.dashboardbg);
                        cb.setEnabled(true);
                        shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RTS, false);
                        txtListChild.setEnabled(true);
                        view.setEnabled(true);
                    }
                }
            }
            if (childText.equals(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RVP)) {
                if (toDoListViewModel.rvpTotalCount.get() == 0) {
                    view.setBackgroundResource(R.color.gray_ecom);
                    cb.setEnabled(false);
                    shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RVP, false);
                    txtListChild.setEnabled(false);
                    view.setEnabled(false);
                } else {
                    if (Constants.is_filter_applied.equals(Constants.with_filter)) {
                        if (Constants.is_rvp) {
                            view.setBackgroundResource(R.color.dashboardbg);
                            cb.setEnabled(true);
                            shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RVP, true);
                            txtListChild.setEnabled(true);
                            view.setEnabled(true);
                        } else {
                            view.setBackgroundResource(R.color.dashboardbg);
                            shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RVP, false);
                        }
                    } else {
                        view.setBackgroundResource(R.color.dashboardbg);
                        cb.setEnabled(true);
                        shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_RVP, false);
                        txtListChild.setEnabled(true);
                        view.setEnabled(true);
                    }
                }
            }
            if (childText.equals(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_EDS)) {
                Log.d(TAG, "getChildView: ");
                if (toDoListViewModel.edsTotalCount.get() == 0) {
                    view.setBackgroundResource(R.color.gray_ecom);
                    cb.setEnabled(false);
                    shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_EDS, false);
                    txtListChild.setEnabled(false);
                    view.setEnabled(false);
                } else {
                    if (Constants.is_filter_applied.equals(Constants.with_filter)) {
                        if (Constants.is_eds) {
                            view.setBackgroundResource(R.color.dashboardbg);
                            cb.setEnabled(true);
                            shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_EDS, true);
                            txtListChild.setEnabled(true);
                            view.setEnabled(true);
                        } else {
                            view.setBackgroundResource(R.color.dashboardbg);
                            shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_EDS, false);
                        }
                    } else {
                        view.setBackgroundResource(R.color.dashboardbg);
                        cb.setEnabled(true);
                        shipmentType.put(GlobalConstant.ShipmentTypeDetailConstants.SHIPMENT_EDS, false);
                        txtListChild.setEnabled(true);
                        view.setEnabled(true);
                    }
                }
            }


        } else if (groupPosition == 1) {
            if (childText.equals(GlobalConstant.ShipmentStatusConstants.ASSIGNED)) {
                if (Objects.requireNonNull(toDoListViewModel.totalAssignedCount.get()).equalsIgnoreCase("0")) {
                    view.setBackgroundResource(R.color.gray_ecom);
                    cb.setEnabled(false);
                    shipmentStatus.put(GlobalConstant.ShipmentStatusConstants.ASSIGNED, false);
                    txtListChild.setEnabled(false);
                    view.setEnabled(false);
                } else {
                    if (Constants.is_filter_applied.equals(Constants.with_filter)) {
                        if (Constants.is_pending) {
                            view.setBackgroundResource(R.color.dashboardbg);
                            cb.setEnabled(true);
                            shipmentStatus.put(GlobalConstant.ShipmentStatusConstants.ASSIGNED, true);
                            txtListChild.setEnabled(true);
                            view.setEnabled(true);
                        } else {
                            view.setBackgroundResource(R.color.dashboardbg);
                            shipmentStatus.put(GlobalConstant.ShipmentStatusConstants.ASSIGNED, false);
                        }
                    } else {
                        view.setBackgroundResource(R.color.dashboardbg);
                        cb.setEnabled(true);
                        shipmentStatus.put(GlobalConstant.ShipmentStatusConstants.ASSIGNED, false);
                        txtListChild.setEnabled(true);
                        view.setEnabled(true);
                    }

                }
            }
            if (childText.equals(GlobalConstant.ShipmentStatusConstants.DELIVERED)) {
                if (Objects.requireNonNull(toDoListViewModel.totalDeliveredCount.get()).equalsIgnoreCase("0")) {
                    view.setBackgroundResource(R.color.gray_ecom);
                    cb.setEnabled(false);
                    txtListChild.setEnabled(false);
                    shipmentStatus.put(GlobalConstant.ShipmentStatusConstants.DELIVERED, false);
                    view.setEnabled(false);
                } else {
                    if (Constants.is_filter_applied.equals(Constants.with_filter)) {
                        if (Constants.is_success) {
                            view.setBackgroundResource(R.color.dashboardbg);
                            cb.setEnabled(true);
                            shipmentStatus.put(GlobalConstant.ShipmentStatusConstants.DELIVERED, true);
                            txtListChild.setEnabled(true);
                            view.setEnabled(true);
                        } else {
                            view.setBackgroundResource(R.color.dashboardbg);
                            shipmentStatus.put(GlobalConstant.ShipmentStatusConstants.DELIVERED, false);
                        }
                    } else {
                        view.setBackgroundResource(R.color.dashboardbg);
                        cb.setEnabled(true);
                        shipmentStatus.put(GlobalConstant.ShipmentStatusConstants.DELIVERED, false);
                        txtListChild.setEnabled(true);
                        view.setEnabled(true);
                    }
                }
            }
            if (childText.equals(GlobalConstant.ShipmentStatusConstants.UNDELIVERED)) {
                if (Objects.requireNonNull(toDoListViewModel.totalUndeliveredCount.get()).equalsIgnoreCase("0")) {
                    view.setBackgroundResource(R.color.gray_ecom);
                    cb.setEnabled(false);
                    shipmentStatus.put(GlobalConstant.ShipmentStatusConstants.UNDELIVERED, false);
                    txtListChild.setEnabled(false);
                    view.setEnabled(false);
                } else {
                    if (Constants.is_filter_applied.equals(Constants.with_filter)) {
                        if (Constants.is_unsuccess) {
                            view.setBackgroundResource(R.color.dashboardbg);
                            cb.setEnabled(true);
                            shipmentStatus.put(GlobalConstant.ShipmentStatusConstants.UNDELIVERED, true);
                            txtListChild.setEnabled(true);
                            view.setEnabled(true);
                        } else {
                            view.setBackgroundResource(R.color.dashboardbg);
                            shipmentStatus.put(GlobalConstant.ShipmentStatusConstants.UNDELIVERED, false);
                        }
                    } else {
                        view.setBackgroundResource(R.color.dashboardbg);
                        cb.setEnabled(true);
                        shipmentStatus.put(GlobalConstant.ShipmentStatusConstants.UNDELIVERED, false);
                        txtListChild.setEnabled(true);
                        view.setEnabled(true);
                    }
                }
            }

        } else if (groupPosition == 2) {
            if (childText.equals(GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL)) {
                if (!toDoListViewModel.remark_one.get()) {
                    view.setBackgroundResource(R.color.gray_ecom);
                    cb.setEnabled(false);
                    shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL, false);
                    txtListChild.setEnabled(false);
                    view.setEnabled(false);
                } else {
                    if (Constants.is_filter_applied.equals(Constants.with_filter)) {
                        if (Constants.is_remark1) {
                            view.setBackgroundResource(R.color.dashboardbg);
                            cb.setEnabled(true);
                            shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL, true);
                            txtListChild.setEnabled(true);
                            view.setEnabled(true);
                        } else {
                            view.setBackgroundResource(R.color.dashboardbg);
                            shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL, true);
                        }
                    } else {
                        view.setBackgroundResource(R.color.dashboardbg);
                        cb.setEnabled(true);
                        shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL, false);
                        txtListChild.setEnabled(true);
                        view.setEnabled(true);
                    }
                }
            }
            if (childText.equals(GlobalConstant.RemarksTypeConstants.CONSIGNEE_REQUESTED_TO_CALL_LATER)) {
                if (!toDoListViewModel.remark_two.get()) {
                    view.setBackgroundResource(R.color.gray_ecom);
                    cb.setEnabled(false);
                    shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.CONSIGNEE_REQUESTED_TO_CALL_LATER, false);
                    txtListChild.setEnabled(false);
                    view.setEnabled(false);
                } else {
                    if (Constants.is_filter_applied.equals(Constants.with_filter)) {
                        if (Constants.is_remark2) {
                            view.setBackgroundResource(R.color.dashboardbg);
                            cb.setEnabled(true);
                            shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.CONSIGNEE_REQUESTED_TO_CALL_LATER, true);
                            txtListChild.setEnabled(true);
                            view.setEnabled(true);
                        } else {
                            view.setBackgroundResource(R.color.dashboardbg);
                            shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.CONSIGNEE_REQUESTED_TO_CALL_LATER, false);
                        }
                    } else {
                        view.setBackgroundResource(R.color.dashboardbg);
                        cb.setEnabled(true);
                        shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.CONSIGNEE_REQUESTED_TO_CALL_LATER, false);
                        txtListChild.setEnabled(true);
                        view.setEnabled(true);

                    }
                }
            }
            if (childText.equals(GlobalConstant.RemarksTypeConstants.SAME_DAY_RESCHEDULE)) {
                if (!toDoListViewModel.remark_three.get()) {
                    view.setBackgroundResource(R.color.gray_ecom);
                    cb.setEnabled(false);
                    shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.SAME_DAY_RESCHEDULE, false);
                    txtListChild.setEnabled(false);
                    view.setEnabled(false);
                } else {
                    if (Constants.is_filter_applied.equals(Constants.with_filter)) {
                        if (Constants.is_remark3) {
                            view.setBackgroundResource(R.color.dashboardbg);
                            cb.setEnabled(true);
                            shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.SAME_DAY_RESCHEDULE, true);
                            txtListChild.setEnabled(true);
                            view.setEnabled(true);
                        } else {
                            view.setBackgroundResource(R.color.dashboardbg);
                            shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.SAME_DAY_RESCHEDULE, false);
                        }
                    } else {
                        view.setBackgroundResource(R.color.dashboardbg);
                        cb.setEnabled(true);
                        shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.SAME_DAY_RESCHEDULE, false);
                        txtListChild.setEnabled(true);
                        view.setEnabled(true);
                    }
                }
            }
            if (childText.equals(GlobalConstant.RemarksTypeConstants.CONSIGNEE_CALLED)) {
                if (!toDoListViewModel.remark_four.get()) {
                    view.setBackgroundResource(R.color.gray_ecom);
                    cb.setEnabled(false);
                    shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.CONSIGNEE_CALLED, false);
                    txtListChild.setEnabled(false);
                    view.setEnabled(false);
                } else {
                    if (Constants.is_filter_applied.equals(Constants.with_filter)) {
                        if (Constants.is_remark4) {
                            view.setBackgroundResource(R.color.dashboardbg);
                            cb.setEnabled(true);
                            shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.CONSIGNEE_CALLED, true);
                            txtListChild.setEnabled(true);
                            view.setEnabled(true);
                        } else {
                            view.setBackgroundResource(R.color.dashboardbg);
                            shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.CONSIGNEE_CALLED, false);
                        }
                    } else {
                        view.setBackgroundResource(R.color.dashboardbg);
                        cb.setEnabled(true);
                        shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.CONSIGNEE_CALLED, false);
                        txtListChild.setEnabled(true);
                        view.setEnabled(true);
                    }
                }
            }
            if (childText.equals(GlobalConstant.RemarksTypeConstants.NO_REMARKS)) {
                if (!toDoListViewModel.remark_none.get()) {
                    view.setBackgroundResource(R.color.gray_ecom);
                    cb.setEnabled(false);
                    shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.NO_REMARKS, false);
                    txtListChild.setEnabled(false);
                    view.setEnabled(false);
                } else {
                    if (Constants.is_filter_applied.equals(Constants.with_filter)) {
                        if (Constants.is_remark5) {
                            view.setBackgroundResource(R.color.dashboardbg);
                            cb.setEnabled(true);
                            shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.NO_REMARKS, true);
                            txtListChild.setEnabled(true);
                            view.setEnabled(true);
                        } else {
                            view.setBackgroundResource(R.color.dashboardbg);
                            shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.NO_REMARKS, false);
                        }
                    } else {
                        view.setBackgroundResource(R.color.dashboardbg);
                        cb.setEnabled(true);
                        shipmentRemarksType.put(GlobalConstant.RemarksTypeConstants.NO_REMARKS, false);
                        txtListChild.setEnabled(true);
                        view.setEnabled(true);
                        Log.d(TAG, "getChildView: fwd present");
                    }
                }
            }
        }

        cb.setOnClickListener(v -> {
            String childTextTag = (String) v.getTag();
            if (groupPosition == 0) {
                shipmentType.put(childTextTag, Boolean.FALSE.equals(shipmentType.get(childTextTag)));
            }
            if (groupPosition == 1) {
                shipmentStatus.put(childTextTag, Boolean.FALSE.equals(shipmentStatus.get(childTextTag)));
            }
            if (groupPosition == 2) {
                shipmentRemarksType.put(childTextTag, Boolean.FALSE.equals(shipmentRemarksType.get(childTextTag)));
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(this._listDataChild.get(this._listDataHeader.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandableview, null);
        }
        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}