package ru.iorcontrol.ior.ior.Recipient;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import ru.iorcontrol.ior.ior.GalleryClickListener;
import ru.iorcontrol.ior.ior.StatusDetails.HorizontalGalleryAdapter;
import ru.iorcontrol.ior.ior.R;

/**
 * Created by alexeykazinets on 30/11/2017.
 */
public class RecipientMediaSection extends StatelessSection {

    List<String> urls;
    HorizontalGalleryAdapter.TYPE type;
    Context context;
    GalleryClickListener clickListener;


    public RecipientMediaSection(Context context, List<String > urls, HorizontalGalleryAdapter.TYPE type, GalleryClickListener clickListener) {
        // call constructor with layout resources for this Section header and items
        super(new SectionParameters.Builder(R.layout.recyclerview_layout)
                .build());
        this.urls = urls;
        this.context = context;
        this.clickListener = clickListener;
        this.type = type;
    }

    @Override
    public int getContentItemsTotal() {
        return 1; // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new RecipientMediaSectionViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecipientMediaSectionViewHolder itemHolder = (RecipientMediaSectionViewHolder) holder;

        if (type == HorizontalGalleryAdapter.TYPE.IMAGEGALLERY) {
            itemHolder.mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            itemHolder.mRecyclerView.setLayoutManager(layoutManager);
            HorizontalGalleryAdapter imageadapter = new HorizontalGalleryAdapter(context, HorizontalGalleryAdapter.TYPE.IMAGEGALLERY, urls, position, clickListener);
            itemHolder.mRecyclerView.setAdapter(imageadapter);
        } else {
            itemHolder.mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            itemHolder.mRecyclerView.setLayoutManager(layoutManager);
            HorizontalGalleryAdapter videoadapter = new HorizontalGalleryAdapter(context, HorizontalGalleryAdapter.TYPE.VIDEOGALLERY, urls, position, clickListener);
            itemHolder.mRecyclerView.setAdapter(videoadapter);
        }
    }

    class RecipientMediaSectionViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView mRecyclerView;

        public RecipientMediaSectionViewHolder(View itemView) {
            super(itemView);

            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }
    }
}
