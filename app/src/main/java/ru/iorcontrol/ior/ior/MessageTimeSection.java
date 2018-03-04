package ru.iorcontrol.ior.ior;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by alexeykazinets on 04/02/2018.
 */

public class MessageTimeSection extends StatelessSection {

        String date;
        Context context;

        public MessageTimeSection(Context context, String date) {
            // call constructor with layout resources for this Section header and items

            super(new SectionParameters.Builder(R.layout.message_date)
                    .build());
            this.date = date;
            this.context = context;
        }

        @Override
        public int getContentItemsTotal() {
            return 1; // number of items of this section
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            // return a custom instance of ViewHolder for the items of this section
            return new MessagesDateSectionViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            MessagesDateSectionViewHolder itemHolder = (MessagesDateSectionViewHolder) holder;

            // bind your view here

            itemHolder.date.setText(date);

        }

        class MessagesDateSectionViewHolder extends RecyclerView.ViewHolder {

            private TextView date;

            public MessagesDateSectionViewHolder(View itemView) {
                super(itemView);

                date = (TextView) itemView.findViewById(R.id.message_date);
            }
        }
}
