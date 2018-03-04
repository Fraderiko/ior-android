package ru.iorcontrol.ior.ior;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import ru.iorcontrol.ior.ior.Model.Status;

/**
 * Created by alexeykazinets on 29/11/2017.
 */

public class MediaScreen extends AppCompatActivity implements Callback<List<Upload>>, MediaScreenAdapterDeleteHandler {

    Status status;
    MediaScreenAdapter adapter;

    ArrayList<Upload> urls = new ArrayList<Upload>();

    String type;

    APIService service;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        type = getIntent().getStringExtra("type");
        position = getIntent().getIntExtra("position", 99);

        ArrayList<String > existingUlrs = getIntent().getStringArrayListExtra("urls");

        if (existingUlrs != null) {
            for (int i = 0; i < existingUlrs.size(); i++) {
                Upload upload = new Upload("", existingUlrs.get(i));
                urls.add(upload);
            }
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_order_details);
        adapter = new MediaScreenAdapter(this, urls, resolveType(type), this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Settings.getInstance().getAPIHost())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(APIService.class);

    }

    private MediaScreenAdapter.MediaScreenAdapterType resolveType(String type) {
        if (type.equals("video")) {
            return MediaScreenAdapter.MediaScreenAdapterType.VIDEO;
        } else {
            return MediaScreenAdapter.MediaScreenAdapterType.IMAGE;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_media_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_media:

                if (type.equals("image")) {
                    new ImagePicker.Builder(MediaScreen.this)
                            .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                            .directory(ImagePicker.Directory.DEFAULT)
                            .extension(ImagePicker.Extension.PNG)
                            .scale(600, 600)
                            .allowMultipleImages(true)
                            .enableDebuggingMode(true)
                            .build();
                    break;
                } else {
                    new VideoPicker.Builder(MediaScreen.this)
                            .mode(VideoPicker.Mode.CAMERA_AND_GALLERY)
                            .directory(VideoPicker.Directory.DEFAULT)
                            .extension(VideoPicker.Extension.MP4)
                            .enableDebuggingMode(true)
                            .build();
                    break;
                }
            case R.id.save_media:
                Intent intent = new Intent();
                intent.putExtra("urls", urls);
                intent.putExtra("position", position);
                setResult(RESULT_OK, intent);
                finish();
            default:
                finish();
        }

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
            upload.enqueue(this);

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
            upload.enqueue(this);

        }
    }

    @Override
    public void onResponse(Call<List<Upload>> call, Response<List<Upload>> response) {
        urls.addAll(response.body());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(Call<List<Upload>> call, Throwable t) {

    }

    @Override
    public void delete(int position) {
        urls.remove(position);
        adapter.notifyDataSetChanged();
    }
}
