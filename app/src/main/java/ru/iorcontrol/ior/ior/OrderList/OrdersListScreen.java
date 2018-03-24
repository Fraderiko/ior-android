package ru.iorcontrol.ior.ior.OrderList;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.onesignal.OneSignal;
import com.tapadoo.alerter.Alerter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.iorcontrol.ior.ior.APIService;
import ru.iorcontrol.ior.ior.CreateOrder.CreateOrderScreen;
import ru.iorcontrol.ior.ior.Order.FavClickHandler;
import ru.iorcontrol.ior.ior.Feedback.FeedbackScreen;
import ru.iorcontrol.ior.ior.Model.Order;
import ru.iorcontrol.ior.ior.Model.User;
import ru.iorcontrol.ior.ior.Order.OrderScreen;
import ru.iorcontrol.ior.ior.Profile.ProfileScreen;
import ru.iorcontrol.ior.ior.R;
import ru.iorcontrol.ior.ior.Settings;

/**
 * Created by me on 24/11/2017.
 */

public class OrdersListScreen extends AppCompatActivity implements Callback <List<Order>>, OrderListClickHandler, FavClickHandler {

    Retrofit retrofit;
    APIService service;

    OrdersListAdapter adapter;

    List<Order> orders;
    List<Order> fetchedOrders;

    int ORDER_UPDATED = 1337, FILTER = 0, ORDER_CREATED = 1338;

    Drawer drawer;

    String typeSort = "asc";
    String statusSort = "asc";
    String userSort = "asc";
    private SearchView searchView;

    enum MODE { UPDATING, FILTERING }

    MODE mode = MODE.UPDATING;

    List<String> favs = new ArrayList<String>();

    Boolean isFav = false;

    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_order_list);

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.add_order_floating);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = MODE.UPDATING;
                Intent intent = new Intent(getApplicationContext(), CreateOrderScreen.class);
                startActivityForResult(intent, ORDER_CREATED);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Заказы");

        orders = new ArrayList<Order>();

        adapter = new OrdersListAdapter(this, orders, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                fetchOrders();
            }
        });


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Settings.getInstance().getAPIHost())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(APIService.class);

        fetchOrders();

        PrimaryDrawerItem orders = new PrimaryDrawerItem().withIdentifier(1).withIcon(R.drawable.orders).withName("Заказы");
        PrimaryDrawerItem profile = new PrimaryDrawerItem().withIdentifier(2).withIcon(R.drawable.profile).withName("Профиль");
        PrimaryDrawerItem favs = new PrimaryDrawerItem().withIdentifier(3).withIcon(R.drawable.star).withName("Избранные");
        PrimaryDrawerItem feedback = new PrimaryDrawerItem().withIdentifier(4).withIcon(R.drawable.support).withName("Обратная связь");


        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        orders,
                        new DividerDrawerItem(),
                        profile,
                        new DividerDrawerItem(),
                        favs,
                        new DividerDrawerItem(),
                        feedback
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch (position) {
                            case 0:
                                Intent orderlistintent = new Intent(getApplicationContext(), OrdersListScreen.class);
                                startActivity(orderlistintent);
                                return true;
                            case 2:
                                Intent profileintent = new Intent(getApplicationContext(), ProfileScreen.class);
                                startActivity(profileintent);
                                return true;
                            case 4:
                                Intent favsintent = new Intent(getApplicationContext(), OrdersListScreen.class);
                                favsintent.putExtra("isFav", true);
                                startActivity(favsintent);
                                return true;
                            case 6:
                                Intent feedbackintent = new Intent(getApplicationContext(), FeedbackScreen.class);
                                startActivity(feedbackintent);
                                return true;
                        }


                        return true;
                    }
                })
                .build();

        isFav = getIntent().getBooleanExtra("isFav", false);

        setupPush();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mode == MODE.UPDATING) {
            fetchOrders();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.orders_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                Intent intent = new Intent(this, SortScreen.class);
                startActivityForResult(intent, FILTER);

            default:
                break;
        }

        return true;
    }

    void setupPush() {
        if (Settings.getInstance().getPUSH() == null || Settings.getInstance().getPUSH().equals("")) {
            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                @Override
                public void idsAvailable(String userId, String registrationId) {
                    Settings.getInstance().setPush(userId);

                    try {
                        JSONObject paramObject = new JSONObject();
                        paramObject.put("_id", Settings.getInstance().getID());
                        paramObject.put("push_id", userId);

                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

                        Call<Void> push = service.setPush(body);
                        push.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                            }
                        });
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    void fetchOrders() {


        Call<User> user = service.getUser(Settings.getInstance().getID());

        user.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                favs = response.body().getFavorites();

                SharedPreferences preferences = getSharedPreferences("storage", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                String _id = preferences.getString("_id", null);

                if (Settings.getInstance().getType().equals("client")) {
                    Call<List<Order>> orders = service.ordersByGroup(_id);
                    orders.enqueue(OrdersListScreen.this);
                } else {
                    Call<List<Order>> orders = service.ordersForEmployee(_id);
                    orders.enqueue(OrdersListScreen.this);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }

    @Override
    public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
        orders = response.body();
        List<Order> favOrders = new ArrayList<Order>();
        for (int i = 0; i < orders.size(); i++) {
            if (favs.contains(orders.get(i)._id)) {
                orders.get(i).setFav(true);
                favOrders.add(orders.get(i));
            } else {
                orders.get(i).setFav(false);
            }
        }

        mSwipeRefreshLayout.setRefreshing(false);

        if (isFav == true) {
            orders = favOrders;
        }

        fetchedOrders = orders;

        Collections.sort(orders, new OrderComparator());

        adapter.refresh(orders);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ORDER_UPDATED && resultCode == RESULT_OK) {
            mode = MODE.UPDATING;

            fetchOrders();
            Alerter.create(this)
                    .setText("Заказ обновлен!")
                    .setDuration(3000)
                    .show();
        }

        if (requestCode == ORDER_CREATED && resultCode == RESULT_OK) {
            showCreatedAlert();
        }

        if (requestCode == FILTER && resultCode == RESULT_OK) {

            mode = MODE.FILTERING;

            String value = data.getStringExtra("FilterType");

            if (value.equals("date")) {

                long start = data.getLongExtra("startDate", 0);
                long end = data.getLongExtra("endDate", 0);

                if (start != 0 && end != 0) {
                    List<Order> filtered = new ArrayList<Order>();

                    for (int i = 0; i < fetchedOrders.size(); i++) {
                        if (fetchedOrders.get(i).getDate() > start && fetchedOrders.get(i).getDate() < end) {
                            filtered.add(fetchedOrders.get(i));
                        }
                    }

                    orders = filtered;

                    adapter.refresh(orders);
                }

                if (start != 0 && end == 0) {
                    List<Order> filtered = new ArrayList<Order>();

                    for (int i = 0; i < fetchedOrders.size(); i++) {
                        if (fetchedOrders.get(i).getDate() > start) {
                            filtered.add(fetchedOrders.get(i));
                        }
                    }

                    orders = filtered;

                    adapter.refresh(orders);
                }

                if (start == 0 && end != 0) {
                    List<Order> filtered = new ArrayList<Order>();

                    for (int i = 0; i < fetchedOrders.size(); i++) {
                        if (fetchedOrders.get(i).getDate() < end) {
                            filtered.add(fetchedOrders.get(i));
                        }
                    }

                    orders = filtered;

                    adapter.refresh(orders);
                }
            } else if (value.equals("status")) {

                List<Order> filtered = new ArrayList<Order>();
                filtered = fetchedOrders;

                if (statusSort.equals("asc")) {
                    statusSort = "desc";
                    Collections.sort(filtered, new Comparator<Order>() {
                        @Override
                        public int compare(final Order order1, final Order order2) {
                            return order1.getCurrentstatus().compareTo(order2.getCurrentstatus());
                        }
                    });

                    adapter.refresh(filtered);
                } else {
                    statusSort = "asc";
                    Collections.sort(filtered, new Comparator<Order>() {
                        @Override
                        public int compare(final Order order1, final Order order2) {
                            return order2.getCurrentstatus().compareTo(order1.getCurrentstatus());
                        }
                    });
                    adapter.refresh(filtered);
                }

            } else if (value.equals("type")) {

                List<Order> filtered = new ArrayList<Order>();
                filtered = fetchedOrders;

                if (typeSort.equals("asc")) {
                    typeSort = "desc";
                    Collections.sort(filtered, new Comparator<Order>() {
                        @Override
                        public int compare(final Order order1, final Order order2) {
                            return order1.getType().getName().compareTo(order2.getType().getName());
                        }
                    });
                    adapter.refresh(filtered);
                } else {
                    typeSort = "asc";
                    Collections.sort(filtered, new Comparator<Order>() {
                        @Override
                        public int compare(final Order order1, final Order order2) {
                            return order2.getType().getName().compareTo(order1.getType().getName());
                        }
                    });
                    adapter.refresh(filtered);
                }

            } else if (value.equals("user")) {
                List<Order> filtered = new ArrayList<Order>();
                filtered = fetchedOrders;

                if (userSort.equals("asc")) {
                    userSort = "desc";
                    Collections.sort(filtered, new Comparator<Order>() {
                        @Override
                        public int compare(final Order order1, final Order order2) {
                            return order1.getResponsible().compareTo(order2.getResponsible());
                        }
                    });
                    adapter.refresh(filtered);
                } else {
                    userSort = "asc";
                    Collections.sort(filtered, new Comparator<Order>() {
                        @Override
                        public int compare(final Order order1, final Order order2) {
                            return order2.getResponsible().compareTo(order1.getResponsible());
                        }
                    });
                    adapter.refresh(filtered);
                }
            } else if (value.equals("clear")) {
                mode = MODE.UPDATING;
                adapter.refresh(fetchedOrders);
            }

        }

    }
    @Override
    public void onFailure(Call<List<Order>> call, Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void itemClicked(Order order) {

        mode = MODE.UPDATING;

        Intent intent = new Intent(this, OrderScreen.class);
        intent.putExtra("Order", order);
        startActivityForResult(intent, ORDER_UPDATED);
    }

    @Override
    public void favClicked(Order order) {
            try {
                JSONObject paramObject = new JSONObject();
                paramObject.put("_id", Settings.getInstance().getID());
                paramObject.put("order_id", order.get_id());

                RequestBody body = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

                if (order.isFav == true) {
                    removeFav(body);
                } else {
                    addFav(body);
                }
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
    }

    void removeFav(RequestBody body) {
        Call<Void> addFav = service.removeFav(body);
        addFav.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                fetchOrders();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    void addFav(RequestBody body) {
        Call<Void> addFav = service.addFav(body);
        addFav.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                fetchOrders();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    void showCreatedAlert() {
        Alerter.create(this)
                .setText("Заказ успешно создан!")
                .setDuration(3000)
                .show();
    }
}
