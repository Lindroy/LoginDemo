package com.lindroid.logindemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        context = this;
        countTimer = new CountTimer(totalTime,intervalTime);
        //监听输入的手机号码
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 11) {
                    btnCaptcha.setBackgroundColor(Color.parseColor("#c7c7c7"));
                    btnCaptcha.setTextColor(ContextCompat.getColor(context,android.R.color.black));
                }else {
                    btnCaptcha.setBackgroundColor(ContextCompat.getColor(context,android.R.color.holo_blue_light));
                    btnCaptcha.setTextColor(ContextCompat.getColor(context,android.R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private CountTimer countTimer;

    /**
     *
     */
    class CountTimer extends CountDownTimer{

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

        @Override
        public void onTick(long millisUntilFinished) {
            //拼接要显示的字符串
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append(String.valueOf(millisUntilFinished /1000));
            sb.append("s后重新发送");
            int index = String.valueOf(sb).indexOf("后");
            //给秒数和单位设置蓝色前景色
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(context,android.R.color.holo_blue_dark));
            sb.setSpan(colorSpan,0,index, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            btnCaptcha.setText(sb);

            btnCaptcha.setClickable(false);//倒计时过程中将按钮设置为不可点击
            btnCaptcha.setBackgroundColor(Color.parseColor("#c7c7c7"));
            btnCaptcha.setTextColor(ContextCompat.getColor(context,android.R.color.black));
            btnCaptcha.setTextSize(16);
        }

        @Override
        public void onFinish() {
            btnCaptcha.setBackgroundColor(ContextCompat.getColor(context,android.R.color.holo_blue_light));
            btnCaptcha.setTextColor(ContextCompat.getColor(context,android.R.color.white));
            btnCaptcha.setTextSize(18);
            btnCaptcha.setText("重新发送");
            btnCaptcha.setClickable(true);
        }
    }

    @OnClick({R.id.btn_captcha, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_captcha:
                if (isPhoneNumber(etPhone.getText().toString())) {
                    countTimer.start();
                }else {
                    Toast.makeText(context, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_login:
                if (TextUtils.isEmpty(etPhone.getText().toString())) {
                    Toast.makeText(context, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时取消倒计时
        countTimer.cancel();
    }

    /**
     * 判断用户输入的手机号码是否正确
     * @param str
     * @return
     */
    public boolean isPhoneNumber(String str) {
        String strRegex = "[1][34578]\\d{9}";
        boolean result = str.matches(strRegex);
        return result;
    }
}
