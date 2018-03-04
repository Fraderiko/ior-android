package ru.iorcontrol.ior.ior;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import ru.iorcontrol.ior.ior.Model.Field;

/**
 * Created by alexeykazinets on 30/11/2017.
 */

public class RecipientTextSection extends StatelessSection {

    Field field;

    public RecipientTextSection(Field field) {
        // call constructor with layout resources for this Section header and items
        super(new SectionParameters.Builder(R.layout.field_text)
                .build());
        this.field = field;
    }

    @Override
    public int getContentItemsTotal() {
        return 1; // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new RecipientTextSectionViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecipientTextSectionViewHolder itemHolder = (RecipientTextSectionViewHolder) holder;

        // bind your view here
        itemHolder.title.setText(field.name);
        itemHolder.value.setText(field.value);
    }

    class RecipientTextSectionViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView value;

        public RecipientTextSectionViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.textfield_title);
            value = (TextView) itemView.findViewById(R.id.textfield_value);
        }
    }
}
