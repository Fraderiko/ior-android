package ru.iorcontrol.ior.ior.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.client.IO;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.iorcontrol.ior.ior.APIService;
import ru.iorcontrol.ior.ior.ImageGallery.ImageGalleryScreen;
import ru.iorcontrol.ior.ior.Model.ChatMessage;
import ru.iorcontrol.ior.ior.Model.Order;
import ru.iorcontrol.ior.ior.R;
import ru.iorcontrol.ior.ior.Settings;
import ru.iorcontrol.ior.ior.Model.Upload;
import ru.iorcontrol.ior.ior.VideoPlayer.VideoPlayer;

/**
 * Created by alexeykazinets on 25/01/2018.
 */

public class ChatScreen extends AppCompatActivity implements ChatMessageClickHandler {

    Retrofit retrofit;
    APIService service;
    Order order;
    List<ChatMessage> messages = new ArrayList<ChatMessage>();

    ChatRecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    ImageButton addVideo;
    ImageButton addPhoto;
    EditText text;
    Button sendText;

    Socket socket;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Сообщения");

        order = (Order) getIntent().getSerializableExtra("order");

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Settings.getInstance().getAPIHost())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(APIService.class);

        Call<Order> getOrder = service.getOrder(order.getNumber());

        getOrder.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                messages = response.body().getMessages();
                setupCollectionView();
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

            }
        });

        addVideo = findViewById(R.id.chat_video);
        addPhoto = findViewById(R.id.chat_image);
        sendText = findViewById(R.id.chat_submit);
        text = findViewById(R.id.chat_edit_text);


        try {
            setupSocket();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new VideoPicker.Builder(ChatScreen.this)
                        .mode(VideoPicker.Mode.CAMERA_AND_GALLERY)
                        .directory(VideoPicker.Directory.DEFAULT)
                        .extension(VideoPicker.Extension.MP4)
                        .enableDebuggingMode(true)
                        .build();
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImagePicker.Builder(ChatScreen.this)
                        .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.PNG)
                        .scale(600, 600)
                        .allowMultipleImages(true)
                        .enableDebuggingMode(true)
                        .build();
            }
        });

        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text.getText().toString().equals("") == false) {
                    ChatMessage chatMessage = new ChatMessage(System.currentTimeMillis(), "TEXT", text.getText().toString(), order.get_id(), Settings.getInstance().getID());
                    text.setText("");
                    try {
                        postMessage(chatMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    void setupSocket() throws URISyntaxException {

        socket = IO.socket(  Settings.getInstance().getAPIHost() + ":3001");
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                socket.emit("_id", Settings.getInstance().getID());
            }

        }).on(order.get_id(), new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Gson gson = new Gson();
                JSONObject obj = (JSONObject)args[0];
                String json_string = obj.toString();
                ChatMessage message= new Gson().fromJson(json_string, ChatMessage.class);
                messages.add(message);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateRecyclerView();
                    }
                });
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {}

        });
        socket.connect();

    }

    private void updateRecyclerView() {
        adapter.refresh(messages);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    void setupCollectionView() {
        recyclerView = (RecyclerView) findViewById(R.id.acitivity_chat_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatRecyclerViewAdapter(messages, this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths = (List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);

            ArrayList<MultipartBody.Part> uploads = new ArrayList<MultipartBody.Part>();

            for (int i = 0; i < mPaths.size(); i++) {
                File file = new File(mPaths.get(i));
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
                uploads.add(body);
            }

            Call<List<Upload>> upload = service.upload(uploads);
            upload.enqueue(new Callback<List<Upload>>() {
                @Override
                public void onResponse(Call<List<Upload>> call, Response<List<Upload>> response) {
                    for (int i = 0; i < response.body().size(); i++) {
                        ChatMessage chatMessage = new ChatMessage(System.currentTimeMillis(), "IMAGE", response.body().get(i).getUrl(), order.get_id(), Settings.getInstance().getID());
                        try {
                            postMessage(chatMessage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Upload>> call, Throwable t) {

                }
            });

        }

        if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths = (List<String>) data.getSerializableExtra(VideoPicker.EXTRA_VIDEO_PATH);

            ArrayList<MultipartBody.Part> uploads = new ArrayList<MultipartBody.Part>();

            for (int i = 0; i < mPaths.size(); i++) {
                File file = new File(mPaths.get(i));
                RequestBody reqFile = RequestBody.create(MediaType.parse("video/mp4"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
                uploads.add(body);
            }

            Call<List<Upload>> upload = service.upload(uploads);
            upload.enqueue(new Callback<List<Upload>>() {
                @Override
                public void onResponse(Call<List<Upload>> call, Response<List<Upload>> response) {

                    for (int i = 0; i < response.body().size(); i++) {
                        ChatMessage chatMessage = new ChatMessage(System.currentTimeMillis(), "VIDEO", response.body().get(i).getUrl(), order.get_id(), Settings.getInstance().getID());
                        try {
                            postMessage(chatMessage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }

                @Override
                public void onFailure(Call<List<Upload>> call, Throwable t) {

                }
            });

        }
    }

    private void postMessage(ChatMessage message) throws JSONException {

        JSONObject item = new JSONObject();
        item.put("date", message.getDate());
        item.put("order", message.getOrder());
        item.put("type", message.getType());
        item.put("username", message.getUsername());
        item.put("value", message.getValue());

        String json = item.toString();

        socket.emit("messageString", json);
    }

    @Override
    public void videoClicked(String url) {
        Intent intent = new Intent(this, VideoPlayer.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    public void imageClicked(String url) {
        Intent intent = new Intent(this, ImageGalleryScreen.class);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(url);

        intent.putStringArrayListExtra("urls", arrayList);
        intent.putExtra("position", 0);
        startActivity(intent);
    }
}
