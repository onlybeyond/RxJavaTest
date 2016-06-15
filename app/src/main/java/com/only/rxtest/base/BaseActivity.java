package com.only.rxtest.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by only on 16/6/14.
 */
public abstract class BaseActivity extends AppCompatActivity {

    //data
    private long requestStartingTime;//record request data start time

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            restore(savedInstanceState);
        } else {
            initData();
        }
        initView();
        fillDate();
        requestData();
    }

    /**
     * restore data from savedInstanceState
     */
    public void restore(Bundle savedInstanceState) {
    }



    /**
     * init  get query from other page
     */
    public void initData() {
    }



    /**
     * find view from layout and set listener
     */
    public abstract void initView();

    /**
     * init data
     */
    public abstract void fillDate();

    /**
     * network request
     */
     public void requestData(){
         requestStartingTime=System.currentTimeMillis();
     };


    /********************************
     * jump to other activity
     *******************************************/
    public void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    public void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    public void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    public void openActivity(String pAction, Bundle pBundle) {
        Intent intent = new Intent(pAction);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    /**
     * 显示TOAST
     */
    public void showToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    /**
     * 显示TOAST
     */
    public void showToast(final int resId) {
        if (resId > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }




}
