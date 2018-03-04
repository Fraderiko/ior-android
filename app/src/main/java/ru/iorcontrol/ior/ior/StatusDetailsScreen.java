package ru.iorcontrol.ior.ior;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import ru.iorcontrol.ior.ior.Model.Status;

/**
 * Created by alexeykazinets on 27/11/2017.
 */

public class StatusDetailsScreen extends AppCompatActivity implements GalleryClickListener {

    Status status;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        status = (Status) bundle.getSerializable("Status");
        getSupportActionBar().setTitle(status.name);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_order_details);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new StatusDetailsAdapter(status,this, this));

    }

    @Override
    public void fieldClicked(int fieldPosition, int elementPosition, HorizontalGalleryAdapter.TYPE type, String url) {

        if (type == HorizontalGalleryAdapter.TYPE.VIDEOGALLERY) {
            Intent intent = new Intent(this, VideoPlayer.class);
            intent.putExtra("url", status.getFields().get(fieldPosition).media.get(elementPosition));
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ImageGalleryScreen.class);

            ArrayList<String> arrayList = new ArrayList<>(status.getFields().get(fieldPosition).media.size());
            arrayList.addAll(status.getFields().get(fieldPosition).media);

            intent.putStringArrayListExtra("urls", arrayList);
            intent.putExtra("position", elementPosition);
            startActivity(intent);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
