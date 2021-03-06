package com.masaworld.catmap.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.masaworld.catmap.Config;
import com.masaworld.catmap.R;
import com.masaworld.catmap.data.model.ImageInfo;

import java.util.ArrayList;
import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ImageInfo> images;
    private ImageClickListener listener;

    public void setImageClickeListener(ImageClickListener listener) {
        this.listener = listener;
    }

    public void setImages(List<ImageInfo> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_list_element, parent, false);
        return new ImageListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ImageListViewHolder vh = (ImageListViewHolder) holder;
        vh.bind(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ImageListViewHolder vh = (ImageListViewHolder) holder;
        vh.imageView.setImageBitmap(null);
    }

    public ImageListAdapter(Context context) {
        this.context = context;
        images = new ArrayList<>();
    }

    class ImageListViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageInfo imageInfo;

        void bind(ImageInfo imageInfo) {
            this.imageInfo = imageInfo;
            String url = Config.BASE_URL + imageInfo.thumbnail;
            Glide.with(context).load(url).into(imageView);
            imageView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onImageClicked(Config.BASE_URL + imageInfo.raw_image);
                }
            });
        }

        ImageListViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_list_element_image_view);
        }
    }

    public interface ImageClickListener {
        public void onImageClicked(String imageUrl);
    }

}
