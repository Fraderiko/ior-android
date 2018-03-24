package ru.iorcontrol.ior.ior.StatusDetails;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.iorcontrol.ior.ior.GalleryClickListener;
import ru.iorcontrol.ior.ior.Model.Status;
import ru.iorcontrol.ior.ior.R;

/**
 * Created by alexeykazinets on 27/11/2017.
 */

public class StatusDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    Status status;
    GalleryClickListener clickListener;

    StatusDetailsAdapter(Status status, Context context, GalleryClickListener clickListener) {
        this.status = status;
        this.context = context;
        this.clickListener = clickListener;
    }

    private final int TEXT = 0, IMAGE = 1, VIDEO = 2;

    @Override
    public int getItemViewType(int position) {
        if (status.getFields().get(position).type.equals("text") || status.getFields().get(position).type.equals("date") || status.getFields().get(position).type.equals("digit") || status.getFields().get(position).type.equals("time")) {
            return TEXT;
        } else if (status.getFields().get(position).type.equals("video")) {
            return VIDEO;
        } else if (status.getFields().get(position).type.equals("image")) {
            return IMAGE;
        }
        return -1;
    }

    private class GalleryCellViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView mRecyclerView;

        public GalleryCellViewHolder(View itemView) {
            super(itemView);

            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }
    }

    private class TextCellViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView value;

        public TextCellViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.textfield_title);
            value = (TextView) itemView.findViewById(R.id.textfield_value);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TEXT:
                View v1 = inflater.inflate(R.layout.field_text, parent, false);
                viewHolder = new TextCellViewHolder(v1);
                break;

            case VIDEO:
                View v2 = inflater.inflate(R.layout.recyclerview_layout, parent, false);
                viewHolder = new GalleryCellViewHolder(v2);
                break;

            case IMAGE:
                View v3 = inflater.inflate(R.layout.recyclerview_layout, parent, false);
                viewHolder = new GalleryCellViewHolder(v3);
                break;

            default:
                View v4 = inflater.inflate(R.layout.field_text, parent, false);
                viewHolder = new GalleryCellViewHolder(v4);
                break;

        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {

            case TEXT: {
                TextCellViewHolder cellViewHolder = (TextCellViewHolder) holder;
                ((TextCellViewHolder) holder).title.setText(status.getFields().get(position).name);
                ((TextCellViewHolder) holder).value.setText(status.getFields().get(position).value);
                break;
            }

            case IMAGE: {
                GalleryCellViewHolder cellViewHolder = (GalleryCellViewHolder) holder;

                cellViewHolder.mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                cellViewHolder.mRecyclerView.setLayoutManager(layoutManager);
                HorizontalGalleryAdapter adapter = new HorizontalGalleryAdapter(context, HorizontalGalleryAdapter.TYPE.IMAGEGALLERY, status.getFields().get(position).media, position, clickListener);
                cellViewHolder.mRecyclerView.setAdapter(adapter);
                break;
            }

            case VIDEO: {
                GalleryCellViewHolder cellViewHolder = (GalleryCellViewHolder) holder;

                cellViewHolder.mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                cellViewHolder.mRecyclerView.setLayoutManager(layoutManager);
                HorizontalGalleryAdapter adapter = new HorizontalGalleryAdapter(context, HorizontalGalleryAdapter.TYPE.VIDEOGALLERY, status.getFields().get(position).media, position, clickListener);
                cellViewHolder.mRecyclerView.setAdapter(adapter);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {

        return status.getFields().size();
    }
}

