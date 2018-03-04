package ru.iorcontrol.ior.ior;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import ru.iorcontrol.ior.ior.Model.AddDiscussion;
import ru.iorcontrol.ior.ior.Model.CheckStatusRequest;
import ru.iorcontrol.ior.ior.Model.Group;
import ru.iorcontrol.ior.ior.Model.NewOrder;
import ru.iorcontrol.ior.ior.Model.Order;
import ru.iorcontrol.ior.ior.Model.OrderTemplate;
import ru.iorcontrol.ior.ior.Model.ResultResponse;
import ru.iorcontrol.ior.ior.Model.User;

/**
 * Created by me on 23/11/2017.
 */

public interface APIService {

    @POST("users/auth")
    Call<ResponseBody> auth(@Body RequestBody body);

    @POST("/group-by-user")
    Call<Group> getCanWorkWith(@Body RequestBody body);

    @POST("/order-templates")
    Call<List<OrderTemplate>> getTemplates();

    @POST("/group/{id}")
    Call<ResponseBody> getGroup(@Path("id") String id);

    @POST("/order/create")
    Call<ResponseBody> createOrder(@Body NewOrder order);

    @POST("/order-by-group/{id}")
    Call<List<Order>> ordersByGroup(@Path("id") String id);

    @POST("/order-by-employee/{id}")
    Call<List<Order>> ordersForEmployee(@Path("id") String id);

    @Multipart
    @POST("/upload")
    Call<List<Upload>> upload(@Part List<MultipartBody.Part> uploads);

    @POST("/order/set-status")
    Call<ResponseBody> updateOrder(@Body Order order);

    @GET("/order/{name}")
    Call<Order> getOrder(@Path("name") String name);

    @POST("/order/add-discussion")
    Call<Void> addDiscussion(@Body AddDiscussion body);

    @POST("/user/{id}")
    Call<User> getUser(@Path("id") String id);

    @POST("/users/update")
    Call<Void> updateUser(@Body User user);

    @POST("/feedback")
    Call<Void> feedback(@Body RequestBody body);

    @POST("/addfavorder")
    Call<Void> addFav(@Body RequestBody body);

    @POST("/removefavorder")
    Call<Void> removeFav(@Body RequestBody body);

    @POST("/users/set-push-id")
    Call<Void> setPush(@Body RequestBody body);

    @POST("/order/update")
    Call<Void> editOrder(@Body Order order);

    @POST("/egroup-user")
    Call<ResultResponse> checkStatusPermission(@Body CheckStatusRequest request);
}
