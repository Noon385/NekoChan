package com.example.nekochancoffee.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nekochancoffee.Model.Adopt;
import com.example.nekochancoffee.Model.User;
import com.example.nekochancoffee.R;

public class AdoptDetail extends AppCompatActivity {

    private ImageView imgAdopt;
    private TextView txtCatName, txtCatOwner,txtAdoptTime;
    private RecyclerView recyclerviewThunhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt_detail);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_adopt_detail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        imgAdopt = findViewById(R.id.imgAdopt);
        txtCatName = findViewById(R.id.txtCatName);
        txtCatOwner = findViewById(R.id.txtCatOwner);
        txtAdoptTime = findViewById(R.id.txtAdoptTime);

        Adopt adopt = (Adopt) getIntent().getSerializableExtra("adopt");
        if (adopt != null) {
            loadAdoptDetails(adopt);
        }
    }
    private void loadAdoptDetails(Adopt adopt) {
        txtCatName.setText("Tên mèo: " + adopt.getCat_name());
        txtCatOwner.setText("Chủ sở hữu: "+adopt.getCustomer_name());
        txtAdoptTime.setText("Thời gian được nhận nuôi: " + adopt.getAdopt_time());


        if (adopt.getCat_image() != null && !adopt.getCat_image().isEmpty()) {

            Bitmap bitmap = decodeBase64(adopt.getCat_image());
            imgAdopt.setImageBitmap(bitmap);
        } else {

            imgAdopt.setImageResource(R.drawable.t);
        }
    }
    private Bitmap decodeBase64(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}