package com.todo.order.view.selfcheck;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.todo.order.R;
import com.todo.order.config.MangoPreferences;
import com.todo.order.util.RecyclerTouchListener;
import com.todo.order.view.common.BasicActivity;

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
import java.util.Calendar;

import static com.todo.order.config.MangoPreferences.GBN_SELF_CHECK_LIST;
import static com.todo.order.config.MangoPreferences.GBN_SELF_CHECK_SEND;
import static com.todo.order.config.ServerAddress.API_ACTION_SELF_CHECK;

public class SelfCheckActivity extends BasicActivity implements View.OnClickListener {

    private ImageButton ibHome;
    private TextView tvSelfCheckDate, tvSelfCheckSend, tv_self_check_title, tv_self_check_count;

    private Calendar calendar;
    private String result_txt;

    private String strMonth, strDay;
    private String strToday, strFirstDay;
    private String uid, udt;

    private Integer month, year, day, days;

    private MangoPreferences pref = new MangoPreferences(this);

    private ArrayList<SelfCheckItem> selfCheckItems = new ArrayList<>();

    private SelfCheckAdapter selfCheckAdapter;
    private RecyclerView target;
    private SelfCheckTask selfCheckTask;
    private SelfCheckSendTask selfCheckSendTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_check);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        days = calendar.getActualMaximum(Calendar.DATE);

        strMonth = String.format("%02d", month);
        strDay = String.format("%02d", day);
        strToday = year.toString() + strMonth + strDay;
        strFirstDay = year.toString() + strMonth + "01";

        uid = pref.getString("uid", "");
        udt = pref.getString("udt", strToday);


        ibHome = findViewById(R.id.ib_home);
        ibHome.setOnClickListener(this);

        tvSelfCheckDate = findViewById(R.id.tv_self_check_date);
        tvSelfCheckSend = findViewById(R.id.tv_self_check_send);

        tvSelfCheckSend.setOnClickListener(this);

        tvSelfCheckDate.setText(year.toString()+"."+month.toString()+"."+day.toString());

        selfCheckTask = new SelfCheckTask();
        selfCheckTask.execute(API_ACTION_SELF_CHECK, GBN_SELF_CHECK_LIST, uid, udt);

        tv_self_check_title = findViewById(R.id.tv_self_check_title);
        tv_self_check_title.setPaintFlags(tv_self_check_title.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);

        tv_self_check_count = findViewById(R.id.tv_self_check_count);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.ib_home:
                finish();
                break;
            case R.id.tv_self_check_send:
                selfCheckSendTask = new SelfCheckSendTask();
                String strChkcd;
                String strChk;
                StringBuilder sbCd = new StringBuilder();
                StringBuilder sb = new StringBuilder();
                for(int i=0; i<selfCheckItems.size(); i++) {
                    SelfCheckItem selfCheckItem = selfCheckItems.get(i);

                    if(!selfCheckItem.getChk().matches(" *")) {
                        sb.append(selfCheckItem.getChk());
                        sbCd.append(selfCheckItem.getChkcd());

                        if(i != (selfCheckItems.size()-1)) {
                            sb.append(",");
                            sbCd.append(",");
                        }
                    }

                    Log.e("sb ::: ", sb.toString());
                    Log.e("sbCd ::: ", sbCd.toString());
                }
                selfCheckSendTask.execute(API_ACTION_SELF_CHECK, GBN_SELF_CHECK_SEND, uid, udt, sbCd.toString(), sb.toString());
                break;
        }
    }


    private class SelfCheckTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String gbn = params[1];
            String uid = params[2];
            String udt = params[3];
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));
                pairs.add(new BasicNameValuePair("udt", udt));

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
                    JSONArray array = new JSONArray(result_txt);

                    Log.e("result", array.toString());

                    int count = 0;
                    for(int i = 0; i < array.length(); i++) {
                        JSONObject listObj = array.getJSONObject(i);
                        SelfCheckItem selfCheckItem = new SelfCheckItem();
                        selfCheckItem.setChkcd(listObj.has("chkcd") ? listObj.get("chkcd").toString() : "");
                        selfCheckItem.setChkItem(listObj.has("chkitem") ? listObj.get("chkitem").toString() : "");

                        String strChk = listObj.has("chk") ? listObj.get("chk").toString() : "";

                        selfCheckItem.setChk(strChk);

                        if(strChk.equals("1")) {
                            count++;
                        }

                        selfCheckItems.add(selfCheckItem);
                    }

                    selfCheckAdapter = new SelfCheckAdapter(selfCheckItems, SelfCheckActivity.this, SelfCheckActivity.this);
                    target = findViewById(R.id.rv_self_check);
                    target.setLayoutManager(new LinearLayoutManager(SelfCheckActivity.this));
                    target.setAdapter(selfCheckAdapter);

                    tv_self_check_count.setText("점검목록(" + String.valueOf(count) + "/" + String.valueOf(array.length()) + ")");

                    target.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), target, new RecyclerTouchListener.ClickListener() {

                        @Override
                        public void onClick(View view, int position) {

//                            OrderItem orderItem = mItems.get(position);
//                            ImageButton ibOrderDeleteItem = (ImageButton) (view.findViewById(R.id.ib_order_delete_item));
//                            ibOrderDeleteItem.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLongClick(View view, int position) {
//                            OrderItem orderItem = mItems.get(position);
//                            ImageButton ibOrderDeleteItem = (ImageButton) (view.findViewById(R.id.ib_order_delete_item));
//                            ibOrderDeleteItem.setVisibility(View.VISIBLE);
                        }
                    }));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if ((SelfCheckActivity.this != null) && (!SelfCheckActivity.this.isFinishing())) {
                    new android.app.AlertDialog.Builder(SelfCheckActivity.this)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
        }
    }
    private class SelfCheckSendTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String gbn = params[1];
            String uid = params[2];
            String udt = params[3];
            String chkcd = params[4];
            String chk = params[5];
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));
                pairs.add(new BasicNameValuePair("udt", udt));
                pairs.add(new BasicNameValuePair("chkcd", chkcd));
                pairs.add(new BasicNameValuePair("chk", chk));

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
                        msg = "점검내역이 저장 되었습니다.";
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(SelfCheckActivity.this);
                        dialog.setMessage(msg)
                                .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    } else {
                        msg = "점검내역 저장에 실패했습니다.";
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(SelfCheckActivity.this);
                        dialog.setMessage(msg)
                                .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if ((SelfCheckActivity.this != null) && (!SelfCheckActivity.this.isFinishing())) {
                    new android.app.AlertDialog.Builder(SelfCheckActivity.this)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
        }
    }
}