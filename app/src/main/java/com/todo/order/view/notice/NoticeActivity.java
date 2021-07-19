package com.todo.order.view.notice;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.todo.order.R;
import com.todo.order.view.common.BasicActivity;
import com.todo.order.config.MangoPreferences;

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

import static com.todo.order.config.MangoPreferences.GBN_NOTICE_LIST;
import static com.todo.order.config.ServerAddress.API_ACTION_BOARD;

public class NoticeActivity extends BasicActivity implements View.OnClickListener {

    private RecyclerView rvNotice;
    private NoticeAdapter noticeAdapter;
    private ArrayList<NoticeItem> mNoticeItems;
    private String result_txt;
    private NoticeTask noticeTask;
    private String uid;
    private MangoPreferences pref;
    private ImageButton ibHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        pref = new MangoPreferences(this);


        ibHome = findViewById(R.id.ib_home);

        ibHome.setOnClickListener(this);

        mNoticeItems = new ArrayList<>();
        uid  = pref.getString("uid", "");
        noticeTask = new NoticeTask();
        noticeTask.execute(API_ACTION_BOARD, GBN_NOTICE_LIST, uid);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        switch(view.getId()) {
            case R.id.ib_home:
                finish();
                break;
        }
    }

    private class NoticeTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String gbn = params[1];
            String uid = params[2];

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));

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

                    for(int i = 0; i < array.length(); i++) {
                        JSONObject listObj = array.getJSONObject(i);
                        String title = listObj.has("title") ? listObj.get("title").toString() : "";
                        String date = listObj.has("regdt") ? listObj.get("regdt").toString() : "";
                        String content = listObj.has("cont") ? listObj.get("cont").toString() : "";
                        String url = listObj.has("url") ? listObj.get("url").toString() : "";
                        String gbn = listObj.has("gbn") ? listObj.get("gbn").toString() : "";
                        String n = listObj.has("new") ? listObj.get("new").toString() : "";

                        String position = "";
                        if(getIntent().getStringExtra("EXTRA_NOTICE_POSITION") != null) {
                            position = getIntent().getStringExtra("EXTRA_NOTICE_POSITION");
                        }
                        String seq = listObj.has("seq") ? listObj.get("seq").toString() : "";

                        NoticeItem noticeItem = new NoticeItem(title, date, content, url, position, seq, gbn, n);

                        mNoticeItems.add(noticeItem);
                    }

                    noticeAdapter = new NoticeAdapter( mNoticeItems, NoticeActivity.this);
                    rvNotice = findViewById(R.id.rv_notice);

                    rvNotice.addItemDecoration(new DividerItemDecoration(NoticeActivity.this, DividerItemDecoration.VERTICAL));

                    rvNotice.setLayoutManager(new LinearLayoutManager(NoticeActivity.this));
                    rvNotice.setAdapter(noticeAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if ((NoticeActivity.this != null) && (!NoticeActivity.this.isFinishing())) {
                    new android.app.AlertDialog.Builder(NoticeActivity.this)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
        }
    }
}