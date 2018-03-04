package ru.iorcontrol.ior.ior.Chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.iorcontrol.ior.ior.APIService;
import ru.iorcontrol.ior.ior.Model.ChatMessage;
import ru.iorcontrol.ior.ior.Model.User;
import ru.iorcontrol.ior.ior.R;
import ru.iorcontrol.ior.ior.Settings;

/**
 * Created by alexeykazinets on 25/01/2018.
 */

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ChatMessage> messages;
    ChatMessageClickHandler clickHandler;
    private Retrofit retrofit;
    private APIService service;

    ChatRecyclerViewAdapter(List<ChatMessage> messages, Context context, ChatMessageClickHandler clickHandler) {
        this.context = context;
        this.messages = messages;
        this.clickHandler = clickHandler;
    }

    private final int TEXT = 0, IMAGE = 1, VIDEO = 2, TEXT_SELF = 3, IMAGE_SELF = 4, VIDEO_SELF = 5;

    @Override
    public int getItemViewType(int position) {

        if (messages.get(position).getType().equals("TEXT") && messages.get(position).getUsername().equals(Settings.getInstance().getID())) {
            return TEXT_SELF;
        }

        if (messages.get(position).getType().equals("TEXT")) {
            return TEXT;
        }

        if (messages.get(position).getType().equals("VIDEO") && messages.get(position).getUsername().equals(Settings.getInstance().getID())) {
            return VIDEO_SELF;
        }

        if (messages.get(position).getType().equals("VIDEO")) {
            return VIDEO;
        }

        if (messages.get(position).getType().equals("IMAGE") && messages.get(position).getUsername().equals(Settings.getInstance().getID())) {
            return IMAGE_SELF;
        }

        if (messages.get(position).getType().equals("IMAGE")) {
            return IMAGE;
        }

        return -1;

    }

    public void refresh(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    private class TextCellViewHolder extends RecyclerView.ViewHolder {

        private TextView value;
        private TextView date;
        private TextView user;

        public TextCellViewHolder(View itemView) {
            super(itemView);

            value = (TextView) itemView.findViewById(R.id.text_message_bubble);
            date = (TextView) itemView.findViewById(R.id.message_date);
            user = (TextView) itemView.findViewById(R.id.message_name);
        }
    }

    private class TextCellSelfViewHolder extends RecyclerView.ViewHolder {

        private TextView value;
        private TextView date;
        private TextView user;


        public TextCellSelfViewHolder(View itemView) {
            super(itemView);

            value = (TextView) itemView.findViewById(R.id.text_message_bubble);
            date = (TextView) itemView.findViewById(R.id.message_date);
            user = (TextView) itemView.findViewById(R.id.message_name);


        }
    }

    class ImageSelfViewHolder extends RecyclerView.ViewHolder {

        private ImageView message;
        private TextView date;
        private TextView user;



        public ImageSelfViewHolder(View itemView) {
            super(itemView);

            message = (ImageView) itemView.findViewById(R.id.image_message_bubble_right);
            date = (TextView) itemView.findViewById(R.id.message_date);
            user = (TextView) itemView.findViewById(R.id.message_name);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ChatMessage message = messages.get(getAdapterPosition());

                    if (message.getType().equals("IMAGE")) {
                        clickHandler.imageClicked(message.getValue());
                    } else {
                        clickHandler.videoClicked(message.getValue());
                    }

                }
            });
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView message;
        private TextView date;
        private TextView user;



        public ImageViewHolder(View itemView) {
            super(itemView);

            message = (ImageView) itemView.findViewById(R.id.image_message_bubble);
            date = (TextView) itemView.findViewById(R.id.message_date);
            user = (TextView) itemView.findViewById(R.id.message_name);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatMessage message = messages.get(getAdapterPosition());

                    if (message.getType().equals("IMAGE")) {
                        clickHandler.imageClicked(message.getValue());
                    } else {
                        clickHandler.videoClicked(message.getValue());
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TEXT:
                View v1 = inflater.inflate(R.layout.message_text, parent, false);
                viewHolder = new TextCellViewHolder(v1);
                break;

            case TEXT_SELF:
                View v2 = inflater.inflate(R.layout.message_text_right, parent, false);
                viewHolder = new TextCellSelfViewHolder(v2);
                break;

            case VIDEO:
                View v3 = inflater.inflate(R.layout.message_image, parent, false);
                viewHolder = new ImageViewHolder(v3);
                break;

            case VIDEO_SELF:
                View v = inflater.inflate(R.layout.message_image_right, parent, false);
                viewHolder = new ImageSelfViewHolder(v);
                break;

            case IMAGE:
                View v4 = inflater.inflate(R.layout.message_image, parent, false);
                viewHolder = new ImageViewHolder(v4);
                break;

            case IMAGE_SELF:
                View v5 = inflater.inflate(R.layout.message_image_right, parent, false);
                viewHolder = new ImageSelfViewHolder(v5);
                break;

            default:
                View v6 = inflater.inflate(R.layout.message_text, parent, false);
                viewHolder = new TextCellViewHolder(v6);
                break;

        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Settings.getInstance().getAPIHost())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(APIService.class);

        Call<User> call = service.getUser(messages.get(position).getUsername());

        switch (holder.getItemViewType()) {

            case TEXT: {
                TextCellViewHolder cellViewHolder = (TextCellViewHolder) holder;
                ((TextCellViewHolder) holder).value.setText(messages.get(position).getValue());
                ((TextCellViewHolder) holder).date.setText(getDate(messages.get(position).getDate()));

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        ((TextCellViewHolder) holder).user.setText(response.body().getName());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

                break;
            }

            case TEXT_SELF: {
                TextCellSelfViewHolder cellViewHolder = (TextCellSelfViewHolder) holder;
                ((TextCellSelfViewHolder) holder).value.setText(messages.get(position).getValue());
                ((TextCellSelfViewHolder) holder).date.setText(getDate(messages.get(position).getDate()));

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        ((TextCellSelfViewHolder) holder).user.setText(response.body().getName());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

                break;
            }

            case IMAGE_SELF: {
                ImageSelfViewHolder cellViewHolder = (ImageSelfViewHolder) holder;
                Glide
                        .with(context.getApplicationContext())
                        .load(Settings.getInstance().getAPIHost() + messages.get(position).getValue())
                        .into((((ImageSelfViewHolder) holder).message));
                ((ImageSelfViewHolder) holder).date.setText(getDate(messages.get(position).getDate()));

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        ((ImageSelfViewHolder) holder).user.setText(response.body().getName());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

                break;
            }

            case IMAGE: {
                ImageViewHolder cellViewHolder = (ImageViewHolder) holder;
                Glide
                        .with(context.getApplicationContext())
                        .load(Settings.getInstance().getAPIHost() + messages.get(position).getValue())
                        .into((((ImageViewHolder) holder).message));
                ((ImageViewHolder) holder).date.setText(getDate(messages.get(position).getDate()));

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        ((ImageViewHolder) holder).user.setText(response.body().getName());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

                break;
            }

            case VIDEO: {
                ImageViewHolder cellViewHolder = (ImageViewHolder) holder;

                String uri = "@drawable/play_video";  // where myresource (without the extension) is the file
                int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                Drawable res = context.getResources().getDrawable(imageResource);
                ((ImageViewHolder) holder).message.setImageDrawable(res);
                ((ImageViewHolder) holder).date.setText(getDate(messages.get(position).getDate()));

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        ((ImageViewHolder) holder).user.setText(response.body().getName());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

                break;
            }

            case VIDEO_SELF: {
                ImageSelfViewHolder cellViewHolder = (ImageSelfViewHolder) holder;

                String uri = "@drawable/play_video";  // where myresource (without the extension) is the file
                int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                Drawable res = context.getResources().getDrawable(imageResource);
                ((ImageSelfViewHolder) holder).message.setImageDrawable(res);
                ((ImageSelfViewHolder) holder).date.setText(getDate(messages.get(position).getDate()));

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        ((ImageSelfViewHolder) holder).user.setText(response.body().getName());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

                break;
            }
        }
    }

    @Override
    public int getItemCount() {

        return messages.size();
    }

    private String getDate(double time) {

        long t = (long) time;

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(t);
        String date = DateFormat.format("dd MMMM HH:mm", cal).toString();
        return date;
    }

}
