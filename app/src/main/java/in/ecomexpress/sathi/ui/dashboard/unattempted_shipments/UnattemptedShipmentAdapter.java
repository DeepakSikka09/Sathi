package in.ecomexpress.sathi.ui.dashboard.unattempted_shipments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import in.ecomexpress.sathi.R;

public class UnattemptedShipmentAdapter extends RecyclerView.Adapter<UnattemptedShipmentAdapter.ViewHolder> {

    private final List<UnattemptedShipments> unattemptedShipmentsList = new ArrayList<>();
    private Context mContext;

    public void setUnattemptedShipmentData(List<UnattemptedShipments> list, Context context) {
        this.unattemptedShipmentsList.clear();
        this.unattemptedShipmentsList.addAll(list);
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_unattempted_shipment, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return unattemptedShipmentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtAWBNo, txtShipmentType;

        public ViewHolder(View view) {
            super(view);
            txtAWBNo = view.findViewById(R.id.txtAWBNo);
            txtShipmentType = view.findViewById(R.id.txtShipmentType);
        }

        public void onBind(int position) {
            UnattemptedShipments unattemptedShipments = unattemptedShipmentsList.get(position);
            txtAWBNo.setText(unattemptedShipments.getAwbNo());
            txtShipmentType.setText(unattemptedShipments.getShipmentType());
        }
    }
}