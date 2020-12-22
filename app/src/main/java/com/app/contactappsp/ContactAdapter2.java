package com.app.contactappsp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter2 extends RecyclerView.Adapter<ContactAdapter2.ViewHolder> implements SectionIndexer {
    private final Context context;
    private final ArrayList<Contact> contacts;
    private final ContactClickListner listner;


    private ArrayList<Integer> mSectionPositions;

    public ContactAdapter2(Context context, ArrayList<Contact> contacts, ContactClickListner listner) {
        this.context = context;
        this.contacts = contacts;
        this.listner = listner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout layout= (RelativeLayout) inflater.inflate(R.layout.contact_list_items1,null);
        return new ViewHolder(layout);*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_contact_view,parent,false);
        ViewHolder holder = new ViewHolder(view,context,contacts);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Contact contact = contacts.get(position);
        holder.fName.setText(contact.getFullName());
        holder.number.setText(contact.getPhone());
        if(contact.getImage() == null){
            holder.imageView.setImageResource(R.drawable.contacts_icon);
        }
        else{
            byte [] arr = Base64.decode(contact.getImage(),Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr,0,arr.length);
            holder.imageView.setImageBitmap(bitmap);
        }

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>(26);
        mSectionPositions = new ArrayList<>(26);
        for (int i = 0, size = contacts.size(); i < size; i++) {
            if(!contacts.get(i).getFullName().equals("")) {
                String section = String.valueOf(contacts.get(i).getFullName().charAt(0)).toUpperCase();
                if (!sections.contains(section)) {
                    sections.add(section);
                    mSectionPositions.add(i);
                }
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView fName,number;
        ArrayList<Contact>contacts = new ArrayList<Contact>();
        Context context;
        public ViewHolder(View itemView,Context context,ArrayList<Contact> contacts) {
            super(itemView);
            this.contacts=contacts;
            this.context=context;
            itemView.setOnClickListener(this);


            imageView = itemView.findViewById(R.id.imageView);
            fName = itemView.findViewById(R.id.tvContactName);
            number = itemView.findViewById(R.id.tvPhoneNumber);
        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(context, ""+getAdapterPosition(), Toast.LENGTH_SHORT).show();
            int position = getAdapterPosition();
            Contact contact = this.contacts.get(position);
            Intent intent= new Intent(this.context, ContactDetailsActivity.class);
//            intent.putExtra("contact",contact.getId());
            intent.putExtra("contact",contact.getId());
            this.context.startActivity(intent);
        }
    }
}
