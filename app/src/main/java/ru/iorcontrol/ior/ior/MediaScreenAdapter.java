package ru.iorcontrol.ior.ior;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by alexeykazinets on 29/11/2017.
 */

public class MediaScreenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Upload> urls;
    Context context;
    MediaScreenAdapterDeleteHandler deleteHandler;
    MediaScreenAdapterType type;

    enum MediaScreenAdapterType { VIDEO, IMAGE }

    public MediaScreenAdapter(Context context, List<Upload> urls, MediaScreenAdapterType type, MediaScreenAdapterDeleteHandler deleteHandler) {
        this.context = context;
        this.urls = urls;
        this.deleteHandler = deleteHandler;
        this.type = type;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        Button deleteButton;

        public ItemViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.media_item_image);
            deleteButton = v.findViewById(R.id.media_item_delete);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteHandler.delete(getAdapterPosition());
                }
            });
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v1 = inflater.inflate(R.layout.recyclerview_media_screen_item, parent, false);
        viewHolder = new ItemViewHolder(v1);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder vh = (ItemViewHolder) holder;

        if (this.type == MediaScreenAdapterType.VIDEO) {
            String uri = "@drawable/play_video";  // where myresource (without the extension) is the file
            int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
            Drawable res = context.getResources().getDrawable(imageResource);
            ((ItemViewHolder) holder).image.setImageDrawable(res);
        } else {
            Glide
                    .with(context.getApplicationContext())
                    .load(Settings.getInstance().getAPIHost() + urls.get(position).getUrl())
                    .into(((ItemViewHolder) holder).image);
        }
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

}
