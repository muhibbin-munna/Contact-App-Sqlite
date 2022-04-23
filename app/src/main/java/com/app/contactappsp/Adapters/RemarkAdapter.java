package com.app.contactappsp.Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import com.app.contactappsp.Utils.AMInterstitialAds;
import com.app.contactappsp.Utils.Constants;
import com.app.contactappsp.Utils.NetUtils;
import com.app.contactappsp.Utils.interfacess.onInterstitialAdsClose;

import java.util.Calendar;
import java.util.List;

public class RemarkAdapter extends RecyclerView.Adapter<RemarkAdapter.RemarkViewHolder> {
    private List<MyRemarkDetails> contactVOList;
    private Context mContext;
    private OnItemClickListener mListener;
    String[] monthName = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    AMInterstitialAds amInterstitialAds;

    public RemarkAdapter(List<MyRemarkDetails> contactVOList, Context mContext) {
        this.contactVOList = contactVOList;
        this.mContext = mContext;
        amInterstitialAds = new AMInterstitialAds(mContext, true);
    }

    @Override
    public RemarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_remark_view, parent, false);
        RemarkViewHolder contactViewHolder = new RemarkViewHolder(view);
        return contactViewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RemarkViewHolder holder, int position) {
        Log.d("TAG", "onBindViewHolder: "+contactVOList.size());
        if(position<contactVOList.size()) {
            if (contactVOList.get(position) != null) {
//            if (contactVOList.get(position).getId().equals(String.valueOf(position))) {
                MyRemarkDetails contactVO = contactVOList.get(position);
                if (!contactVO.getDescription().trim().equals("")) {
                    holder.descriptionTv.setText(contactVO.getDescription());
                }
                if (!contactVO.getRemark1().trim().equals("")) {
                    holder.remark1Tv.setText(contactVO.getRemark1());
                }
                if (!contactVO.getRemark2().trim().equals("")) {
                    holder.remark2Tv.setText(contactVO.getRemark2());
                }
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
                    if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
                        ap = "AM";
                    } else {
                        ap = "PM";
                    }
                    int hour =  calendar.get(Calendar.HOUR);
                    if(calendar.get(Calendar.HOUR)==0){
                        hour = 12;
                    }
                    holder.alarmDateTv.setText("Date: " + calendar.get(Calendar.DAY_OF_MONTH) + " " + monthName[calendar.get(Calendar.MONTH)] + " " + (calendar.get(Calendar.YEAR) % 100));
                    if (calendar.get(Calendar.MINUTE) < 10) {
                        holder.alarmTimeTv.setText("Time: " + hour + ":0" + calendar.get(Calendar.MINUTE) + " " + ap);
                    } else {
                        holder.alarmTimeTv.setText("Time: " + hour + ":" + calendar.get(Calendar.MINUTE) + " " + ap);
                    }
                } else {
                    holder.notifyCB.setSelected(false);
                    holder.enableCB.setChecked(false);
                }
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
        return 20;
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

            itemView.setOnLongClickListener(this);
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
            if (amInterstitialAds != null && NetUtils.isOnline(mContext) && !Constants.is_remarkAd_shown) {
                if (amInterstitialAds.isLoaded()) {
                    new AsyncTask<Void, Void, Void>() {
                        ProgressDialog dialog = new ProgressDialog(mContext);

                        @SuppressLint("StaticFieldLeak")
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();

                            dialog.setTitle("Showing Ads");
                            dialog.setMessage("Please Wait...");
                            dialog.setCancelable(false);
                            dialog.show();
                        }


                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                Thread.sleep(500);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }


                            //Show Ads Here
                            try {
                                if (amInterstitialAds != null) {
                                    amInterstitialAds.showFullScreenAds(new onInterstitialAdsClose() {
                                        public void onAdClosed() {

                                            Constants.is_remarkAd_shown = true;
                                            if (mListener != null) {
                                                if (position != RecyclerView.NO_POSITION) {
                                                    mListener.setOnRemarkLongClickListener(position);
                                                }
                                            }


                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }.execute();

                } else {
                    //Start Activity Here When Ads Not Loaded
                    if (mListener != null) {
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.setOnRemarkLongClickListener(position);
                        }
                    }
                }
            }
            else {
                //Start Activity Here When Offline
                if (mListener != null) {
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.setOnRemarkLongClickListener(position);
                    }
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
