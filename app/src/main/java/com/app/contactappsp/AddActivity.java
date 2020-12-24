package com.app.contactappsp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    EditText fullName, phone, age, address1, address2, city, postCode, remark;
    TextView dobtv;
    Spinner genderSpinnerText;
    //    DatePicker datePicker;
    ImageButton dateButton;
    ImageView imageView;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    int day, month, year;
    String dob = "";
    String[] monthName = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        fullName = findViewById(R.id.fullNameEditText);
        phone = findViewById(R.id.phoneNoEditText);
//        gender = findViewById(R.id.genderEditText);
//        age = findViewById(R.id.ageEditText);
//        datePicker = findViewById(R.id.datePickerId);
        dateButton = findViewById(R.id.dateButton);
        genderSpinnerText = findViewById(R.id.genderSpinnerId);
        address1 = findViewById(R.id.addressLine1EditText);
        address2 = findViewById(R.id.addressLine2EditText);
        city = findViewById(R.id.cityEditText);
        postCode = findViewById(R.id.postCodeEditText);
        remark = findViewById(R.id.remarkEditText);
        dobtv = findViewById(R.id.dob_tv);

        imageView = findViewById(R.id.imageButtonAdd);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
//                DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this,android.R.style.Theme_Holo_Light_Dialog, AddActivity.this, year, month, day);
//                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this, AddActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    //--------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[1] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                imageView.setEnabled(true);
                if (userChoosenTask.equals("Take Photo")) {
                    cameraIntent();
                } else if (userChoosenTask.equals("Choose from Library")) {
                    galleryIntent();
                }
            } else {
                imageView.setEnabled(false);
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

        imageView.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imageView.setImageBitmap(bm);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(AddActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

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

    //--------------------------------------------------------------------------------------------------------------------------------


    public long save(View view) {

        DbHelper helper = new DbHelper(this, DbHelper.DB_NAME, null, DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getWritableDatabase();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
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

        if (fullName.getText().toString().isEmpty()) {
            fullName.setError("Enter a Phone no");
            fullName.requestFocus();
            return 0;
        }else if(phone.getText().toString().isEmpty()){
            phone.setError("Enter a Phone no");
            phone.requestFocus();
            return 0;
        }else {
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

        long insertId = db.insert(DbHelper.TABLE_NAME, null, values);
        Toast.makeText(this, "Inserted 1 item..", Toast.LENGTH_SHORT).show();
        Log.d("AddActivity", "Inserted.." + insertId);
        db.close();
        finish();
        return insertId;
        }
    }

    public void cancel(View view) {
        finish();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        month = monthhere + 1;
//        if (dayOfMonth < 10) {
//            daystr = "0" + dayOfMonth;
//        } else {
//            daystr = "" + dayOfMonth;
//        }
//        if (month < 10) {
//            monthstr = "0" + month;
//        } else {
//            monthstr = "" + month;
//        }
//        yearstr = "" + year;
//
//        yearstr = ""+year;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,dayOfMonth);
        dob = String.valueOf(calendar.getTimeInMillis());
        dobtv.setText(dayOfMonth+" "+monthName[month]+" "+(year%100));

    }
}
