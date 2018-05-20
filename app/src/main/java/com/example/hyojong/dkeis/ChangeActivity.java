package com.example.hyojong.dkeis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ChangeActivity extends AppCompatActivity {

    private TextView changeText;
    private String msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);


        msg = getIntent().getStringExtra("msg");
        changeText = (TextView)findViewById(R.id.changeText);
        changeText.setText(msg);



    }



}
