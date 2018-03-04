package ru.iorcontrol.ior.ior;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by alexeykazinets on 27/11/2017.
 */

public class HorizontalGalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum TYPE { IMAGEGALLERY, VIDEOGALLERY, }
    private TYPE type;
    private List<String> urls;
    private Context context;
    private GalleryClickListener clickListener;
    private int position;

    public HorizontalGalleryAdapter(Context context, TYPE type, List<String> urls, int position, GalleryClickListener clickListener) {
        this.type = type;
        this.urls = urls;
        this.context = context;
        this.position = position;
        this.clickListener = clickListener;
    }

    private class CellViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public CellViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imagefield);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.fieldClicked(position, getAdapterPosition(), type, urls.get(getAdapterPosition()));
                }
            });

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            default: {
                View v1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycletview_gallery, viewGroup, false);
                return new CellViewHolder(v1);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            default: {
                if (this.type == TYPE.IMAGEGALLERY) {
                    Glide
                            .with(context.getApplicationContext())
                            .load(Settings.getInstance().getAPIHost() + urls.get(position))
                            .into(((CellViewHolder) holder).image);
                    break;
                } else {

                    String uri = "@drawable/play_video";  // where myresource (without the extension) is the file
                    int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                    Drawable res = context.getResources().getDrawable(imageResource);
                    ((CellViewHolder) holder).image.setImageDrawable(res);

                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    void update() {
        notifyDataSetChanged();
    }

}
