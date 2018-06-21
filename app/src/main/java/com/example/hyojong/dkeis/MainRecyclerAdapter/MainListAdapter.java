package com.example.hyojong.dkeis.MainRecyclerAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hyojong.dkeis.CycleGanActivity;
import com.example.hyojong.dkeis.ImageTransferActivity;
import com.example.hyojong.dkeis.R;

import java.util.ArrayList;

/**
 * MainActivity에 화풍바꿔주는 버튼과 Gan을 활용하여 이미지를 바꿔주는 버튼을
 * RecyclerView로 만들어주기위한 Adapter
 */

public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MainListItem> mainListItemArrayList;
    private Context parentActivity;

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView listName, listContent;
        CardView cardView;
        LinearLayout linearLayout;
        ImageView mainImage;

        public ViewHolder(View view) {
            super(view);
            listName = (TextView) view.findViewById(R.id.listName);
            //listContent = (TextView) view.findViewById(R.id.listContent);
            cardView = (CardView) view.findViewById(R.id.cardView_main);
            //linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_main);
            mainImage = (ImageView) view.findViewById(R.id.mainImage);
        }
    }

    public MainListAdapter(ArrayList<MainListItem> mainListItemArrayList, Context parentActivity) {
        this.mainListItemArrayList = mainListItemArrayList;
        this.parentActivity = parentActivity;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;

        final MainListItem listItem = mainListItemArrayList.get(position);
        viewHolder.listName.setText(listItem.getListName());
        //viewHolder.listContent.setText(listItem.getListContent());
        viewHolder.mainImage.setBackgroundResource(listItem.getListImage());

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int listNumber = listItem.getListNumber();
                switch (listNumber) {
                    case 1:     // 1번 CardView : 화풍 바꿔주는 Activity로 이동
                        Intent intent1 = new Intent(parentActivity, ImageTransferActivity.class);
                        parentActivity.startActivity(intent1);
                        break;
                    case 2:     // 2번 CardView : Gan을 활용하여 이미지를 바꿔주는 Activity로 이동
                        Intent intent2 = new Intent(parentActivity, CycleGanActivity.class);
                        parentActivity.startActivity(intent2);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mainListItemArrayList.size();
    }
}