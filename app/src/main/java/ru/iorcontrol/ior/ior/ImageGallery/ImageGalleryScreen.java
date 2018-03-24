package ru.iorcontrol.ior.ior.ImageGallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import com.afollestad.easyvideoplayer.EasyVideoPlayer;

import java.util.ArrayList;

import ru.iorcontrol.ior.ior.R;

/**
 * Created by alexeykazinets on 28/11/2017.
 */

public class ImageGalleryScreen extends AppCompatActivity {

    ArrayList<String> urls;
    int position = 0;

    private EasyVideoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_screen);

        urls = getIntent().getStringArrayListExtra("urls");
        position = getIntent().getIntExtra("position", 0);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_gallery);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageGalleryAdapter adapter = new ImageGalleryAdapter(this, urls);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(recyclerView);

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        horizontalLayoutManagaer.scrollToPosition(position);

        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
