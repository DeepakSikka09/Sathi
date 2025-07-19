package in.ecomexpress.sathi.ui.drs.rts.rts_signature;

import static com.paytmmoneyagent.core.utils.CoreUtility.getString;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.CHECKFIRSTUPLOAD;
import static in.ecomexpress.sathi.utils.Constants.Image_Array_Size;
import static in.ecomexpress.sathi.utils.Constants.Image_Click_Pos;
import static in.ecomexpress.sathi.utils.Constants.permissions;
import static in.ecomexpress.sathi.utils.Constants.rtsVWDetailID;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

public class MultiplePodAdapter extends RecyclerView.Adapter<MultiplePodAdapter.myViewHolder> {

    List<Integer> listItem;
    private final String TAG = MultiplePodAdapter.class.getSimpleName();
    Context mContext;
    OnPODListener monPODListener;

    public MultiplePodAdapter(Context context,List<Integer> passedListItem,OnPODListener onPODListener){
        this.mContext = context;
        this.listItem = passedListItem;
        this.monPODListener = onPODListener;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_image_pod, parent, false);
        return new myViewHolder(itemView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        int itemNumber = position + 1;
        CHECKFIRSTUPLOAD = 0;
        holder.imgItemPOD.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cam));
        Constants.Image_Check = 0;
        logScreenNameInGoogleAnalytics(TAG, mContext);
        holder.imgItemPOD.setOnClickListener(v -> {
            monPODListener.onPODClick(String.valueOf(itemNumber));
            try{
                if(!isAllPermissionAllow()){
                    openSettingActivity();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext,R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                new Thread(() -> {
                    try{
                        Thread.sleep(1000);
                    } catch(Exception e){
                        Logger.e(TAG, String.valueOf(e));
                    }
                }).start();
                String AlertText1 = "Attention : ";
                builder.setMessage(AlertText1 + getString(R.string.alert)).setCancelable(false).setPositiveButton("OK", (dialog, id) -> {
                    ((RTSSignatureActivity)mContext).imageHandler.captureImage(rtsVWDetailID + "_" + rtsVWDetailID + "_Image"+itemNumber+".png", holder.imgItemPOD, "Image"+itemNumber+".png");
                     Image_Click_Pos = itemNumber;
                     Image_Array_Size = listItem.size();
                     holder.idTVCourse.setText(itemNumber+"");
                });
                AlertDialog alert = builder.create();
                alert.show();
            } catch(Exception e){
                showInfo(e.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {

        ImageView imgItemPOD;
        TextView idTVCourse;

        public myViewHolder(View view){
            super(view);
            imgItemPOD = view.findViewById(R.id.img_item_pod);
            idTVCourse = view.findViewById(R.id.idTVCourse);
        }
    }

    public interface OnPODListener{
        void onPODClick(String id);
    }

    public boolean isAllPermissionAllow(){
        for(String permission : permissions){
            if(!hasPermission(permission)){
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission){
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || mContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void showInfo(String info){
        Toast.makeText(mContext, info, Toast.LENGTH_LONG).show();
    }

    public void openSettingActivity(){
        showInfo(mContext.getResources().getString(R.string.permission_required));
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        mContext.startActivity(intent);
    }
}