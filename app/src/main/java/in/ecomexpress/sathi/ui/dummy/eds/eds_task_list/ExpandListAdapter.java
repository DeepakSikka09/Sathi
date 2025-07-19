package in.ecomexpress.sathi.ui.dummy.eds.eds_task_list;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.List;

import in.ecomexpress.sathi.R;

/**
 * Created by shivangis on 10/9/2018.
 */

public class ExpandListAdapter extends BaseExpandableListAdapter {
    private final Context _context;

    private final LinkedHashMap<String, List<String>> _listDataChild;
    private final List<String> _header;
    private final List<Boolean> _option;
    LinkedHashMap<String, Boolean> childList_optional_flag;


    public ExpandListAdapter(Context context, List<String> _header,
                             LinkedHashMap<String, List<String>> listChildData, List<Boolean> _option,
                             LinkedHashMap<String, Boolean> childList_optional_flag) {
        this._context = context;
        this._listDataChild = listChildData;
        this._header = _header;
        this._option = _option;
        this.childList_optional_flag = childList_optional_flag;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._header.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_eds_list_item, null);
        }

        TextView txtListChild = convertView.findViewById(R.id.name_tv);
        TextView optional = convertView.findViewById(R.id.option_tv);
//        if (_option.get(childPosition)) {
//            optional.setText("M");
//        } else {
//            optional.setText("O");
//        }
        txtListChild.setText((childPosition + 1) + ") " + childText);
        if (childList_optional_flag.get(childText)) {
            optional.setText("M");
        } else {
            optional.setText("O");
        }
        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._header.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._header.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._header.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
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
