package com.app.contactappsp.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.contactappsp.Databases.DbHelper;
import com.app.contactappsp.Models.Contact;
import com.app.contactappsp.Models.Utility;
import com.app.contactappsp.R;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class UpdateContactActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    EditText fullName, phone, address1, address2, city, postCode, remark;
    ImageView imageButton;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Spinner genderSpinnerText;
    ImageButton dateButton;
    int day, month, year;
    TextView dobtv;
    String dob = "";
    String[] monthName = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);

        fullName = findViewById(R.id.fullNameEditTextUpdate);
        phone = findViewById(R.id.phoneNoEditTextUpdate);
        genderSpinnerText = findViewById(R.id.genderSpinnerId);
        address1 = findViewById(R.id.addressLine1EditTextUpdate);
        address2 = findViewById(R.id.addressLine2EditTextUpdate);
        city = findViewById(R.id.cityEditTextUpdate);
        postCode = findViewById(R.id.postCodeEditTextUpdate);
        remark = findViewById(R.id.remarkEditTextUpdate);
        imageButton = findViewById(R.id.imageButtonUpdate);
        dobtv = findViewById(R.id.dob_tv_update);
        dateButton = findViewById(R.id.dateButtonUpdate);
        getData();

        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateContactActivity.this, UpdateContactActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.saveFlaotingButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
                Toast.makeText(UpdateContactActivity.this, "Updated successfully...!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[1] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                imageButton.setEnabled(true);
                if (userChoosenTask.equals("Take Photo")) {
                    cameraIntent();
                } else if (userChoosenTask.equals("Choose from Library")) {
                    galleryIntent();
                }
            } else {
                imageButton.setEnabled(false);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            destination.getAbsolutePath();
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageButton.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Glide.with(this).load(data.getData()).into(imageButton);

    }

    private void selectImage() {
        final CharSequence[] items = {"Remove Photo", "Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateContactActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(UpdateContactActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Remove Photo")) {
                    userChoosenTask = "Remove Photo";
                    if (result) {
                        imageButton.setImageResource(R.drawable.contacts_icon);
                    }

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    //--------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    public void getData() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("updateContact", 0);
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

        fullName.setText(contact.getFullName());
        phone.setText(contact.getPhone());
        String[] baths = getResources().getStringArray(R.array.gender);
        genderSpinnerText.setSelection(Arrays.asList(baths).indexOf(contact.getGender().trim()));
        dob = contact.getAge();
        if (!dob.trim().equals("")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(dob));
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            dobtv.setText(day + " " + monthName[month] + " " + (year % 100));
        } else {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
        address1.setText(contact.getAddress1());
        address2.setText(contact.getAddress2());
        city.setText(contact.getCity());
        postCode.setText(contact.getPostCode());
        remark.setText(contact.getRemark());
        postCode.setText(contact.getPostCode());
        if (contact.getImage() == null) {
            imageButton.setImageResource(R.drawable.contacts_icon);
        } else {
            byte[] arr = Base64.decode(contact.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);
//            imageButton.setImageBitmap(bitmap);
            Glide.with(this).asBitmap().load(bitmap).into(imageButton);
        }
    }

    public void addData() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("updateContact", 0);
        DbHelper helper = new DbHelper(this, DbHelper.DB_NAME, null, DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getWritableDatabase();
        Log.v("TAG", "Updated row no " + id);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bm = ((BitmapDrawable) imageButton.getDrawable()).getBitmap();
        if (bm.getWidth() > bm.getHeight()) {
            int ratio = bm.getWidth() / bm.getHeight();
            bm = Bitmap.createScaledBitmap(bm, 200 * ratio, 200, true);
        } else {
            int ratio = bm.getHeight() / bm.getWidth();
            bm = Bitmap.createScaledBitmap(bm, 200, 200 * ratio, true);
        }
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] arr1 = stream.toByteArray();
        String result = Base64.encodeToString(arr1, Base64.DEFAULT);

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_2, fullName.getText().toString());
        values.put(DbHelper.COLUMN_3, phone.getText().toString());
        values.put(DbHelper.COLUMN_4, genderSpinnerText.getSelectedItem().toString());
        values.put(DbHelper.COLUMN_5, dob);
        values.put(DbHelper.COLUMN_6, address1.getText().toString());
        values.put(DbHelper.COLUMN_7, address2.getText().toString());
        values.put(DbHelper.COLUMN_8, city.getText().toString());
        values.put(DbHelper.COLUMN_9, postCode.getText().toString());
        values.put(DbHelper.COLUMN_10, remark.getText().toString());
        values.put(DbHelper.COLUMN_11, result);

        db.update(DbHelper.TABLE_NAME, values, "ID=" + id, null);
        db.close();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        dob = String.valueOf(calendar.getTimeInMillis());
        dobtv.setText(dayOfMonth + " " + monthName[month] + " " + (year % 100));
    }
}
