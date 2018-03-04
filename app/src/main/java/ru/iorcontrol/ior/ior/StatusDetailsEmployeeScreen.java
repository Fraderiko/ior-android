package ru.iorcontrol.ior.ior;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.iorcontrol.ior.ior.Model.CheckStatusRequest;
import ru.iorcontrol.ior.ior.Model.Order;
import ru.iorcontrol.ior.ior.Model.ResultResponse;
import ru.iorcontrol.ior.ior.Model.Status;

/**
 * Created by alexeykazinets on 28/11/2017.
 */

public class StatusDetailsEmployeeScreen extends AppCompatActivity implements StatusDetailsEmployeeClickHandler, Callback<ResponseBody> {

    Order order;
    Status status;
    StatusDetailsEmployeeAdapter adapter;

    int TEXT_RESULT = 1, IMAGE_RESULT = 2, VIDEO_RESULT = 3;

    Boolean isNotReadyToFill;
    Boolean canEditOrders;

    Retrofit retrofit;
    APIService service;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        order = (Order) bundle.getSerializable("Order");
        status = (Status) bundle.getSerializable("Status");
        isNotReadyToFill = getIntent().getBooleanExtra("isNotReadyToFill", false);
        canEditOrders = getIntent().getBooleanExtra("canEditOrders", false);


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Settings.getInstance().getAPIHost())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(APIService.class);


        if (Settings.getInstance().getType().equals("client")) {
            setupRecyclerView();
        } else {
            if (isNotReadyToFill == false) {

                if ((status.users_permission_to_edit != null && status.groups_permission_to_edit != null)) {

                    if (status.users_permission_to_edit.isEmpty() == false || status.groups_permission_to_edit.isEmpty() == false) {
                        Call<ResultResponse> checkPermission = service.checkStatusPermission(new CheckStatusRequest(status.groups_permission_to_edit, Settings.getInstance().getID()));

                        checkPermission.enqueue(new Callback<ResultResponse>() {
                            @Override
                            public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                                if (response.body().result.equals("true") || status.users_permission_to_edit.contains(Settings.getInstance().getID())) {
                                    setupRecyclerView();
                                } else {
                                    new AlertDialog.Builder(StatusDetailsEmployeeScreen.this)
                                            .setTitle("Статус недоступен")
                                            .setMessage("Нет прав для заполнения статуса")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            }).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultResponse> call, Throwable t) {

                            }
                        });
                    } else {
                        setupRecyclerView();
                    }

                } else {
                    setupRecyclerView();
                }




            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Статус недоступен")
                        .setMessage("Прежде чем заполнять данный статус, необходимо заполнить предыдущий")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }

        }





    }

    void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_order_details);
        adapter = new StatusDetailsEmployeeAdapter(this, status, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:

                int index = 0;

                for (int i = 0; i < order.getStatuses().size(); i++) {
                    if (order.getStatuses().get(i).get_id().equals(status.get_id())) {
                        index = i;
                    }
                }

                if (canEditOrders && status.getState().equals("Filled")) {
                    order.getStatuses().set(index, status);
                    Call <Void> call = service.editOrder(order);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                } else {
                    status.setState("preFilled");
                    order.getStatuses().set(index, status);

                    Call<ResponseBody> call = service.updateOrder(order);
                    call.enqueue(this);
                }

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
    public void fieldClicked(final int position) {

        if (status.getFields().get(position).getType().equals("image")) {
            Intent intent = new Intent(this, MediaScreen.class);
            intent.putExtra("type", "image");
            intent.putExtra("position", position);

            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.addAll(status.getFields().get(position).getMedia());

            intent.putStringArrayListExtra("urls", arrayList);

            startActivityForResult(intent, IMAGE_RESULT);
        }

        if (status.getFields().get(position).getType().equals("video")) {
            Intent intent = new Intent(this, MediaScreen.class);
            intent.putExtra("type", "video");
            intent.putExtra("position", position);

            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.addAll(status.getFields().get(position).getMedia());

            intent.putStringArrayListExtra("urls", arrayList);
            startActivityForResult(intent, VIDEO_RESULT);
        }

        if (status.getFields().get(position).getType().equals("digit")) {
            Intent intent = new Intent(this, FieldTextScreen.class);
            intent.putExtra("position", position);
            intent.putExtra("digit", true);
            intent.putExtra("value", status.getFields().get(position).getValue());
            startActivityForResult(intent, TEXT_RESULT);
        }

        if (status.getFields().get(position).getType().equals("text")) {
            Intent intent = new Intent(this, FieldTextScreen.class);
            intent.putExtra("position", position);
            intent.putExtra("value", status.getFields().get(position).getValue());
            startActivity(intent);
        }

        if (status.getFields().get(position).getType().equals("date")) {

            int mYear;
            int mMonth;
            int mDay;

            if (status.getFields().get(position).getValue() != "") {
                String[] array = status.getFields().get(position).getValue().split(" ");
                mYear = Integer.parseInt(array[2]);
                mMonth = Integer.parseInt(array[1]);
                mDay = Integer.parseInt(array[0]);
            } else {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,  int monthOfYear, int dayOfMonth) {
                            status.getFields().get(position).setValue(String.valueOf(String.valueOf(dayOfMonth) + " " + String.valueOf(monthOfYear) + " " + String.valueOf(year)));
                            adapter.update(status);
                        }
                    }, mYear, mMonth, mDay);
                    datePickerDialog.show();

        }

        if (status.getFields().get(position).getType().equals("time")) {

            int mHour;
            int mMinute;

            if (status.getFields().get(position).getValue() != "") {
                String[] array = status.getFields().get(position).getValue().split(":");
                mMinute = Integer.parseInt(array[1]);
                mHour = Integer.parseInt(array[0]);
            } else {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
            }

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            status.getFields().get(position).setValue(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
                            adapter.update(status);
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TEXT_RESULT && resultCode == RESULT_OK) {
            String value = data.getStringExtra("value");
            int position = data.getIntExtra("position", 99);
            status.getFields().get(position).setValue(value);
            adapter.update(status);
        }

        if (requestCode == IMAGE_RESULT && resultCode == RESULT_OK) {
            List<Upload> urls = (List<Upload>) data.getSerializableExtra("urls");
            int position = data.getIntExtra("position", 99);

            List<String> value = new ArrayList<>();

            for (int i = 0; i < urls.size(); i++) {
                value.add(urls.get(i).url);
            }

            status.getFields().get(position).setMedia(value);
            adapter.update(status);
        }

        if (requestCode == VIDEO_RESULT && resultCode == RESULT_OK) {
            List<Upload> urls = (List<Upload>) data.getSerializableExtra("urls");
            int position = data.getIntExtra("position", 99);

            List<String> value = new ArrayList<>();

            for (int i = 0; i < urls.size(); i++) {
                value.add(urls.get(i).url);
            }

            status.getFields().get(position).setMedia(value);
            adapter.update(status);
        }
    }

    String prepareMonth(int month) {
        switch (month) {
            case 0:
                return "Января";
            case 1:
                return "Февраля";
            case 2:
                return "Марта";
            case 3:
                return "Апреля";
            case 4:
                return "Мая";
            case 5:
                return "Июня";
            case 6:
                return "Июля";
            case 7:
                return "Августа";
            case 8:
                return "Сентября";
            case 9:
                return "Октября";
            case 10:
                return "Ноября";
            case 11:
                return "Декабря";
            default:
                return "";

        }
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

        try {

            JSONObject json = new JSONObject(response.body().string());

            String result = json.getString("status");

            if (result.equals("ok")) {

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();

            } else {

                JSONArray arrJson = json.getJSONArray("missedFields");
                String string = new String( );
                for(int i = 0; i < arrJson.length(); i++)
                   string = string + arrJson.getString(i) + ", ";

                string = string.substring(0, string.length() - 1);

                Alerter.create(this)
                        .setTitle("Ошибка!")
                        .setText("Заполните поля: " + string)
                        .setDuration(3000)
                        .setBackgroundColorRes(R.color.error)
                        .show();
            }



        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
}
