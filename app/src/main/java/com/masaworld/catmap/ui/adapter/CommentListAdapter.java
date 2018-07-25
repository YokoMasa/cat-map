package com.masaworld.catmap.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.masaworld.catmap.R;
import com.masaworld.catmap.data.model.CatComment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter {

    private List<CatComment> comments = new ArrayList<>();
    private Context context;

    public void setComments(List<CatComment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_list_element, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentViewHolder vh = (CommentViewHolder) holder;
        CatComment comment = comments.get(position);
        vh.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public CommentListAdapter(Context context) {
        this.context = context;
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        private final String DATE_FORMAT = "yyyy/MM/dd";
        TextView comment;
        TextView info;

        void bind(CatComment comment) {
            this.comment.setText(comment.comment);
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            String info = comment.user + " | " + dateFormat.format(comment.created_at);
            this.info.setText(info);
        }

        CommentViewHolder(View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.comment_comment);
            info = itemView.findViewById(R.id.comment_info);
        }
    }
}
