package com.game.index;



import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.game.http.HttpServer;
import com.game.http.Messages;
import com.game.http.User;
import com.game.splash.R;
import com.game.view.Adapter;
import com.game.view.PuzzleLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class IndexActivity extends AppCompatActivity {

    private User user;
    private Messages message;
    private ArrayList<User> worldLi;
    private PuzzleLayout mPuzzleLayout;
    private TextView timeView;
    private TextView levleView;
    private TextView name;
    private ImageButton stateBotton;
    private ImageButton restartBotton;
    private ImageButton userBotton;
    private EditText userEdit;
    private EditText pwdEdit;
    private ImageButton worldBotton;

    private RecyclerView recyclerView;

    private int gameState;
    private String mUser;
    private String mPwd;
    private int stream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);


        mPuzzleLayout = findViewById(R.id.id_PuzzleLayout);
        timeView = findViewById(R.id.timeView);
        levleView = findViewById(R.id.levelView);
        name = findViewById(R.id.name);
        stateBotton = findViewById(R.id.stateBotton);
        userBotton = findViewById(R.id.user);
        worldBotton = findViewById(R.id.world);
        restartBotton = findViewById(R.id.reStartButton);

        final View recView = View.inflate(IndexActivity.this, R.layout.layout_relative, null);
        final View inputView = View.inflate(IndexActivity.this, R.layout.activity_login, null);
        userEdit = inputView.findViewById(R.id.userEdit);
        pwdEdit = inputView.findViewById(R.id.pwdEdit);

        recyclerView = recView.findViewById(R.id.layout_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(IndexActivity.this));

        final MaterialDialog materialDialog = new MaterialDialog.Builder(IndexActivity.this)
                .title("江左梅郎-琅琊榜首")
                .titleGravity(GravityEnum.CENTER)
                .customView(recyclerView,true)
                .build();

        worldBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null)
                {
                    HttpServer.worldLi(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            String jsonStr = null;
                            try {
                                jsonStr = new String(response.body().bytes());
                                Gson gson = new Gson();
                                JsonParser parser = new JsonParser();
                                JsonArray jsonArray = parser.parse(jsonStr).getAsJsonArray();
                                worldLi = new ArrayList<User>();
                                for (JsonElement obj : jsonArray) {
                                    User liItem = gson.fromJson(obj, User.class);
                                    worldLi.add(liItem);
                                }
                                recyclerView.setAdapter(new Adapter(IndexActivity.this,worldLi));
                                materialDialog.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                            Toast.makeText(IndexActivity.this, "--" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(IndexActivity.this, "先登录，后看榜" , Toast.LENGTH_SHORT).show();
                }
            }
        });


        //玩家登录按钮监听
        userBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(IndexActivity.this)
                        .title("登录")
                        .titleGravity(GravityEnum.CENTER)
                        .customView(inputView, true)
                        .neutralText("注册")
                        .positiveText("登录")
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                mUser = userEdit.getText().toString();
                                mPwd = pwdEdit.getText().toString();
                                if (mUser.equals("") || mPwd.equals("")) {
                                    Toast.makeText(IndexActivity.this, "用户名或密码为空", Toast.LENGTH_SHORT).show();
                                } else if (which.toString().equals("POSITIVE")) {
                                    HttpServer.logIn(mUser, mPwd, new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            String jsonStr = null;
                                            try {
                                                jsonStr = new String(response.body().bytes());
                                                Gson gson = new Gson();
                                                if (response.code() == 200) {
                                                    user = gson.fromJson(jsonStr, User.class);
                                                    name.setText(user.getUser() + "--" + user.getCount());
                                                } else if (response.code() == 201) {
                                                    message = gson.fromJson(jsonStr, Messages.class);
                                                    Toast.makeText(IndexActivity.this, message.getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                                            Toast.makeText(IndexActivity.this, "--" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else if (which.toString().equals("NEUTRAL")) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("user", mUser);
                                    map.put("password", mPwd);
                                    HttpServer.logUp(map, new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            String jsonStr = null;
                                            try {
                                                jsonStr = new String(response.body().bytes());
                                                Gson gson = new Gson();
                                                message = gson.fromJson(jsonStr, Messages.class);
                                                Toast.makeText(IndexActivity.this, message.getMessage(), Toast.LENGTH_SHORT).show();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                                            Toast.makeText(IndexActivity.this, "--" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        })
                        .show();
            }
        });


        mPuzzleLayout.setTimeEnabled(true);
        //puzzle接口监听
        mPuzzleLayout.setPuzzleLayoutListener(new PuzzleLayout.PuzzleLayoutListener() {
            @Override
            public void nextLevel(final int nextLevel, final int mTime) {
                if (user != null) {
                    System.out.println(user.getUser());
                    user.setCount(user.getCount() + mTime);
                    name.setText(user.getUser() + "--" + user.getCount());
                    HttpServer.upData(user.getUser(), user.getCount(), new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Toast.makeText(IndexActivity.this, "积分 + "+mTime , Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                        }
                    });
                }
                new AlertDialog.Builder(IndexActivity.this)
                        .setTitle("游戏提示")
                        .setMessage("恭喜完成拼图！")
                        .setCancelable(false)
                        .setPositiveButton("下一关", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mPuzzleLayout.nextLevel();
                                levleView.setText("第   " + nextLevel + "   关");
                            }
                        }).show();
            }

            @Override
            public void timeChanged(int currentTime) {
                timeView.setText("" + currentTime);
                if (currentTime >= 30) {
                    timeView.setTextColor(timeView.getResources().getColor(R.color.colorPrimaryDark));
                } else {
                    timeView.setTextColor(timeView.getResources().getColor(R.color.colorAccent));
                }
            }

            @Override
            public void gameOver() {
                new AlertDialog.Builder(IndexActivity.this)
                        .setTitle("—_—!!!")
                        .setMessage("盲人选手还是手残患者？")
                        .setPositiveButton("再次挑战", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mPuzzleLayout.reStart();
                            }
                        })
                        .setNegativeButton("溜了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
            }
        });

        stateBotton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                gameState = mPuzzleLayout.getGameState();
                if (gameState != 0) {
                    if (gameState == 1) {
                        stateBotton.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                    } else if (gameState == 2) {
                        stateBotton.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
                    }
                    mPuzzleLayout.toggleGameState();
                }
            }
        });

        restartBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPuzzleLayout.reStart();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPuzzleLayout.toggleGameState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPuzzleLayout.toggleGameState();
    }
}
