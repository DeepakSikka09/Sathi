package in.ecomexpress.sathi.ui.side_drawer.adm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.repo.remote.model.adm.ADMSpinnerData;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<ADMSpinnerData> availability_hour1;
    LayoutInflater inflater;

    public CustomAdapter(Context applicationContext, ArrayList<ADMSpinnerData> availability_hour1) {
        this.context = applicationContext;
        this.availability_hour1 = availability_hour1;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return availability_hour1.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.spinner_single_item_adm, null);
        TextView names = view.findViewById(R.id.spinner_text_view);
        if(availability_hour1.get(i).getSlot_hour().equalsIgnoreCase("Not Available")) {
            names.setTextColor(context.getResources().getColor(R.color.green));
        }
        if(availability_hour1.get(i).getSlot_hour().equalsIgnoreCase("Weekly Off")) {
            names.setTextColor(context.getResources().getColor(R.color.ecomRed));
        }
        names.setText(availability_hour1.get(i).getSlot_hour());
        return view;
    }
}