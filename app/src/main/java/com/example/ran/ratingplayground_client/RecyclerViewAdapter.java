package com.example.ran.ratingplayground_client;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.ran.ratingplayground_client.model.ElementTO;
import com.example.ran.ratingplayground_client.utils.AppConstants;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {


    public interface OnItemClickListener {
        void onElementClicked(int position);
    }


    private Context mContext;
    private ArrayList<ElementTO> elements;
    private final OnItemClickListener listener;


    public RecyclerViewAdapter(Context mContext, OnItemClickListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        this.elements = new ArrayList<>();
    }


    public void bindElements(List<ElementTO> elements) {
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

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.image_not_found)
                .priority(Priority.HIGH);


        Map<String,Object> map = elements.get(position).getAttributes();
        if (map != null) {
            if (map.containsKey("image")) {
                String imageUrl = map.get("image").toString();
                try {
                Glide.with(mContext).load(new URL(imageUrl))
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(holder.imageView);
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            holder.imageView.setImageResource(R.drawable.image_not_found);
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