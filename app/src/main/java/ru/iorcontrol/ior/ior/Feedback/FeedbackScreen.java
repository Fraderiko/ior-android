package ru.iorcontrol.ior.ior.Feedback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.tapadoo.alerter.Alerter;

import org.json.JSONObject;

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
import ru.iorcontrol.ior.ior.Model.User;
import ru.iorcontrol.ior.ior.OrderList.OrdersListScreen;
import ru.iorcontrol.ior.ior.Profile.ProfileScreen;
import ru.iorcontrol.ior.ior.R;
import ru.iorcontrol.ior.ior.Settings;

/**
 * Created by alexeykazinets on 03/12/2017.
 */

public class FeedbackScreen extends AppCompatActivity {

    Retrofit retrofit;
    APIService service;
    Drawer drawer;

    EditText feedback_message;
    Button feedback_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Обратная связь");

        feedback_message = findViewById(R.id.feedback_message);
        feedback_button = findViewById(R.id.feedback_button);

        feedback_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFeedback();
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

        PrimaryDrawerItem orders = new PrimaryDrawerItem().withIdentifier(1).withIcon(R.drawable.orders).withName("Заказы");
        PrimaryDrawerItem profile = new PrimaryDrawerItem().withIdentifier(2).withIcon(R.drawable.profile).withName("Профиль");
        PrimaryDrawerItem favs = new PrimaryDrawerItem().withIdentifier(3).withIcon(R.drawable.star).withName("Избранные");
        PrimaryDrawerItem feedback = new PrimaryDrawerItem().withIdentifier(4).withIcon(R.drawable.support).withName("Обратная связь");


        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(true)
                .withSelectedItem(6)
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

    }

    private void sendFeedback() {
        Call<User> user = service.getUser(Settings.getInstance().getID());
        user.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();

                try {
                    JSONObject paramObject = new JSONObject();
                    paramObject.put("message", feedback_message.getText().toString());
                    paramObject.put("sender", user.getMail());

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

                    final Call<Void> feedback = service.feedback(body);
                    feedback.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            showAlert();
                            feedback_message.setText("");
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    void showAlert() {
        Alerter.create(this)
                .setText("Сообщение отправлено")
                .setDuration(3000)
                .show();
    }

}
