package com.eladfinish.googlesheet2json;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<String> mData;
    private ArrayList<String> mUrls;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView flagImage;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.country_name);
            flagImage = itemView.findViewById(R.id.flag_image);
        }
    }

    MyAdapter(Context context, ArrayList<String> data, ArrayList<String> urls) {
        mContext = context;
        mData = data;
        mUrls = urls;
    }

    @NotNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_flag, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull MyViewHolder holder, int position) {
        holder.textView.setText(mData.get(position));

        Glide.with(mContext)
                .load(mUrls.get(position))
                .placeholder(R.drawable.ic_flag)
                .into(holder.flagImage);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}