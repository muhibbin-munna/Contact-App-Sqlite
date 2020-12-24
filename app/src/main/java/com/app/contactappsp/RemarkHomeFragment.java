package com.app.contactappsp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RemarkHomeFragment extends Fragment implements RemarkAdapterHome.OnItemClickListener {
    RecyclerView remarkRV;
    List<MyRemarkDetails> userList = new ArrayList<>();
    MyDatabaseHelper myDatabaseHelper;
    RemarkAdapterHome remarkAdapterHome;
    int todayDay, todayMonth, todayYear;
    TextView noNotificationDisplay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_remark_home, container, false);
        noNotificationDisplay = view.findViewById(R.id.noNotificationDisplay);
        remarkRV = view.findViewById(R.id.remarkRvHome);
        myDatabaseHelper = new MyDatabaseHelper(getContext());
        SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();

        Calendar calendar = Calendar.getInstance();
        todayYear = calendar.get(Calendar.YEAR);
        todayMonth = calendar.get(Calendar.MONTH);
        todayDay = calendar.get(Calendar.DAY_OF_MONTH);

        return view;
    }

    private void loadRemarks() {
        userList.clear();
        Cursor cursor = myDatabaseHelper.read_all_remarks();
        if (cursor.getCount() != 0) {
            noNotificationDisplay.setVisibility(View.GONE);
            while (cursor.moveToNext()) {
                MyRemarkDetails contactDetails = new MyRemarkDetails();
                contactDetails.setId(cursor.getString(0));
                contactDetails.setContactId(cursor.getString(1));
                contactDetails.setEvent(cursor.getString(2));
                contactDetails.setRow(cursor.getString(3));
                contactDetails.setDescription(cursor.getString(4));
                contactDetails.setRemark1(cursor.getString(5));
                contactDetails.setRemark2(cursor.getString(6));
                contactDetails.setNotify(cursor.getString(7));
                contactDetails.setDate(cursor.getString(8));
                contactDetails.setStatus(cursor.getString(9));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(contactDetails.getNotify()));
                if (calendar.get(Calendar.YEAR) == todayYear) {
                    if (calendar.get(Calendar.DAY_OF_MONTH) == todayDay && calendar.get(Calendar.MONTH) == todayMonth) {
                        userList.add(contactDetails);
                    }
                }

            }
            Log.d("TAG", "load: " + userList.size());
            cursor.close();
            Collections.sort(
                    userList,
                    new Comparator<MyRemarkDetails>() {
                        public int compare(MyRemarkDetails lhs, MyRemarkDetails rhs) {
                            return lhs.getNotify().compareTo(rhs.getNotify());
                        }
                    }
            );
            remarkAdapterHome = new RemarkAdapterHome(userList, getContext());
            remarkRV.setLayoutManager(new LinearLayoutManager(getContext()));
            remarkRV.setAdapter(remarkAdapterHome);
            remarkAdapterHome.setOnRemarkClickListener(this);
            if (userList.size() == 0) {
                {
                    noNotificationDisplay.setText("No Notification");
                    noNotificationDisplay.setVisibility(View.VISIBLE);
                }
            }
        } else {
            noNotificationDisplay.setText("No Notification");
            noNotificationDisplay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setOnRemarkClickListener(int position, View v) {
        Intent intent = new Intent(getContext(), ContactDetailsActivity.class);
        int data = Integer.parseInt(userList.get(position).getContactId());
        int event = Integer.parseInt(userList.get(position).getEvent());
        int row = Integer.parseInt(userList.get(position).getRow());
        intent.putExtra("contact", data);
        intent.putExtra("event", event);
        intent.putExtra("row", 1);
        startActivity(intent);

    }

    @Override
    public void setOnRemarkLongClickListener(int position) {

    }


    @Override
    public void onResume() {
        super.onResume();
        loadRemarks();
    }
}