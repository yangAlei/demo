package com.feicuiedu.readgroup.presentation.books.bookinfo;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.feicuiedu.apphx.Preconditions;
import com.feicuiedu.readgroup.R;
import com.feicuiedu.readgroup.network.entity.BookEntity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookHeaderHolder {

    @BindView(R.id.image_cover) ImageView ivCover;
    @BindView(R.id.text_author) TextView tvAuthor;
    @BindView(R.id.text_price) TextView tvPrice;
    @BindView(R.id.text_publisher) TextView tvPublisher;
    @BindView(R.id.text_author_introduction) TextView tvAuthorIntro;
    @BindView(R.id.text_summary) TextView tvSummary;

    @BindView(R.id.text_summary_header) TextView tvHeaderSummary;
    @BindView(R.id.text_intro_header) TextView tvHeaderIntro;


    private final View view;

    public BookHeaderHolder(View view) {
        this.view = view;
        ButterKnife.bind(this, view);
    }

    public void bind(@NonNull BookEntity bookEntity) {

        Preconditions.checkNotNull(bookEntity);

        tvAuthor.setText(bookEntity.getAuthor());
        tvPrice.setText(bookEntity.getPrice());
        tvPublisher.setText(bookEntity.getPublisher());

        String intro = bookEntity.getAuthorIntro();
        String summary = bookEntity.getSummary();

        tvAuthorIntro.setText(intro);
        tvSummary.setText(summary);

        int introVisibility = TextUtils.isEmpty(intro)? View.GONE : View.VISIBLE;
        tvAuthorIntro.setVisibility(introVisibility);
        tvHeaderIntro.setVisibility(introVisibility);

        int summaryVisibility = TextUtils.isEmpty(summary)? View.GONE : View.VISIBLE;
        tvSummary.setVisibility(summaryVisibility);
        tvHeaderSummary.setVisibility(summaryVisibility);

        Glide.with(ivCover.getContext())
                .load(bookEntity.getImg())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.book_empty_bg)
                .into(ivCover);

        view.setVisibility(View.VISIBLE);
    }
}
