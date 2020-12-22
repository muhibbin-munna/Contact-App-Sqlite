package com.app.contactappsp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ContactDetailsActivity extends AppCompatActivity implements View.OnClickListener, RemarkAdapter.OnItemClickListener {
    TextView fullName, phone, gender, age, address1, address2, city, postCode, remark;
    ImageView imageView, notifyCB;
    Contact contact;
    ImageButton call, message, mail;
    RecyclerView remarkRV;
    TextView event1, event2, event3;
    List<MyRemarkDetails> userList = new ArrayList<>();
    MyRemarkDetails[] array = new MyRemarkDetails[20];
    MyDatabaseHelper myDatabaseHelper;
    String TAG = "ContactDetailsActivity";
    int event = 1;
    int pos = 0;
    RemarkAdapter contactAdapter;
    CheckBox enableCB;
    ImageButton editTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        fullName = findViewById(R.id.nameTv);
        phone = findViewById(R.id.phoneTv);
        gender = findViewById(R.id.genderTv);
        age = findViewById(R.id.ageTv);
        address1 = findViewById(R.id.addressLine1Tv);
        address2 = findViewById(R.id.addressLine2Tv);
        city = findViewById(R.id.cityTv);
        postCode = findViewById(R.id.postCodeTv);
        remark = findViewById(R.id.remarkTv);
        imageView = findViewById(R.id.contactImageView);
        editTv = findViewById(R.id.editTv);
        remarkRV = findViewById(R.id.remarkRV);
        event1 = findViewById(R.id.event1Tv);
        event2 = findViewById(R.id.event2Tv);
        event3 = findViewById(R.id.event3Tv);
        enableCB = findViewById(R.id.enableCB);
        myDatabaseHelper = new MyDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();

        Intent intent = getIntent();
        event = intent.getIntExtra("event", 1);
        pos = intent.getIntExtra("row", 1);

//        enableCB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean checked = ((CheckBox) v).isChecked();
//                if(checked){
//                    v.setActivated();
//                }
//
//            }
//        });
        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
//        call =(ImageButton) findViewById(R.id.buttonCall);
//        message =(ImageButton) findViewById(R.id.buttonMessage);
//        mail =(ImageButton) findViewById(R.id.buttonEmail);

        setData();

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE},
                    1);
        }

        switch (event)
        {
            case 1:
                event1.setBackgroundResource(R.drawable.edittextbg);
                break;
            case 2:
                event2.setBackgroundResource(R.drawable.edittextbg);
                break;
            case 3:
                event3.setBackgroundResource(R.drawable.edittextbg);
                break;
        }


        load(event);
        event1.setOnClickListener(this);
        event2.setOnClickListener(this);
        event3.setOnClickListener(this);
    }

    private void load(int i) {
        userList.clear();
        array = new MyRemarkDetails[20];
        Cursor cursor = myDatabaseHelper.read_event_2(String.valueOf(contact.getId()));
        if (i == 1) {
            cursor = myDatabaseHelper.read_event_1(String.valueOf(contact.getId()));
        }
        if (i == 2) {
            cursor = myDatabaseHelper.read_event_2(String.valueOf(contact.getId()));
        }
        if (i == 3) {
            cursor = myDatabaseHelper.read_event_3(String.valueOf(contact.getId()));
        }
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                MyRemarkDetails contactDetails = new MyRemarkDetails();
                contactDetails.setId(cursor.getString(0));
                contactDetails.setEvent(cursor.getString(2));
                contactDetails.setRow(cursor.getString(3));
                contactDetails.setDescription(cursor.getString(4));
                contactDetails.setRemark1(cursor.getString(5));
                contactDetails.setRemark2(cursor.getString(6));
                contactDetails.setNotify(cursor.getString(7));
                contactDetails.setDate(cursor.getString(8));
                contactDetails.setStatus(cursor.getString(9));
                array[Integer.parseInt(cursor.getString(3))] = contactDetails;
            }
        }
        userList.addAll(Arrays.asList(array).subList(0, 20));
        Log.d(TAG, "load: "+userList.size());
        cursor.close();
        contactAdapter = new RemarkAdapter(userList, ContactDetailsActivity.this);
        remarkRV.setLayoutManager(new LinearLayoutManager(this));
        remarkRV.setAdapter(contactAdapter);
        contactAdapter.setOnRemarkClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        contact = getData();
        if (!contact.getFullName().trim().equals("")) {
            fullName.setText(contact.getFullName());
        }

        if (!contact.getPhone().trim().equals("")) {
            phone.setText(contact.getPhone());
        }

        if (!contact.getGender().trim().equals("")) {
            gender.setText(contact.getGender());
        }
        if (!contact.getAge().trim().equals("")) {

            String date= contact.getAge();
            int day = Integer.parseInt(""+ date.charAt(0)+date.charAt(1));
            int month = Integer.parseInt(""+ date.charAt(2)+date.charAt(3));
            int year = Integer.parseInt(""+ date.charAt(4)+date.charAt(5)+date.charAt(6)+date.charAt(7));
            String ageStr = calculateAge(year,month-1,day);
            age.setText(ageStr+" years");
        }
        if (!contact.getAddress1().trim().equals("")) {
            address1.setText(contact.getAddress1());
        }
        if (!contact.getAddress2().trim().equals("")) {
            address2.setText(contact.getAddress2());
        }
        if (!contact.getCity().trim().equals("")) {
            city.setText(contact.getCity());
        }
        if (!contact.getPostCode().trim().equals("")) {
            postCode.setText(contact.getPostCode());
        }
        if (!contact.getRemark().trim().equals("")) {
            remark.setText(contact.getRemark());
        }
        if (contact.getImage() == null) {
            imageView.setImageResource(R.drawable.contacts_icon);
        } else {
            byte[] arr = Base64.decode(contact.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            imageView.setImageBitmap(bitmap);
        }
    }

    private String calculateAge(int year, int month, int day) {

            Calendar dob = Calendar.getInstance();
            Calendar today = Calendar.getInstance();

            dob.set(year, month, day);

            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
                age--;
            }

            Integer ageInt = new Integer(age);
            String ageS = ageInt.toString();
            return ageS;
    }

    public Contact getData() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("contact", 1);
        DbHelper helper = new DbHelper(this, DbHelper.DB_NAME, null, DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Contact> contacts = new ArrayList<>();

        String query = "SELECT * FROM " + DbHelper.TABLE_NAME + " WHERE ID=" + id;
        Cursor cursor = db.rawQuery(query, null);
        Contact contact = null;
        if (cursor.moveToFirst()) {
            do {
                contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setFullName(cursor.getString(1));
                contact.setPhone(cursor.getString(2));
                contact.setGender(cursor.getString(3));
                contact.setAge(cursor.getString(4));
                contact.setAddress1(cursor.getString(5));
                contact.setAddress2(cursor.getString(6));
                contact.setCity(cursor.getString(7));
                contact.setPostCode(cursor.getString(8));
                contact.setRemark(cursor.getString(9));
                contact.setImage(cursor.getString(10));

                contacts.add(contact);

            } while (cursor.moveToNext());
        }

        return contact;
    }


    public void updateData() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("contact", 0);
        Intent intent1 = new Intent(this, UpdateContact.class);
        intent1.putExtra("updateContact", id);
        startActivity(intent1);
    }

    public void deleteData() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("contact", 0);
        DbHelper helper = new DbHelper(this, DbHelper.DB_NAME, null, DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getWritableDatabase();
        int result = db.delete(DbHelper.TABLE_NAME, "ID=" + id, null);
        Log.v("TAG", "Contact deleted " + result + " " + id);
        Toast.makeText(this, "Contact deleted " , Toast.LENGTH_SHORT).show();
        db.close();
        finish();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setData();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    public void call(View view) {
        Contact contact = getData();
        Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+88" + contact.getPhone()));
        startActivity(intent1);
        Log.v("Number", contact.getPhone());
    }

    public void message(View view) {
        Contact contact = getData();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + contact.getPhone()));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.event1Tv:
                event = 1;
                event1.setBackgroundResource(R.drawable.edittextbg);
                event2.setBackgroundResource(android.R.color.transparent);
                event3.setBackgroundResource(android.R.color.transparent);
                load(1);
                break;
            case R.id.event2Tv:
                event = 2;
                event2.setBackgroundResource(R.drawable.edittextbg);
                event1.setBackgroundResource(android.R.color.transparent);
                event3.setBackgroundResource(android.R.color.transparent);
                load(2);
                break;
            case R.id.event3Tv:
                event = 3;
                event3.setBackgroundResource(R.drawable.edittextbg);
                event2.setBackgroundResource(android.R.color.transparent);
                event1.setBackgroundResource(android.R.color.transparent);
                load(3);
                break;
        }
    }

//    @Override
//    public void setOnRemarkClickListener(int position, View v) {
//        pos = position;
//        if (v.getId() == R.id.notifyCB) {
//            notifyCB = (ImageView) v;
//            boolean found = false;
//            Log.d(TAG, "setOnRemarkClickListener: " + userList.size());
//            for (int i = 0; i < userList.size(); i++) {
//                if (userList.get(i) != null && Integer.parseInt(userList.get(i).getRow()) == position) {
//                    found = true;
//                }
//            }
//            if (!found) {
//                Toast.makeText(this, "Add the event first", Toast.LENGTH_SHORT).show();
//            } else {
//                if (notifyCB.isSelected()) {
//                    notifyCB.setSelected(false);
//                    myDatabaseHelper.updateNotification(userList.get(position).getId(), "0");
////                    contactAdapter.notifyDataSetChanged();
//                    load(event);
//                    String req_code = "" + userList.get(pos).getEvent() + userList.get(pos).getRow();
//                    Log.d(TAG, "setOnRemarkClickListener: "+userList.get(position).getId());
//
//                    Intent intent = new Intent(this, NotificationReceiver.class);
//                    boolean isWorking = (PendingIntent.getBroadcast(this, Integer.parseInt(req_code), intent, PendingIntent.FLAG_NO_CREATE) != null);
//                    if (isWorking) {
//                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(req_code), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//                        alarmMgr.cancel(pendingIntent);
////                        contactAdapter.notifyDataSetChanged();
//                        load(event);
//                        Log.d(TAG, "setOnRemarkClickListener: cancel alarm");
//                    }
//                } else {
//                    Calendar calendar = Calendar.getInstance();
//                    year = calendar.get(Calendar.YEAR);
//                    month = calendar.get(Calendar.MONTH);
//                    day = calendar.get(Calendar.DAY_OF_MONTH);
//                    DatePickerDialog datePickerDialog = new DatePickerDialog(ContactDetailsActivity.this, ContactDetailsActivity.this, year, month, day);
//                    datePickerDialog.show();
//                }
//            }
//        }
//    }

    @Override
    public void setOnRemarkClickListener(int position, View v) {

    }

    @Override
    public void setOnRemarkLongClickListener(int position) {
        pos = position;
        if (userList.size() != 0) {
            if (userList.get(position) != null) {
                Intent intent = new Intent(ContactDetailsActivity.this, EditRemarkActivity.class);
                intent.putExtra("has_data", "yes");
                intent.putExtra("row_index", String.valueOf(position));
                intent.putExtra("event",  String.valueOf(event));
                intent.putExtra("contact_id",  String.valueOf(contact.getId()));

                intent.putExtra("user", (Serializable) userList.get(position));
                startActivity(intent);
            } else {
                Intent intent = new Intent(ContactDetailsActivity.this, EditRemarkActivity.class);
                intent.putExtra("has_data", "null");
                intent.putExtra("row_index", String.valueOf(position));
                intent.putExtra("event",  String.valueOf(event));

                intent.putExtra("contact_id",  String.valueOf(contact.getId()));
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(ContactDetailsActivity.this, EditRemarkActivity.class);
            intent.putExtra("has_data", "null");
            intent.putExtra("event",  String.valueOf(event));
            intent.putExtra("row_index", String.valueOf(position));
            intent.putExtra("contact_id",  String.valueOf(contact.getId()));
            startActivity(intent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_manu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.delete_menu) {
            deleteData();
            return true;
        }
        else if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        load(event);
        remarkRV.scrollToPosition(pos);
    }

    //    @Override
//    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        myYear = year;
//        myday = day;
//        myMonth = month;
//        Calendar c = Calendar.getInstance();
//        hour = c.get(Calendar.HOUR);
//        minute = c.get(Calendar.MINUTE);
//        TimePickerDialog timePickerDialog = new TimePickerDialog(ContactDetailsActivity.this, ContactDetailsActivity.this, hour, minute, DateFormat.is24HourFormat(this));
//        timePickerDialog.show();
//    }
//
//    @Override
//    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        myHour = hourOfDay;
//        myMinute = minute;
//        Log.d(TAG, "onTimeSet: " + "Year: " + myYear + "\n" +
//                "Month: " + myMonth + "\n" +
//                "Day: " + myday + "\n" +
//                "Hour: " + myHour + "\n" +
//                "Minute: " + myMinute);
//        setAlarm(myYear, myMonth, myday, myHour, myMinute, "");
//
//
//    }

//    private void setAlarm(int year, int monthAlarm, int dateAlarm, int hourAlarm, int minuteAlarm, String timeAlarm) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.DATE, dateAlarm);
//        calendar.set(Calendar.MONTH, monthAlarm);
//        calendar.set(Calendar.YEAR, year);
//        calendar.set(Calendar.HOUR_OF_DAY, hourAlarm);
//        calendar.set(Calendar.MINUTE, minuteAlarm);
//        calendar.set(Calendar.SECOND, 1);
//
//        String req_code = "" + userList.get(pos).getEvent() + userList.get(pos).getRow();
//
//        Log.d(TAG, "setAlarm: pos " + pos);
//
//        if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
//            Intent intent = new Intent(this, NotificationReceiver.class);
//            boolean isWorking = (PendingIntent.getBroadcast(this, Integer.parseInt(req_code), intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
//            if (!isWorking) {
//                Log.d(TAG, "setAlarm: reqCode " + req_code);
//                intent.putExtra("timeAlarm", timeAlarm);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(req_code), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                myDatabaseHelper.updateNotification(userList.get(pos).getId(), ""+calendar.getTimeInMillis());
////                contactAdapter.notifyDataSetChanged();
//                load(event);
//                Log.d(TAG, "setAlarm: calender " + calendar.getTimeInMillis());
//                Log.d(TAG, "setAlarm: Id: "+ userList.get(pos).getId());
//            } else {
//                Log.d(TAG, "setAlarm: alam already setted " + req_code);
//            }
//        }
//    }

//    @Override
//    public void applyTexts(String row_index, String description, String date, String status,String notify, String remark1, String remark2) {
//        myDatabaseHelper.insertDetailsData(String.valueOf(event), row_index, String.valueOf(contact.getId()), description, remark1, remark2, notify, date, status);
//        load(event);
//    }
//
//    @Override
//    public void updateTexts(String id, String description, String date, String status, String notify, String remark1, String remark2) {
//        boolean i = myDatabaseHelper.updateData(id, description, remark1, remark2, date, status);
//        myDatabaseHelper.updateNotification(id,notify);
//        Log.d(TAG, "updateTexts: updated " + i + "id = " + id + "userid: " + contact.getId() + " " + userList.get(pos).getId());
//        load(event);
//
//    }

//    public void email(View view){
//        Contact contact = getData();
//        String email=contact.getEmail();
//        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts( "mailto",email, null));
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
//        startActivity(Intent.createChooser(emailIntent, "Send email..."));
//    }
}