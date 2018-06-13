package com.example.hyojong.dkeis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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


public class ImageTransferActivity extends AppCompatActivity {
    private String userUid;
    private ActionBar actionBar;

    private TextView content;
    private ImageView selectImage;
    private ImageView styleimage;
    private ImageView changeImage;
    private String path, userURL, styleURL;
    private String filePath;
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
                //System.out.println("****************PATH: " + absoultePath + "  styleURL : " + styleURL);
                if (absoultePath == null || styleURL == null) {
                    Toast.makeText(ImageTransferActivity.this, "사진 선택해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    path = absoultePath;
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

                                        userURL = "http://114.70.234.172:3004/transfer/trans-" + (absoultePath.split("/")[absoultePath.split("/").length - 1]);
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
    private File createFile() throws IOException{
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
                } catch (Exception e) { e.printStackTrace(); break; }
                //styleURL = photoUri.getPath().toString();
                Log.d("Get Photo", photoUri.getPath().toString());
                //startActivityForResult(data, 2);
                String filepaths = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DKEIS/" + System.currentTimeMillis() +".jpg";
                System.out.println("CASE 2 ");
                //if(extras != null) {
                //Bitmap photo = extras.getParcelable("data");
                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(data.getData().toString()));
                    photo = Bitmap.createScaledBitmap(photo, photo.getWidth()/2, photo.getHeight()/2, true);
                    selectImage.setImageBitmap(photo);
                    storeCropImage(photo, filepaths);
                    //    new AndroidBmpUtil().save(photo, filePath);
                    absoultePath = filepaths;
                    System.out.println("#########FILEPATH:" + absoultePath);
                } catch (IOException e) { e.printStackTrace(); }
                //}
                break;
            // CROP -> 이미지 깨짐 현상 -> 사용 X
            case 0:

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(photoUri, "image/*");

           //     intent.putExtra("outputX", 500);
            //    intent.putExtra("outputY",500);
             //   intent.putExtra("aspectX", 1);
                //intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, 2);
               /* final Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                }*/

                break;
               // intent.putExtra("return-data",true);
               // intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
               /* String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DKEIS/";
                String tempfilename = System.currentTimeMillis() +".jpg";
                String filepath = tempPath + tempfilename;
                        //Environment.getExternalStorageDirectory().getAbsolutePath() + "/DKEIS/" + System.currentTimeMillis() +".png";

                File tempFile = null;
                File tempDir = new File(tempPath);

                if(!tempDir.exists()) { tempDir.mkdirs(); }
                try {
                    tempFile = File.createTempFile(tempfilename, ".jpg", tempDir);
                } catch (IOException e) { e.printStackTrace(); }*/

                /*File photoFile = null;
                try {
                    photoFile = createFile();
                } catch (IOException e) { e.printStackTrace(); }
                System.out.println("뭔데: " +photoFile.getAbsolutePath());
              //  refreshMedia(photoFile);

                if(photoFile != null) {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        photoUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.hyojong.dkeis", photoFile);
                  }
               } else {
                    System.out.println("!!안됨");
                }

               // photoUri = Uri.fromFile(new File(filepath));

                absoultePath = filePath;
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);

                //startActivityForResult(intent, 2);
                System.out.println("#########FILEPATH:" + absoultePath);
                break;
*/
            case 2:
               // final Bundle extras = data.getExtras();
                String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DKEIS/" + System.currentTimeMillis() +".jpg";

                //if(extras != null) {
                    //Bitmap photo = extras.getParcelable("data");
                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(data.getData().toString()));
                    selectImage.setImageBitmap(photo);
                    storeCropImage(photo, filepath);
                    //    new AndroidBmpUtil().save(photo, filePath);
                    absoultePath = filepath;
                    System.out.println("#########FILEPATH:" + absoultePath);
                } catch (IOException e) { e.printStackTrace(); }
                //}
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
