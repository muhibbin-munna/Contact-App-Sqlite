package com.app.contactappsp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.app.contactappsp.Models.MyRemarkDetails;

import java.util.Calendar;

public class AddEventDialog extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{
    private EditText edit_des, edit_date, edit_status, edit_remark1, edit_remark2;
    private AddEventDialogListener listener;
    String row_index,has_data, notify="0";
    MyRemarkDetails user;
    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;
    TextView alarmDateTv,alarmTimeTv;
    ImageButton notifyCB;
    CheckBox enableCB;
    String TAG = "AddEventDialog";

    public AddEventDialog(String row_index, String has_data) {
        this.row_index = row_index;
        this.has_data = has_data;

    }
    public AddEventDialog(String row_index, MyRemarkDetails user) {
        this.user = user;
        this.row_index = row_index;
        this.has_data = "yes";
    }

    @SuppressLint("SetTextI18n")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_remark_layout, null);

        builder.setView(view)
                .setTitle("Edit")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String description = edit_des.getText().toString();
                        String date = edit_date.getText().toString();
                        String status = edit_status.getText().toString();
                        String remark1 = edit_remark1.getText().toString();
                        String remark2 = edit_remark2.getText().toString();
                        if (has_data.equals("null")) {
                            listener.applyTexts(row_index,description, date, status, notify,remark1, remark2);
                            Log.d(TAG, "onClick: add "+ row_index +" "+description+" "+ date+" "+ status+" "+ notify+" "+remark1+" "+ remark2);
                            Toast.makeText(getContext(), "added  "+notify, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            listener.updateTexts(user.getId(),description, date, status, notify, remark1, remark2);
                            Log.d(TAG, "onClick: update "+ row_index +" "+description+" "+ date+" "+ status+" "+ notify+" "+remark1+" "+ remark2);
                            Toast.makeText(getContext(), "data updated", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        edit_des = view.findViewById(R.id.edit_des);
        edit_date = view.findViewById(R.id.edit_date);
        edit_status = view.findViewById(R.id.edit_status);
        edit_remark1 = view.findViewById(R.id.edit_remark1);
        edit_remark2 = view.findViewById(R.id.edit_remark2);
        alarmDateTv = view.findViewById(R.id.alarmDateTv);
        alarmTimeTv = view.findViewById(R.id.alarmTimeTv);
        notifyCB = view.findViewById(R.id.notifyCB);
        enableCB = view.findViewById(R.id.enableCB);
        enableCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if(checked){

                        if (notifyCB.isSelected()) {
                            notifyCB.setSelected(false);

                            notify = "0";
                            String req_code = "" + user.getEvent() + user.getRow();

                            Intent intent = new Intent(getContext(), NotificationReceiver.class);
                            boolean isWorking = (PendingIntent.getBroadcast(getContext(), Integer.parseInt(req_code), intent, PendingIntent.FLAG_NO_CREATE) != null);
                            if (isWorking) {
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), Integer.parseInt(req_code), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                                alarmMgr.cancel(pendingIntent);

                            }
                        } else {
                            Calendar calendar = Calendar.getInstance();
                            year = calendar.get(Calendar.YEAR);
                            month = calendar.get(Calendar.MONTH);
                            day = calendar.get(Calendar.DAY_OF_MONTH);
                            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getTargetFragment(), year, month, day);
                            datePickerDialog.show();

                    }
                }


            }
        });
        if(!has_data.equals("null")){
            edit_des.setText(user.getDescription());
            edit_date.setText(user.getDate());
            edit_status.setText(user.getStatus());
            edit_remark1.setText(user.getRemark1());
            edit_remark2.setText(user.getRemark2());
            if (!user.getNotify().equals("0")) {
                notifyCB.setSelected(true);
                enableCB.setChecked(true);
                String ap;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(user.getNotify()));
                if(calendar.get(Calendar.AM_PM) == Calendar.AM){
                    ap= "AM";
                }
                else {
                    ap="PM";
                }
                alarmDateTv.setText("Date: " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR));
                alarmTimeTv.setText("Time: " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE)+" "+ap);
            }
            else {
                notifyCB.setSelected(false);
                enableCB.setChecked(false);
            }
        }
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AddEventDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement AddEventDialogListener");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = day;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        Log.d(TAG, "onDateSet: "+hour + " "+ minute);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getTargetFragment(), hour, minute, DateFormat.is24HourFormat(getContext()));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
//        setAlarm(myYear, myMonth, myday, myHour, myMinute, "");

        Log.d(TAG, "onTimeSet: "+myYear+ " " +myMonth+" "+ myday+" "+ myHour+" " +myMinute);
    }
    private void setAlarm(int year, int monthAlarm, int dateAlarm, int hourAlarm, int minuteAlarm, String timeAlarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DATE, dateAlarm);
        calendar.set(Calendar.MONTH, monthAlarm);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, hourAlarm);
        calendar.set(Calendar.MINUTE, minuteAlarm);
        calendar.set(Calendar.SECOND, 1);

        String req_code = "" + user.getEvent() + user.getRow();

        if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
            Intent intent = new Intent(getContext(), NotificationReceiver.class);
            boolean isWorking = (PendingIntent.getBroadcast(getContext(), Integer.parseInt(req_code), intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
            if (!isWorking) {
                intent.putExtra("timeAlarm", timeAlarm);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), Integer.parseInt(req_code), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                notify = ""+calendar.getTimeInMillis();


            } else {
                Log.d("TAG", "setAlarm: alam already setted " + req_code);
            }
        }
    }

    public interface AddEventDialogListener {
        void applyTexts(String row_index, String description, String date, String status,String notify, String remark1, String remark2);

        void updateTexts(String id, String description, String date, String status, String notify, String remark1, String remark2);

    }
}
