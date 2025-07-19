package in.ecomexpress.sathi.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import in.ecomexpress.sathi.R;

public class PopUpClass {

    //PopupWindow display method


    View popupView;
    PopupWindow popupWindow;

    public void showPopupWindow( Activity activity) {

        //Create a View object yourself through inflater
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.pop_up_layout, null);

        //Specify the length and width thr ough constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setTouchable(false);
        popupWindow.setFocusable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Set the location of the window on the screen
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        // popupWindow.

        TextView test2 = popupView.findViewById(R.id.titleText);
        Button  MessageButton   = popupView.findViewById(R.id.messageButton);
        test2.setText("Please wait ");
        new CountDownTimer(25000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                MessageButton.setText("00:"+ (millisUntilFinished/1000));
            }
            @Override
            public void onFinish() {
                popupWindow.dismiss();
                //info.setVisibility(View.GONE);
            }
        }.start();
        //Initialize the elements of our window, install the handler



        Button buttonEdit = popupView.findViewById(R.id.messageButton);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //As an example, display the message
                Toast.makeText(activity, "Wow, popup action button", Toast.LENGTH_SHORT).show();

            }
        });


        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });

    }
    public void closepopup(Activity activity){
//        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        //LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
//        View popupView = layoutInflater.inflate(R.layout.pop_up_layout, null);
//
//        //Specify the length and width through constants
//        int width = LinearLayout.LayoutParams.MATCH_PARENT;
//        int height = LinearLayout.LayoutParams.MATCH_PARENT;
//
//        //Make Inactive Items Outside Of PopupWindow
//        boolean focusable = true;
//
//        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.dismiss();
        //return true;

    }

}