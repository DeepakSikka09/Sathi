package in.ecomexpress.sathi.ui.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.material.snackbar.Snackbar;
import javax.inject.Inject;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.remote.model.DeviceDetails;
import in.ecomexpress.sathi.ui.dashboard.global_activity.GlobalDialogActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.NetworkUtils;

public abstract class BaseActivity<T extends ViewDataBinding, V extends BaseViewModel> extends AppCompatActivity implements BaseFragment.Callback {

    @Inject
    public DeviceDetails deviceDetails;
    private T mViewDataBinding;
    private ProgressDialog mProgressDialog;
    private V mViewModel;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            adjustFontScale(getResources().getConfiguration());
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            performDataBinding();
            setStatusBar();
            SathiApplication.EMP_CODE = getViewModel().getDataManager().getCode();
        } catch (Exception e) {
            Logger.e("BaseActivity-OnCreate", String.valueOf(e));
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMockLocationReceiver, new IntentFilter("LIVE_TRACKING_MOCK_LOCATION"));
    }

    public void adjustFontScale(Configuration configuration) {
        if (configuration.fontScale <= 0.9 || configuration.fontScale > 1.30) {
            configuration.fontScale = 1.15f;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.densityDpi = DisplayMetrics.DENSITY_DEVICE_STABLE;
            }
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            getBaseContext().getResources().updateConfiguration(configuration, metrics);
        }
    }

    private void performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        this.mViewModel = mViewModel == null ? getViewModel() : mViewModel;
        mViewDataBinding.setVariable(getBindingVariable(), mViewModel);
        mViewDataBinding.executePendingBindings();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    /**
     * Request permissions safely.
     *
     * @param permissions the permissions
     * @param requestCode the request code
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Has permission boolean.
     *
     * @param permission the permission
     * @return the boolean
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onFragmentAttached() {
    }

    @Override
    public void onFragmentDetached(String tag) {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Open activity on token expire.
     */
    public void openActivityOnTokenExpire() {
        finish();
    }

    /**
     * Is network connected boolean.
     *
     * @return the boolean
     */
    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }

    /**
     * Check endpoint const or not.
     */
    public boolean eventValidator(String endPointName) {
        return SathiApplication.hashMapAppUrl.get(endPointName) == null;
    }

    /**
     * Show loading.
     */
    public void showLoading() {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(this);
        mProgressDialog.setCancelable(false);
    }

    /**
     * Hide loading.
     */
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    /**
     * Gets view data binding.
     *
     * @return the view data binding
     */
    public T getViewDataBinding() {
        return mViewDataBinding;
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract V getViewModel();

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * Gets layout id.
     *
     * @return layout resource id
     */
    public abstract @LayoutRes int getLayoutId();

    /**
     * Show info.
     *
     * @param info the info
     */
    public void showInfo(String info) {
        Toast.makeText(this, info, Toast.LENGTH_LONG).show();
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void showSuccessInfo(String info) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setMinimumHeight(20);
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(13);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        snackbar.show();
    }

    public void showSnackbar(String info) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), info, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setMinimumHeight(20);
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_ecom));
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(13);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        snackbar.show();
    }

    /**
     * Perform dependency injection.
     */

    public void openSettingActivity() {
        showInfo(getResources().getString(R.string.permission_required));
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    public void showToast(String toast) {
        Toast toast1 = Toast.makeText(BaseActivity.this, toast, Toast.LENGTH_SHORT);
        toast1.setGravity(Gravity.CENTER, 0, 0);
        toast1.show();
    }

    public static InputFilter EMOJI_FILTER = (source, start, end, dest, dstart, dend) -> {
        for (int index = start; index < end; index++) {
            int type = Character.getType(source.charAt(index));
            if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                return "";
            }
        }
        return null;
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMockLocationReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver mMockLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String message = intent.getStringExtra("IS_MOCK");
                if (message == null) {
                    message = "";
                }
                if (message.equalsIgnoreCase("true")) {
                    Intent i = new Intent(BaseActivity.this, GlobalDialogActivity.class);
                    i.putExtra("FlagType", "FakeLocation");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    if (GlobalDialogActivity.finishActivityFlag != null) {
                        GlobalDialogActivity.finishActivityFlag.finish();
                    }
                }
            } catch (Exception e) {
                Logger.e("BaseActivity-BroadcastReceiver", String.valueOf(e));
            }
        }
    };

    private void setStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initiateStatusBar(this);
        } else{
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.sathi_background_color));
        }
    }

    // Set status bar of the activity:-
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void initiateStatusBar(AppCompatActivity activity) {
        Window window = activity.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.sathi_background_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}