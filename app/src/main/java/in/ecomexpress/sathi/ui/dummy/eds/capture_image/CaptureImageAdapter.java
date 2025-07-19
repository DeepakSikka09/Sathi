package in.ecomexpress.sathi.ui.dummy.eds.capture_image;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.DocumentCollectionImageListBinding;
import in.ecomexpress.sathi.repo.remote.model.EdsImageStatus;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;
import in.ecomexpress.sathi.ui.dummy.eds.eds_document_collection.DocumentCollectionListImageViewModel;
import in.ecomexpress.sathi.ui.dummy.eds.eds_document_collection.DocumentCollectionListImageViewModel;
import in.ecomexpress.sathi.utils.Constants;

/**
 * Created by dhananjayk on 30-03-2019.
 */

public class CaptureImageAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    CaptureImageViewModel captureImageViewModel;
    MyViewHolder myViewHolder;
    private List<Boolean> min;
    private List<Boolean> max;
    public ArrayList<EdsImageStatus> edsImageStatuses = new ArrayList<>();

    // RecyclerView recyclerView;
    public CaptureImageAdapter(List<Boolean> min, List<Boolean> max) {
        this.min = min;
        this.max = max;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DocumentCollectionImageListBinding documentCollectionImageListBinding = DocumentCollectionImageListBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        myViewHolder = new CaptureImageAdapter.MyViewHolder(documentCollectionImageListBinding);
        return new CaptureImageAdapter.MyViewHolder(documentCollectionImageListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return max.size();
    }

    public void setData(List<Boolean> min, List<Boolean> max, CaptureImageViewModel captureImageViewModel) {
        this.min = min;
        this.max = max;
        this.captureImageViewModel = captureImageViewModel;
    }


    public class MyViewHolder extends BaseViewHolder {

        DocumentCollectionImageListBinding documentCollectionImageListBinding;
        DocumentCollectionListImageViewModel documentCollectionListImageViewModel;

        public MyViewHolder(DocumentCollectionImageListBinding documentCollectionImageListBinding) {
            super(documentCollectionImageListBinding.getRoot());
            this.documentCollectionImageListBinding = documentCollectionImageListBinding;

        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBind(int position) {
            int pos = position + 1;
            documentCollectionListImageViewModel = new DocumentCollectionListImageViewModel(max.get(position));
            documentCollectionImageListBinding.setViewModel(documentCollectionListImageViewModel);
            documentCollectionImageListBinding.executePendingBindings();
            documentCollectionImageListBinding.imgKycActivityCapture.setTag(position);
            documentCollectionImageListBinding.imgKycActivityCapture.setImageResource(R.drawable.cam);
            documentCollectionImageListBinding.txtImageHeader.setText(Constants.IMAGE + "" + pos);
            if (min.size() > position) {
                documentCollectionImageListBinding.txtImageOption.setText("M");
            } else {
                documentCollectionImageListBinding.txtImageOption.setText("O");
            }

            EdsImageStatus edsImageStatus = new EdsImageStatus();
            edsImageStatus.setImage_position(getAdapterPosition());
            edsImageStatus.setImage_status(documentCollectionImageListBinding.txtImageOption.getText().toString());
            edsImageStatuses.add(edsImageStatus);
            captureImageViewModel.setImageStatus(edsImageStatuses);


//            if (min.size() == 0) {
//                documentCollectionImageListBinding.txtImageOption.setText("O");
//            } else
//                documentCollectionImageListBinding.txtImageOption.setText("M");

            /*if (pos < min.size())
                documentCollectionImageListBinding.txtImageOption.setText("M");
            else
                documentCollectionImageListBinding.txtImageOption.setText("O");*/
            documentCollectionImageListBinding.imgKycActivityCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    captureImageViewModel.passImageView(documentCollectionImageListBinding.imgKycActivityCapture, getPosition(), captureImageViewModel.masterActivityData.get().getImageSettings().isVerify_image_on_server());

                }

            });

        }

        public void doBlankImageView() {
            notifyDataSetChanged();
        }

    }


}
