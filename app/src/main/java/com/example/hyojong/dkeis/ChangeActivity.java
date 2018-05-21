package com.example.hyojong.dkeis;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.HttpURLConnection;
import java.net.URL;

public class ChangeActivity extends AppCompatActivity {

    private TextView changeText;
    private ImageView changeImage;
    private String msg;
    private String userURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        msg = getIntent().getStringExtra("msg");
        userURL = getIntent().getStringExtra("userURL");
        changeText = (TextView)findViewById(R.id.changeText);
        changeText.setText(msg);

        LoadImageTask loadImageTask = new LoadImageTask();
        loadImageTask.execute();

        changeImage = (ImageView)findViewById(R.id.changeImage);


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
            Log.d("#####성공함?: ", "알ㄴㅇ러ㅏㅁㄴ어리ㅏㄴ멍ㄹ");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            Glide.with(ChangeActivity.this).load(userURL).into(changeImage);
            super.onPostExecute(result);
        }
    }
}
