package ru.iorcontrol.ior.ior.Recipient;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import ru.iorcontrol.ior.ior.R;

/**
 * Created by alexeykazinets on 01/12/2017.
 */

public class RecipientDiscussionCountSection extends StatelessSection {

    int discussionCount;
    RecipientDiscussionCountClickHandler clickHandler;

    public RecipientDiscussionCountSection(int discussionCount, RecipientDiscussionCountClickHandler clickHandler) {
        // call constructor with layout resources for this Section header and items
        super(new SectionParameters.Builder(R.layout.field_text)
                .headerResourceId(R.layout.section_header)
                .footerResourceId(R.layout.section_header)
                .build());
        this.discussionCount = discussionCount;
        this.clickHandler = clickHandler;
    }

    @Override
    public int getContentItemsTotal() {
        return 1; // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new RecipientDiscussionCountSection.RecipientDiscussionCountSectionViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getFooterViewHolder(View view) {
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecipientDiscussionCountSectionViewHolder itemHolder = (RecipientDiscussionCountSectionViewHolder) holder;

        // bind your view here
        itemHolder.title.setText("Сообщений");
        itemHolder.value.setText(String.valueOf(discussionCount));

        itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickHandler.messagesClicked();
            }
        });
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


    class RecipientDiscussionCountSectionViewHolder extends RecyclerView.ViewHolder {

        private View rootView;
        private TextView title;
        private TextView value;

        public RecipientDiscussionCountSectionViewHolder(View itemView) {
            super(itemView);

            rootView = itemView;
            title = (TextView) itemView.findViewById(R.id.textfield_title);
            value = (TextView) itemView.findViewById(R.id.textfield_value);
        }
    }
}
