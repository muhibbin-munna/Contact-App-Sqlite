package com.app.contactappsp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.contactappsp.Adapters.ContactAdapter;
import com.app.contactappsp.Databases.DbHelper;
import com.app.contactappsp.Listener.ContactClickListner;
import com.app.contactappsp.Models.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

import static com.app.contactappsp.MainActivity.no_of_contact;

public class ContactListFragment extends Fragment implements ContactClickListner {


    IndexFastScrollRecyclerView recyclerView;
    static ArrayList<Contact> contacts = new ArrayList<>();
    ArrayList<Contact> searchList = new ArrayList<>();

    androidx.appcompat.widget.SearchView searchView;
    ContactAdapter adapter;
    TextView noContactDisplay;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        noContactDisplay = view.findViewById(R.id.noContactDisplay);
        recyclerView = view.findViewById(R.id.fast_scroller_recycler);
        searchView = view.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    loadRV(contacts);

                } else {
                    searchList.clear();
                    for (int i = 0; i < contacts.size(); i++) {
                        if(contacts.get(i).getFullName().toUpperCase().startsWith(newText.toUpperCase()) || contacts.get(i).getPhone().contains(newText)){
                            searchList.add(contacts.get(i));
                        }
                    }
                    loadRV(searchList);
                }
                return false;
            }
        });
        return view;
    }



    public ArrayList<Contact> getData(){
        contacts.clear();
        DbHelper helper = new DbHelper(getContext(),DbHelper.DB_NAME,null,DbHelper.DB_VERSION);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Contact> contacts = new ArrayList<>();

        String query = "SELECT * FROM "+DbHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
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

            }while (cursor.moveToNext());
        }
        return contacts;
    }

    public void onContactClick(Contact contact) {
        Intent intent = new Intent(getContext(), ContactDetailsActivity.class);
        intent.putExtra("contact",contact.getId());
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        contacts = getData();
        no_of_contact.setText(contacts.size()+" contacts");
        if(contacts.size()==0){
            noContactDisplay.setVisibility(View.VISIBLE);
            noContactDisplay.setText("No Contacts");
        }
        else {
            noContactDisplay.setVisibility(View.GONE);
            loadRV(contacts);
        }

    }
    private void loadRV(ArrayList<Contact> contactList) {
        Collections.sort(contactList, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o1.getFullName().toUpperCase().compareTo(o2.getFullName().toUpperCase());
            }
        });


        adapter = new ContactAdapter(getContext(), contactList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setIndexbarWidth(40);
        recyclerView.setIndexTextSize(12);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
    }
    public static ArrayList<Contact> getContactList() {
        return contacts;
    }
}