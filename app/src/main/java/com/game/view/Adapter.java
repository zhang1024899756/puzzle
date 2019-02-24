package com.game.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.game.http.User;
import com.game.splash.R;




import java.util.ArrayList;

/**
 * Created by zhangxinyu on 2019/1/8.
 */

public class Adapter extends RecyclerView.Adapter <Adapter.LinearViewHolder> {
    private Context mContext;
    //数据User
    private ArrayList<User> mList = new ArrayList<User>();
    //实例化方法
    public Adapter(Context mContext, ArrayList<User> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }
    //创建视图ViewHolder
    @Override
    public LinearViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item,viewGroup,false));
    }
    //绑定数据
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Adapter.LinearViewHolder linearViewHolder, int i) {
        linearViewHolder.count.setText(""+mList.get(i).getCount());
        linearViewHolder.user.setText(""+mList.get(i).getUser());
    }
    //设置显示条目
    @Override
    public int getItemCount() {
        return mList.size();
    }
    //LinearViewHolder定位数据布局
    public class LinearViewHolder  extends RecyclerView.ViewHolder {

        public TextView count;
        public TextView user;
        public LinearViewHolder (View itemView) {
            super(itemView);
            count = itemView.findViewById(R.id.count);
            user = itemView.findViewById(R.id.user);
        }
    }
}
