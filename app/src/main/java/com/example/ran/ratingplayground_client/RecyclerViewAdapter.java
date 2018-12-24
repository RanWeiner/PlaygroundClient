package com.example.ran.ratingplayground_client;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.ran.ratingplayground_client.model.Element;
import com.example.ran.ratingplayground_client.utils.AppConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {


    public interface OnItemClickListener {
        void onElementClicked(int position);
    }


    private Context mContext;
    private ArrayList<Element> elements;
    private final OnItemClickListener listener;


    public RecyclerViewAdapter(Context mContext, OnItemClickListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        this.elements = new ArrayList<>();
    }


    public void bindElements(List<Element> elements) {
        this.elements = new ArrayList<>(elements);
        notifyDataSetChanged();
    }




    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        holder.textView.setText(elements.get(position).getName());

        switch (elements.get(position).getType()) {
            case AppConstants.BILLBOARD_TYPE:
                holder.imageView.setImageResource(R.drawable.sign);
                break;
            case AppConstants.BOOK_TYPE:
                holder.imageView.setImageResource(R.drawable.book);
                break;
            case AppConstants.MOVIE_TYPE:
                holder.imageView.setImageResource(R.drawable.clapperboard);
                break;
        }
        holder.bind(position,listener);
    }



    @Override
    public int getItemCount() {
        return elements.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;


        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view_id);
            textView = (TextView)itemView.findViewById(R.id.text_view_id);
        }


        public void bind(final int position, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onElementClicked(position);
                }
            });

        }
    }
}