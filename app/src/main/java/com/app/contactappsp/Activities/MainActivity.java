package com.app.contactappsp.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.app.contactappsp.ContactListFragment;
import com.app.contactappsp.Databases.CSVWriter;
import com.app.contactappsp.Databases.DbHelper;
import com.app.contactappsp.Databases.MyDatabaseHelper;
import com.app.contactappsp.EditTextV2;
import com.app.contactappsp.Models.Contact;
import com.app.contactappsp.Models.Utility;
import com.app.contactappsp.R;
import com.app.contactappsp.RemarkHomeFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener, PopupMenu.OnMenuItemClickListener {
    EditTextV2 name, remark1, remark2;
    ImageButton menu;
    String TAG = "MainActivity";
    @SuppressLint("StaticFieldLeak")
    public static TextView no_of_contact;
    SharedPreferences sharedpreferences;
    CircleImageView ppImageView;
    FloatingActionButton addContactFabButton;
    private String userChoosenTask;
    ArrayList<Contact> contacts = new ArrayList<>();
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1, IMPORT_CONTACTS = 2, IMPORT_REMARK = 3;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.nameEditText);
        remark1 = findViewById(R.id.remarkEditText1);
        remark2 = findViewById(R.id.remarkEditText2);
        menu = findViewById(R.id.menu);
        ppImageView = findViewById(R.id.ppImageView);
        no_of_contact = findViewById(R.id.no_of_contact);
        addContactFabButton = findViewById(R.id.addContactFabButton);
        addContactFabButton.setOnClickListener(this);
        mAdView = findViewById(R.id.adViewMain);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
        ppImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Fragment contactListFragment = new ContactListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contactFrame, contactListFragment, "pframe")
                .commit();
        Fragment remarkHomeFragment = new RemarkHomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.remarkFrame, remarkHomeFragment, "rframe")
                .commit();
        contacts = getData();

        sharedpreferences = getSharedPreferences("com.app.contactapp", Context.MODE_PRIVATE);
        String namePref = sharedpreferences.getString("name", "Enter Name");
        String remark1Pref = sharedpreferences.getString("remark1", "Remark");
        String remark2Pref = sharedpreferences.getString("remark2", "Remark");

        String theme = sharedpreferences.getString("theme", "light");
        if (theme.equals("light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        loadImageFromStorage();

        name.setText(namePref);
        remark1.setText(remark1Pref);
        remark2.setText(remark2Pref);

        name.setOnEditorActionListener(this);
        remark1.setOnEditorActionListener(this);
        remark2.setOnEditorActionListener(this);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("name", String.valueOf(s));
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        remark1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("remark1", String.valueOf(s));
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        remark2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("remark2", String.valueOf(s));
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public ArrayList<Contact> getData() {
        contacts.clear();
        DbHelper helper = new DbHelper(this, DbHelper.DB_NAME, null, DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Contact> contacts = new ArrayList<>();

        String query = "SELECT * FROM " + DbHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
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
        return contacts;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(MainActivity.this);

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
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.main_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export_contact:
                boolean result = Utility.checkPermission(MainActivity.this);
                if (result) {
                    exportDB();
                }
                return true;
            case R.id.import_contact:
                result = Utility.checkPermission(MainActivity.this);
                if (result) {
                    importDb();
                }
                return true;
            case R.id.export_event:
                result = Utility.checkPermission(MainActivity.this);
                if (result) {
                    exportRemark();
                }
                return true;
            case R.id.import_event:
                result = Utility.checkPermission(MainActivity.this);
                if (result) {
                    importRemark();
                }
                return true;
            case R.id.item3:
                String[] themes = {"Light", "Dark"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select theme");
                builder.setItems(themes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("theme", "light");
                            editor.apply();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("theme", "night");
                            editor.apply();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        }
                    }
                });
                builder.show();
                return true;
            case R.id.item4:
                Toast.makeText(this, "Item 4 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item5:
                Toast.makeText(this, "Item 5 clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    private void importRemark() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimetypes = {"text/csv", "text/comma-separated-values", "application/csv"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select File"), IMPORT_REMARK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportRemark() {
        Boolean success = false;
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        dialog.show();
        MyDatabaseHelper dbhelper = new MyDatabaseHelper(this);
        File exportDir = new File(Environment.getExternalStorageDirectory(), "/FinCard/");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        String backupDBPath = "event.csv";
        File file = new File(exportDir, backupDBPath);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            Cursor curCSV = dbhelper.raw();
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                String arrStr[] = null;
                String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                for (int i = 0; i < curCSV.getColumnNames().length; i++) {
                    mySecondStringArray[i] = curCSV.getString(i);
                }
                csvWrite.writeNext(mySecondStringArray);
            }
            csvWrite.close();
            curCSV.close();
            success = true;
        } catch (IOException e) {
            success = false;
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (success) {
            Toast.makeText(MainActivity.this, "Export successful! to dir/FinCard/" + backupDBPath, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(MainActivity.this, "Export failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void importDb() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimetypes = {"text/csv", "text/comma-separated-values", "application/csv"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select File"), IMPORT_CONTACTS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void exportDB() {
        Boolean success = false;
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        dialog.show();
        DbHelper dbhelper = new DbHelper(this, DbHelper.DB_NAME, null, DbHelper.DB_VERSION);
        File exportDir = new File(Environment.getExternalStorageDirectory(), "/FinCard/");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        String backupDBPath = "contacts.csv";
        File file = new File(exportDir, backupDBPath);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            Cursor curCSV = dbhelper.raw();
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                String arrStr[] = null;
                String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                for (int i = 0; i < curCSV.getColumnNames().length; i++) {
                    mySecondStringArray[i] = curCSV.getString(i);
                }
                csvWrite.writeNext(mySecondStringArray);
            }
            csvWrite.close();
            curCSV.close();
            success = true;
        } catch (IOException e) {
            success = false;
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (success) {
            Toast.makeText(MainActivity.this, "Export successful! to dir/FinCard" + backupDBPath, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(MainActivity.this, "Export failed", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            switch (v.getId()) {
                case R.id.nameEditText:
                    name.setFocusable(false);
                    name.setFocusableInTouchMode(true);
                    break;
                case R.id.remarkEditText1:
                    remark1.setFocusable(false);
                    remark1.setFocusableInTouchMode(true);
                    break;
                case R.id.remarkEditText2:
                    remark2.setFocusable(false);
                    remark2.setFocusableInTouchMode(true);
                    break;
            }
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addContactFabButton) {
            startActivity(new Intent(MainActivity.this, AddActivity.class));
        }
    }


    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (reqCode == REQUEST_CAMERA) {
                Log.d(TAG, "onActivityResult: REQUEST_CAMERA");
                onCaptureImageResult(data);
            } else if (reqCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (reqCode == IMPORT_CONTACTS) {
                if (data != null && data.getData() != null) {
                    String filePath = data.getData().getPath();
                    if (filePath.contains("/root_path")) {
                        filePath = filePath.replace("/root_path", "");
                    } else if (filePath.contains("/document/primary:")) {
                        filePath = filePath.replace("/document/primary:", "");
                        filePath = Environment.getExternalStorageDirectory() + "/" + filePath;
                    }
                    Log.d(TAG, "onActivityResult: " + filePath);
                    File csvFile = new File(filePath);
                    if (csvFile.exists()) {
                        try {
                            DbHelper helper = new DbHelper(this, DbHelper.DB_NAME, null, DbHelper.DB_VERSION);
                            SQLiteDatabase db = helper.getWritableDatabase();
                            CSVReader csvReader = new CSVReader(new FileReader(csvFile.getAbsolutePath()));
                            String[] nextLine;
                            while ((nextLine = csvReader.readNext()) != null) {
                                ContentValues values = new ContentValues();
                                values.put(DbHelper.COLUMN_1, nextLine[0]);
                                values.put(DbHelper.COLUMN_2, nextLine[1]);
                                values.put(DbHelper.COLUMN_3, nextLine[2]);
                                values.put(DbHelper.COLUMN_4, nextLine[3]);
                                values.put(DbHelper.COLUMN_5, nextLine[4]);
                                values.put(DbHelper.COLUMN_6, nextLine[5]);
                                values.put(DbHelper.COLUMN_7, nextLine[6]);
                                values.put(DbHelper.COLUMN_8, nextLine[7]);
                                values.put(DbHelper.COLUMN_9, nextLine[8]);
                                values.put(DbHelper.COLUMN_10, nextLine[9]);
                                values.put(DbHelper.COLUMN_11, nextLine[10]);
                                long insertId = db.insert(DbHelper.TABLE_NAME, null, values);
                            }
                            Toast.makeText(this, "Imported Successfully..", Toast.LENGTH_SHORT).show();
                            db.close();
                        } catch (IOException e) {
                            Toast.makeText(this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        } catch (CsvValidationException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "Failed to get data", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (reqCode == IMPORT_REMARK) {
                if (data != null && data.getData() != null) {
                    String filePath = data.getData().getPath();
                    if (filePath.contains("/root_path")) {
                        filePath = filePath.replace("/root_path", "");
                    } else if (filePath.contains("/document/primary:")) {
                        filePath = filePath.replace("/document/primary:", "");
                        filePath = Environment.getExternalStorageDirectory() + "/" + filePath;
                    }
                    Log.d(TAG, "onActivityResult: " + filePath);
                    File csvFile = new File(filePath);
                    if (csvFile.exists()) {
                        try {
                            MyDatabaseHelper helper = new MyDatabaseHelper(this);
                            SQLiteDatabase db = helper.getWritableDatabase();
                            CSVReader csvReader = new CSVReader(new FileReader(csvFile.getAbsolutePath()));
                            String[] nextLine;
                            while ((nextLine = csvReader.readNext()) != null) {
                                ContentValues values = new ContentValues();
                                values.put(MyDatabaseHelper.KEY_ID, nextLine[0]);
                                values.put(MyDatabaseHelper.CONTACT_ID, nextLine[1]);
                                values.put(MyDatabaseHelper.EVENT, nextLine[2]);
                                values.put(MyDatabaseHelper.ROW, nextLine[3]);
                                values.put(MyDatabaseHelper.DES, nextLine[4]);
                                values.put(MyDatabaseHelper.REM_1, nextLine[5]);
                                values.put(MyDatabaseHelper.REM_2, nextLine[6]);
                                values.put(MyDatabaseHelper.NOTIFY, nextLine[7]);
                                values.put(MyDatabaseHelper.DATE, nextLine[8]);
                                values.put(MyDatabaseHelper.STATUS, nextLine[9]);
                                long insertId = db.insert(MyDatabaseHelper.TABLE1_NAME, null, values);
                            }
                            Toast.makeText(this, "Imported Successfully..", Toast.LENGTH_SHORT).show();
                            db.close();
                        } catch (IOException e) {
                            Toast.makeText(this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        } catch (CsvValidationException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "Failed to get data", Toast.LENGTH_SHORT).show();
                    }

                }
            }
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
        ppImageView.setImageBitmap(thumbnail);
        saveToInternalStorage(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Glide.with(this).load(data.getData()).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                ppImageView.setImageDrawable(resource);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bm = ((BitmapDrawable) ppImageView.getDrawable()).getBitmap();
                if (bm.getWidth() > bm.getHeight()) {
                    int ratio = bm.getWidth() / bm.getHeight();
                    bm = Bitmap.createScaledBitmap(bm, 200 * ratio, 200, true);
                } else {
                    int ratio = bm.getHeight() / bm.getWidth();
                    bm = Bitmap.createScaledBitmap(bm, 200, 200 * ratio, true);
                }
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                saveToInternalStorage(bm);
            }
        });

    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "profile.jpg");
        if(mypath.exists())
        {
            mypath.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "saveToInternalStorage: " + directory.getAbsolutePath());
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage() {
        String path = getApplicationContext().getFilesDir().getParentFile().getPath() + "/app_imageDir";
        Glide.with(this).load(new File(path,"profile.jpg"))
                .placeholder(R.drawable.contacts_icon)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(ppImageView);
    }
}