package com.emmanuel.wechatdemo.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emmanuel.wechatdemo.App;
import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.adapter.FriendsAdapter;
import com.emmanuel.wechatdemo.adapter.RecycleViewItemListener;
import com.emmanuel.wechatdemo.bean.Comment;
import com.emmanuel.wechatdemo.bean.ShuoShuo;
import com.emmanuel.wechatdemo.util.DataFactory;
import com.emmanuel.wechatdemo.util.Helper;
import com.emmanuel.wechatdemo.util.SoftKeyBoardUtil;
import com.emmanuel.wechatdemo.util.UserUtil;
import com.emmanuel.wechatdemo.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "FriendsActivity";
    private boolean keyboardShowFlag = false;

    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<ShuoShuo>listSS;

    private boolean isLoadMore = false;
    private ImageView ivMore;
    private EditText etInput;
    private LinearLayout layoutInput;
    private TextView tvSend, tvVoice;

    private int commentPosition = -1; //评论的说说id

    private RecycleViewItemListener listener = new RecycleViewItemListener() {
        @Override
        public void onItemClick(int position) {

        }

        @Override
        public void onCommentClick(int position) {
            layoutInput.setVisibility(View.VISIBLE);
            etInput.setFocusableInTouchMode(true);
            etInput.setFocusable(true);
            etInput.requestFocus();
            SoftKeyBoardUtil.showSoftKeyBoard(etInput);
            commentPosition = position;
        }
    };

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friends);
        initViews();
        updata();
    }

    private void initViews() {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_friends);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,  LinearLayoutManager.VERTICAL));
        friendsAdapter = new FriendsAdapter(this);
        friendsAdapter.setItemListener(listener);
        recyclerView.setAdapter(friendsAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
                if(lastVisibleItemPosition >= friendsAdapter.getItemCount()-1  && !isLoadMore){
                    isLoadMore = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadMore();
                        }
                    }, 3000);
                }
            }
        });
        initKeyBoard();
    }

    private void initKeyBoard() {
        ivMore = (ImageView)findViewById(R.id.iv_more);
        etInput = (EditText)findViewById(R.id.et_input);
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String content = etInput.getText().toString();
                if(content != null && content.length() > 0){
                    tvSend.setVisibility(View.VISIBLE);
                    ivMore.setVisibility(View.GONE);
                }else {
                    tvSend.setVisibility(View.GONE);
                    ivMore.setVisibility(View.VISIBLE);
                }
            }
        });
        layoutInput = (LinearLayout)findViewById(R.id.layout_input);
        tvSend = (TextView)findViewById(R.id.tv_send);
        tvSend.setOnClickListener(this);
    }

    private void updata(){
        listSS = new ArrayList<>();
        listSS.addAll(DataFactory.createShuoShuo(10));//随机产生10条说说
        friendsAdapter.setDatas(listSS);
    }

    private void loadMore(){
        listSS.addAll(DataFactory.createShuoShuo(10));//随机产生10条说说
        friendsAdapter.notifyDataSetChanged();
        isLoadMore = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_send:
                String content = etInput.getText().toString().trim();
                if(commentPosition >=0 && commentPosition < listSS.size() && content != null && content.length() > 0){
                    Comment comment = new Comment();
                    comment.content = content;
                    comment.fromUser = App.getInstance().getUser();
                    comment.toUser = listSS.get(commentPosition).user;
                    listSS.get(commentPosition).commentList.add(comment);
                    friendsAdapter.notifyDataSetChanged();
                    SoftKeyBoardUtil.hideSoftKeyBoard(etInput);
                    etInput.setText("");
                    layoutInput.setVisibility(View.GONE);
                }
                break;
        }
    }
}