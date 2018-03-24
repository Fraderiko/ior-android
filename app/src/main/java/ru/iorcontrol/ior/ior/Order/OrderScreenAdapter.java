package ru.iorcontrol.ior.ior.Order;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ru.iorcontrol.ior.ior.Model.Order;
import ru.iorcontrol.ior.ior.Model.Status;
import ru.iorcontrol.ior.ior.R;
import ru.iorcontrol.ior.ior.Settings;
import ru.iorcontrol.ior.ior.StatusDetails.StatusClickHandler;

/**
 * Created by alexeykazinets on 26/11/2017.
 */

public class OrderScreenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Order order;
    StatusClickHandler clickHandler;
    FavClickHandler favClickHandler;


    private final int HEADER = 0, ORDER = 1, STATUS = 2;

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderScreenAdapter(Order order, StatusClickHandler clickHandler, FavClickHandler favClickHandler) {
        this.order = order;
        this.clickHandler = clickHandler;
        this.favClickHandler = favClickHandler;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        if (Settings.getInstance().getType().equals("client")) {
            ArrayList<Status> filledStatuses = new ArrayList<Status>();

            for (int i = 0; i < order.getStatuses().size(); i++) {

                if (order.getStatuses().get(i).getState() != null) {
                    filledStatuses.add(order.getStatuses().get(i));
                }
            }

            return filledStatuses.size() + 3;
        } else {
            return order.getStatuses().size() + 3;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else if (position == 1) {
            return ORDER;
        } else if (position == 2) {
            return HEADER;
        } else {
            return STATUS;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case HEADER:
                View v1 = inflater.inflate(R.layout.section_header, viewGroup, false);
                viewHolder = new HeaderViewHolder(v1);
                break;
            case ORDER:
                View v2 = inflater.inflate(R.layout.activity_order_list_item_details, viewGroup, false);
                viewHolder = new OrderViewHolder(v2);
                break;
            case STATUS:
                View v3 = inflater.inflate(R.layout.activity_order_details_status, viewGroup, false);
                viewHolder = new StatusViewHolder(v3);
                break;
            default:
                View v4 = inflater.inflate(R.layout.section_header, viewGroup, false);
                viewHolder = new HeaderViewHolder(v4);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case HEADER:
                HeaderViewHolder vh1 = (HeaderViewHolder) holder;
                break;
            case ORDER:
                OrderViewHolder vh2 = (OrderViewHolder) holder;

                ((OrderViewHolder) holder).number.setText(order.getNumber());
                ((OrderViewHolder) holder).created.setText(getDate(order.getDate()));
                ((OrderViewHolder) holder).updated.setText(getDate(order.getUpdated()));
                ((OrderViewHolder) holder).status.setText(order.getCurrentstatus());
                ((OrderViewHolder) holder).type.setText(order.getType().getName());

                ((OrderViewHolder) holder).userTypeLabel.setText("Исполнитель");


                if (order.getAssignedTo() != null) {
                    ((OrderViewHolder) holder).user.setText(order.getAssignedTo().getName());
                } else {
                    ((OrderViewHolder) holder).user.setText(order.assignedToGroup.name);
                }

                ((OrderViewHolder) holder).overlay.setVisibility(View.GONE);

                if (order.getFav() == true) {
                    ((OrderViewHolder) holder).fav_button.setImageResource(R.drawable.star_filled);
                } else {
                    ((OrderViewHolder) holder).fav_button.setImageResource(R.drawable.star);
                }

                if (order.getComment().equals("")) {
                    ((OrderViewHolder) holder).status.setPadding(0,0,0,20);
                    ((OrderViewHolder) holder).comment.setVisibility(View.GONE);
                    ((OrderViewHolder) holder).order_list_item_comment_details_label.setVisibility(View.GONE);
                } else {
                    ((OrderViewHolder) holder).comment.setText(order.getComment());
                }

                break;
            case STATUS:
                StatusViewHolder vh3 = (StatusViewHolder) holder;
                ((StatusViewHolder) holder).name.setText(order.getStatuses().get(position - 3).getName());
                break;
            default:
                HeaderViewHolder vh4 = (HeaderViewHolder) holder;
                break;
        }
    }


    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View v) {
            super(v);
        }
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        private final TextView number;
        private final TextView created;
        private final TextView updated;
        private final TextView status;
        private final TextView type;
        private final TextView user;
        private final TextView comment;
        private final View overlay;
        private final TextView userTypeLabel;

        private final TextView order_list_item_comment_details_label;
        public ImageButton fav_button;

        public OrderViewHolder(View view) {
            super(view);
            number = (TextView) view.findViewById(R.id.order_list_item_number_details);
            created = (TextView) view.findViewById(R.id.order_list_item_created_details);
            updated = (TextView) view.findViewById(R.id.order_list_item_updated_details);
            status = (TextView) view.findViewById(R.id.order_list_item_status_details);
            type = (TextView) view.findViewById(R.id.order_list_item_type_details);
            user = (TextView) view.findViewById(R.id.order_list_item_user_details);
            overlay = (View) view.findViewById(R.id.new_overlay_details);
            fav_button = (ImageButton) itemView.findViewById(R.id.fav_button_details);
            comment = (TextView) view.findViewById(R.id.order_list_item_comment_details);
            order_list_item_comment_details_label = (TextView) view.findViewById(R.id.order_list_item_comment_details_label);

            userTypeLabel = view.findViewById(R.id.order_user_type);

            fav_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favClickHandler.favClicked(order);
                }
            });

        }
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;

        public StatusViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.status_name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickHandler.statusClicked(order.getStatuses().get(getAdapterPosition() - 3), getAdapterPosition() - 3);
                }
            });
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("dd MMMM HH:mm", cal).toString();
    }
}
