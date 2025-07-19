package in.ecomexpress.sathi.ui.side_drawer.adm;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityAdmBinding;
import in.ecomexpress.sathi.repo.remote.model.adm.ADMDATA;
import in.ecomexpress.sathi.repo.remote.model.adm.ADMSpinnerData;
import in.ecomexpress.sathi.repo.remote.model.adm.ADMUpdateRequest;
import in.ecomexpress.sathi.repo.remote.model.adm.ADMUpdateResponse;
import in.ecomexpress.sathi.ui.base.BaseActivity;

@AndroidEntryPoint
public class ADMActivity extends BaseActivity<ActivityAdmBinding, ADMViewModel> implements IADMNavigator {

    @Inject
    ADMViewModel admViewModel;
    private final String TAG = ADMActivity.class.getSimpleName();
    ActivityAdmBinding activityAdmBinding;
    List<ADMDATA> admResponse = new ArrayList<>();
    List<ADMUpdateRequest> admUpdateRequests = new ArrayList<>();
    ArrayList<ADMSpinnerData> availability_hour1, availability_hour2, availability_hour3, availability_hour4, availability_hour5, availability_hour6, availability_hour7;
    String planned_status_code1, planned_status_code2, planned_status_code3, planned_status_code4, planned_status_code5, planned_status_code6, planned_status_code7;
    boolean isWeeklyOff1, isWeeklyOff2, isWeeklyOff3, isWeeklyOff4, isWeeklyOff5, isWeeklyOff6, isWeeklyOff7;
    int spinnerStatusTruePosition ,spinnerStatusTruePosition2 ,spinnerStatusTruePosition3 ,spinnerStatusTruePosition4, spinnerStatusTruePosition5, spinnerStatusTruePosition6 ,spinnerStatusTruePosition7;
    String spinner_selection1 ,  spinner_selection2 , spinner_selection3 ,spinner_selection4 ,spinner_selection5 ,spinner_selection6 ,spinner_selection7;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        admViewModel.setNavigator(this);
        activityAdmBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        if(getIntent().getExtras() != null) {
            from = getIntent().getStringExtra("from");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        admViewModel.getADMData();
    }

    @Override
    public ADMViewModel getViewModel(){
        return admViewModel;
    }

    @Override
    public int getBindingVariable(){
        return BR.viewModel;
    }

    @Override
    public int getLayoutId(){
        return R.layout.activity_adm;
    }

    @Override
    public void showToast(String message){
        Toast.makeText(ADMActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void sendADMResponse(List<ADMDATA> response){
        admResponse = response;
        activityAdmBinding.dayOfWeek1.setText(response.get(0).getDay_of_week());
        activityAdmBinding.dayOfWeek2.setText(response.get(1).getDay_of_week());
        activityAdmBinding.dayOfWeek3.setText(response.get(2).getDay_of_week());
        activityAdmBinding.dayOfWeek4.setText(response.get(3).getDay_of_week());
        activityAdmBinding.dayOfWeek5.setText(response.get(4).getDay_of_week());
        activityAdmBinding.dayOfWeek6.setText(response.get(5).getDay_of_week());
        activityAdmBinding.dayOfWeek7.setText(response.get(6).getDay_of_week());
        // for date
        activityAdmBinding.date1.setText(response.get(0).getDate());
        activityAdmBinding.date2.setText(response.get(1).getDate());
        activityAdmBinding.date3.setText(response.get(2).getDate());
        activityAdmBinding.date4.setText(response.get(3).getDate());
        activityAdmBinding.date5.setText(response.get(4).getDate());
        activityAdmBinding.date6.setText(response.get(5).getDate());
        activityAdmBinding.date7.setText(response.get(6).getDate());
        // for spinner hour availability
        availability_hour1 = new ArrayList<>();
        for(int i = 0; i < response.get(0).getAvailable_slots().size(); i++){
            ADMSpinnerData admSpinnerData = new ADMSpinnerData();
            admSpinnerData.setPlanned_status_code(response.get(0).getAvailable_slots().get(i).getPlanned_status_code());
            admSpinnerData.setSlot_hour(response.get(0).getAvailable_slots().get(i).getSlot_hour());
            admSpinnerData.setStatus(response.get(0).getAvailable_slots().get(i).getStatus());
            availability_hour1.add(admSpinnerData);
            if(response.get(0).getAvailable_slots().get(i).getStatus().equalsIgnoreCase("true")) {
                spinnerStatusTruePosition = i;
            }
        }

        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), availability_hour1);
        activityAdmBinding.spinnerAvailability1.setAdapter(customAdapter);
        activityAdmBinding.spinnerAvailability1.setSelection(spinnerStatusTruePosition);
        availability_hour2 = new ArrayList<>();
        for(int i = 0; i < response.get(1).getAvailable_slots().size(); i++){
            ADMSpinnerData admSpinnerData = new ADMSpinnerData();
            admSpinnerData.setPlanned_status_code(response.get(1).getAvailable_slots().get(i).getPlanned_status_code());
            admSpinnerData.setSlot_hour(response.get(1).getAvailable_slots().get(i).getSlot_hour());
            admSpinnerData.setStatus(response.get(1).getAvailable_slots().get(i).getStatus());
            availability_hour2.add(admSpinnerData);
            if(response.get(1).getAvailable_slots().get(i).getStatus().equalsIgnoreCase("true")) {
                spinnerStatusTruePosition2 = i;
            }
        }

        CustomAdapter customAdapter2 = new CustomAdapter(getApplicationContext(), availability_hour2);
        activityAdmBinding.spinnerAvailability2.setAdapter(customAdapter2);
        activityAdmBinding.spinnerAvailability2.setSelection(spinnerStatusTruePosition2);
        availability_hour3 = new ArrayList<>();
        for(int i = 0; i < response.get(2).getAvailable_slots().size(); i++){
            ADMSpinnerData admSpinnerData = new ADMSpinnerData();
            admSpinnerData.setPlanned_status_code(response.get(2).getAvailable_slots().get(i).getPlanned_status_code());
            admSpinnerData.setSlot_hour(response.get(2).getAvailable_slots().get(i).getSlot_hour());
            admSpinnerData.setStatus(response.get(2).getAvailable_slots().get(i).getStatus());
            availability_hour3.add(admSpinnerData);
            if(response.get(2).getAvailable_slots().get(i).getStatus().equalsIgnoreCase("true")) {
                spinnerStatusTruePosition3 = i;
            }
        }

        CustomAdapter customAdapter3 = new CustomAdapter(getApplicationContext(), availability_hour3);
        activityAdmBinding.spinnerAvailability3.setAdapter(customAdapter3);
        activityAdmBinding.spinnerAvailability3.setSelection(spinnerStatusTruePosition3);
        availability_hour4 = new ArrayList<>();
        for(int i = 0; i < response.get(3).getAvailable_slots().size(); i++){
            ADMSpinnerData admSpinnerData = new ADMSpinnerData();
            admSpinnerData.setPlanned_status_code(response.get(3).getAvailable_slots().get(i).getPlanned_status_code());
            admSpinnerData.setSlot_hour(response.get(3).getAvailable_slots().get(i).getSlot_hour());
            admSpinnerData.setStatus(response.get(3).getAvailable_slots().get(i).getStatus());
            availability_hour4.add(admSpinnerData);
            if(response.get(3).getAvailable_slots().get(i).getStatus().equalsIgnoreCase("true")) {
                spinnerStatusTruePosition4 = i;
            }
        }

        CustomAdapter customAdapter4 = new CustomAdapter(getApplicationContext(), availability_hour4);
        activityAdmBinding.spinnerAvailability4.setAdapter(customAdapter4);
        activityAdmBinding.spinnerAvailability4.setSelection(spinnerStatusTruePosition4);
        availability_hour5 = new ArrayList<>();
        for(int i = 0; i < response.get(4).getAvailable_slots().size(); i++){
            ADMSpinnerData admSpinnerData = new ADMSpinnerData();
            admSpinnerData.setPlanned_status_code(response.get(4).getAvailable_slots().get(i).getPlanned_status_code());
            admSpinnerData.setSlot_hour(response.get(4).getAvailable_slots().get(i).getSlot_hour());
            admSpinnerData.setStatus(response.get(4).getAvailable_slots().get(i).getStatus());
            availability_hour5.add(admSpinnerData);
            if(response.get(4).getAvailable_slots().get(i).getStatus().equalsIgnoreCase("true")) {
                spinnerStatusTruePosition5 = i;
            }
        }

        CustomAdapter customAdapter5 = new CustomAdapter(getApplicationContext(), availability_hour5);
        activityAdmBinding.spinnerAvailability5.setAdapter(customAdapter5);
        activityAdmBinding.spinnerAvailability5.setSelection(spinnerStatusTruePosition5);
        availability_hour6 = new ArrayList<>();
        for(int i = 0; i < response.get(5).getAvailable_slots().size(); i++){
            ADMSpinnerData admSpinnerData = new ADMSpinnerData();
            admSpinnerData.setPlanned_status_code(response.get(5).getAvailable_slots().get(i).getPlanned_status_code());
            admSpinnerData.setSlot_hour(response.get(5).getAvailable_slots().get(i).getSlot_hour());
            admSpinnerData.setStatus(response.get(5).getAvailable_slots().get(i).getStatus());
            availability_hour6.add(admSpinnerData);
            if(response.get(5).getAvailable_slots().get(i).getStatus().equalsIgnoreCase("true")) {
                spinnerStatusTruePosition6 = i;
            }
        }

        CustomAdapter customAdapter6 = new CustomAdapter(getApplicationContext(), availability_hour6);
        activityAdmBinding.spinnerAvailability6.setAdapter(customAdapter6);
        activityAdmBinding.spinnerAvailability6.setSelection(spinnerStatusTruePosition6);
        availability_hour7 = new ArrayList<>();
        for(int i = 0; i < response.get(6).getAvailable_slots().size(); i++){
            ADMSpinnerData admSpinnerData = new ADMSpinnerData();
            admSpinnerData.setPlanned_status_code(response.get(6).getAvailable_slots().get(i).getPlanned_status_code());
            admSpinnerData.setSlot_hour(response.get(6).getAvailable_slots().get(i).getSlot_hour());
            admSpinnerData.setStatus(response.get(6).getAvailable_slots().get(i).getStatus());
            availability_hour7.add(admSpinnerData);
            if(response.get(6).getAvailable_slots().get(i).getStatus().equalsIgnoreCase("true")) {
                spinnerStatusTruePosition7 = i;
            }
        }

        CustomAdapter customAdapter7 = new CustomAdapter(getApplicationContext(), availability_hour7);
        activityAdmBinding.spinnerAvailability7.setAdapter(customAdapter7);
        activityAdmBinding.spinnerAvailability7.setSelection(spinnerStatusTruePosition7);
        activityAdmBinding.etChooseTime1.setText( response.get(0).getPlanned_intime());
        activityAdmBinding.etChooseTime2.setText(response.get(1).getPlanned_intime());
        activityAdmBinding.etChooseTime3.setText(response.get(2).getPlanned_intime());
        activityAdmBinding.etChooseTime4.setText(response.get(3).getPlanned_intime());
        activityAdmBinding.etChooseTime5.setText(response.get(4).getPlanned_intime());
        activityAdmBinding.etChooseTime6.setText(response.get(5).getPlanned_intime());
        activityAdmBinding.etChooseTime7.setText(response.get(6).getPlanned_intime());
    }

    @Override
    public Context getActivityContext(){
        return this;
    }

    @Override
    public void setTimer1(){
        if(spinner_selection1.equalsIgnoreCase("Weekly off") || spinner_selection1.equalsIgnoreCase("Not Available")) {
            showInfo("You cannot change time.");
        }
        else {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,R.style.Theme_AppCompat_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                    activityAdmBinding.etChooseTime1.setText(String.format("%02d:%02d", hourOfDay, minutes));
                }
            }, 0, 0, false);
            timePickerDialog.show();
        }
    }

    @Override
    public void setTimer2(){
        if(spinner_selection2.equalsIgnoreCase("Weekly off") || spinner_selection2.equalsIgnoreCase("Not Available")) {
            showInfo("You cannot change time.");
        }
        else{
            @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog(this,R.style.Theme_AppCompat_Light_Dialog, (timePicker, hourOfDay, minutes) -> activityAdmBinding.etChooseTime2.setText(String.format("%02d:%02d", hourOfDay, minutes)), 0, 0, false);
            timePickerDialog.show();
        }
    }

    @Override
    public void setTimer3(){
        if(spinner_selection3.equalsIgnoreCase("Weekly off") || spinner_selection3.equalsIgnoreCase("Not Available")) {
            showInfo("You cannot change time.");
        }
        else{
            @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.Theme_AppCompat_Light_Dialog,(timePicker, hourOfDay, minutes) -> activityAdmBinding.etChooseTime3.setText(String.format("%02d:%02d", hourOfDay, minutes)), 0, 0, false);
            timePickerDialog.show();
        }
    }

    @Override
    public void setTimer4(){
        if(spinner_selection4.equalsIgnoreCase("Weekly off") || spinner_selection4.equalsIgnoreCase("Not Available")) {
            showInfo("You cannot change time.");
        }
        else{
            @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog(this,R.style.Theme_AppCompat_Light_Dialog, (timePicker, hourOfDay, minutes) -> activityAdmBinding.etChooseTime4.setText(String.format("%02d:%02d", hourOfDay, minutes)), 0, 0, false);
            timePickerDialog.show();
        }
    }

    @Override
    public void setTimer5(){
        if(spinner_selection5.equalsIgnoreCase("Weekly off") || spinner_selection5.equalsIgnoreCase("Not Available")) {
            showInfo("You cannot change time.");
        }
        else{
            @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog(this,R.style.Theme_AppCompat_Light_Dialog, (timePicker, hourOfDay, minutes) -> activityAdmBinding.etChooseTime5.setText(String.format("%02d:%02d", hourOfDay, minutes)), 0, 0, false);
            timePickerDialog.show();
        }
    }

    @Override
    public void setTimer6(){
        if(spinner_selection6.equalsIgnoreCase("Weekly off") || spinner_selection6.equalsIgnoreCase("Not Available")) {
            showInfo("You cannot change time.");
        }
        else{
            @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog(this,R.style.Theme_AppCompat_Light_Dialog, (timePicker, hourOfDay, minutes) -> activityAdmBinding.etChooseTime6.setText(String.format("%02d:%02d", hourOfDay, minutes)), 0, 0, false);
            timePickerDialog.show();
        }
    }

    @Override
    public void setTimer7(){
        if(spinner_selection7.equalsIgnoreCase("Weekly off") || spinner_selection7.equalsIgnoreCase("Not Available")) {
            showInfo("You cannot change time.");
        }
        else{
            @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.Theme_AppCompat_Light_Dialog,(timePicker, hourOfDay, minutes) -> activityAdmBinding.etChooseTime7.setText(String.format("%02d:%02d", hourOfDay, minutes)), 0, 0, false);
            timePickerDialog.show();
        }
    }

    @Override
    public void setUpdatedData(){
        ADMUpdateRequest admUpdateRequest = new ADMUpdateRequest();
        admUpdateRequest.setDay_of_week(activityAdmBinding.dayOfWeek1.getText().toString());
        admUpdateRequest.setDate(activityAdmBinding.date1.getText().toString());
        admUpdateRequest.setEmployee_code(admViewModel.getDataManager().getEmp_code());
        admUpdateRequest.setPlanned_intime(activityAdmBinding.etChooseTime1.getText().toString());
        admUpdateRequest.setPlanned_slot_code(planned_status_code1);
        admUpdateRequest.setWeekly_off(isWeeklyOff1);
        admUpdateRequests.add(admUpdateRequest);

        // Second
        ADMUpdateRequest admUpdateRequest2 = new ADMUpdateRequest();
        admUpdateRequest2.setDay_of_week(activityAdmBinding.dayOfWeek2.getText().toString());
        admUpdateRequest2.setDate(activityAdmBinding.date2.getText().toString());
        admUpdateRequest2.setEmployee_code(admViewModel.getDataManager().getEmp_code());
        admUpdateRequest2.setPlanned_intime(activityAdmBinding.etChooseTime2.getText().toString());
        admUpdateRequest2.setPlanned_slot_code(planned_status_code2);
        admUpdateRequest2.setWeekly_off(isWeeklyOff2);
        admUpdateRequests.add(admUpdateRequest2);

        // Third
        ADMUpdateRequest admUpdateRequest3 = new ADMUpdateRequest();
        admUpdateRequest3.setDay_of_week(activityAdmBinding.dayOfWeek3.getText().toString());
        admUpdateRequest3.setDate(activityAdmBinding.date3.getText().toString());
        admUpdateRequest3.setEmployee_code(admViewModel.getDataManager().getEmp_code());
        admUpdateRequest3.setPlanned_intime(activityAdmBinding.etChooseTime3.getText().toString());
        admUpdateRequest3.setPlanned_slot_code(planned_status_code3);
        admUpdateRequest3.setWeekly_off(isWeeklyOff3);
        admUpdateRequests.add(admUpdateRequest3);

        // Fourth
        ADMUpdateRequest admUpdateRequest4 = new ADMUpdateRequest();
        admUpdateRequest4.setDay_of_week(activityAdmBinding.dayOfWeek4.getText().toString());
        admUpdateRequest4.setDate(activityAdmBinding.date4.getText().toString());
        admUpdateRequest4.setEmployee_code(admViewModel.getDataManager().getEmp_code());
        admUpdateRequest4.setPlanned_intime(activityAdmBinding.etChooseTime4.getText().toString());
        admUpdateRequest4.setPlanned_slot_code(planned_status_code4);
        admUpdateRequest4.setWeekly_off(isWeeklyOff4);
        admUpdateRequests.add(admUpdateRequest4);

        // Fifth
        ADMUpdateRequest admUpdateRequest5 = new ADMUpdateRequest();
        admUpdateRequest5.setDay_of_week(activityAdmBinding.dayOfWeek5.getText().toString());
        admUpdateRequest5.setDate(activityAdmBinding.date5.getText().toString());
        admUpdateRequest5.setEmployee_code(admViewModel.getDataManager().getEmp_code());
        admUpdateRequest5.setPlanned_intime(activityAdmBinding.etChooseTime5.getText().toString());
        admUpdateRequest5.setPlanned_slot_code(planned_status_code5);
        admUpdateRequest5.setWeekly_off(isWeeklyOff5);
        admUpdateRequests.add(admUpdateRequest5);

        // Sixth
        ADMUpdateRequest admUpdateRequest6 = new ADMUpdateRequest();
        admUpdateRequest6.setDay_of_week(activityAdmBinding.dayOfWeek6.getText().toString());
        admUpdateRequest6.setDate(activityAdmBinding.date6.getText().toString());
        admUpdateRequest6.setEmployee_code(admViewModel.getDataManager().getEmp_code());
        admUpdateRequest6.setPlanned_intime(activityAdmBinding.etChooseTime6.getText().toString());
        admUpdateRequest6.setPlanned_slot_code(planned_status_code6);
        admUpdateRequest6.setWeekly_off(isWeeklyOff6);
        admUpdateRequests.add(admUpdateRequest6);

        // Seventh
        ADMUpdateRequest admUpdateRequest7 = new ADMUpdateRequest();
        admUpdateRequest7.setDay_of_week(activityAdmBinding.dayOfWeek7.getText().toString());
        admUpdateRequest7.setDate(activityAdmBinding.date7.getText().toString());
        admUpdateRequest7.setEmployee_code(admViewModel.getDataManager().getEmp_code());
        admUpdateRequest7.setPlanned_intime(activityAdmBinding.etChooseTime7.getText().toString());
        admUpdateRequest7.setPlanned_slot_code(planned_status_code7);
        admUpdateRequest7.setWeekly_off(isWeeklyOff7);
        admUpdateRequests.add(admUpdateRequest7);
        admViewModel.doUpdateADMData(admUpdateRequests ,from);
    }

    @Override
    public void chooseSpinner1(AdapterView<?> parent, View view, int pos, long id){
        if(isWeeklyOff2 || isWeeklyOff3 || isWeeklyOff4 || isWeeklyOff5 || isWeeklyOff6 || isWeeklyOff7) {
            if(availability_hour1.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off")){
                showInfo("You cannot choose weekly off");
                activityAdmBinding.spinnerAvailability1.setSelection(spinnerStatusTruePosition);
                return;
            }
        }
        planned_status_code1 = availability_hour1.get(pos).getPlanned_status_code();
        isWeeklyOff1 = availability_hour1.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off");
        if(availability_hour1.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off") || availability_hour1.get(pos).getSlot_hour().equalsIgnoreCase("Not Available")) {
            activityAdmBinding.Time1.setTextColor(getResources().getColor(R.color.drsiconbg));
            activityAdmBinding.etChooseTime1.setTextColor(getResources().getColor(R.color.drsiconbg));
        } else {
            activityAdmBinding.Time1.setTextColor(getResources().getColor(R.color.black));
            activityAdmBinding.etChooseTime1.setTextColor(getResources().getColor(R.color.black));
        }
        spinner_selection1 = availability_hour1.get(pos).getSlot_hour();
    }

    @Override
    public void chooseSpinner2(AdapterView<?> parent, View view, int pos, long id){
        if(isWeeklyOff1 || isWeeklyOff3 || isWeeklyOff4 || isWeeklyOff5 || isWeeklyOff6 || isWeeklyOff7) {
            if(availability_hour2.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off")){
                showInfo("You cannot choose weekly off");
                activityAdmBinding.spinnerAvailability2.setSelection(spinnerStatusTruePosition2);
                return;
            }
        }
        planned_status_code2 = availability_hour2.get(pos).getPlanned_status_code();
        isWeeklyOff2 = availability_hour2.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off");
        if(availability_hour2.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off") || availability_hour2.get(pos).getSlot_hour().equalsIgnoreCase("Not Available")) {
            activityAdmBinding.Time2.setTextColor(getResources().getColor(R.color.drsiconbg));
            activityAdmBinding.etChooseTime2.setTextColor(getResources().getColor(R.color.drsiconbg));
        } else {
            activityAdmBinding.Time2.setTextColor(getResources().getColor(R.color.black));
            activityAdmBinding.etChooseTime2.setTextColor(getResources().getColor(R.color.black));
        }
        spinner_selection2 = availability_hour2.get(pos).getSlot_hour();
    }

    @Override
    public void chooseSpinner3(AdapterView<?> parent, View view, int pos, long id){
        if(isWeeklyOff1 || isWeeklyOff2 || isWeeklyOff4 || isWeeklyOff5 || isWeeklyOff6 || isWeeklyOff7) {
            if(availability_hour3.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off")){
                showInfo("You cannot choose weekly off");
                activityAdmBinding.spinnerAvailability3.setSelection(spinnerStatusTruePosition3);
                return;
            }
        }
        planned_status_code3 = availability_hour3.get(pos).getPlanned_status_code();
        isWeeklyOff3 = availability_hour3.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off");
        if(availability_hour3.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off") || availability_hour3.get(pos).getSlot_hour().equalsIgnoreCase("Not Available")) {
            activityAdmBinding.Time3.setTextColor(getResources().getColor(R.color.drsiconbg));
            activityAdmBinding.etChooseTime3.setTextColor(getResources().getColor(R.color.drsiconbg));
        } else {
            activityAdmBinding.Time3.setTextColor(getResources().getColor(R.color.black));
            activityAdmBinding.etChooseTime3.setTextColor(getResources().getColor(R.color.black));
        }
        spinner_selection3 = availability_hour3.get(pos).getSlot_hour();
    }

    @Override
    public void chooseSpinner4(AdapterView<?> parent, View view, int pos, long id){
        if(isWeeklyOff1 || isWeeklyOff2 || isWeeklyOff3 || isWeeklyOff5 || isWeeklyOff6 || isWeeklyOff7) {
            if(availability_hour4.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off")){
                showInfo("You cannot choose weekly off");
                activityAdmBinding.spinnerAvailability4.setSelection(spinnerStatusTruePosition4);
                return;
            }
        }
        planned_status_code4 = availability_hour4.get(pos).getPlanned_status_code();
        isWeeklyOff4 = availability_hour4.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off");
        if(availability_hour4.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off") || availability_hour4.get(pos).getSlot_hour().equalsIgnoreCase("Not Available")) {
            activityAdmBinding.Time4.setTextColor(getResources().getColor(R.color.drsiconbg));
            activityAdmBinding.etChooseTime4.setTextColor(getResources().getColor(R.color.drsiconbg));
        } else {
            activityAdmBinding.Time4.setTextColor(getResources().getColor(R.color.black));
            activityAdmBinding.etChooseTime4.setTextColor(getResources().getColor(R.color.black));
        }
        spinner_selection4 = availability_hour4.get(pos).getSlot_hour();
    }

    @Override
    public void chooseSpinner5(AdapterView<?> parent, View view, int pos, long id){
        if(isWeeklyOff1 || isWeeklyOff2 || isWeeklyOff3 || isWeeklyOff4 || isWeeklyOff6 || isWeeklyOff7) {
            if(availability_hour5.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off")){
                showInfo("You cannot choose weekly off");
                activityAdmBinding.spinnerAvailability5.setSelection(spinnerStatusTruePosition5);
                return;
            }
        }
        planned_status_code5 = availability_hour5.get(pos).getPlanned_status_code();
        isWeeklyOff5 = availability_hour5.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off");
        if(availability_hour5.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off") || availability_hour5.get(pos).getSlot_hour().equalsIgnoreCase("Not Available")) {
            activityAdmBinding.Time5.setTextColor(getResources().getColor(R.color.drsiconbg));
            activityAdmBinding.etChooseTime5.setTextColor(getResources().getColor(R.color.drsiconbg));
        } else {
            activityAdmBinding.Time5.setTextColor(getResources().getColor(R.color.black));
            activityAdmBinding.etChooseTime5.setTextColor(getResources().getColor(R.color.black));
        }
        spinner_selection5 = availability_hour5.get(pos).getSlot_hour();
    }

    @Override
    public void chooseSpinner6(AdapterView<?> parent, View view, int pos, long id){
        if(isWeeklyOff1 || isWeeklyOff2 || isWeeklyOff3 || isWeeklyOff4 || isWeeklyOff5 || isWeeklyOff7) {
            if(availability_hour6.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off")){
                showInfo("You cannot choose weekly off");
                activityAdmBinding.spinnerAvailability6.setSelection(spinnerStatusTruePosition6);
                return;
            }
        }
        planned_status_code6 = availability_hour6.get(pos).getPlanned_status_code();
        isWeeklyOff6 = availability_hour6.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off");
        if(availability_hour6.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off") || availability_hour6.get(pos).getSlot_hour().equalsIgnoreCase("Not Available")) {
            activityAdmBinding.Time6.setTextColor(getResources().getColor(R.color.drsiconbg));
            activityAdmBinding.etChooseTime6.setTextColor(getResources().getColor(R.color.drsiconbg));
        } else {
            activityAdmBinding.Time6.setTextColor(getResources().getColor(R.color.black));
            activityAdmBinding.etChooseTime6.setTextColor(getResources().getColor(R.color.black));
        }
        spinner_selection6 = availability_hour6.get(pos).getSlot_hour();
    }

    @Override
    public void chooseSpinner7(AdapterView<?> parent, View view, int pos, long id){
        if(isWeeklyOff1 || isWeeklyOff2 || isWeeklyOff3 || isWeeklyOff4 || isWeeklyOff5 || isWeeklyOff6) {
            if(availability_hour7.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off")){
                showInfo("You cannot choose weekly off");
                activityAdmBinding.spinnerAvailability7.setSelection(spinnerStatusTruePosition7);
                return;
            }
        }
        planned_status_code7 = availability_hour7.get(pos).getPlanned_status_code();
        isWeeklyOff7 = availability_hour7.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off");
        if(availability_hour7.get(pos).getSlot_hour().equalsIgnoreCase("Weekly off") || availability_hour7.get(pos).getSlot_hour().equalsIgnoreCase("Not Available")) {
            activityAdmBinding.Time7.setTextColor(getResources().getColor(R.color.drsiconbg));
            activityAdmBinding.etChooseTime7.setTextColor(getResources().getColor(R.color.drsiconbg));
        }
        else {
            activityAdmBinding.Time7.setTextColor(getResources().getColor(R.color.black));
            activityAdmBinding.etChooseTime7.setTextColor(getResources().getColor(R.color.black));
        }
        spinner_selection7 = availability_hour7.get(pos).getSlot_hour();
    }

    @Override
    public void showInfo(ADMUpdateResponse response){
        showInfo(response.getDescription());
    }

    @Override
    public void setFinish(){
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        applyTransitionToBackFromActivity(this);
    }
}