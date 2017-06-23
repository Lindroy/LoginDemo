package com.lindroid.logindemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 *
 */
public class LoginActivity extends AppCompatActivity {
    private Context context;

    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_captcha)
    EditText etCaptcha;
    @BindView(R.id.btn_captcha)
    Button btnCaptcha;
    @BindView(R.id.btn_login)
    Button btnLogin;

    //倒计时的总时间
    private long totalTime = 10000;
    //倒计时的间隔时间
    private long intervalTime = 1000;
    private EventHandler eventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        context = this;

        if (countTimer != null) {
            countTimer.cancel();
        }
        countTimer = new CountTimer(totalTime, intervalTime);
        //监听输入的手机号码
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 11) {
                    btnCaptcha.setBackgroundColor(Color.parseColor("#c7c7c7"));
                    btnCaptcha.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                } else {
                    btnCaptcha.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_blue_light));
                    btnCaptcha.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private CountTimer countTimer;

    /**
     * 点击按钮后倒计时
     */
    class CountTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * 倒计时过程中调用
         *
         * @param millisUntilFinished
         */
        @Override
        public void onTick(long millisUntilFinished) {
//            double time = millisUntilFinished;
            int time = (int) (Math.round((double) millisUntilFinished / 1000) - 1);
            //拼接要显示的字符串
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.clear();
            sb.append(String.valueOf(time));
            sb.append("s后重新发送");
            int index = String.valueOf(sb).indexOf("后");
            //给秒数和单位设置蓝色前景色
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, android.R.color.holo_blue_dark));
            sb.setSpan(colorSpan, 0, index, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            btnCaptcha.setText(sb);
            //设置倒计时中的按钮外观
            btnCaptcha.setClickable(false);//倒计时过程中将按钮设置为不可点击
            btnCaptcha.setBackgroundColor(Color.parseColor("#c7c7c7"));
            btnCaptcha.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            btnCaptcha.setTextSize(16);
        }

        /**
         * 倒计时完成后调用
         */
        @Override
        public void onFinish() {
            //设置倒计时结束之后的按钮样式
            btnCaptcha.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_blue_light));
            btnCaptcha.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            btnCaptcha.setTextSize(18);
            btnCaptcha.setText("重新发送");
            btnCaptcha.setClickable(true);
        }
    }

    //发送短信验证码
    private void sendSMSCaptcha() {
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };

        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
        //向服务器请求发送验证码的服务，需要传递国家代号和接收验证码的手机号码
        SMSSDK.getVerificationCode("86", etPhone.getText().toString());
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    Toast.makeText(LoginActivity.this, "验证码正确", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, MainActivity.class));
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    Toast.makeText(LoginActivity.this, "验证码已经发送", Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                }
            } else {
                ((Throwable) data).printStackTrace();
                Toast.makeText(LoginActivity.this, "验证码获取失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @OnClick({R.id.btn_captcha, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_captcha:
                if (!isPhoneNumber(etPhone.getText().toString())) {
                    Toast.makeText(context, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                countTimer.start();

//                if (isPhoneNumber(etPhone.getText().toString())) {
////                    sendSMSCaptcha();
//                    countTimer.start();
////                    // 注册监听器
////                    SMSSDK.registerEventHandler(eventHandler);
////                    //向服务器请求发送验证码的服务，需要传递国家代号和接收验证码的手机号码
////                    SMSSDK.getVerificationCode("86",etPhone.getText().toString());
//                } else {
//                    Toast.makeText(context, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
//                }
                break;
            case R.id.btn_login:
                String phone = etPhone.getText().toString();
                String captcha = etCaptcha.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(context, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (captcha.length() == 4) {
                    SMSSDK.submitVerificationCode("86", phone, captcha);
                } else {
                    Toast.makeText(context, "请输入完整的验证码", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时取消计时
        countTimer.cancel();
        if (eventHandler != null) {
            SMSSDK.unregisterEventHandler(eventHandler);
        }
    }

    /**
     * 判断用户输入的手机号码是否正确
     *
     * @param str
     * @return
     */
    public boolean isPhoneNumber(String str) {
        String strRegex = "[1][34578]\\d{9}";
        boolean result = str.matches(strRegex);
        return result;
    }
}
