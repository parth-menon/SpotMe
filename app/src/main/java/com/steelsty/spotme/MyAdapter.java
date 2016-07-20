package com.steelsty.spotme;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private String[] mDataset1,mDataset2,mDataset3;


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView placeTextView,timeTextView,dateTextView;
        public ViewHolder(View v) {
            super(v);
            placeTextView = (TextView) v.findViewById(R.id.place);
            timeTextView= (TextView) v.findViewById(R.id.time);
            dateTextView = (TextView) v.findViewById(R.id.date);
        }
    }

    public MyAdapter(String[] myDataset1,String[] myDataset2,String[] myDataset3) {
        mDataset1 = myDataset1;
        mDataset2=myDataset2;
        mDataset3=myDataset3;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.placeTextView.setText(mDataset1[position]);
        holder.timeTextView.setText(mDataset2[position]);
        holder.dateTextView.setText(mDataset2[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset1.length;
    }
}