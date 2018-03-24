package ru.iorcontrol.ior.ior;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ru.iorcontrol.ior.ior.Login.LoginScreen;
import ru.iorcontrol.ior.ior.OrderList.OrdersListScreen;
import ru.iorcontrol.ior.ior.Recipient.RecipientScreen;

/**
 * Created by me on 23/11/2017.
 */

public class Main extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("storage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String _id = preferences.getString("_id", null);
        String type = preferences.getString("type", null);
        String name = preferences.getString("name", null);

        Settings.getInstance().setID(_id);
        Settings.getInstance().setType(type);
        Settings.getInstance().setOrderName(name);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            Log.d("mesaj", "String bundle : " + bundle.getString("onesignal_data"));
        }


        if (_id == null) {
            intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
            finish();
        } else {
            if (type.equals("client") || type.equals("employee")) {
                intent = new Intent(this, OrdersListScreen.class);
                startActivity(intent);
                finish();
            } else {
                intent = new Intent(this, RecipientScreen.class);
                startActivity(intent);
                finish();
            }

        }

    }
}
