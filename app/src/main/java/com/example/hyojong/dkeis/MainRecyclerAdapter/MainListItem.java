package com.example.hyojong.dkeis.MainRecyclerAdapter;

/**
 * MainActivity Cardview에 입력될 데이터
 */

public class MainListItem {
    private String listName;        //CardView 이름
    private String listContent;     //CardView 내용
    private int listImage;          //CardView에 적용될 이미지
    private int listNumber;         //CardView 순서

    public MainListItem(String listName, String listContent, int listNumber, int listImage) {
        this.listName = listName;
        this.listContent = listContent;
        this.listNumber = listNumber;
        this.listImage = listImage;
    }


    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListContent() {
        return listContent;
    }

    public int getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
    }

    public int getListImage() { return listImage; }

    public void setListImage(int listImage) {
        this.listImage = listImage;
    }
}
