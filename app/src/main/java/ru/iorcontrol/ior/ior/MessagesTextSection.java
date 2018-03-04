package ru.iorcontrol.ior.ior;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import ru.iorcontrol.ior.ior.Model.Discussion;

/**
 * Created by alexeykazinets on 03/12/2017.
 */

public class MessagesTextSection extends StatelessSection {

    Discussion discussion;

    public MessagesTextSection(Discussion discussion) {
        // call constructor with layout resources for this Section header and items

        super(new SectionParameters.Builder(R.layout.message_text)
                .build());
        this.discussion = discussion;
    }

    @Override
    public int getContentItemsTotal() {
        return 1; // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new MessagesTextSectionViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessagesTextSectionViewHolder itemHolder = (MessagesTextSectionViewHolder) holder;

        // bind your view here
        itemHolder.message.setText(discussion.getMessage());
    }

    class MessagesTextSectionViewHolder extends RecyclerView.ViewHolder {

        private TextView message;

        public MessagesTextSectionViewHolder(View itemView) {
            super(itemView);

            message = (TextView) itemView.findViewById(R.id.text_message_bubble);
        }
    }
}
