package com.feicuiedu.readgroup.presentation.books.booklist;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.feicuiedu.readgroup.R;
import com.feicuiedu.readgroup.network.entity.BookEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BooksAdapter extends BaseAdapter{

    private final List<BookEntity> bookList;

    public BooksAdapter() {
        bookList = new ArrayList<>();
    }

    @Override public int getCount() {
        return bookList.size();
    }

    @Override public BookEntity getItem(int position) {
        return bookList.get(position);
    }

    @Override public long getItemId(int position) {
        return getItem(position).getObjectId().hashCode();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = (convertView != null)? convertView : createItemView(parent);

        ViewHolder viewHolder = (ViewHolder) itemView.getTag();
        viewHolder.bind(getItem(position));

        return itemView;
    }

    public void setBooks(List<BookEntity> books) {
        bookList.clear();
        bookList.addAll(books);
        notifyDataSetChanged();
    }


    private View createItemView(ViewGroup parent) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemView);
        itemView.setTag(viewHolder);

        return itemView;
    }


    static class ViewHolder{

        @BindView(R.id.text_title) TextView tvTitle;
        @BindView(R.id.text_author) TextView tvAuthor;
        @BindView(R.id.text_price) TextView tvPrice;
        @BindView(R.id.text_publisher) TextView tvPublisher;
        @BindView(R.id.image_cover) ImageView ivCover;

        ViewHolder(View itemView){
            ButterKnife.bind(this, itemView);
        }

        void bind(BookEntity bookEntity){
            tvTitle.setText(bookEntity.getTitle());
            tvAuthor.setText(bookEntity.getAuthor());
            tvPrice.setText(bookEntity.getPrice());
            tvPublisher.setText(bookEntity.getPublisher());

            tvPrice.setVisibility(bookEntity.getPrice() == null? View.INVISIBLE : View.VISIBLE);

            Glide.with(ivCover.getContext())
                    .load(bookEntity.getImg())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.book_empty_bg)
                    .into(ivCover);
        }
    }
}
