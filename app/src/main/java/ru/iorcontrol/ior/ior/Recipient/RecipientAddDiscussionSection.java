package ru.iorcontrol.ior.ior.Recipient;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import ru.iorcontrol.ior.ior.GalleryClickListener;
import ru.iorcontrol.ior.ior.StatusDetails.HorizontalGalleryAdapter;
import ru.iorcontrol.ior.ior.R;

/**
 * Created by alexeykazinets on 01/12/2017.
 */

public class RecipientAddDiscussionSection extends StatelessSection {

    RecepientCreateDiscussionHandler clickHandler;
    RecepientCreateDiscussionAddVideoHandler addVideoHandler;
    RecepientCreateDiscussionAddImageHandler addImageHandler;
    RecepientCreateDiscussionMediaHandler mediaHandler;

    GalleryClickListener galleryClickListener;

    List<String> video_urls;
    List<String> image_urls;

    String message;

    Context context;

    HorizontalGalleryAdapter videoadapter;
    HorizontalGalleryAdapter imageadapter;

    public RecipientAddDiscussionSection(Context context, RecepientCreateDiscussionHandler clickHandler, RecepientCreateDiscussionAddVideoHandler addVideoHandler, RecepientCreateDiscussionAddImageHandler addImageHandler, GalleryClickListener galleryClickListener, RecepientCreateDiscussionMediaHandler mediaHandler, List<String> video_urls, List<String> image_urls, String message) {
        // call constructor with layout resources for this Section header and items
        super(new SectionParameters.Builder(R.layout.field_recipient_add_discussion)
                .headerResourceId(R.layout.section_header)
                .footerResourceId(R.layout.section_header)
                .build());
        this.clickHandler = clickHandler;
        this.addImageHandler = addImageHandler;
        this.addVideoHandler = addVideoHandler;
        this.context = context;
        this.galleryClickListener = galleryClickListener;
        this.image_urls = image_urls;
        this.video_urls = video_urls;
        this.mediaHandler = mediaHandler;
        this.message = message;
    }

    @Override
    public int getContentItemsTotal() {
        return 1; // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new RecipientAddDiscussionViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RecipientAddDiscussionViewHolder itemHolder = (RecipientAddDiscussionViewHolder) holder;

        ((RecipientAddDiscussionViewHolder) holder).button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickHandler.createDiscussion(((RecipientAddDiscussionViewHolder) itemHolder).text.getText().toString());
            }
        });

        ((RecipientAddDiscussionViewHolder) holder).add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageHandler.addImageClicked(itemHolder.text.getText().toString());
            }
        });

        ((RecipientAddDiscussionViewHolder) holder).add_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVideoHandler.addVideoClicked(itemHolder.text.getText().toString());
            }
        });


        itemHolder.imageRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        itemHolder.imageRecyclerView.setLayoutManager(layoutManager);
        imageadapter = new HorizontalGalleryAdapter(context, HorizontalGalleryAdapter.TYPE.IMAGEGALLERY, image_urls, position, galleryClickListener);
        itemHolder.imageRecyclerView.setAdapter(imageadapter);


        itemHolder.videoRecyclerView.setHasFixedSize(true);
        LinearLayoutManager videolayoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        itemHolder.videoRecyclerView.setLayoutManager(videolayoutManager);
        videoadapter = new HorizontalGalleryAdapter(context, HorizontalGalleryAdapter.TYPE.VIDEOGALLERY, video_urls, position, galleryClickListener);
        itemHolder.videoRecyclerView.setAdapter(videoadapter);

        itemHolder.imageRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaHandler.mediaClicked(HorizontalGalleryAdapter.TYPE.IMAGEGALLERY);
            }
        });

        itemHolder.videoRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaHandler.mediaClicked(HorizontalGalleryAdapter.TYPE.VIDEOGALLERY);
            }
        });

        itemHolder.text.setText(message);
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        HeaderViewHolder(View view) {
            super(view);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View view) {
            super(view);
        }
    }

    class RecipientAddDiscussionViewHolder extends RecyclerView.ViewHolder {

        private EditText text;
        private Button button;
        private Button add_video;
        private Button add_image;
        private RecyclerView imageRecyclerView;
        private RecyclerView videoRecyclerView;

        public RecipientAddDiscussionViewHolder(View itemView) {
            super(itemView);

            text = (EditText) itemView.findViewById(R.id.textfield_discussion);
            button = (Button) itemView.findViewById(R.id.button_discussion);
            add_video = (Button) itemView.findViewById(R.id.add_video);
            add_image = (Button) itemView.findViewById(R.id.add_image);
            imageRecyclerView = (RecyclerView) itemView.findViewById(R.id.recipient_image_recyclerview);
            videoRecyclerView = (RecyclerView) itemView.findViewById(R.id.recipient_video_recyclerview);
        }
    }

}
