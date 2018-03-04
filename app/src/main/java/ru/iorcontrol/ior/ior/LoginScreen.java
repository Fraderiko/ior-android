package ru.iorcontrol.ior.ior;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginScreen extends AppCompatActivity implements Callback<ResponseBody> {

    Retrofit retrofit;
    APIService service;

    EditText login;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        login = (EditText) findViewById(R.id.loginEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        Button loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Settings.getInstance().getAPIHost())
                .client(client)
                .build();

        service = retrofit.create(APIService.class);

    }

    void Login() {
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("mail", login.getText());
            paramObject.put("password", password.getText());

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

            Call<ResponseBody> auth = service.auth(body);
            auth.enqueue(this);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

        try {

            JSONObject json = new JSONObject(response.body().string());

            String result = json.getString("result");

            if (result.equals("ok")) {
                String id = json.getString("_id");
                String type = json.getString("type");
                String name = json.getString("name");

                Log.d("DEBUG", id);

                SharedPreferences preferences = getSharedPreferences("storage",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("_id", id);
                editor.putString("type", type);
                editor.putString("name", name);
                editor.apply();

                Intent intent = new Intent(this, Main.class);
                startActivity(intent);
                finish();
            } else {
                showErrorAlert("Неверные логин или пароль!");
            }



        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void showErrorAlert(String message) {
        Alerter.create(this)
                .setTitle("Ошибка!")
                .setText(message)
                .setDuration(3000)
                .setBackgroundColorRes(R.color.error)
                .show();
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        Log.d("DEBUG", "ОШИБКА СОЕДИНЕНИЯ");

    }
}
