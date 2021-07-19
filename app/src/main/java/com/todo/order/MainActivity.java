package com.todo.order;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gun0912.tedpermission.util.ObjectUtils;
import com.todo.order.config.MangoPreferences;
import com.todo.order.view.common.BasicActivity;
import com.todo.order.view.common.CustomDialog;
import com.todo.order.view.common.OwnerInfoDialog;
import com.todo.order.view.login.LoginActivity;
import com.todo.order.view.notice.NoticeActivity;
import com.todo.order.view.order.OrderActivity;
import com.todo.order.view.order.OrderTimeActivity;
import com.todo.order.view.order.status.OrderStatusActivity;
import com.todo.order.view.selfcheck.SelfCheckActivity;
import com.todo.order.view.talk.TalkActivity;
import com.todo.order.util.Etc;

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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.todo.order.config.MangoPreferences.GBN_MAIN;
import static com.todo.order.config.ServerAddress.API_ACTION;

public class MainActivity extends BasicActivity implements View.OnClickListener {

    private TextView tv_hi, tv_hi0;
    private TextView tv_amount_outstanding, tv_amount_outstanding_text, tv_won;
    private TextView tv_payment_date, tv_label_payment_date;
    private TextView tv_bank, tv_bank_account, tv_bank_account_holder;
    private ImageView iv_owner_info;
    private ImageView iv_talk;
    private ImageView iv_notice;
    private ImageView iv_self_check;
    private TextView tv_order, tv_status;
    private LinearLayout btn_logout;
    OwnerInfoDialog ownerInfoDialog;

    private String result_txt, strOwnerInfoBnum, strOwnerInfoAddr, strOwnerInfoTel, strOwnerInfoManager;
    private MangoPreferences pref = new MangoPreferences(this);

    Etc etc = new Etc();

    DecimalFormat formater1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_talk = findViewById(R.id.iv_talk);
        iv_notice = findViewById(R.id.iv_notice);
        iv_self_check = findViewById(R.id.iv_self_check);
        tv_order = findViewById(R.id.tv_order);
        tv_status = findViewById(R.id.tv_status);
        btn_logout = findViewById(R.id.btn_logout);
        iv_owner_info = findViewById(R.id.iv_owner_info);

        tv_hi = findViewById(R.id.tv_hi);
        tv_hi0 = findViewById(R.id.tv_hi0);
        tv_amount_outstanding = findViewById(R.id.tv_amount_outstanding);
        tv_amount_outstanding_text = findViewById(R.id.tv_amount_outstanding_text);
        tv_won = findViewById(R.id.tv_won);
        tv_payment_date = findViewById(R.id.tv_payment_date);
        tv_label_payment_date = findViewById(R.id.tv_label_payment_date);

        tv_bank = findViewById(R.id.tv_bank);
        tv_bank_account = findViewById(R.id.tv_bank_account);
        tv_bank_account_holder = findViewById(R.id.tv_bank_account_holder);

        iv_talk.setOnClickListener(this);
        iv_notice.setOnClickListener(this);
        iv_self_check.setOnClickListener(this);
        tv_order.setOnClickListener(this);
        tv_status.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        iv_owner_info.setOnClickListener(this);

        String uid = pref.getString("uid", "");

        MainTask mainTask = new MainTask();
        mainTask.execute(API_ACTION, GBN_MAIN, uid);

        formater1000 = new DecimalFormat("###,###");

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_logout :
                pref.resetData("uid");
                pref.resetData("upw");
                pref.resetData("isLogin");
                pref.resetData("m_email");
                pref.resetData("notice");
                intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_talk :
                intent = new Intent(MainActivity.this, TalkActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.iv_notice :
                intent = new Intent(MainActivity.this, NoticeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.iv_self_check :
                intent = new Intent(MainActivity.this, SelfCheckActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.iv_owner_info :

                int[] location = new int[2];
                iv_owner_info.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];

                ownerInfoDialog = new OwnerInfoDialog(MainActivity.this, strOwnerInfoBnum, strOwnerInfoAddr, strOwnerInfoTel, strOwnerInfoManager);
                ownerInfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams wmlp = ownerInfoDialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                wmlp.x = x-560;
                wmlp.y = y;

                ownerInfoDialog.setOnDismissListener(onDismissListener);
                ownerInfoDialog.show();
                break;
            case R.id.tv_order :

                String stTm = pref.getString("stTm", "");
                String edTm = pref.getString("edTm", "");

                if (!stTm.equals("") && !edTm.equals("")) {

                    int intStartTime = 0, intEndTime = 0;

                    try {
                        intStartTime = Integer.parseInt(stTm);
                        intEndTime = Integer.parseInt(edTm);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat sdfToday = new SimpleDateFormat("yyyyMMddHHmmss");
                    sdfToday.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

                    Calendar calendarToday = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
                    int currentHour = calendarToday.get(Calendar.HOUR_OF_DAY);
                    Date dateToday = calendarToday.getTime();
                    String strToday = sdfToday.format(dateToday);

                    Calendar calendarTodayStart = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
                    calendarTodayStart.set(Calendar.HOUR_OF_DAY, Integer.valueOf(stTm));
                    calendarTodayStart.set(Calendar.MINUTE, 0);
                    calendarTodayStart.set(Calendar.SECOND, 0);
                    Date dateTodayStart = calendarTodayStart.getTime();
                    String strTodayStart = sdfToday.format(dateTodayStart);

                    Calendar calendarTomorrowEnd = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
                    calendarTomorrowEnd.add(Calendar.DATE, 1);
                    calendarTomorrowEnd.set(Calendar.HOUR_OF_DAY, Integer.valueOf(edTm));
                    calendarTomorrowEnd.set(Calendar.MINUTE, 0);
                    calendarTomorrowEnd.set(Calendar.SECOND, 0);
                    Date dateTomorrowEnd = calendarTomorrowEnd.getTime();
                    String strTomorrowEnd = sdfToday.format(dateTomorrowEnd);

                    if(intStartTime > currentHour && intEndTime < currentHour ) {
                        intent = new Intent(MainActivity.this, OrderTimeActivity.class);
                        intent.putExtra("EXTRA_ORDER_TIME",  "금일 " + pref.getString("stTm", "") + "시 ~ 익일 " + pref.getString("edTm", "") + "시");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }

                    Log.e("currentHour", String.valueOf(currentHour));
                    Log.e("intStartTime", String.valueOf(intStartTime));
                    Log.e("intEndTime", String.valueOf(intEndTime));

                }

                intent = new Intent(MainActivity.this, OrderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.tv_status :
                intent = new Intent(MainActivity.this, OrderStatusActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialogInterface) {
//            barcode_et.setText("");
//            barcode_et.requestFocus();
//            barcode_et.setEnabled(true);
        }
    };

    private class MainTask extends AsyncTask<String, Void, Boolean> {

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
                    JSONArray objArray = new JSONArray(result_txt);
                    JSONObject data = objArray.getJSONObject(0);

                    Log.e("data", data.toString());

                    String ownerNm = data.getString("ownerNm"); //점주명
                    String corpNo = data.getString("corpNo");   //사업자등록번호
                    String adr = data.getString("adr");         //주소
                    String telNo = data.getString("telNo");     //전화번호
                    String svEmp = data.getString("svEmp");     //담당자
                    String amt = data.getString("amt");         //미결제금액
                    String sancDt = etc.dateFormat(data.getString("sancDt"), "yyyyMMdd", "yyyy.MM.dd");  //결제일
                    String loanAmt = data.getString("loanAmt"); //여신금액
                    String strHi = ownerNm + " 점주님";
                    String strAmountOutstanding = etc.comma(amt);
                    String strPaymentDate = sancDt;
                    String strAmountLoan = etc.comma(loanAmt) + "원";
                    strOwnerInfoBnum = "• 사업자번호 : " + corpNo;
                    strOwnerInfoAddr = adr;
                    strOwnerInfoTel = "• 전화번호 : " + telNo;
                    strOwnerInfoManager = "• 담당자 : " + svEmp;
                    String strBank = data.getString("bank");
                    String strBankAccount = ObjectUtils.isEmpty(data.getString("acctno")) ? "" : data.getString("acctno");
                    String strBankAccountHolder = "(" + data.getString("achr") + ")";
                    if(strBankAccountHolder.equals("()")) {
                        strBankAccountHolder = "";
                    }

                    pref.putString("loanAmt", data.has("loanAmt") ? data.getString("loanAmt") : "");
                    pref.putString("amt", data.has("amt") ? data.getString("amt") : "");
                    pref.putString("loanCkYn", data.has("loanCkYn") ? data.getString("loanCkYn") : "");

                    pref.putString("ownerNm", ownerNm);

                    tv_hi.setText(strHi);

                    tv_hi0.setPaintFlags(tv_hi0.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);
                    tv_hi.setPaintFlags(tv_hi.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);
                    tv_amount_outstanding.setPaintFlags(tv_amount_outstanding.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);
                    tv_payment_date.setPaintFlags(tv_payment_date.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);
                    tv_amount_outstanding_text.setPaintFlags(tv_amount_outstanding_text.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);
                    tv_won.setPaintFlags(tv_won.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);

                    tv_amount_outstanding.setText(strAmountOutstanding);
                    tv_payment_date.setText(strPaymentDate);

                    if(TextUtils.isEmpty(strPaymentDate)) {
                        tv_label_payment_date.setVisibility(View.GONE);
                    }

                    tv_bank.setText(strBank);
                    tv_bank_account.setText(strBankAccount);
                    tv_bank_account_holder.setText(strBankAccountHolder);

                    String noticeTitle = data.getString("title");
                    String noticeContent = data.getString("cont");

                    String stTm = data.getString("stTm");
                    String edTm = data.getString("edTm");
                    pref.putString("stTm", stTm);
                    pref.putString("edTm", edTm);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmm");

                    if(!noticeTitle.isEmpty() && !pref.getString("notice", "").equals(sdf.format(new Date()))) {
                        CustomDialog customDialog = new CustomDialog(MainActivity.this, noticeTitle, noticeContent);
                        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        customDialog.show();
                    }

//                    pref.putString("stTm", listObj.has("stTm") ? listObj.get("stTm").toString() : "");
//                    pref.putString("edTm", listObj.has("edTm") ? listObj.get("edTm").toString() : "");

//                    for(int i = 0; i < array.length(); i++) {
//                        JSONObject listObj = array.getJSONObject(i);
//
//                        ChurchItem churchItem = new ChurchItem();
//                        churchItem.setCategory(listObj.has("c_vName") ? listObj.get("c_vName").toString() : "");
//                        churchItem.setName(listObj.has("c_pName") ? listObj.get("c_pName").toString() : "");
//                        churchItem.setNum(listObj.has("c_num") ? listObj.get("c_num").toString() : "");
//
//                        mItems.add(churchItem);
//                    }
//
//                    getListByTab("청사", R.id.target1, R.id.target1_txt);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if ((MainActivity.this != null) && (!MainActivity.this.isFinishing())) {
                    new android.app.AlertDialog.Builder(MainActivity.this)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
        }
    }

}