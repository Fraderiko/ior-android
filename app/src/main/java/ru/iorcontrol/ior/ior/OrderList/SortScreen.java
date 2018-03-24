package ru.iorcontrol.ior.ior.OrderList;

import android.app.DatePickerDialog;
import java.text.SimpleDateFormat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Date;

import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import ru.iorcontrol.ior.ior.R;

/**
 * Created by alexeykazinets on 04/12/2017.
 */

public class SortScreen extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    TextView start_filter_value;
    TextView end_filter_value;

    Button start_filter_button;
    Button end_filter_button;
    Button sort_by_dates_button;

    final Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);

    String startdate;
    String enddate;

    RadioGroup sort_segmented;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Сортировка");

        start_filter_button = (Button) findViewById(R.id.start_filter_button);
        end_filter_button = (Button) findViewById(R.id.end_filter_button);
        sort_by_dates_button = (Button) findViewById(R.id.sort_by_dates_button);

        start_filter_value = (TextView) findViewById(R.id.start_filter_value);
        end_filter_value = (TextView) findViewById(R.id.end_filter_value);

        sort_segmented = (RadioGroup) findViewById(R.id.segmented2);

        start_filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStartPicker();
            }
        });

        end_filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEndPicker();
            }
        });

        sort_by_dates_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long startDate = prepareTimeStamp(startdate);
                long endDate = prepareEndDate(prepareTimeStamp(enddate));

                Intent intent = new Intent();
                intent.putExtra("FilterType", "date");
                intent.putExtra("startDate", startDate);
                intent.putExtra("endDate", endDate);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        sort_segmented.setOnCheckedChangeListener(this);
    }

    long prepareTimeStamp(String input_date) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date date = formatter.parse(input_date);
            return date.getTime();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    long prepareEndDate(Long date) {
        return date + 86400000; // прибавлям 24 часа, чтобы это был конец дня
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_orders_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                Intent intent = new Intent();
                intent.putExtra("FilterType", "clear");
                setResult(RESULT_OK, intent);
                finish();
            default:
                finish();
                break;
        }

        return true;
    }

    void showStartPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        start_filter_value.setText(String.valueOf(String.valueOf(dayOfMonth) + " " + prepareMonth(monthOfYear) + " " + String.valueOf(year)));
                        startdate = String.valueOf(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(year));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    void showEndPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        end_filter_value.setText(String.valueOf(String.valueOf(dayOfMonth) + " " + prepareMonth(monthOfYear) + " " + String.valueOf(year)));
                        enddate = String.valueOf(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(year));

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
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
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.sort_status:
                Intent status_intent = new Intent();
                status_intent.putExtra("FilterType", "status");
                setResult(RESULT_OK, status_intent);
                finish();
                break;
            case R.id.sort_type:
                Intent type_intent = new Intent();
                type_intent.putExtra("FilterType", "type");
                setResult(RESULT_OK, type_intent);
                finish();
                break;
            case R.id.sort_user:
                Intent sort_intent = new Intent();
                sort_intent.putExtra("FilterType", "user");
                setResult(RESULT_OK, sort_intent);
                finish();
                break;
        }
    }
}
