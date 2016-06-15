package com.only.rxtest.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.only.rxtest.R;
import com.only.rxtest.common.model.JokeBean;

import java.util.List;

/**
 * Created by only on 16/6/15.
 * Email: onlybeyond99@gmail.com
 */
public class JokeAdapter extends RecyclerView.Adapter<RecycleViewHolder> {
    private Context mContext;
    private List<JokeBean> jokeBeanList;

    public JokeAdapter(Context mContext, List<JokeBean> jokeBeanList) {
        this.mContext = mContext;
        this.jokeBeanList = jokeBeanList;
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_joke, parent, false);
        return new RecycleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder holder, int position) {
     TextView tvContent=(TextView) holder.getView(R.id.tv_content);

        JokeBean jokeBean = jokeBeanList.get(position);
        tvContent.setText(jokeBean.getContent());

    }

    @Override
    public int getItemCount() {
        return jokeBeanList.size();
    }
}
