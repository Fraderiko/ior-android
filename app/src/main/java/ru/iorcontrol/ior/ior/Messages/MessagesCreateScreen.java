package ru.iorcontrol.ior.ior.Messages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.iorcontrol.ior.ior.APIService;
import ru.iorcontrol.ior.ior.GalleryClickListener;
import ru.iorcontrol.ior.ior.StatusDetails.HorizontalGalleryAdapter;
import ru.iorcontrol.ior.ior.MediaScreen.MediaScreen;
import ru.iorcontrol.ior.ior.Model.AddDiscussion;
import ru.iorcontrol.ior.ior.Model.Discussion;
import ru.iorcontrol.ior.ior.Model.Order;
import ru.iorcontrol.ior.ior.Model.Upload;
import ru.iorcontrol.ior.ior.R;
import ru.iorcontrol.ior.ior.Settings;

/**
 * Created by alexeykazinets on 03/12/2017.
 */

public class MessagesCreateScreen extends AppCompatActivity implements GalleryClickListener {

    EditText textfield_discussion;
    Button add_image;
    Button add_video;
    RecyclerView recipient_image_recyclerview;
    RecyclerView recipient_video_recyclerview;
    Button button_discussion;

    ArrayList<String> image_urls = new ArrayList<String>();
    ArrayList<String> video_urls = new ArrayList<String>();

    int IMAGE_RESULT = 0, VIDEO_RESULT = 1;

    HorizontalGalleryAdapter imageadapter;
    HorizontalGalleryAdapter videoadapter;

    Retrofit retrofit;
    APIService service;

    Order order;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.field_recipient_add_discussion);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        order = (Order) bundle.getSerializable("Order");

        textfield_discussion = (EditText) findViewById(R.id.textfield_discussion);
        add_image = (Button) findViewById(R.id.add_image);
        add_video = (Button) findViewById(R.id.add_video);

        recipient_image_recyclerview = (RecyclerView) findViewById(R.id.recipient_image_recyclerview);
        recipient_video_recyclerview = (RecyclerView) findViewById(R.id.recipient_video_recyclerview);

        button_discussion = (Button) findViewById(R.id.button_discussion);

        button_discussion.setVisibility(View.GONE);

        setupImagesRecyclerView();
        setupVideoRecyclerView();

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });

        add_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVideo();
            }
        });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Settings.getInstance().getAPIHost())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(APIService.class);
    }

    void setupImagesRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recipient_image_recyclerview.setLayoutManager(layoutManager);
        imageadapter = new HorizontalGalleryAdapter(this, HorizontalGalleryAdapter.TYPE.IMAGEGALLERY, image_urls, 0, this);
        recipient_image_recyclerview.setAdapter(imageadapter);
    }

    void setupVideoRecyclerView() {
        LinearLayoutManager videolayoutManager = new LinearLayoutManager(this);
        videolayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recipient_video_recyclerview.setLayoutManager(videolayoutManager);
        videoadapter = new HorizontalGalleryAdapter(this, HorizontalGalleryAdapter.TYPE.VIDEOGALLERY, video_urls, 0, this);
        recipient_video_recyclerview.setAdapter(videoadapter);
    }

    void addImage() {
        Intent intent = new Intent(this, MediaScreen.class);
        intent.putExtra("type", "image");
        intent.putStringArrayListExtra("urls", image_urls);
        startActivityForResult(intent, IMAGE_RESULT);
    }

    void addVideo() {
        Intent intent = new Intent(this, MediaScreen.class);
        intent.putExtra("type", "video");
        intent.putStringArrayListExtra("urls", video_urls);
        startActivityForResult(intent, VIDEO_RESULT);
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
            recipient_image_recyclerview.invalidate();
            setupImagesRecyclerView();
        }

        if (requestCode == VIDEO_RESULT && resultCode == RESULT_OK) {
            List<Upload> urls = (List<Upload>) data.getSerializableExtra("urls");
            int position = data.getIntExtra("position", 99);

            ArrayList<String> value = new ArrayList<>();

            for (int i = 0; i < urls.size(); i++) {
                value.add(urls.get(i).url);
            }

            video_urls = value;
            recipient_video_recyclerview.invalidate();
            setupVideoRecyclerView();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:
                createDiscussion(textfield_discussion.getText().toString());
                break;
            default:
                finish();
        }

        return true;
    }

    @Override
    public void fieldClicked(int fieldPosition, int elementPosition, HorizontalGalleryAdapter.TYPE type, String url) {

    }

    public void createDiscussion(final String message) {

        Discussion discussion = new Discussion(System.currentTimeMillis() / 1000, message, image_urls, video_urls, Settings.getInstance().getID());
        AddDiscussion addDiscussion = new AddDiscussion(order.get_id(), discussion);

        Call<Void> d = service.addDiscussion(addDiscussion);
        d.enqueue(new Callback<Void>() {
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

    }
}
