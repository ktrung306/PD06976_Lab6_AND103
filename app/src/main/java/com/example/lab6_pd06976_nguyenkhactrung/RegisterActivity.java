package com.example.lab6_pd06976_nguyenkhactrung;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lab6_pd06976_nguyenkhactrung.model.Response;
import com.example.lab6_pd06976_nguyenkhactrung.model.User;
import com.example.lab6_pd06976_nguyenkhactrung.services.HttpRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {
    EditText username, password, email, name;
    Button btn_resgister;
    TextView txt_Login;
    ImageView avatar;
    File file;


    private static final int READ_EXTERNAL_STORAGE_REQUEST = 1;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST = 2;
    private static final int WRITE_REQUEST_CODE = 42;

    private HttpRequest httpRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Kiểm tra và yêu cầu quyền READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST);
        }

        // Kiểm tra và yêu cầu quyền WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST);
        }

        username = findViewById(R.id.edtUserName);
        password = findViewById(R.id.edtpassword);
        email = findViewById(R.id.edtEmail);
        name = findViewById(R.id.edtname);
        btn_resgister = findViewById(R.id.btnResgister);
        avatar = findViewById(R.id.image);
        txt_Login = findViewById(R.id.txtLogin);
        httpRequest = new HttpRequest();
        btn_resgister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterActivity.this, "click", Toast.LENGTH_SHORT).show();
                // sử dụng RequestBody
                RequestBody _username = RequestBody.create(MediaType.parse("multipart/form-data"), username.getText().toString().trim());
                RequestBody _password = RequestBody.create(MediaType.parse("multipart/form-data"), password.getText().toString().trim());
                RequestBody _email = RequestBody.create(MediaType.parse("multipart/form-data"), email.getText().toString().trim());
                RequestBody _name = RequestBody.create(MediaType.parse("multipart/form-data"), name.getText().toString().trim());
                MultipartBody.Part multipartBody;
                if (file != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                    multipartBody = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
                    // avatar là cùng tên với key trong multipart
                } else {
                    multipartBody = null;
                }
                httpRequest.CallAPI().register(_username, _password, _email, _name, multipartBody).enqueue(responseUser);

            }
        });

        txt_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }
    // ham chon anh
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        getImage.launch(intent);
    }

    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // there are no request codes
                Intent data = result.getData();
                Uri imagePath = data.getData();
                // file avatar bien global
                file = createFileFromUri(imagePath, "avatar");
                // glide de load hinh
                Glide.with(RegisterActivity.this)
                        .load(file) // load file hinh
                        .thumbnail(Glide.with(RegisterActivity.this).load(R.drawable.avatar))
                        .centerCrop() // center cắt ảnh
                        .circleCrop() // bo tròn ảnh
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // clear cache
                        .skipMemoryCache(true)
                        .into(avatar);
            }
        }
    });

    // hàm tạo file hình từ Uri
    private File createFileFromUri(Uri path, String name) {
        File _file = new File(getExternalFilesDir(null), name + ".png");
        try {
            InputStream in = getContentResolver().openInputStream(path);
            OutputStream out = new FileOutputStream(_file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return _file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    Callback<Response<User>> responseUser = new Callback<Response<User>>() {
        @Override
        public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
            if (response.isSuccessful()){
                // check status code
                if (response.body().getStatus() == 200){
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
            }
        }

        @Override
        public void onFailure(Call<Response<User>> call, Throwable t) {
            Log.d(">>> trung", "onFailure" + t.getMessage());
        }
    };
}