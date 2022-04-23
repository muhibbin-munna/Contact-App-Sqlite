package com.app.contactappsp.Activities;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.contactappsp.Adapters.RemarkAdapter;
import com.app.contactappsp.Databases.DbHelper;
import com.app.contactappsp.Databases.MyDatabaseHelper;
import com.app.contactappsp.Models.Contact;
import com.app.contactappsp.Models.MyRemarkDetails;
import com.app.contactappsp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactDetailsActivity extends AppCompatActivity implements View.OnClickListener, RemarkAdapter.OnItemClickListener {
    TextView fullName, phone, gender, age, address1, address2, city, postCode, remark;
    RoundedImageView imageView;
    Contact contact;
    ImageButton call, message, mail;
    RecyclerView remarkRV;
    TextView event1, event2, event3;
    List<MyRemarkDetails> userList = new ArrayList<>();
    MyDatabaseHelper myDatabaseHelper;
    String TAG = "ContactDetailsActivity";
    int event = 1;
    int pos = 0;
    RemarkAdapter contactAdapter;
    CheckBox enableCB;
    ImageButton editTv;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
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

        mAdView = findViewById(R.id.adViewCD);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        });

        myDatabaseHelper = new MyDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();

        Intent intent = getIntent();
        event = intent.getIntExtra("event", 1);

        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
        call = findViewById(R.id.buttonCall);
        message = findViewById(R.id.buttonMessage);


        setData();

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE},
                    1);
        }

        switch (event) {
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
                userList.add(contactDetails);

            }
        }

        Log.d(TAG, "load: " + userList.size());
        cursor.close();
        Collections.sort(
                userList,
                new Comparator<MyRemarkDetails>()
                {
                    public int compare(MyRemarkDetails lhs, MyRemarkDetails rhs)
                    {
                        return lhs.getDate().compareTo(rhs.getDate());
                    }
                }
        );

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

            String date = contact.getAge();
            int day,month,year;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(date));
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
            String ageStr = calculateAge(year, month, day);
            age.setText(ageStr + " years");
            Log.d(TAG, "setData: "+date);
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

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
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
        Intent intent1 = new Intent(this, UpdateContactActivity.class);
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
        Toast.makeText(this, "Contact deleted ", Toast.LENGTH_SHORT).show();
        db.close();
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();
        int i = myDatabaseHelper.deleteData(String.valueOf(id));

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
        Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getPhone()));
        startActivity(intent1);
        Log.v("Number", contact.getPhone());
    }

    public void message(View view) {
        Contact contact = getData();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + contact.getPhone()));
        startActivity(intent);
    }

    public void whatsapp(View view) {
        try {
            Contact contact = getData();
            String toNumber = contact.getPhone(); // contains spaces
            startActivity(
                    new Intent(Intent.ACTION_VIEW,
                            Uri.parse(
                                    String.format("https://api.whatsapp.com/send?phone=%s&text=%s", toNumber, "")
                            )
                    )
            );

        } catch (Exception e) {
            Toast.makeText(this, "could not find WhatsApp", Toast.LENGTH_LONG).show();
        }
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


    @Override
    public void setOnRemarkClickListener(int position, View v) {

    }

    @Override
    public void setOnRemarkLongClickListener(int position) {
        pos = position;
        if (position < userList.size()) {
            Intent intent = new Intent(ContactDetailsActivity.this, EditRemarkActivity.class);
            intent.putExtra("has_data", "yes");
            intent.putExtra("row_index", String.valueOf(position));
            intent.putExtra("event", String.valueOf(event));
            intent.putExtra("contact_id", String.valueOf(contact.getId()));
            intent.putExtra("contact_name", String.valueOf(contact.getFullName()));
            intent.putExtra("user", (Serializable) userList.get(position));
            startActivity(intent);

        } else {
            Intent intent = new Intent(ContactDetailsActivity.this, EditRemarkActivity.class);
            intent.putExtra("has_data", "null");
            intent.putExtra("event", String.valueOf(event));
            intent.putExtra("row_index", String.valueOf(position));
            intent.putExtra("contact_id", String.valueOf(contact.getId()));
            intent.putExtra("contact_name", String.valueOf(contact.getFullName()));
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
        } else if (item.getItemId() == android.R.id.home) {
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

}