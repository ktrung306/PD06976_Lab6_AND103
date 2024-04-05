package com.example.lab6_pd06976_nguyenkhactrung;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab6_pd06976_nguyenkhactrung.model.Response;
import com.example.lab6_pd06976_nguyenkhactrung.model.User;
import com.example.lab6_pd06976_nguyenkhactrung.services.HttpRequest;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {

     EditText username, password;
     Button btn_Login;
     TextView txt_register;
     private HttpRequest httpRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.edtUsername);
        password = findViewById(R.id.edtPassword);
        btn_Login = findViewById(R.id.btnLogin);
        txt_register = findViewById(R.id.txtSignUp);
        httpRequest = new HttpRequest();

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                String _username = username.getText().toString();
                String _password = password.getText().toString();
                user.setUsername(_username);
                user.setPassword(_password);
                httpRequest.CallAPI().login(user).enqueue(responseUser);
            }
        });

        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    Callback<Response<User>> responseUser = new Callback<Response<User>>() {
        @Override
        public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
            if(response.isSuccessful())
            {
                //check status code
                if(response.body().getStatus() == 200)
                {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    //Lưu token, lưu device token, id
                    SharedPreferences sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", response.body().getToken());
                    editor.putString("refreshToken", response.body().getRefreshToken());
                    editor.putString("id", response.body().getData().get_id());
                    editor.apply();
                    //Sau khi chuyển sang màn hình chính
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                }
            }
        }

        @Override
        public void onFailure(Call<Response<User>> call, Throwable t) {
            Log.d(">>> GetListDistributor", "onFailure" +t.getMessage());
        }
    };
}