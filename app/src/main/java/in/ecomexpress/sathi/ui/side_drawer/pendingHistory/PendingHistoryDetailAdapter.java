package in.ecomexpress.sathi.ui.side_drawer.pendingHistory;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import in.ecomexpress.sathi.databinding.AdapterDetailPendingHistoryBinding;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;
import in.ecomexpress.sathi.utils.Logger;
import android.os.Handler;
import android.os.Looper;

public class PendingHistoryDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = PendingHistoryDetailAdapter.class.getSimpleName();
    private final List<ImageModel> imageModels;
    private PendingHistoryDetailViewModel pendingHistoryDetailViewModel;
    AdapterDetailPendingHistoryBinding mItemBinding;

    public PendingHistoryDetailAdapter(List<ImageModel> list){
        this.imageModels = list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(PendingHistoryDetailViewModel pendingHistoryDetailViewModel, List<ImageModel> list){
        this.pendingHistoryDetailViewModel = pendingHistoryDetailViewModel;
        this.imageModels.clear();
        addAllImageData(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        AdapterDetailPendingHistoryBinding mBinding = AdapterDetailPendingHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(mBinding);
    }

    class MyViewHolder extends BaseViewHolder {
        AdapterDetailPendingHistoryBinding itemBinding;

        MyViewHolder(AdapterDetailPendingHistoryBinding mBinding){
            super(mBinding.getRoot());
            itemBinding = mBinding;
            mItemBinding = mBinding;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBind(int position){
            try{
                mItemBinding.imageName.setText(imageModels.get(position).getImageName());
                mItemBinding.uploadImage.setText("Upload Image");
                itemBinding.uploadImage.setEnabled(true);
            } catch(Exception e){
                Logger.e(TAG, String.valueOf(e));
            }
            itemBinding.uploadImage.setOnClickListener(v ->  {
                try{
                    itemBinding.uploadImage.setEnabled(false);
                    pendingHistoryDetailViewModel.uploadDRSImageToServer(imageModels.get(position));
                    new Handler(Looper.getMainLooper()).postDelayed(() -> itemBinding.uploadImage.setEnabled(true), 2000);
                } catch(Exception e){
                    Logger.e(TAG, String.valueOf(e));
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position){
        holder.onBind(position);
    }

    @Override
    public int getItemCount(){
        return imageModels.size();
    }

    private void addAllImageData(List<ImageModel> imageModel){
        if (imageModel == null || imageModel.isEmpty()) {
            return;
        }

        for (ImageModel model : imageModel) {
            if (model.getStatus() != 2) {
                this.imageModels.add(model);
            }
        }
    }
}