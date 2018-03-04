package ru.iorcontrol.ior.ior;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.tapadoo.alerter.Alerter;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.iorcontrol.ior.ior.Model.User;

/**
 * Created by alexeykazinets on 03/12/2017.
 */

public class ProfileScreen extends AppCompatActivity {

    Retrofit retrofit;
    APIService service;
    Drawer drawer;

    EditText name;
    EditText mail;
    EditText phone;

    Switch first_switch;
    Switch second_switch;

    TextView first_label;
    TextView second_label;

    Button save_profile_button;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.profile_name);
        mail = findViewById(R.id.profile_mail);
        phone = findViewById(R.id.profile_phone);

        first_switch = findViewById(R.id.first_switch);
        second_switch = findViewById(R.id.second_switch);

        first_label = findViewById(R.id.first_label);
        second_label = findViewById(R.id.second_label);

        save_profile_button = findViewById(R.id.save_profile_button);

        if (Settings.getInstance().getType().equals("client")) {
            first_label.setText("Присылать почтовые уведомления о новых статусах");
            second_label.setText("Присылать push уведомления о новых статусах");
        }

        save_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Профиль");

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
                .withActionBarDrawerToggle(true)
                .withSelectedItem(2)
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


        Call<User> getUser = service.getUser(Settings.getInstance().getID());
        getUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();

                name.setText(user.getName());
                phone.setText(user.getPhone());
                mail.setText(user.getMail());

                if (Settings.getInstance().getType().equals("client")) {
                    first_switch.setChecked(user.getNew_status_notification());
                    second_switch.setChecked(user.getNew_status_push_notification());
                } else {
                    first_switch.setChecked(user.getNew_orders_notification());
                    second_switch.setChecked(user.getNew_orders_push_notification());
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:

                SharedPreferences preferences = getSharedPreferences("storage", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().commit();

                Intent intent = new Intent(this, LoginScreen.class);
                startActivity(intent);
                finish();
                break;
            default:
                finish();
        }

        return true;
    }


    void updateProfile() {

        user.setName(name.getText().toString());
        user.setPhone(phone.getText().toString());
        user.setMail(mail.getText().toString());

        if (Settings.getInstance().getType().equals("client")) {
            user.setNew_status_notification(first_switch.isChecked());
            user.setNew_status_push_notification(second_switch.isChecked());
        } else {
            user.setNew_orders_notification(first_switch.isChecked());
            user.setNew_orders_push_notification(second_switch.isChecked());
        }

        Call<Void> updateUser = service.updateUser(user);
        updateUser.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showAlert();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    void showAlert() {
        Alerter.create(this)
                .setText("Профиль обновлен")
                .setDuration(3000)
                .show();
    }

}
