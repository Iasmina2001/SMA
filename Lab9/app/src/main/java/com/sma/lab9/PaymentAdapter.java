package com.sma.lab9;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sma.lab9.ui.Payment;
import com.sma.lab9.ui.PaymentType;
import java.util.List;


public class PaymentAdapter extends ArrayAdapter<Payment> {

    private Context context;
    private List<Payment> payments;
    private int layoutResID;

    public PaymentAdapter(Context context, int layoutResourceID, List<Payment> payments) {
        super(context, layoutResourceID, payments);
        this.context = context;
        this.payments = payments;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.tIndex = view.findViewById(R.id.tIndex);
            itemHolder.tName = view.findViewById(R.id.tName);
            itemHolder.lHeader = view.findViewById(R.id.lHeader);
            itemHolder.tDate = view.findViewById(R.id.tDate);
            itemHolder.tTime = view.findViewById(R.id.tTime);
            itemHolder.tCost = view.findViewById(R.id.tCost);
            itemHolder.tType = view.findViewById(R.id.tType);
            itemHolder.iEdit = view.findViewById(R.id.iEdit);
            itemHolder.iDelete = view.findViewById(R.id.iDelete);

            view.setTag(itemHolder);

        } else {
            itemHolder = (ItemHolder) view.getTag();
        }

        // current values of payment
        final Payment pItem = payments.get(position);
        String timestamp = pItem.getTimestamp();
        String name = pItem.getName();
        String type = pItem.getType();
        double cost = pItem.getCost();

        String[] date_time = timestamp.split("\\s");
        String date = date_time[0];
        String time = date_time[1];

        itemHolder.tIndex.setText(String.valueOf(position + 1));
        itemHolder.tName.setText(pItem.getName());
        itemHolder.lHeader.setBackgroundColor(PaymentType.getColorFromPaymentType(pItem.getType()));
        itemHolder.tDate.setText(date);
        itemHolder.tTime.setText(time);
        itemHolder.tCost.setText(pItem.getCost() + " LEI");
        itemHolder.tType.setText(pItem.getType());

        itemHolder.iEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ... edit payment at "position"
                edit(timestamp, name, cost, type);
            }
        });

        itemHolder.iDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ... delete payment at "position"
                delete(timestamp);
            }
        });

        return view;
    }

    private static class ItemHolder {
        TextView tIndex;
        TextView tName;
        RelativeLayout lHeader;
        TextView tDate, tTime;
        TextView tCost, tType;
        ImageView iEdit, iDelete;
    }

    private void delete(String timestamp) {
        AppState.get().getDatabaseReference().child("wallet").child(timestamp).removeValue();
    }

    private void edit(String timestamp, String name, double cost, String type) {
        Payment currentPayment = new Payment(cost, name, type, timestamp);
        AppState.get().setCurrentPayment(currentPayment);
        ((Activity)context).startActivity(new Intent(context.getApplicationContext(), AddPaymentActivity.class));
    }
}