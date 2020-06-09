package com.graduate.a2020_graduateproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class GalleryImageViewerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageView;

    private Intent intent;
    private Uri imageUrl;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private String selected_room_id; // 여행방 id

    private String key="";
    private String key2="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image_viewer);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼
        setTitle("");

        imageView = findViewById(R.id.imageView);

        intent = getIntent();
        imageUrl = Uri.parse(intent.getExtras().getString("clicked image"));
        selected_room_id = intent.getExtras().getString("selected room id");

        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
/*
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("sharing_trips").child("gallery_list").child(selected_room_id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {  // 반복문으로 데이터 리스트 추출

                    key = snapshot.getKey();


                    String image_url = snapshot.child(key).child("imageUrl").getValue().toString();

                    if(image_url.equals(imageUrl.toString())) {
                        key2 = snapshot.getKey();
                        Log.v("gallery", "key2 = " + key2);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // DB를 가져올 때 에러가 발생할 경우
                Log.e("SharingGalleryActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.detail_image_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // toolbar의 back 키 눌렀을 때 동작
                finish();
                return true;
            }

            case R.id.saveImage_menu:{
                Toast.makeText(this, "Save image successful", Toast.LENGTH_SHORT).show();
                return true;
            }


//                URL imgUrl = null;
//                HttpURLConnection connection = null;
//                InputStream in = null;
//                Bitmap retBitmap = null;
/*
                try {
                    Log.v("gallery", "imageUrl = " + imageUrl);

                    //URL imgUrl = new URL(String.valueOf(imageUrl));
                    //Log.v("gallery", "URL = " + imgUrl);
/*
                    HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.setDoInput(true); // url로 input받는 flag 허용
                    connection.connect(); //연결
                    InputStream in = connection.getInputStream(); //get inputstream
                    Bitmap imageToBitmap = BitmapFactory.decodeStream(in);  // Save to bitmap
* /
//                    InputStream in = getContentResolver().openInputStream(image_url);   // Open uri inputStream to read bitmap
                    //Log.v("gallery", "InputStream image url = " + image_url);
                    Bitmap imageToBitmap = BitmapFactory.decodeStream(in);  // Save to bitmap

                    in.close();
                    saveImage(imageToBitmap);   // 핸드폰 저장소에 이미지를 저장하는 함수 호출

                    //saveImage(imageUrl);   // 핸드폰 저장소에 이미지를 저장하는 함수 호출

                    Toast.makeText(this, "save image", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
*/
/* 마지막으로 수정 하던 거
                intent = getIntent();

                imageUrl = Uri.parse(intent.getExtras().getString("clicked image"));

                //Uri image_uri = intent.getData();
                Log.v("gallery", "imageUrl = " + imageUrl);
                //Log.v("gallery", "지금 인텐트에서 받아온 = " + image_uri);

                try {
                    //InputStream in = getContentResolver().openInputStream(image_uri);   // Open uri inputStream to read bitmap
                    InputStream in = getContentResolver().openInputStream(imageUrl);   // Open uri inputStream to read bitmap
                    Bitmap imageToBitmap = BitmapFactory.decodeStream(in);  // Save to bitmap

                    in.close();
                    //saveImage(imageToBitmap, "demo");   // 핸드폰 저장소에 이미지를 저장하는 함수 호출
                    saveImage(imageToBitmap);   // 핸드폰 저장소에 이미지를 저장하는 함수 호출
                } catch (Exception e) {
                    e.printStackTrace();
                }
*/
            case R.id.deleteImage_menu: {
/*
                databaseReference.child(key2).child("imageUrl").removeValue();
*/

                Toast.makeText(this, "Delete image successful", Toast.LENGTH_SHORT).show();
                //finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    //private void saveImage(Bitmap bitmap) throws IOException {
    private void saveImage(Bitmap bitmap) throws IOException {

        OutputStream fos; // File Output Stream

        // 타겟 SDK가 안드로이드 10(Q) (SDK 29) 이상일 때
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            /*
            MediaStore API를 사용하여 외부저장소의 공용 폴더 안의 미디어 파일(사진/동영상)에 접근 - 현재는 다운로드 받은 이미지 파일 저장
            ... 공용 폴더 안의 미디어 파일(사진/동영상/오디오)들은 MediaStore를 통해 읽을 수 있음
            ... 사진 파일을 찾고 싶으면 공용 폴더 아래의 모든 파일 탐색 X, MediaStore에 쿼리를 하여 Uri 객체를 얻어 사용
            ... 기본적으로 외부저장소 공용 영역의 /Pictures 아래 저장됨
            */
            final String relativePath = Environment.DIRECTORY_PICTURES + "/SharingTrips";
            // 해당 경로가 없을 때 생성해주는지.. 폴더를 만들어줘야 하는지!

            ContentResolver resolver = getContentResolver();    // ContentResolver 인스턴스를 가져옴
            ContentValues contentValues = new ContentValues();
            //contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpg"); // Set image name
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, ".jpg"); // Set image name
            //contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, relativePath); // 파일을 저장할 구체적 위치 설정

            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
        }
        // 타겟 SDK가 안드로이드 10(Q) (SDK 29) 이하일 때
        else {
            File galleryFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/SharingTrips");

            // Pictures 아래 SharingTrips 폴더가 없다면 생성
            if(!galleryFolder.exists()){
                galleryFolder.mkdir();
            }

            //File Image = new File(galleryFolder.toString(), name + ".jpg");
            File Image = new File(galleryFolder.toString(),".jpg");
            fos = new FileOutputStream(Image);
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        Objects.requireNonNull(fos).close();
    }
}
