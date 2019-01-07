package com.example.ran.ratingplayground_client.activities.player;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.model.ActivityTO;

import java.util.List;

public class BillboardAdapter extends RecyclerView.Adapter<BillboardAdapter.MyViewHolder> {

        private List<ActivityTO> postList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView userTextView, messageTextView, dateTextView;

            public MyViewHolder(View view) {
                super(view);
                userTextView = (TextView) view.findViewById(R.id.author_text);
                messageTextView = (TextView) view.findViewById(R.id.post_text);
                dateTextView = (TextView) view.findViewById(R.id.date_text);
            }
        }


        public BillboardAdapter(List<ActivityTO> postList) {
            this.postList = postList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.billboard_item_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ActivityTO post = postList.get(position);
            holder.messageTextView.setText((String)post.getAttributes().get("message"));
            holder.userTextView.setText((String)post.getAttributes().get("username"));
            holder.dateTextView.setText((String)post.getAttributes().get("date"));
        }

        @Override
        public int getItemCount() {
            return postList.size();
        }
}
