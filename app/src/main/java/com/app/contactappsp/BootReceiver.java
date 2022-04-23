package com.app.contactappsp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.contactappsp.Databases.MyDatabaseHelper;

public class BootReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLETED =
            "android.intent.action.BOOT_COMPLETED";
    private static final String QUICKBOOT_POWERON =
            "android.intent.action.QUICKBOOT_POWERON";
    MyDatabaseHelper myDatabaseHelper;
    String TAG = "BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BOOT_COMPLETED.equals(action) ||
                QUICKBOOT_POWERON.equals(action)) {
            Log.d(TAG, "onReceive: 1");
            myDatabaseHelper = new MyDatabaseHelper(context);
            SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
            Cursor cursor = myDatabaseHelper.read_all_remarks();
            if (cursor.getCount() != 0) {
                Log.d(TAG, "onReceive: 2");
                if(cursor.moveToNext()){
                    String contact_id , event , row_index, reqCode,description,contact_name;
                    contact_id = cursor.getString(1);
                    event = cursor.getString(2);
                    row_index = cursor.getString(3);
                    description = cursor.getString(4);
                    contact_name = cursor.getString(10);
                    reqCode = contact_id + event + row_index;
                    long notification = Long.parseLong(cursor.getString(7));
                    Log.d(TAG, "onReceive: 3 "+contact_id+notification + reqCode+description);
                    if( notification >= System.currentTimeMillis() ){
                        setAlarm(context,contact_name,notification , reqCode,description );
                    }
                }
            }

        }

    }

    private void setAlarm(Context context, String contact_name, long notification, String req_code, String description) {
        Intent intent = new Intent(context, NotificationReceiver.class);

        boolean isWorking = (PendingIntent.getBroadcast(context, Integer.parseInt(req_code), intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
        if (!isWorking) {
            intent.putExtra("contact_name", contact_name);
            intent.putExtra("description", description);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(req_code), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, notification, pendingIntent);

            Log.d("TAG", "setAlarm: alam setted " + req_code);

        } else {
            Log.d("TAG", "setAlarm: alam already setted " + req_code);
        }
    }
}
