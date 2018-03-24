package ru.iorcontrol.ior.ior.OrderList;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ru.iorcontrol.ior.ior.Order.FavClickHandler;
import ru.iorcontrol.ior.ior.Model.Order;
import ru.iorcontrol.ior.ior.R;

/**
 * Created by me on 24/11/2017.
 */

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> implements Filterable {

    OrderListClickHandler clickHandler;
    FavClickHandler favClickHandler;

    private List<Order> orders;
    private List<Order> ordersFiltered;
    // Store the context for easy access
    private Context context;

    public OrdersListAdapter(Context context, List<Order> orders, OrderListClickHandler clickHandler, FavClickHandler favClickHandler) {
        this.orders = orders;
        this.ordersFiltered = orders;
        this.context = context;
        this.clickHandler = clickHandler;
        this.favClickHandler = favClickHandler;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    ordersFiltered = orders;
                } else {
                    List<Order> filteredList = new ArrayList<>();
                    for (Order row : orders) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getNumber().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    ordersFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = ordersFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ordersFiltered = (ArrayList<Order>) filterResults.values;
                refreshAfterSearch();
            }
        };
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView number;
        public TextView date;
        public TextView updated;
        public TextView type;
        public TextView user;
        public TextView status;
        public View overlay;
        public ImageButton fav_button;
        public TextView userTypeLabel;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            number = (TextView) itemView.findViewById(R.id.order_list_item_number);
            date = (TextView) itemView.findViewById(R.id.order_list_item_created);
            updated = (TextView) itemView.findViewById(R.id.order_list_item_updated);
            type = (TextView) itemView.findViewById(R.id.order_list_item_type);
            user = (TextView) itemView.findViewById(R.id.order_list_item_user);
            status = (TextView) itemView.findViewById(R.id.order_list_item_status);
            overlay = (View) itemView.findViewById(R.id.new_overlay);
            fav_button = (ImageButton) itemView.findViewById(R.id.fav_button);

            userTypeLabel = itemView.findViewById(R.id.order_user_type);


            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    clickHandler.itemClicked(orders.get(getAdapterPosition()));

                    SharedPreferences preferences = context.getSharedPreferences("storage", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    Set<String> seen = preferences.getStringSet("seen", null);

                    Set<String> copy = new HashSet<String>();

                    if (seen != null) {

                        Set<String> set = new HashSet<String>();
                        set.add(orders.get(getAdapterPosition())._id + "-" + orders.get(getAdapterPosition()).updated);
                        set.addAll(seen);

                        editor.putStringSet("seen", set);
                        editor.commit();
                    } else {

                        Set<String> set = new HashSet<String>();
                        set.add(orders.get(getAdapterPosition())._id + "-" + orders.get(getAdapterPosition()).updated);

                        editor.putStringSet("seen", set);
                        editor.commit();
                    }
                }
            });

            fav_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favClickHandler.favClicked(orders.get(getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public OrdersListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.activity_order_list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(OrdersListAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Order order = ordersFiltered.get(position);

        // Set item views based on your views and data model
        TextView number = viewHolder.number;
        number.setText(order.getNumber());

        TextView date = viewHolder.date;
        date.setText(getDate(order.getDate()));

        TextView updated = viewHolder.updated;
        updated.setText(getDate(order.getUpdated()));

        TextView type = viewHolder.type;
        type.setText(order.getType().getName());

        if (order.getAssignedTo() != null) {
            TextView user = viewHolder.user;
            user.setText(order.getAssignedTo().getName());
        } else {
            TextView user = viewHolder.user;
            user.setText(order.assignedToGroup.name);
        }

        viewHolder.userTypeLabel.setText("Исполнитель");


        TextView status = viewHolder.status;
        status.setText(order.getCurrentstatus());


        SharedPreferences preferences = context.getSharedPreferences("storage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> seen = preferences.getStringSet("seen", null);

        if (seen != null) {
            if (seen.contains(order._id + "-" + order.updated)) {
                viewHolder.overlay.setVisibility(View.GONE);
            }
        }

        if (order.getFav() == true) {
            viewHolder.fav_button.setImageResource(R.drawable.star_filled);
        } else {
            viewHolder.fav_button.setImageResource(R.drawable.star);
        }
    }


    void refresh(List<Order> orders) {
        this.orders = orders;
        this.ordersFiltered = orders;
        notifyDataSetChanged();
    }

    void refreshAfterSearch() {
        notifyDataSetChanged();
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return ordersFiltered.size();
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd MMMM HH:mm", cal).toString();
        return date;
    }
}
