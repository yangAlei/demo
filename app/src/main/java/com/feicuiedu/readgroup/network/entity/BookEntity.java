package com.feicuiedu.readgroup.network.entity;


import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class BookEntity {

    private String objectId;
    private String title; // 标题
    private String publisher; // 出版社
    private String price; // 价格
    private String img; // 图像
    private String author; // 作者

    @SerializedName("author_intro")
    private String authorIntro;
    private String summary;

    public String getObjectId() {
        return objectId;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPrice() {
        return price;
    }

    public String getImg() {
        return img;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorIntro() {
        return authorIntro;
    }

    public String getSummary() {
        return summary;
    }

    @Override public String toString() {
        return String.format(" %s--%s ", title, author);
    }
}
