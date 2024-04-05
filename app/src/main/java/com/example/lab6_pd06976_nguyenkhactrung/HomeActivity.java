package com.example.lab6_pd06976_nguyenkhactrung;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6_pd06976_nguyenkhactrung.adapter.FruitAdapter;
import com.example.lab6_pd06976_nguyenkhactrung.databinding.ActivityHomeBinding;
import com.example.lab6_pd06976_nguyenkhactrung.model.Fruit;
import com.example.lab6_pd06976_nguyenkhactrung.model.Response;
import com.example.lab6_pd06976_nguyenkhactrung.services.HttpRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;


public class HomeActivity extends AppCompatActivity implements FruitAdapter.FruitClick {
    private ActivityHomeBinding binding;
    private HttpRequest httpRequest;
    private SharedPreferences sharedPreferences;
    private String token;
    private FruitAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        httpRequest = new HttpRequest();
        sharedPreferences = getSharedPreferences("INFO",MODE_PRIVATE);

        token = sharedPreferences.getString("token","");
        httpRequest.CallAPI().getListFruit("Bearer " + token).enqueue(getListFruitResponse);
        userListener();
    }

    private void userListener () {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this , AddFruitActivity.class));
            }
        });
    }

    Callback<Response<ArrayList<Fruit>>> getListFruitResponse = new Callback<Response<ArrayList<Fruit>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
            if(response.isSuccessful()){
                if (response.body().getStatus() == 200) {
                    ArrayList<Fruit> ds = response.body().getData();
                    getData(ds);
//                    Toast.makeText(HomeActivity.this, "hihi"+dsFruits, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable t) {
        }
    };

    private void getData (ArrayList<Fruit> ds) {
        adapter = new FruitAdapter(this, ds,this);
        binding.rcvFruit.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        httpRequest.CallAPI().getListFruit("Bearer "+token).enqueue(getListFruitResponse);
    }

    @Override
    public void delete(Fruit fruit) {

    }

    @Override
    public void edit(Fruit fruit) {

    }

    @Override
    public void showDetail(Fruit fruit) {

    }
}