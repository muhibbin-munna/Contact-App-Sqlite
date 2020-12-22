package com.app.contactappsp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditRemarkActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private EditText edit_des, edit_status, edit_remark1, edit_remark2;
    TextView edit_date;
    String row_index, has_data, notify = "0", event, contact_id;
    MyRemarkDetails user;
    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;
    TextView alarmDateTv, alarmTimeTv;
    ImageButton notifyCB;
    CheckBox enableCB;
    String TAG = "EditRemarkActivity";
    MyDatabaseHelper myDatabaseHelper;
    boolean commencing = true;
    ImageButton dateCommencingButton;
    String[] monthName = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    String commenceDate = "";
    String monthStr="", yearStr="", dayStr="";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_remark);

        edit_des = findViewById(R.id.edit_des);
        edit_date = findViewById(R.id.edit_date);
        edit_status = findViewById(R.id.edit_status);
        edit_remark1 = findViewById(R.id.edit_remark1);
        edit_remark2 = findViewById(R.id.edit_remark2);
        alarmDateTv = findViewById(R.id.alarmDateTv);
        alarmTimeTv = findViewById(R.id.alarmTimeTv);
        notifyCB = findViewById(R.id.notifyCB);
        enableCB = findViewById(R.id.enableCB);
        dateCommencingButton = findViewById(R.id.dateCommencingButton);
        myDatabaseHelper = new MyDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();

        Intent intent = getIntent();
        has_data = intent.getStringExtra("has_data");
        row_index = intent.getStringExtra("row_index");
        event = intent.getStringExtra("event");
        contact_id = intent.getStringExtra("contact_id");

        user = (MyRemarkDetails) intent.getSerializableExtra("user");
        if (!has_data.equals("null")) {
            edit_des.setText(user.getDescription());
            String dateTemp = user.getDate();
            if(!dateTemp.trim().equals("")) {
                commenceDate = dateTemp;
                int monthTemp = Integer.parseInt("" + dateTemp.charAt(3) + dateTemp.charAt(4));
                edit_date.setText("" + dateTemp.charAt(0) + dateTemp.charAt(1) + " " + monthName[monthTemp] + " " + dateTemp.charAt(8) + dateTemp.charAt(9));
            }
            edit_status.setText(user.getStatus());
            edit_remark1.setText(user.getRemark1());
            edit_remark2.setText(user.getRemark2());
            if (!user.getNotify().equals("0")) {
                notifyCB.setSelected(true);
                enableCB.setChecked(true);
                String ap;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(user.getNotify()));
                notify = user.getNotify();
                if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
                    ap = "AM";
                } else {
                    ap = "PM";
                }
                alarmDateTv.setText("Date: " + calendar.get(Calendar.DAY_OF_MONTH) + " " + monthName[calendar.get(Calendar.MONTH)] + " " + (calendar.get(Calendar.YEAR) % 100));
                if (calendar.get(Calendar.MINUTE) < 10) {
                    alarmTimeTv.setText("Time: " + calendar.get(Calendar.HOUR) + ":0" + calendar.get(Calendar.MINUTE) + " " + ap);
                } else {
                    alarmTimeTv.setText("Time: " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + " " + ap);
                }
            } else {
                notifyCB.setSelected(false);
                enableCB.setChecked(false);
            }
        }

        enableCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commencing = false;
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    if (notifyCB.isSelected()) {
                        notifyCB.setSelected(false);
                        notify = "0";
                        String req_code = "" + event + row_index;
                        Intent intent = new Intent(EditRemarkActivity.this, NotificationReceiver.class);
                        boolean isWorking = (PendingIntent.getBroadcast(EditRemarkActivity.this, Integer.parseInt(req_code), intent, PendingIntent.FLAG_NO_CREATE) != null);
                        if (isWorking) {
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(EditRemarkActivity.this, Integer.parseInt(req_code), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarmMgr = (AlarmManager) EditRemarkActivity.this.getSystemService(Context.ALARM_SERVICE);
                            alarmMgr.cancel(pendingIntent);
                        }
                    } else {
                        ((CheckBox) v).setChecked(false);
                        Calendar calendar = Calendar.getInstance();
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);
                        day = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(EditRemarkActivity.this, EditRemarkActivity.this, year, month, day);
                        datePickerDialog.show();
                    }
                } else {
                    enableCB.setChecked(false);
                    notifyCB.setSelected(false);
                    alarmDateTv.setText("Date: ");
                    alarmTimeTv.setText("Time: ");

                }
            }
        });
        dateCommencingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commencing = true;
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditRemarkActivity.this, EditRemarkActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (commencing) {
            edit_date.setText(dayOfMonth + " " + monthName[month] + " " + (year % 100));
            if(dayOfMonth<10) {
                dayStr = "0" + dayOfMonth;
            }
            else {
                dayStr = "" + dayOfMonth;
            }
            if(month<10) {
                monthStr = "0" + month;
            }
            else {
                monthStr = "" + month;
            }
            commenceDate = dayStr + "/" + monthStr + "/" + year;
            Log.d(TAG, "onDateSet:1 "+commenceDate);
        } else {
            myYear = year;
            myday = dayOfMonth;
            myMonth = month;
            Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR);
            minute = c.get(Calendar.MINUTE);
            Log.d(TAG, "onDateSet: " + hour + " " + minute);
            TimePickerDialog timePickerDialog = new TimePickerDialog(EditRemarkActivity.this, EditRemarkActivity.this, hour, minute, DateFormat.is24HourFormat(this));
            timePickerDialog.show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
//        setAlarm(myYear, myMonth, myday, myHour, myMinute, "");
        if (hourOfDay > 12) {
            int time = (hourOfDay - 12);
            if (minute < 10) {
                alarmTimeTv.setText("Time: " + time + ": 0" + minute + " PM");
            } else {
                alarmTimeTv.setText("Time: " + time + ":" + minute + " PM");
            }
        } else {
            if (minute < 10) {
                alarmTimeTv.setText("Time: " + hourOfDay + ": 0" + minute + " PM");
            } else {
                alarmTimeTv.setText("Time: " + hourOfDay + ":" + minute + " PM");
            }
        }
        alarmDateTv.setText("Date: " + myday + " " + monthName[myMonth] + " " + (myYear % 100));
        Calendar calendar = Calendar.getInstance();
        calendar.set(myYear,myMonth,myday,myHour,myMinute);
        notify = "" +calendar.getTimeInMillis();
        enableCB.setChecked(true);
        notifyCB.setSelected(true);

        Log.d(TAG, "onTimeSet: " + myYear + " " + myMonth + " " + myday + " " + myHour + " " + myMinute);
    }

    private void setAlarm(int year, int monthAlarm, int dateAlarm, int hourAlarm, int minuteAlarm, String description, String status) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DATE, dateAlarm);
        calendar.set(Calendar.MONTH, monthAlarm);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, hourAlarm);
        calendar.set(Calendar.MINUTE, minuteAlarm);
        calendar.set(Calendar.SECOND, 1);

        String req_code = "" + event + row_index;

        if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
            Intent intent = new Intent(EditRemarkActivity.this, NotificationReceiver.class);
            boolean isWorking = (PendingIntent.getBroadcast(EditRemarkActivity.this, Integer.parseInt(req_code), intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
            if (!isWorking) {
                intent.putExtra("description", description);
                intent.putExtra("status", status);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(EditRemarkActivity.this, Integer.parseInt(req_code), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                notify = "" + calendar.getTimeInMillis();

                Log.d("TAG", "setAlarm: alam setted " + req_code);

            } else {
                Log.d("TAG", "setAlarm: alam already setted " + req_code);
            }
        }
    }

    public void save(View view) {
        setAlarm(myYear, myMonth, myday, myHour, myMinute, edit_des.getText().toString(), edit_status.getText().toString());
        String description = edit_des.getText().toString();
        String date = commenceDate;
        String status = edit_status.getText().toString();
        String remark1 = edit_remark1.getText().toString();
        String remark2 = edit_remark2.getText().toString();
        if(!enableCB.isChecked()){
            notify = "0";
        }
        if (has_data.equals("null")) {
            myDatabaseHelper.insertDetailsData(String.valueOf(event), row_index, contact_id, description, remark1, remark2, notify, date, status);
            Log.d(TAG, "onClick: add " + row_index + " " + description + " " + date + " " + status + " " + notify + " " + remark1 + " " + remark2);
            Toast.makeText(this, "added", Toast.LENGTH_SHORT).show();
        } else {
            boolean i = myDatabaseHelper.updateData(user.getId(), description, remark1, remark2, date, status);
            myDatabaseHelper.updateNotification(user.getId(), notify);
            Log.d(TAG, "onClick: update " + row_index + " " + description + " " + date + " " + status + " " + notify + " " + remark1 + " " + remark2);
            Toast.makeText(this, "data updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void cancel(View view) {
        finish();
    }
}