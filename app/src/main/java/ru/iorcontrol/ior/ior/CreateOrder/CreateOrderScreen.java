package ru.iorcontrol.ior.ior.CreateOrder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.iorcontrol.ior.ior.APIService;
import ru.iorcontrol.ior.ior.Model.Discussion;
import ru.iorcontrol.ior.ior.Model.Egroup;
import ru.iorcontrol.ior.ior.Model.Group;
import ru.iorcontrol.ior.ior.Model.NewOrder;
import ru.iorcontrol.ior.ior.Model.OrderTemplate;
import ru.iorcontrol.ior.ior.Model.User;
import ru.iorcontrol.ior.ior.R;
import ru.iorcontrol.ior.ior.Settings;

/**
 * Created by alexeykazinets on 30/11/2017.
 */

public class CreateOrderScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner order_type;
    Spinner order_client;

    ArrayAdapter<String> typeAdapter;
    ArrayAdapter<String> clientAdapter;

    Retrofit retrofit;
    APIService service;

    TextView order_number;
    TextView order_comment;
    TextView order_email;
    TextView order_phone;

    Button create_order;

    String client;

    List<User> canWorkWithUsers;
    List<Egroup> canWorkWithGroups;
    List<OrderTemplate> orderTemplates;
    User selectedUser;
    Egroup selectedGroup;
    OrderTemplate selectedOrderTemplate;

    Boolean userSelected = false;
    Boolean orderSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        order_type = (Spinner) findViewById(R.id.order_type);
        order_client = (Spinner) findViewById(R.id.order_client);

        order_type.setOnItemSelectedListener(this);
        order_client.setOnItemSelectedListener(this);

        order_number = (TextView) findViewById(R.id.order_number);
        order_comment = (TextView) findViewById(R.id.order_comment);
        order_email = (TextView) findViewById(R.id.order_email);
        order_phone = (TextView) findViewById(R.id.order_phone);


        create_order = (Button) findViewById(R.id.create_order_button);

        create_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOrder();
            }
        });

        setupOrderTypeSpinner();
        setupOrderClientSpinner();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Settings.getInstance().getAPIHost())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(APIService.class);

        getData();
    }

    void sendOrder() {

        if (order_number.getText().toString().equals("")) {
            showErrorAlert("Укажите номер!");
            return;
        }

        if (userSelected == false) {
            if (Settings.getInstance().getType().equals("client")) {
                showErrorAlert("Выберите исполителя!");
                return;
            } else {
                showErrorAlert("Выберите клиента!");
                return;
            }
        }

        if (orderSelected == false) {
            showErrorAlert("Выберите тип заказа!");
            return;
        }


        Call<ResponseBody> group = service.getGroup(getClientID());
        group.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    JSONObject json = new JSONObject(response.body().string());

                    String groupID = json.getString("_id");

                    NewOrder order = new NewOrder();

                    order.setNumber(order_number.getText().toString() + '-' + randomString(5));
                    order.setDate(System.currentTimeMillis());
                    order.setUpdated(System.currentTimeMillis());
                    order.setType(selectedOrderTemplate.get_id());
                    order.setCurrentstatus("Создан");
                    order.setAssignedTo(resolveAssignedTo());
                    order.assignedToGroup = resolveAssignedToGroup();
                    order.setComment(order_comment.getText().toString());
                    order.setStatuses(selectedOrderTemplate.getStatuses());
                    order.setCreatedBy(Settings.getInstance().getID());
                    order.setGroup(groupID);
                    order.setRecipientmail(order_email.getText().toString());
                    order.setRecipientphone(order_phone.getText().toString());
                    order.setClient(resolveClient());
                    order.setCancelReason("");
                    order.setDiscussion(new ArrayList<Discussion>());
                    order.setArchived(false);

                    Call<ResponseBody> newOrder = service.createOrder(order);

                    newOrder.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {

                                JSONObject json = new JSONObject(response.body().string());

                                String result = json.getString("result");

                                if (result.equals("ok")) {

                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();

                                } else {
                                    showErrorAlert("Заказ с таким номером уже существует!");
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
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }



    static final String AB = "0123456789abcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    void showCreatedAlert() {
        Alerter.create(this)
                .setText("Заказ успешно создан!")
                .setDuration(3000)
                .show();
    }

    void showErrorAlert(String message) {
        Alerter.create(this)
                .setTitle("Ошибка!")
                .setText(message)
                .setDuration(3000)
                .setBackgroundColorRes(R.color.error)
                .show();
    }

    String resolveAssignedTo() {
        if (Settings.getInstance().getType().equals("client")) {

            if (selectedUser != null) {
                return selectedUser.get_id();
            } else {
                return null;
            }

        } else {
            return Settings.getInstance().getID();
        }
    }

    private String resolveAssignedToGroup() {
        if (selectedGroup != null) {
            return selectedGroup._id;
        } else {
            return null;
        }
    }

    String resolveClient() {
        if (Settings.getInstance().getType().equals("client")) {
            return Settings.getInstance().getID();
        } else {
            return selectedUser.get_id();
        }
    }

    String getClientID() {
        if (Settings.getInstance().getType().equals("client")) {
            return Settings.getInstance().getID();
        } else {
            return selectedUser._id;
        }
    }

    void getData() {
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("_id", Settings.getInstance().getID());

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), paramObject.toString());

//            Call<List<OrderTemplate>> templates = service.getTemplates();
            final Call<Group> canWorkWith = service.getCanWorkWith(body);
//
//            templates.enqueue(new Callback<List<OrderTemplate>>() {
//                @Override
//                public void onResponse(Call<List<OrderTemplate>> call, Response<List<OrderTemplate>> response) {
//                    orderTemplates = response.body();
//
//                    typeAdapter.add("Тип");
//
//                    for (int i = 0; i < orderTemplates.size(); i++) {
//                        typeAdapter.add(orderTemplates.get(i).getName());
//                    }
//
//
//                    order_type.setAdapter(typeAdapter);
////                    order_type.setSelection(typeAdapter.getCount());
//                }
//
//                @Override
//                public void onFailure(Call<List<OrderTemplate>> call, Throwable t) {
//
//                }
//            });

            canWorkWith.enqueue(new Callback<Group>() {
                @Override
                public void onResponse(Call<Group> call, Response<Group> response) {

                    orderTemplates = response.body().getOrders();

                    typeAdapter.add("Тип");

                    for (int i = 0; i < orderTemplates.size(); i++) {
                        typeAdapter.add(orderTemplates.get(i).getName());
                    }


                    order_type.setAdapter(typeAdapter);

                    order_type.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            orderSelected = true;
                            return false;
                        }
                    });

                    canWorkWithUsers = response.body().canworkwith;
                    canWorkWithGroups = response.body().canworkwithgroups;
                    List <User> canWorkWith = canWorkWithUsers;
                    List <Egroup> canWorkWithG = canWorkWithGroups;

                    clientAdapter.add(getUserName());

                    for (int i = 0; i < canWorkWith.size(); i++) {
                        clientAdapter.add(canWorkWith.get(i).getName());
                    }

                    if (Settings.getInstance().getType().equals("client")) {
                        for (int i = 0; i < canWorkWithG.size(); i++) {
                            clientAdapter.add(canWorkWithG.get(i).name);
                        }
                    }

                    order_client.setAdapter(clientAdapter);
//                    order_client.setSelection(clientAdapter.getCount());

                    order_client.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            userSelected = true;
                            return false;
                        }
                    });
                }

                @Override
                public void onFailure(Call<Group> call, Throwable t) {

                }
            });

        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    void setupOrderTypeSpinner() {
        typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item) {

//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//
//                View v = super.getView(position, convertView, parent);
//                if (position == getCount()) {
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
//                }
//
//                return v;
//            }
//
//            @Override
//            public int getCount() {
//                return super.getCount();
//            }

        };

        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    String getUserName() {
        if (Settings.getInstance().getType().equals("client")) {
            return "Исполнитель";
        } else {
            return "Клиент";
        }
    }

    void setupOrderClientSpinner() {
        clientAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item) {

//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//
//                View v = super.getView(position, convertView, parent);
//                if (position == getCount()) {
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
//                }
//
//                return v;
//            }
//
//            @Override
//            public int getCount() {
//                return super.getCount();
//            }

        };

        clientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        int j = 0;

        if (i == 0) {
            j = 0;
        } else {
            j = i - 1;
        }

        if (adapterView.getId() == R.id.order_type && orderTemplates != null) {
            selectedOrderTemplate = orderTemplates.get(j);
        } else if (adapterView.getId() == R.id.order_client && canWorkWithUsers != null) {
            String result = clientAdapter.getItem(j);

            for (int g = 0; g < canWorkWithGroups.size(); g++) {
                if (canWorkWithGroups.get(g).name.equals(clientAdapter.getItem(i))) {
                    selectedGroup = canWorkWithGroups.get(j - canWorkWithUsers.size());
                    return;
                }
            }

            for (int g = 0; g < canWorkWithUsers.size(); g++) {
                if (canWorkWithUsers.get(g).name.equals(clientAdapter.getItem(i))) {
                    selectedUser = canWorkWithUsers.get(j);
                    return;
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
