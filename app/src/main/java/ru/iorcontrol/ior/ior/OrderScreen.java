package ru.iorcontrol.ior.ior;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.util.ArrayList;
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
import ru.iorcontrol.ior.ior.Chat.ChatScreen;
import ru.iorcontrol.ior.ior.Model.Discussion;
import ru.iorcontrol.ior.ior.Model.Order;
import ru.iorcontrol.ior.ior.Model.Status;
import ru.iorcontrol.ior.ior.Model.User;

/**
 * Created by alexeykazinets on 25/11/2017.
 */

public class OrderScreen extends AppCompatActivity implements StatusClickHandler, FavClickHandler {

    Order order;
    Button messages;

    Retrofit retrofit;
    APIService service;

    int ORDER_UPDATED = 1337, MESSAGE_SENT_RESULT = 3;

    OrderScreenAdapter adapter;

    Boolean canEditOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        order = (Order) bundle.getSerializable("Order");
        getSupportActionBar().setTitle("№"+ order.number);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_order_details);
        messages = (Button) findViewById(R.id.messages_button);

        adapter = new OrderScreenAdapter(order, this, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (Settings.getInstance().getType().equals("client") && order.discussion.isEmpty() == false) {
            messages.setVisibility(View.VISIBLE);
            messages.setText("Обратная связь: " + String.valueOf(order.getDiscussion().size()));
            messages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openMessages();
                }
            });
        }

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Settings.getInstance().getAPIHost())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(APIService.class);

        Call<User> getUser = service.getUser(Settings.getInstance().getID());
        getUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                canEditOrders = response.body().getPermission_to_edit_orders();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    void openMessages() {
        Intent intent = new Intent(this, MessagesScreen.class);
        ArrayList<Discussion> discussions = new ArrayList<Discussion>();
        discussions.addAll(order.discussion);
        intent.putExtra("Discussion", discussions);
        intent.putExtra("Order", order);
        startActivityForResult(intent, MESSAGE_SENT_RESULT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat:

                Intent intent = new Intent(this, ChatScreen.class);
                intent.putExtra("order", order);
                startActivity(intent);
                break;
            default:
                finish();
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void statusClicked(Status status, int position) {

        if (Settings.getInstance().getType().equals("employee") && status.getState() == null) {
            Intent intent = new Intent(this, StatusDetailsEmployeeScreen.class);
            intent.putExtra("Status", status);
            intent.putExtra("Order", order);
            intent.putExtra("isNotReadyToFill", resolveReadyToFill(position));
            startActivityForResult(intent, ORDER_UPDATED);
        } else if (canEditOrders == true) {
            Intent intent = new Intent(this, StatusDetailsEmployeeScreen.class);
            intent.putExtra("Status", status);
            intent.putExtra("Order", order);
            intent.putExtra("canEditOrders", canEditOrders);
            startActivityForResult(intent, ORDER_UPDATED);
        } else {
            Intent intent = new Intent(this, StatusDetailsScreen.class);
            intent.putExtra("Status", status);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ORDER_UPDATED && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }

        if (requestCode == MESSAGE_SENT_RESULT && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private Boolean resolveReadyToFill(int position) {

        List<Status> emptyStatuses = new ArrayList<Status>();

        for (int i = 0; i < order.getStatuses().size(); i++) {
            if (order.getStatuses().get(i).getState() == null) {
                emptyStatuses.add(order.getStatuses().get(i));
            }
        }


        if (order.getStatuses().get(position).getState() == null &&  order.getStatuses().get(position).get_id().equals(emptyStatuses.get(0).get_id()) == false) {
            return true;
        } else {
            return false;

        }
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
                order.isFav = false;
            } else {
                addFav(body);
                order.isFav = true;
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
                adapter.notifyDataSetChanged();
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
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}

