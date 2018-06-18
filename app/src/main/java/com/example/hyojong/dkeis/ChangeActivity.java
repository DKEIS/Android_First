package com.example.hyojong.dkeis;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
/*
import com.kakao.AppActionBuilder;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.kakao.exception.KakaoException;
*/

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class ChangeActivity extends AppCompatActivity {

    private TextView changeText;
    private ImageView changeImage;
    private String msg;
    private String userURL;
    private Button downloadBtn;
    private ImageButton kakaoBtn;
    private FloatingActionButton storeFAB;
    Bitmap mSaveBm;

    /*
    private KakaoLink kakaoLink;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        final String kakaoURL = "http://114.70.235.44:18081/capstone/style/1.jpg";
        final String kakaoText = "테스트";
        final String kakaoURLtest = "http://114.70.235.44:18081/capstone/style/1.jpg";

        Toolbar changeToolbar = (Toolbar) findViewById(R.id.changeToolbar);
        setSupportActionBar(changeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("편집 완료");

        /*
        try {
            kakaoLink = KakaoLink.getKakaoLink(ChangeActivity.this);
            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* 카카오 링크 API *
        kakaoBtn = (ImageButton)findViewById(R.id.kakaoBtn);
        kakaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    kakaoTalkLinkMessageBuilder.addText(kakaoText);
                    kakaoTalkLinkMessageBuilder.addImage(kakaoURL, 320, 320);
                    kakaoTalkLinkMessageBuilder.addWebLink(kakaoURLtest);
                    kakaoTalkLinkMessageBuilder.addAppButton("앱열기",
                            new AppActionBuilder().setAndroidExecuteURLParam("target=main").
                                    setIOSExecuteURLParam("target=main",AppActionBuilder.DEVICE_TYPE.PHONE).build());
                } catch (KakaoParameterException e) {
                    e.printStackTrace();
                }
            }
        });*/

        /*
        msg = getIntent().getStringExtra("msg");
        changeText = (TextView)findViewById(R.id.changeText);
        changeText.setText(msg);*/

        //  msg = getIntent().getStringExtra("msg");
        userURL = getIntent().getStringExtra("userURL");

        downloadBtn = (Button)findViewById(R.id.downloadBtn);

        LoadImageTask loadImageTask = new LoadImageTask();
        loadImageTask.execute();

        changeImage = (ImageView)findViewById(R.id.changeImage);

        BitmapFactory.Options bmOptions;
        bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        OpenHttpConnection opHttpCon = new OpenHttpConnection();
        opHttpCon.execute(changeImage, userURL);

        storeFAB = (FloatingActionButton) findViewById(R.id.storeFAB);
        storeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String storeName;
                OutputStream outStream = null;

                storeName = userURL.split("/")[userURL.split("/").length - 1];

                String extStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DKEIS/";

                //System.out.println("**********DIR: " + extStorageDirectory);
                File file = new File(extStorageDirectory, storeName);
                //storeCropImage(GetImageFromURL(userURL),extStorageDirectory);

                try {
                    outStream = new FileOutputStream(file);
                    mSaveBm.compress(
                            Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();

                    Toast.makeText(ChangeActivity.this,
                            "Saved", Toast.LENGTH_LONG).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(ChangeActivity.this,
                            e.toString(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ChangeActivity.this,
                            e.toString(), Toast.LENGTH_LONG).show();
                }
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA,
                        extStorageDirectory + storeName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userURL));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                Toast.makeText(ChangeActivity.this, "저장", Toast.LENGTH_SHORT).show();
            }
        });

        /*
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String storeName;
                OutputStream outStream = null;

                storeName = userURL.split("/")[userURL.split("/").length-1];

                String extStorageDirectory =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/DKEIS/";

                //System.out.println("**********DIR: " + extStorageDirectory);
                File file = new File(extStorageDirectory, storeName);
                //storeCropImage(GetImageFromURL(userURL),extStorageDirectory);

                try {
                    outStream = new FileOutputStream(file);
                    mSaveBm.compress(
                            Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();

                    Toast.makeText(ChangeActivity.this,
                            "Saved", Toast.LENGTH_LONG).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(ChangeActivity.this,
                            e.toString(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ChangeActivity.this,
                            e.toString(), Toast.LENGTH_LONG).show();
                }
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA,
                        extStorageDirectory + storeName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userURL));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*
                Toast.makeText(ChangeActivity.this, "저장", Toast.LENGTH_SHORT).show();
            }
        });*/
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

    private Bitmap GetImageFromURL(String imgaeURL) {
        Bitmap imgBitmap = null;

        try {
            URL url = new URL(imgaeURL);
            URLConnection conn = url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);

            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  imgBitmap;
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

    public Boolean isExists(String URLName) {
        try {

            HttpURLConnection.setFollowRedirects(false);

            /**
             * HTTP 요청 메소드 SET 본 예제는 파일의 존재여부만 확인하려니 간단히 HEAD 요청을 보냄 HEAD요청에 대해 웹서버는 수정된 시간이
             * 포함된 리소스의 해더 정보를 간단히 리턴 GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE 값등이 올 수 있다.
             * 디폴트는 GET
             **/
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");

            // FILE이 있는 경우 HTTP_OK 200
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private class LoadImageTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(ChangeActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            while (!isExists(userURL))
                Log.d("#######doInBackground: ", String.valueOf(isExists(userURL)));
            // 사진 용량이 커서 사이트에서 불러올 시간이 필요..
            try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            Glide.with(ChangeActivity.this).load(userURL).into(changeImage);
            super.onPostExecute(result);
        }

    }
    private class OpenHttpConnection extends AsyncTask<Object, Void, Bitmap> {

        private ImageView bmImage;

        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap mBitmap = null;
            bmImage = (ImageView) params[0];
            String url = (String) params[1];
            InputStream in = null;
            try {
                in = new java.net.URL(url).openStream();
                mBitmap = BitmapFactory.decodeStream(in);
                in.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return mBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            super.onPostExecute(bm);
            mSaveBm = bm;
            bmImage.setImageBitmap(bm);
        }
    }

}
