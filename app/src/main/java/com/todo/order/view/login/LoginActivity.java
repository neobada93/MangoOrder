package com.todo.order.view.login;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.todo.order.MainActivity;
import com.todo.order.R;
import com.todo.order.config.MangoPreferences;
import com.todo.order.view.common.BasicActivity;
import com.todo.order.view.common.SoftKeyboardDectectorView;
import com.todo.order.view.talk.TalkActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.todo.order.config.ServerAddress.API_ACTION;
import static com.todo.order.config.MangoPreferences.GBN_LOGIN;

public class LoginActivity extends BasicActivity implements View.OnClickListener{

    private EditText inputId, inputPass;
    private ImageView ivId, ivPass;
    private LinearLayout lyId;
    private LinearLayout lyPass;
    private LinearLayout lyLogin;
    private LinearLayout ly_login_main;
    private CheckBox cb_save_id;
    private String result_txt;
    private String g_m_name, g_f_c_num, g_m_seq;
    private String gbn, uid, upw;
    private MangoPreferences pref = new MangoPreferences(this);
    private LinearLayout.LayoutParams layoutParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ly_login_main = (LinearLayout) findViewById(R.id.ly_login_main);

        inputId  = findViewById(R.id.ed_id);
        inputPass = findViewById(R.id.ed_pass);
        lyId = findViewById(R.id.ly_id);
        lyPass = findViewById(R.id.ly_pass);
        lyLogin = findViewById(R.id.ly_login);
        ly_login_main = findViewById(R.id.ly_login_main);
        ivId = findViewById(R.id.iv_id);
        ivPass = findViewById(R.id.iv_pass);
        cb_save_id = findViewById(R.id.cb_save_id);

        LinearLayout login = findViewById(R.id.ly_login);
        login.setOnClickListener(this);

        String m_email = pref.getString("m_email","");
        inputId.setText(m_email);

        if(pref.getBoolean("isSaveLogin", false)) {
            cb_save_id.setChecked(true);
            inputId.setText(pref.getString("saveid", ""));
        } else {
            cb_save_id.setChecked(false);
            inputId.setText("");
        }

        if(pref.getBoolean("isLogin", false)) {
            LoginTask loginTask = new LoginTask();
            uid = pref.getString("uid", "");
            upw = pref.getString("upw", "");
            pref.putString("uid", uid);
            pref.putString("upw", upw);
            loginTask.execute(new String[] {API_ACTION, GBN_LOGIN, uid, upw});
        }

        changeColor(R.color.white);

        //setIdFocus();

        if(!inputId.getText().toString().isEmpty()) {
            inputPass.requestFocus();
            setPassFocus();
        }

        final SoftKeyboardDectectorView softKeyboardDecector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDecector, new FrameLayout.LayoutParams(-1, -1));

        softKeyboardDecector.setOnShownKeyboard(() -> {
            Log.e("Keyboard", "show");
            final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 440, getResources().getDisplayMetrics());

            layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, height);
            layoutParams.setMargins(0, -200, 0, 0);
            ly_login_main.setLayoutParams(layoutParams);
        });

        softKeyboardDecector.setOnHiddenKeyboard(() -> {
            // 키보드 사라질 때
            Log.e("Keyboard", "hide");
            final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 440, getResources().getDisplayMetrics());

            layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, height);
            layoutParams.setMargins(0, 0, 0, 0);
            ly_login_main.setLayoutParams(layoutParams);
        });

        inputId.setOnFocusChangeListener((view, b) -> {
            setIdFocus();
        });

        inputPass.setOnFocusChangeListener((view, b) -> {
            setPassFocus();
        });

        inputId.addTextChangedListener(tw);
        inputPass.addTextChangedListener(tw);

//        //키보드 올리기
//        InputMethodManager immShow = (InputMethodManager) getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
//        immShow.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(inputPass.getText().toString().trim().length() > 0 && inputId.getText().toString().trim().length() > 0)
            {
                //lyLogin.setBackgroundResource(R.drawable.shape_stroke_under);
            } else {
                //lyLogin.setBackgroundResource(R.drawable.ic_login_btn1);
            }
        }
    };

    public void changeColor(int resourseColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), resourseColor));
        }
    }

    private void setIdFocus() {
        ivId.setImageResource(R.drawable.ic_id_on);
        ivPass.setImageResource(R.drawable.ic_pass_off);
        lyId.setBackgroundResource(R.drawable.shape_login_on);
        lyPass.setBackgroundResource(R.drawable.shape_login_off);
    }

    private void setPassFocus() {
        ivId.setImageResource(R.drawable.ic_id_off);
        ivPass.setImageResource(R.drawable.ic_pass_on);
        lyId.setBackgroundResource(R.drawable.shape_login_off);
        lyPass.setBackgroundResource(R.drawable.shape_login_on);
    }

    @Override
    public void onClick(View view) {

        LoginTask loginTask = new LoginTask();
        String id = inputId.getText().toString().trim();
        String pass = inputPass.getText().toString().trim();
        Boolean isVaild = true;

        if(isVaild && id.isEmpty()) {
            new android.app.AlertDialog.Builder(LoginActivity.this).setTitle("").setMessage(R.string.email_blank).setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss()).setCancelable(false).create().show();
            setIdFocus();
            isVaild = false;
        }

//        Boolean isEmail = id.matches("^[a-zA-Z0-9_.+-]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,4}$");
//        if(isVaild && !isEmail) {
//            new android.app.AlertDialog.Builder(LoginActivity.this).setTitle("").setMessage(R.string.email_inVaild).setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss()).setCancelable(false).create().show();
//            isVaild = false;
//        }

        if(isVaild && pass.isEmpty()) {
            new android.app.AlertDialog.Builder(LoginActivity.this).setTitle("").setMessage(R.string.password_blank).setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss()).setCancelable(false).create().show();
            setPassFocus();
            isVaild = false;
        }

        if (isVaild) {
            loginTask.execute(API_ACTION, GBN_LOGIN, id, pass);
        }

        // 키보드 내리기
        InputMethodManager immhide = (InputMethodManager) getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            if(cb_save_id.isChecked()) {
                pref.putBoolean("isSaveLogin", true);
                pref.putString("saveid", params[2]);
            } else {
                pref.putBoolean("isSaveLogin", false);
                pref.putString("saveid", "");
            }

            String uri = params[0];
            gbn = params[1];
            uid = params[2];
            upw = params[3];
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));
                pairs.add(new BasicNameValuePair("upw", upw));

                post.setEntity(new UrlEncodedFormEntity(pairs,"utf-8"));
                HttpResponse response = client.execute(post);

                HttpEntity ent = response.getEntity();
                result_txt = EntityUtils.toString(ent);
                return true;
            } catch(Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                try{
                    Log.e("result", result.toString());
                    JSONArray objArray = new JSONArray(result_txt);
                    JSONObject data = objArray.getJSONObject(0);
                    String code = data.getString("code");
                    String msg = data.getString("msg");

                    if(code.equals("200")) {
                        //pref.putString("m_email",data.getString("m_email"));
                        pref.putBoolean("isLogin" , true);
                        pref.putString("uid" , uid);
                        pref.putString("upw" , upw);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {
                        pref.putBoolean("isLogin" , false);
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                        dialog.setMessage(msg)
                                .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if ((LoginActivity.this != null) && (!LoginActivity.this.isFinishing())) {
                    new android.app.AlertDialog.Builder(LoginActivity.this)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
        }
    }

    private class PayCheckApi extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String m_seq = params[1];
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("m_seq", m_seq));

                post.setEntity(new UrlEncodedFormEntity(pairs,"utf-8"));
                HttpResponse response = client.execute(post);

                HttpEntity ent = response.getEntity();
                result_txt = EntityUtils.toString(ent);
                return true;
            } catch(Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean) {
                try{

                    JSONObject obj = new JSONObject(result_txt);
                    String isPay = obj.getString("isPay");

                    if(isPay.equals("Y")) {
                        Intent intent = new Intent(LoginActivity.this, TalkActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        pref.putBoolean("isPay", true);

                        startActivity(intent);
                        finish();
                    } else {
                        pref.putBoolean("isLogin", false);
                        pref.putString("id", "");
                        pref.putString("pass", "");
                        pref.putBoolean("isPay", false);

                        String msg = (obj.has("msg") ? obj.getString("msg") : "사용신청 되지 않았거나 기간이 만료 되었습니다.");
                        new android.app.AlertDialog.Builder(LoginActivity.this)
                                .setMessage(msg)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }

                } catch(Exception e) {
                    new android.app.AlertDialog.Builder(LoginActivity.this)
                            .setMessage("에러")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                    e.printStackTrace();
                }
            } else {
                new android.app.AlertDialog.Builder(LoginActivity.this)
                        .setMessage("통신에 실패하였습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        }
    }

}

