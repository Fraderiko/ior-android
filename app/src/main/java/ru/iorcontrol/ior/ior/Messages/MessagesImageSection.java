package ru.iorcontrol.ior.ior.Messages;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import ru.iorcontrol.ior.ior.R;
import ru.iorcontrol.ior.ior.Settings;

/**
 * Created by alexeykazinets on 03/12/2017.
 */

public class MessagesImageSection extends StatelessSection {

    String url;
    Context context;
    MEDIATYPE type;
    MessageClickHandler clickHandler;

    enum MEDIATYPE { IMAGE, VIDEO };

    public MessagesImageSection(Context context, MEDIATYPE type, String url, MessageClickHandler clickHandler) {
        // call constructor with layout resources for this Section header and items

        super(new SectionParameters.Builder(R.layout.message_image)
                .build());
        this.url = url;
        this.context = context;
        this.type = type;
        this.clickHandler = clickHandler;
    }

    @Override
    public int getContentItemsTotal() {
        return 1; // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new MessagesImageSectionViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessagesImageSectionViewHolder itemHolder = (MessagesImageSectionViewHolder) holder;

        // bind your view here

        if (this.type == MEDIATYPE.VIDEO) {
            String uri = "@drawable/play_video";  // where myresource (without the extension) is the file
            int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
            Drawable res = context.getResources().getDrawable(imageResource);
            itemHolder.message.setImageDrawable(res);
        } else {
            Glide
                    .with(context.getApplicationContext())
                    .load(Settings.getInstance().getAPIHost() + url)
                    .into((itemHolder.message));
        }

        itemHolder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickHandler.messageClicked(url, type);
            }
        });

    }

    class MessagesImageSectionViewHolder extends RecyclerView.ViewHolder {

        private ImageView message;

        public MessagesImageSectionViewHolder(View itemView) {
            super(itemView);

            message = (ImageView) itemView.findViewById(R.id.image_message_bubble);
        }
    }
}
