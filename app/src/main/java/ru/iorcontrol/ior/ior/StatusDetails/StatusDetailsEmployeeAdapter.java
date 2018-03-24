package ru.iorcontrol.ior.ior.StatusDetails;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.iorcontrol.ior.ior.Model.Status;
import ru.iorcontrol.ior.ior.R;

/**
 * Created by alexeykazinets on 28/11/2017.
 */

public class StatusDetailsEmployeeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    Status status;
    StatusDetailsEmployeeClickHandler clickHandler;

    private final int EMPTY = 0, FILLED = 1, PLACEHOLDER = 2;

    public StatusDetailsEmployeeAdapter(Context context, Status status, StatusDetailsEmployeeClickHandler clickHandler) {
        this.clickHandler = clickHandler;
        this.context = context;
        this.status = status;
    }

    public class EmptyFieldViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;

        public EmptyFieldViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.status_name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickHandler.fieldClicked(getAdapterPosition());
                }
            });
        }
    }

    public class FilledFieldViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView value;

        public FilledFieldViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textfield_title);
            value = (TextView) view.findViewById(R.id.textfield_value);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickHandler.fieldClicked(getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (status.getFields().get(position).getType().equals("video") || status.getFields().get(position).getType().equals("image")) {
            if (status.getFields().get(position).getMedia().size() == 0) {
                return EMPTY;
            } else {
                return FILLED;
            }
        } else if (status.getFields().get(position).getType().equals("file")) {
            return PLACEHOLDER;
        } else {
            if (status.getFields().get(position).getValue() == null || status.getFields().get(position).getValue().equals("")) {
                return EMPTY;
            } else {
                return FILLED;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case EMPTY:
                View v1 = inflater.inflate(R.layout.activity_order_details_status, parent, false);
                viewHolder = new EmptyFieldViewHolder(v1);
                break;
            case FILLED:
                View v2 = inflater.inflate(R.layout.field_text, parent, false);
                viewHolder = new FilledFieldViewHolder(v2);
                break;
            default:
                View v3 = inflater.inflate(R.layout.field_text, parent, false);
                viewHolder = new FilledFieldViewHolder(v3);
                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case EMPTY:
                EmptyFieldViewHolder vh1 = (EmptyFieldViewHolder) holder;

                if (status.getFields().get(position).getRequired() == true) {
                    ((EmptyFieldViewHolder) holder).name.setText(status.getFields().get(position).name + " (*)");

                } else {
                    ((EmptyFieldViewHolder) holder).name.setText(status.getFields().get(position).name);
                }
                break;
            case FILLED:
                FilledFieldViewHolder vh2 = (FilledFieldViewHolder) holder;

                if (status.getFields().get(position).getRequired() == true) {
                    ((FilledFieldViewHolder) holder).title.setText(status.getFields().get(position).name + " (*)");
                } else {
                    ((FilledFieldViewHolder) holder).title.setText(status.getFields().get(position).name);
                }



                if (status.getFields().get(position).getType().equals("video") || status.getFields().get(position).getType().equals("image")) {
                    ((FilledFieldViewHolder) holder).value.setText(String.valueOf(status.getFields().get(position).getMedia().size()));
                } else {
                    ((FilledFieldViewHolder) holder).value.setText(status.getFields().get(position).value);
                }


                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return status.getFields().size();
    }

    void update(Status status) {
        this.status = status;
        notifyDataSetChanged();
    }
}
