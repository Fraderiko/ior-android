package ru.iorcontrol.ior.ior;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import ru.iorcontrol.ior.ior.Model.Discussion;
import ru.iorcontrol.ior.ior.Model.Order;

/**
 * Created by alexeykazinets on 01/12/2017.
 */

public class MessagesScreen extends AppCompatActivity implements MessageClickHandler {

    List<Discussion> discussions;
    Order order;

    int MESSAGE_SENT_RESULT = 3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Сообщения");

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        discussions = (List<Discussion>) bundle.getSerializable("Discussion");
        order = (Order) bundle.getSerializable("Order");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_messages);

        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();

        for (int i = 0; i < discussions.size(); i++) {

            sectionAdapter.addSection(new MessageTimeSection(this, getDate(discussions.get(i).getDate())));

            if (discussions.get(i).getAuthor().equals(Settings.getInstance().getID())) {

                if (discussions.get(i).getMessage().equals("") == false) {
                    sectionAdapter.addSection(new MessagesTextSectionRight(discussions.get(i)));
                }

                for (int j = 0; j < discussions.get(i).getImage_media().size(); j++) {
                    sectionAdapter.addSection(new MessagesImageSectionRight(this, MessagesImageSection.MEDIATYPE.IMAGE, discussions.get(i).getImage_media().get(j), this));
                }

                for (int j = 0; j < discussions.get(i).getVideo_media().size(); j++) {
                    sectionAdapter.addSection(new MessagesImageSectionRight(this, MessagesImageSection.MEDIATYPE.VIDEO, discussions.get(i).getVideo_media().get(j), this));
                }
            } else {

                if (discussions.get(i).getMessage().equals("") == false) {
                    sectionAdapter.addSection(new MessagesTextSection(discussions.get(i)));
                }

                for (int j = 0; j < discussions.get(i).getImage_media().size(); j++) {
                    sectionAdapter.addSection(new MessagesImageSection(this, MessagesImageSection.MEDIATYPE.IMAGE, discussions.get(i).getImage_media().get(j), this));
                }

                for (int j = 0; j < discussions.get(i).getVideo_media().size(); j++) {
                    sectionAdapter.addSection(new MessagesImageSection(this, MessagesImageSection.MEDIATYPE.VIDEO, discussions.get(i).getVideo_media().get(j), this));
                }
            }

        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sectionAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:
                Intent intent = new Intent(this, MessagesCreateScreen.class);
                intent.putExtra("Order", order);
                startActivityForResult(intent, MESSAGE_SENT_RESULT);
                break;
            default:
                finish();
        }

        return true;
    }

    @Override
    public void messageClicked(String url, MessagesImageSection.MEDIATYPE type) {
        if (type == MessagesImageSection.MEDIATYPE.VIDEO) {
            Intent intent = new Intent(this, VideoPlayer.class);
            intent.putExtra("url", url);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ImageGalleryScreen.class);

            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(url);

            intent.putStringArrayListExtra("urls", arrayList);
            intent.putExtra("position", 0);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MESSAGE_SENT_RESULT && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("dd MMMM HH:mm", cal).toString();
    }
}
