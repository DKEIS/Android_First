package com.example.hyojong.dkeis.Adapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.hyojong.dkeis.R;

import java.util.ArrayList;
import java.util.List;

public class StyleActivity extends AppCompatActivity {

    final int ITEM_SIZE = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        List<Item> items = new ArrayList<>();
        Item[] item = new Item[ITEM_SIZE];
        for(int i=0; i<20; i++) {
            String finename = "http://114.70.235.44:18081/capstone/style/"+(i+1)+".jpg";
            System.out.println(finename);
            item[i] = new Item(i, "#"+(i+1),finename);
        }
        for (int j=0; j<ITEM_SIZE; j++)  items.add(item[j]);
        recyclerView.setAdapter(new RecyclerAdapter(StyleActivity.this, items, R.layout.activity_style));

    }
}