package ru.iorcontrol.ior.ior.Recipient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.onesignal.OneSignal;
import com.tapadoo.alerter.Alerter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
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
import ru.iorcontrol.ior.ior.GalleryClickListener;
import ru.iorcontrol.ior.ior.StatusDetails.HorizontalGalleryAdapter;
import ru.iorcontrol.ior.ior.ImageGallery.ImageGalleryScreen;
import ru.iorcontrol.ior.ior.Login.LoginScreen;
import ru.iorcontrol.ior.ior.MediaScreen.MediaScreen;
import ru.iorcontrol.ior.ior.Messages.MessagesScreen;
import ru.iorcontrol.ior.ior.Model.AddDiscussion;
import ru.iorcontrol.ior.ior.Model.Discussion;
import ru.iorcontrol.ior.ior.Model.Field;
import ru.iorcontrol.ior.ior.Model.Order;
import ru.iorcontrol.ior.ior.Model.Upload;
import ru.iorcontrol.ior.ior.R;
import ru.iorcontrol.ior.ior.Settings;
import ru.iorcontrol.ior.ior.VideoPlayer.VideoPlayer;

/**
 * Created by alexeykazinets on 30/11/2017.
 */

public class RecipientScreen extends AppCompatActivity implements GalleryClickListener, Callback<Order>, RecepientCreateDiscussionHandler, RecepientCreateDiscussionAddImageHandler, RecepientCreateDiscussionAddVideoHandler, RecepientCreateDiscussionMediaHandler, RecipientDiscussionCountClickHandler {

    Order order;

    Retrofit retrofit;
    APIService service;
    RecyclerView recyclerView;

    ArrayList<String> video_urls = new ArrayList<String >();
    ArrayList<String> image_urls = new ArrayList<String >();

    String message;

    SectionedRecyclerViewAdapter sectionAdapter;

    public int IMAGE_RESULT = 0, VIDEO_RESULT = 1, MESSAGE_SENT_RESULT = 3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_order_details);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Settings.getInstance().getAPIHost())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(APIService.class);

        fetchOrder();
        setupPush();

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

    void fetchOrder() {
        String order = Settings.getInstance().getOrderName();

        Call<Order> getOrder = service.getOrder(order);
        getOrder.enqueue(this);

    }

    void setupRecyclerView() {
        sectionAdapter = new SectionedRecyclerViewAdapter();

        if (order.getDiscussion().size() > 0) {
            sectionAdapter.addSection(new RecipientDiscussionCountSection(order.getDiscussion().size(), this));
        }

        for (int i = 0; i < order.getStatuses().size(); i++) {
            for (int j = 0; j < order.getStatuses().get(i).getFields().size(); j++) {
                if (order.getStatuses().get(i).getFields().get(j).getRecepientvisible() == true) {
                    Field field = order.getStatuses().get(i).getFields().get(j);
                    String type = order.getStatuses().get(i).getFields().get(j).getType();
                    List<String> media = order.getStatuses().get(i).getFields().get(j).getMedia();
                    if (type.equals("text") || type.equals("digit") || type.equals("date") || type.equals("time")) {
                        sectionAdapter.addSection(new RecipientTextSection(field));
                    } else if (type.equals("video")) {
                       sectionAdapter.addSection(new RecipientMediaSection(this, media, HorizontalGalleryAdapter.TYPE.VIDEOGALLERY, this));
                       Log.d("DEBUG", "video media is " + media.toString());
                    } else if (type.equals("image")) {
                        sectionAdapter.addSection(new RecipientMediaSection(this, media, HorizontalGalleryAdapter.TYPE.IMAGEGALLERY, this));
                        Log.d("DEBUG", "image media is " + media.toString());

                    }
                }
            }
        }

        sectionAdapter.addSection("0",  new RecipientAddDiscussionSection(this, this, this, this, this, this, video_urls, image_urls, message));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sectionAdapter);
    }

    @Override
    public void fieldClicked(int fieldPosition, int elementPosition, HorizontalGalleryAdapter.TYPE type, String url) {
        if (type == HorizontalGalleryAdapter.TYPE.VIDEOGALLERY) {
            Intent intent = new Intent(this, VideoPlayer.class);
            intent.putExtra("url", url);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ImageGalleryScreen.class);

            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(url);

            intent.putStringArrayListExtra("urls", arrayList);
            intent.putExtra("position", elementPosition);
            startActivity(intent);
        }
    }

    @Override
    public void onResponse(Call<Order> call, Response<Order> response) {
        order = response.body();
        getSupportActionBar().setTitle("№" + order.number);

        setupRecyclerView();
    }

    @Override
    public void onFailure(Call<Order> call, Throwable t) {
        Log.d("DEBUG", "onFailure");
    }

    public void createDiscussion(final String message) {

        Discussion discussion = new Discussion(System.currentTimeMillis() / 1000, message, image_urls, video_urls, Settings.getInstance().getID());
        AddDiscussion addDiscussion = new AddDiscussion(order.get_id(), discussion);

        Call<Void> d = service.addDiscussion(addDiscussion);
        d.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showSuccessAlert();
                removeData();
                recyclerView.invalidate();
                fetchOrder();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    void removeData() {
        image_urls.clear();
        video_urls.clear();
        message = "";
    }

    void showSuccessAlert() {
        Alerter.create(this)
                .setText("Сообщение отправлено!")
                .setDuration(3000)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_RESULT && resultCode == RESULT_OK) {
            List<Upload> urls = (List<Upload>) data.getSerializableExtra("urls");

            ArrayList<String> value = new ArrayList<>();

            for (int i = 0; i < urls.size(); i++) {
                value.add(urls.get(i).url);
            }

            image_urls = value;
            recyclerView.invalidate();
            setupRecyclerView();
        }

        if (requestCode == VIDEO_RESULT && resultCode == RESULT_OK) {
            List<Upload> urls = (List<Upload>) data.getSerializableExtra("urls");
            int position = data.getIntExtra("position", 99);

            ArrayList<String> value = new ArrayList<>();

            for (int i = 0; i < urls.size(); i++) {
                value.add(urls.get(i).url);
            }

            video_urls = value;
            recyclerView.invalidate();
            setupRecyclerView();
        }

        if (requestCode == MESSAGE_SENT_RESULT && resultCode == RESULT_OK) {
            showSuccessAlert();
            fetchOrder();
        }
    }

    @Override
    public void addImageClicked(String message) {
        Intent intent = new Intent(this, MediaScreen.class);
        intent.putExtra("type", "image");
        intent.putStringArrayListExtra("urls", image_urls);
        startActivityForResult(intent, IMAGE_RESULT);

        this.message = message;
    }

    @Override
    public void addVideoClicked(String message) {
        Intent intent = new Intent(this, MediaScreen.class);
        intent.putExtra("type", "video");
        intent.putStringArrayListExtra("urls", video_urls);
        startActivityForResult(intent, VIDEO_RESULT);

        this.message = message;
    }

    @Override
    public void mediaClicked(HorizontalGalleryAdapter.TYPE type) {
        if (type == HorizontalGalleryAdapter.TYPE.VIDEOGALLERY) {
            Intent intent = new Intent(this, MediaScreen.class);
            intent.putExtra("type", "video");
            intent.putStringArrayListExtra("urls", video_urls);
            startActivityForResult(intent, VIDEO_RESULT);
        } else {
            Intent intent = new Intent(this, MediaScreen.class);
            intent.putExtra("type", "image");
            intent.putStringArrayListExtra("urls", image_urls);
            startActivityForResult(intent, IMAGE_RESULT);
        }
    }

    @Override
    public void messagesClicked() {
        Intent intent = new Intent(this, MessagesScreen.class);
        ArrayList<Discussion> discussions = new ArrayList<Discussion>();
        discussions.addAll(order.discussion);
        intent.putExtra("Discussion", discussions);
        intent.putExtra("Order", order);
        startActivityForResult(intent, MESSAGE_SENT_RESULT);
    }
}
