package com.app.contactappsp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.contactappsp.Models.MyRemarkDetails;
import com.app.contactappsp.R;

import java.util.Calendar;
import java.util.List;

public class RemarkAdapterHome extends RecyclerView.Adapter<RemarkAdapterHome.RemarkViewHolder> {
    private List<MyRemarkDetails> contactVOList;
    private Context mContext;
    private OnItemClickListener mListener;
    String[] monthName = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public RemarkAdapterHome(List<MyRemarkDetails> contactVOList, Context mContext) {
        this.contactVOList = contactVOList;
        this.mContext = mContext;
    }

    @Override
    public RemarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_remark_view,parent, false);
        RemarkViewHolder contactViewHolder = new RemarkViewHolder(view);
        return contactViewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RemarkViewHolder holder, int position) {
        Log.d("TAG", "onBindViewHolder: "+contactVOList.size());
            if(contactVOList.get(position)!=null) {
//            if (contactVOList.get(position).getId().equals(String.valueOf(position))) {
                MyRemarkDetails contactVO = contactVOList.get(position);
                holder.descriptionTv.setText(contactVO.getDescription());
                holder.remark1Tv.setText(contactVO.getRemark1());
                holder.remark2Tv.setText(contactVO.getRemark2());

                String dateTemp = contactVO.getDate();
                if (!dateTemp.trim().equals("")) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(dateTemp));
                    holder.dateTv.setText("Date Commencing: " + calendar.get(Calendar.DAY_OF_MONTH) + " " + monthName[calendar.get(Calendar.MONTH)] + " " + (calendar.get(Calendar.YEAR) % 100));

                }
                holder.statusTv.setText("Status: " + contactVO.getStatus());

                if (!contactVO.getNotify().equals("0")) {
                    holder.notifyCB.setSelected(true);
                    holder.enableCB.setChecked(true);
                    String ap = "am";

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(contactVO.getNotify()));
                    if(calendar.get(Calendar.AM_PM) == Calendar.AM){
                        ap= "AM";
                    }
                    else {
                        ap="PM";
                    }

                    holder.alarmDateTv.setText("Date: " + calendar.get(Calendar.DAY_OF_MONTH) + " " + monthName[calendar.get(Calendar.MONTH)] + " " + (calendar.get(Calendar.YEAR)%100));
                    if(calendar.get(Calendar.MINUTE)<10){
                        holder.alarmTimeTv.setText("Time: " + calendar.get(Calendar.HOUR) + ":0" + calendar.get(Calendar.MINUTE) + " " + ap);
                    }
                    else {
                        holder.alarmTimeTv.setText("Time: " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + " " + ap);
                    }                }
                else {
                    holder.notifyCB.setSelected(false);
                    holder.enableCB.setChecked(false);
                }
            }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    @Override
    public int getItemCount() {
        return contactVOList.size();
    }

    public class RemarkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView descriptionTv, remark1Tv, remark2Tv, dateTv, statusTv,alarmDateTv,alarmTimeTv;
        ImageView notifyCB;
        CheckBox enableCB;

        public RemarkViewHolder(View itemView) {
            super(itemView);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            remark1Tv = itemView.findViewById(R.id.remark1Tv);
            remark2Tv = itemView.findViewById(R.id.remark2Tv);
            dateTv = itemView.findViewById(R.id.dateTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            notifyCB = itemView.findViewById(R.id.notifyCB);
            alarmDateTv = itemView.findViewById(R.id.alarmDateTv);
            alarmTimeTv = itemView.findViewById(R.id.alarmTimeTv);
            enableCB = itemView.findViewById(R.id.enableCB);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (mListener != null) {
                if (position != RecyclerView.NO_POSITION) {
                    mListener.setOnRemarkClickListener(position, v);
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (mListener != null) {
                if (position != RecyclerView.NO_POSITION) {
                    mListener.setOnRemarkLongClickListener(position);
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void setOnRemarkClickListener(int position, View v);
        void setOnRemarkLongClickListener(int position);
    }

    public void setOnRemarkClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
