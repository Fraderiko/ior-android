package ru.iorcontrol.ior.ior.StatusDetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.easyvideoplayer.EasyVideoPlayer;

import ru.iorcontrol.ior.ior.R;

/**
 * Created by alexeykazinets on 28/11/2017.
 */

public class FieldTextScreen extends AppCompatActivity {

    TextView value;
    Button okButton;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_field);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Boolean digitKeyboard = getIntent().getBooleanExtra("digit", false);
        position = getIntent().getIntExtra("position", 99);

        String existingValue = getIntent().getStringExtra("value");

        value = findViewById(R.id.textfield);
        okButton = findViewById(R.id.textfield_save);

        if (existingValue != null) {
            value.setText(existingValue);
        }

        getSupportActionBar().setTitle("");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("value", String.valueOf(value.getText()));
                intent.putExtra("position", position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        if (digitKeyboard == true) {
            value.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
