package com.game.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.game.splash.R;
import com.game.utils.ImagePiece;
import com.game.utils.ImageSplitter;


import java.util.Collections;
import java.util.List;


/**
 * 自定义游戏拼图控件
 */

public class PuzzleLayout extends RelativeLayout implements View.OnClickListener {

    //拼图格数,默认设为3*3
    private int mColumn = 3;
    //拼图容器内边距
    private int mPadding;
    //切片外边距
    private int mMargin = 3;
    //图片
    private Bitmap mBitmap;
    //切片图数组
    private ImageView[] imageItems;
    //位图切片信息数组
    private List<ImagePiece> mItemBitmaps;
    //切片宽度
    private int mItemWidth;
    //游戏面板宽度
    private int mWidth;
    //开关
    private boolean once;
    private boolean isGameSuccess;
    private boolean isTimeEnabled;
    //游戏状态( 0:结束游戏状态；1：正常运行；2：暂停状态)
    private int gameState = 1;
    //计时
    private int mTime;
    //接口
    public PuzzleLayoutListener mListener;
    //关卡
    private int mLevel = 1;





    //接口set
    public void setPuzzleLayoutListener(PuzzleLayoutListener mListener) {
        this.mListener = mListener;
    }
    //isTimeEnabled的set
    public void setTimeEnabled(boolean timeEnabled) {
        isTimeEnabled = timeEnabled;
    }
    //游戏状态的get
    public int getGameState() {
        return gameState;
    }

    //message类型常量
    private static final int TIME_CHANGED = 000000;
    private static final int NEXT_LEVEL = 111111;



    /**
     * 设置接口回调
     * */
    public interface PuzzleLayoutListener
    {
        void nextLevel(int nextLevel,int count);
        void timeChanged(int currentTime);
        void gameOver();
    }
    //handler线程更新ui
    private android.os.Handler mHandler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what)
            {
                case TIME_CHANGED:
                    if (isGameSuccess || gameState == 0 || gameState == 2)
                        //游戏成功、结束、暂停状态时返回
                        return false;
                    if (mListener != null)
                    {
                        mListener.timeChanged(mTime);
                    }
                    if (mTime == 0)
                    {
                        //超时结束游戏
                        gameState = 0;
                        mListener.gameOver();
                        return false;
                    }
                    //时间自减
                    mTime--;
                    //延时一秒向线程再发送一次TIME_CHANGED，这样每秒运行handleMessage（）一次
                    mHandler.sendEmptyMessageDelayed(TIME_CHANGED,1000);
                    break;
                case NEXT_LEVEL:
                    mLevel = mLevel + 1;
                    if (mListener != null)
                    {
                        mListener.nextLevel(mLevel,mTime);
                    }else {
                        nextLevel();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    //下一关
    public void nextLevel() {
        this.removeAllViews();
        mAnimationLayout = null;
        if (mColumn <= 6){
            mColumn++;
        }
        isGameSuccess = false;
        checkTimeEnable();
        initBitmap();
        initItem();
    }
    //重新开始
    public void reStart() {
        mHandler.removeMessages(TIME_CHANGED);
        gameState = 1;
        if (mColumn < 6){
            mColumn--;
        }
        nextLevel();
    }
    //切换状态
    public void toggleGameState() {
        if (gameState == 2)
        {
            gameState = 1;
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }else {
            //设置状态为2（暂停）
            gameState = 2;
            mHandler.removeMessages(TIME_CHANGED);
        }
    }


    /**
     * 构造方法
     * */
    public PuzzleLayout(Context context) {
        this(context,null);
    }
    public PuzzleLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public PuzzleLayout(Context context, AttributeSet attrs, int defStyleRes) {
        super(context, attrs, defStyleRes);
        init();
    }

    /**
     * 初始化
     * */
    private void init() {
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,3,getResources().getDisplayMetrics());
        mPadding = min(getPaddingLeft(),getPaddingRight(),getPaddingTop(),getPaddingBottom());
    }
    //多个参数取最小值
    private int min(int... params) {
        int min = params[0];
        for (int param : params)
        {
            if (param < min)
                min = param;
        }
        return 0;
    };



    /**
     * 自定义控件重写onMeasure测量方法
     * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //取宽高最小值
        mWidth = Math.min(getMeasuredHeight(),getMeasuredWidth());
        if (!once)
        {
            //进行切图，及排序
            initBitmap();
            //设置ImageView(Item)的宽高等属性
            initItem();
            //检查时钟是否开启
            checkTimeEnable();
            once = true;
        }
        //设置测量值
        setMeasuredDimension(mWidth,mWidth);
    }
    //切图
    private void initBitmap() {
        if (mBitmap == null)
        {
            mBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.image_1);
        }
        mItemBitmaps = ImageSplitter.splitImage(mBitmap,mColumn);
        //对切片数组元素进行打乱洗牌
        Collections.shuffle(mItemBitmaps);

    }
    //初始化，设置宽高等属性
    private void initItem() {
        //计算切片宽度
        mItemWidth = (mWidth - mPadding*2 - mMargin*(mColumn - 1)) / mColumn;
        //容器
        imageItems = new ImageView[mColumn * mColumn];
        //生成切片，设置切片及之间的相邻规则
        for (int i = 0;i < imageItems.length; i++ )
        {
            ImageView item  = new ImageView(getContext());
            //切片监听
            item.setOnClickListener(this);
            //切片填充位图、入组、设置ID
            item.setImageBitmap(mItemBitmaps.get(i).getBitmap());
            imageItems[i] = item;
            item.setId(i + 1);
            //在item的tag中存储index，这样打乱后的切片能记住自己ImagePiece的位置
            item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());
            //向父容器传递item的宽高(LayoutParams用于告诉父容器，子元素的信息、布局)
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth,mItemWidth);
            //设置item的横向间距
            //最后一列不设置
            if ((i + 1) % mColumn != 0)
            {
                lp.rightMargin = mMargin;
            }
            //第一列不设置
            if (i % mColumn != 0)
            {
                lp.addRule(RelativeLayout.RIGHT_OF,imageItems[i - 1].getId());
            }
            //如果不是第一行，设置topMargin和rule
            if ((i + 1) > mColumn)
            {
                lp.topMargin = mMargin;
                lp.addRule(RelativeLayout.BELOW,imageItems[i - mColumn].getId());
            }
            addView(item,lp);
        }
    }
    //检查时钟是否开启
    private void checkTimeEnable() {
        if (isTimeEnabled)
        {
            //根据当前等级设置时间
            countTimeBaseLevel();
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }
    //根据当前等级设置时间
    private void countTimeBaseLevel() {
        if (mColumn < 6){
            mTime = (int) Math.pow(2,mLevel) * 60;
        }else {
            mTime = (int) Math.pow(2,4) * 60 - mLevel*100;
        }
    }



    private ImageView mFirstClick;
    private ImageView mSecondClick;
    //动画层
    private RelativeLayout mAnimationLayout;
    private boolean isAniming;
    /**
     * 切片的点击监听
     * */
    @Override
    public void onClick(View view) {
        if (isAniming)
            return;
        //两次点击同一view
        if (mFirstClick == view)
        {
            //清除高亮
            mFirstClick.clearColorFilter();
            mFirstClick = null;
            return;
        }
        if (mFirstClick == null)
        {
            mFirstClick = (ImageView) view;
            //高亮
            mFirstClick.setColorFilter(Color.parseColor("#55ffffff"));
        }else {
            mSecondClick = (ImageView) view;
            //交换图片
            exchangeView();
        }
    }
    //交换图片
    private void exchangeView() {
        //清除高亮
        mFirstClick.clearColorFilter();
        //创建动画层，来运行交换图片时的动画效果
        setUpAnmationLayout();
        //复制要交换的两张图添加到动画层
        ImageView first = new ImageView(getContext());
        ImageView second = new ImageView(getContext());
        final Bitmap firstBitmap = addViewToManmationLayout(mFirstClick,first);
        final Bitmap secondBitmap = addViewToManmationLayout(mSecondClick,second);
        //设置动画
        TranslateAnimation animation = new TranslateAnimation(0,mSecondClick.getLeft() - mFirstClick.getLeft(),0,mSecondClick.getTop() - mFirstClick.getTop());
        animation.setDuration(300);
        animation.setFillAfter(true);
        first.startAnimation(animation);
        TranslateAnimation animation2 = new TranslateAnimation(0,-mSecondClick.getLeft() + mFirstClick.getLeft(),0,-mSecondClick.getTop() + mFirstClick.getTop());
        animation2.setDuration(300);
        animation2.setFillAfter(true);
        second.startAnimation(animation2);

        //监听动画
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //动画开始时,隐藏真实层图片
                mFirstClick.setVisibility(View.INVISIBLE);
                mSecondClick.setVisibility(View.INVISIBLE);
                isAniming = true;
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束时
                String firstTag = (String) mFirstClick.getTag();
                String secondTag = (String) mSecondClick.getTag();
                mSecondClick.setImageBitmap(firstBitmap);
                mFirstClick.setImageBitmap(secondBitmap);
                mSecondClick.setTag(firstTag);
                mFirstClick.setTag(secondTag);
                //显示真实图片
                mFirstClick.setVisibility(View.VISIBLE);
                mSecondClick.setVisibility(View.VISIBLE);
                //清除变量指向
                mFirstClick = mSecondClick = null;
                //清空动画层
                mAnimationLayout.removeAllViews();
                isAniming = false;
                //判断拼图是否成功
                checkSuccess();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
    //检查拼图成功
    private void checkSuccess() {
        boolean isSuccess = true;
        for (int i = 0;i < imageItems.length; i++ )
        {
            ImageView imageView = imageItems[i];
            if (getImageIndexByTag((String)imageView.getTag()) != i)
            {
                //如果图片的tag不等于索引，表示图片不在正确位置上，拼图未完成
                isSuccess = false;
            }
        }
        if (isSuccess)
        {
            isGameSuccess = true;
            Toast.makeText(getContext(),getContext().getString(R.string.toastSuccess),Toast.LENGTH_LONG).show();
            mHandler.sendEmptyMessage(NEXT_LEVEL);
        }
    }
    //根据tag获取index
    private int getImageIndexByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }
    //添加view到动画层
    private Bitmap addViewToManmationLayout(ImageView mClick,ImageView newView) {
        //拿到位图
        Bitmap newBitmap = mItemBitmaps.get(getImageIdByTag((String) mClick.getTag())).getBitmap();
        //填充
        newView.setImageBitmap(newBitmap);
        LayoutParams lp = new LayoutParams(mItemWidth,mItemWidth);
        lp.leftMargin = mClick.getLeft() - mPadding;
        lp.topMargin = mClick.getTop() - mPadding;
        newView.setLayoutParams(lp);
        mAnimationLayout.addView(newView);
        return newBitmap;
    }
    //根据tag获取id
    private int getImageIdByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);
    }
    //创建动画层
    private void setUpAnmationLayout() {
        if (mAnimationLayout == null)
        {
            mAnimationLayout = new RelativeLayout(getContext());
            addView(mAnimationLayout);
        }
    }
}
