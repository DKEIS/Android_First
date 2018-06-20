package com.example.hyojong.dkeis;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hyojong.dkeis.Adapter.StyleActivity;
import com.google.android.gms.common.api.Response;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.login.LoginException;


public class ImageTransferActivity extends AppCompatActivity {
    private String userUid;
    private ActionBar actionBar;

    private TextView content;
    private ImageView selectImage;
    private ImageView styleimage;

    private Button changeButton;

    private String path, userURL, styleURL;
    private String filePath;
    private String absoultePath;

    private Uri photoUri;
    private Uri imageUri;

    private final int MY_PERMISSION_CAMERA = 1111;
    private final int CAMERA_REQUEST_CODE = 4;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_transfer);

        Toolbar transferToolbar = (Toolbar) findViewById(R.id.transferToolbar);
        setSupportActionBar(transferToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Style Transfer Page");

        Intent intent = getIntent();
        userUid = intent.getStringExtra("userUid");

        selectImage = (ImageView)findViewById(R.id.cameraimage);
        styleimage = (ImageView)findViewById(R.id.paintimage);
        changeButton = (Button)findViewById(R.id.changeButton);


        // 자신의 사진 선택
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageSelect[] = new String[] {"사진 촬영", "앨범에서 가져오기"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ImageTransferActivity.this);
                builder.setTitle("이미지 선택");
                builder.setItems(imageSelect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0:
                                takeCameraAction();
                                break;
                            case 1:
                                takeAlbumAction();
                                break;
                        }
                        dialog.dismiss();
                    }

                });
                builder.show();
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
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("****************PATH: " + absoultePath + "  styleURL : " + styleURL);
                if (absoultePath == null || styleURL == null) {
                    Toast.makeText(ImageTransferActivity.this, "사진 선택해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    path = absoultePath;
                    File f = new File(path);
                    com.koushikdutta.async.future.Future uploading = Ion.with(ImageTransferActivity.this)
                            //.load("http://192.168.150.1:8080/upload")
                            .load("http://114.70.234.121:3004/upload")
                            .setMultipartFile("image", f)
                            .setMultipartParameter("style", styleURL)
                            .asString()
                            .withResponse()
                            .setCallback(new FutureCallback<com.koushikdutta.ion.Response<String>>() {
                                @Override
                                public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> result) {
                                    try {

                                        userURL = "http://114.70.234.121:3004/transfer/trans-" + (absoultePath.split("/")[absoultePath.split("/").length - 1]);
                                        //System.out.println("@@@@@@@@@@@@@@@@@@@" + userURL);
                                        //String msg = result.getResult();

                                        //Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(ImageTransferActivity.this, ChangeActivity.class);
                                        //intent.putExtra("msg", msg);
                                        intent.putExtra("userURL", userURL);
                                        startActivity(intent);

                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }

                                }
                            });

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void takeCameraAction() {
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(cameraIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;

                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    Log.e("captureCamera Error", e.toString());
                }

                if(photoFile != null) {
                    Uri providerURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageUri = providerURI;

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }
            }
        } else {
            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void takeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    private void refreshMedia(File photoFile) {
        MediaScannerConnection.scanFile(this,
                new String[] {photoFile.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener(){
                    public void onScanCompleted(String path, Uri uri){

                    }
                });
    }
    private File createFile() throws IOException {
        // 임시파일명 생성
        String tempFilename = "test";
        //""+System.currentTimeMillis();
        // 임시파일 저장용 디렉토리 생성
        File tempDir = new File(Environment.getExternalStorageDirectory() + "/DKEIS/");
        if(!tempDir.exists()){
            tempDir.mkdirs();
        }
        filePath = tempDir + "/" + tempFilename + ".jpg";
        System.out.println("##### " + filePath);
        // 실제 임시파일을 생성
        File tempFile = new File(filePath);

        return tempFile;
    }

    @Override
    public void onActivityResult(int requestCode ,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                try {
                    photoUri = data.getData();
                } catch (Exception e) {
                    e.printStackTrace(); break;
                }

                Log.d("Get Photo", photoUri.getPath().toString());

                String filepaths = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DKEIS/" + System.currentTimeMillis() +".jpg";
                System.out.println("CASE 2 ");

                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(data.getData().toString()));
                    photo = Bitmap.createScaledBitmap(photo, photo.getWidth()/2, photo.getHeight()/2, true);
                    selectImage.setImageBitmap(photo);
                    storeCropImage(photo, filepaths);

                    absoultePath = filepaths;
                    System.out.println("#########FILEPATH:" + absoultePath);
                } catch (IOException e) { e.printStackTrace(); }

                break;
            // CROP -> 이미지 깨짐 현상 -> 사용 X
            case 0:

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(photoUri, "image/*");

                intent.putExtra("scale", true);
                intent.putExtra("return-data",true);
                startActivityForResult(intent, 2);

                break;

            case 2:
                String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DKEIS/" + System.currentTimeMillis() +".jpg";

                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(data.getData().toString()));
                    selectImage.setImageBitmap(photo);
                    storeCropImage(photo, filepath);
                    absoultePath = filepath;
                    System.out.println("#########FILEPATH:" + absoultePath);
                } catch (IOException e) { e.printStackTrace(); }

                break;
            case 3:
                Intent intents = data;
                String url = null;
                try {
                    url = intents.getStringExtra("url");
                    System.out.println(url);
                    styleURL = url;
                    Glide.with(ImageTransferActivity.this).load(url).into(styleimage);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case CAMERA_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK) {
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK");
                        galleryAddPic();
                        selectImage.setImageURI(imageUri);
                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(ImageTransferActivity.this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }

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

    private void galleryAddPic() {
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        File f = new File(absoultePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG" + timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "DKEIS"); //test라는 경로에 이미지를 저장하기 위함

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        File image = new File(storageDir, imageFileName);
        absoultePath = image.getAbsolutePath();
        return image;
    }

    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }
}
