package com.example.hyojong.dkeis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hyojong.dkeis.Adapter.StyleActivity;
import com.google.android.gms.common.api.Response;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;


public class ImageTransferActivity extends AppCompatActivity {
    private String userUid;
    private ActionBar actionBar;

    private TextView content;
    private ImageView selectImage;
    private ImageView styleimage;
    private ImageView changeImage;
    private String path, userURL, styleURL;

    private String absoultePath;
    Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_transfer);

        //actionBar = getSupportActionBar();
        //actionBar.hide();

        Intent intent = getIntent();
        userUid = intent.getStringExtra("userUid");

        content = (TextView) findViewById(R.id.content);
        content.setText("바꾸고 싶은 이미지를 집어넣어라!!");

        selectImage = (ImageView)findViewById(R.id.cameraimage);
        styleimage = (ImageView)findViewById(R.id.paintimage);
        changeImage = (ImageView)findViewById(R.id.albumimage);

        // 자신의 사진 선택
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeAlbumAction();
            }
        });

        //  Style 선택
        styleimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImageTransferActivity.this, StyleActivity.class);
                startActivityForResult(intent,3);
            }
        });

        // 변환
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Node.js or JSP 파일 업로드[][][][][][][][][][][][][]][][][][][][][]
                // 1) User Image 웹 서버에 업로드, 2) Style URL 전송, 3) Node js 호출?
                //path = getPathFromURI(selectImage.set);
                path = absoultePath;
                //System.out.println("###############PATH: " + path);
                File f = new File(path);
                com.koushikdutta.async.future.Future uploading = Ion.with(ImageTransferActivity.this)
                        //.load("http://192.168.150.1:8080/upload")
                        .load("http://114.70.234.172:3004/upload")
                        .setMultipartFile("image", f)
                        .setMultipartParameter("style", styleURL)
                        .asString()
                        .withResponse()
                        .setCallback(new FutureCallback<com.koushikdutta.ion.Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> result) {
                                try {

                                    userURL = "http://114.70.234.172:3004/transfer/trans-"+ (absoultePath.split("/")[absoultePath.split("/").length-1]);
                                    //System.out.println("@@@@@@@@@@@@@@@@@@@" + userURL);
                                    String msg = result.getResult();

                                    //Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(ImageTransferActivity.this, ChangeActivity.class);
                                    intent.putExtra("msg", msg);
                                    intent.putExtra("userURL", userURL);
                                    startActivity(intent);

                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }

                            }
                        });

            }
        });
    }

    public void takeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode ,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                photoUri = data.getData();
                //styleURL = photoUri.getPath().toString();
                Log.d("Get Photo", photoUri.getPath().toString());
            case 0:

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(photoUri, "image/*");

                intent.putExtra("outputX", 200);
                intent.putExtra("outputY",200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data",true);
                startActivityForResult(intent, 2);

                break;

            case 2:
                final Bundle extras = data.getExtras();
                String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DKEIS/" + System.currentTimeMillis() +".jpg";

                if(extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    selectImage.setImageBitmap(photo);
                    storeCropImage(photo, filepath);
                    absoultePath = filepath;
                    //System.out.println("#########FILEPATH:" + absoultePath);

                }
                break;
            case 3:
                Intent intents = data;
                String url = intents.getStringExtra("url");
                styleURL = url;
                Glide.with(ImageTransferActivity.this).load(url).into(styleimage);
                break;

            default:
                System.out.println("!!!!!1취소");
                break;
        }
    }

    private void storeCropImage(Bitmap bitmap, String filePath){
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DKEIS";
        File directory_DKEIS = new File(dirPath);
        if(!directory_DKEIS.exists())
            directory_DKEIS.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            // sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
