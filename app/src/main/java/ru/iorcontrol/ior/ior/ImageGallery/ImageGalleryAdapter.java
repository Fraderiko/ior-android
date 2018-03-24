package ru.iorcontrol.ior.ior.ImageGallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ru.iorcontrol.ior.ior.R;
import ru.iorcontrol.ior.ior.Settings;

/**
 * Created by alexeykazinets on 28/11/2017.
 */

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.viewHolder> {

    Context context;
    ArrayList<String> urls;

    ImageGalleryAdapter(Context context, ArrayList<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public viewHolder(View view) {
            super(view);

            image = (ImageView) view.findViewById(R.id.image_gallery_image);

        }
    }

    @Override
    public ImageGalleryAdapter.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_gallery_item, parent, false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ImageGalleryAdapter.viewHolder holder, int position) {
        Glide
                .with(context.getApplicationContext())
                .load(Settings.getInstance().getAPIHost() + urls.get(position))
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }
}
