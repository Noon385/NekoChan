package com.example.nekochancoffee;

import com.example.nekochancoffee.Activities.AddOrder;
import com.example.nekochancoffee.Model.Adopt;
import com.example.nekochancoffee.Model.Cat;
import com.example.nekochancoffee.Model.Category;
import com.example.nekochancoffee.Model.Customer;
import com.example.nekochancoffee.Model.Drink;
import com.example.nekochancoffee.Model.LoginResponse;
import com.example.nekochancoffee.Model.Order;
import com.example.nekochancoffee.Model.Payment;
import com.example.nekochancoffee.Model.Table;
import com.example.nekochancoffee.Model.User;
import com.google.gson.JsonObject;


import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.PUT;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(
            @Field("username") String username,
            @Field("password") String password

    );
    //api lấy mèo
    @GET("cats")
    Call<List<Cat>> getCats();

    // api thêm mèo
    @Multipart
    @POST("cats")
    Call<Void> addCat(@Part("cat_name") RequestBody cat_name,
                      @Part("cat_status") RequestBody cat_status,
                      @Part("cat_price") RequestBody cat_price,
                      @Part MultipartBody.Part cat_image);
    //delete cat
    @DELETE("cats/{id}")
    Call<Void>  deleteCat(@Path("id") int catId);
    //update cat
    @GET("cats/{id}")
    Call<Cat> getCatById(@Path("id") int id);
    @Multipart
    @PUT("cats/{catId}")
    Call<Void> updateCat(
            @Path("catId") int catId,
            @Part("cat_name") RequestBody cat_name,
            @Part("cat_status") RequestBody cat_status,
            @Part("cat_price") RequestBody cat_price,
            @Part MultipartBody.Part cat_image
    );

    // Lấy danh sách tất cả người dùng////////////////////////////////////////////////////////////
    @GET("/users")
    Call<List<User>> getAllUsers();

    // Thêm người dùng mới
    @POST("/users")
    Call<User> addUser(@Body User user);

    // Cập nhật thông tin người dùng
    @PUT("/users/{id}")
    Call<User> updateUser(@Path("id") int id, @Body User user);

    // Xóa người dùng
    @DELETE("/users/{id}")
    Call<Void> deleteUser(@Path("id") int id);



    // Lấy danh sách tất cả khách hàng/////////////////////////////////////////////////////////////
    @GET("customers")
    Call<List<Customer>> getCustomers();

    // Thêm khách hàng mới
    @POST("customers")
    Call<Customer> addCustomer(@Body Customer customer);

    // Cập nhật thông tin khách hàng
    @PUT("customers/{id}")
    Call<Customer> updateCustomer(@Path("id") int id, @Body Customer customer);

    // Xóa khách hàng
    @DELETE("customers/{id}")
    Call<Void> deleteCustomer(@Path("id") int id);


    // Nhận nuôi ///////////////////////////////////////////////////////////////////////////////
    // Lấy danh sách adopt
    @GET("adopts")
    Call<List<Adopt>> getAdopts();
    @GET("adopts/customer/{customer_id}")
    Call<List<Adopt>> getAdoptByCustomerId(@Path("customer_id") int customerId);

    // Thêm mới adopt
    @FormUrlEncoded
    @POST("adopts")
    Call<Void> addAdopt(
            @Field("cat_id") int catId,
            @Field("customer_id") int customerId
    );

    // Cập nhật thông tin adopt
    @FormUrlEncoded
    @PUT("adopts/{id}")
    Call<Void> updateAdopt(
            @Path("adopt_id") int adopt_id,
            @Path ("cat_id") int cat_id,
            @Path ("customer_id") int cutomer_id
        );

    // Xóa adopt
    @DELETE("adopts/{id}")
    Call<Void> deleteAdopt(@Path("id") int id);

    @GET("catsatstore")
    Call<List<Cat>> getCatsAtStore();

    //lấy danh sách loại món////////////////////////////////////////////////
    @GET("categories")
    Call<List<Category>> getCategory();
    // Thêm loại món
    @Multipart
    @POST("categories")
    Call<Void> addCategory(
            @Part("category_name") RequestBody category_name,
            @Part MultipartBody.Part category_image
    );
    // Xóa loại món
    @DELETE("categories/{id}")
    Call<Void> deleteCategory(@Path("id") int categoryId);
    // sưả loại món
    @Multipart
    @PUT("categories/{id}")
    Call<Void> updateCategory(
            @Path("id") int categoryId,
            @Part("category_name") RequestBody category_name,
            @Part MultipartBody.Part category_image
    );
    @GET("categories/{id}")
    Call<Category> getCategoryById(@Path("id") int id);


    //lấy thông tin món theo id///////////////////////////////////////////////////////////////////////
    @GET("drinks/{id}")
    Call<Drink> getDrinkById(@Path("id") int id);
    // lấy thông tin món
    @GET("drinks")
    Call<List<Drink>> getDrink();
    //lấy danh sách món theo danh  mục
    @GET("drinks/category/{categoryId}")
    Call<List<Drink>> getDrinksByCategoryId(@Path("categoryId") int categoryId);


    // Cập nhật thông tin đồ uống
    @Multipart
    @PUT("drinks/{drinkId}")
    Call<Void> updateDrink(@Path("drink_id") int drink_id,
                           @Part("drink_name") RequestBody drink_name,
                           @Part("drink_price") RequestBody drink_price,
                           @Part("drink_status") RequestBody drink_status,
                           @Part("category_id") RequestBody category_id,
                           @Part MultipartBody.Part drink_image);

    // Xóa đồ uống
    @DELETE("drinks/{drinkId}")
    Call<Void> deleteDrink(@Path("drinkId") int drinkId);
    @Multipart
    @POST("drinks")
    Call<Void> addDrink(@Part("drink_name") RequestBody drink_name,
                      @Part("drink_price") RequestBody drink_price,
                      @Part("drink_status") RequestBody drink_status,
                        @Part("category_id") RequestBody category_id,
                      @Part MultipartBody.Part drink_image);




    //Lấy thông tin bàn /////////////////////////////////////////////
    @GET("tables")
    Call<List<Table>> getTable();
    //lấy thông tin bàn theo id
    @GET("tables/{id}")
    Call<Table> getTableById(@Path("id") int tableId);

    //xóa bàn
    @DELETE("tables/{tableId}")
    Call<Void> deleteTable(@Path("tableId") int tableId);
    //Thêm bàn
    @POST("tables")
    Call<Void> addTable( @Body Table table);
    //Sửa bàn
    @PUT("tables/{table_id}")
    Call<Void> updateTable(@Path("table_id") int tableId, @Body Table table);
    @PUT("tablestatus/{table_id}")
    Call<Void> updateTableStatus(@Path("table_id") int tableId, @Body Table table);

    //Lấy thông tin hóa đơn
    @GET("orders")
    Call<List<Order>> getOrders();
    @GET("order")
    Call<List<Order>> getOrder();
//Thêm order
    @POST("/addOrder")
    Call<AddOrder.OrderResponse> addOrder(@Body Order order);

    // thêm order_detail
    @POST("/addOrderDetail")
    Call<Order> addOrderDetail(@Body Order orderDetail);
    //lấy thông tin món của order
    @GET("orderdetails/{order_id}")
    Call<List<Order>> getOrderDetailById(@Path("order_id") int order_id);
    @DELETE("orders/{order_id}")
    Call<Void> deleteOrder(@Path("order_id") int order_id);
    @PUT("orderstatus/{order_id}")
    Call<Void> updateOrderStatus(@Path("order_id") int order_id, @Body Order order);


    //thanh toán//////////////////////////////////////////////////////////
    @POST("/payment")
    Call<JsonObject> payment(@Body Payment payment);
}



