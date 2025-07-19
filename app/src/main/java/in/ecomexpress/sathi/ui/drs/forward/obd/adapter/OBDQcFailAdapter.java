package in.ecomexpress.sathi.ui.drs.forward.obd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import in.ecomexpress.sathi.R;

public class OBDQcFailAdapter extends RecyclerView.Adapter<OBDQcFailAdapter.MyViewHolder> {

    private final List<String> qcFailedData;

    public OBDQcFailAdapter(List<String> qcFailedData) {
        this.qcFailedData = qcFailedData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.non_qc_recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String question = qcFailedData.get(position);
        holder.tvQuestions.setText(question);
    }

    @Override
    public int getItemCount() {
        return qcFailedData.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestions;

        MyViewHolder(View itemView) {
            super(itemView);
            tvQuestions = itemView.findViewById(R.id.tvQuestions);
        }
    }
}