package com.todo.order.view.order;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.todo.order.R;
import com.todo.order.config.MangoPreferences;
import com.todo.order.util.CalendarAdapter;
import com.todo.order.util.MyCalendar;
import com.todo.order.util.RecyclerTouchListener;
import com.todo.order.util.myCalendarData;
import com.todo.order.view.common.BasicActivity;
import com.todo.order.view.favoirtes.FavoritesActivity;
import com.todo.order.view.order.additem.AddItemActivity;
import com.todo.order.view.order.orderlast.OrderLastActivity;

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
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.todo.order.config.MangoPreferences.GBN_ORDER;
import static com.todo.order.config.MangoPreferences.GBN_ORDER_CANCEL;
import static com.todo.order.config.MangoPreferences.GBN_ORDER_LIST;
import static com.todo.order.config.MangoPreferences.GBN_ORDER_SEND;
import static com.todo.order.config.ServerAddress.API_ACTION;
import static com.todo.order.config.ServerAddress.API_ACTION_MOBILE;

public class OrderActivity extends BasicActivity implements View.OnClickListener {

    private static final int ACTIVITY_CONSTANT = 1;
    private static final int ACTIVITY_LAST = 2;
    private List<MyCalendar> calendarList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CalendarAdapter mAdapter;
    private ImageView forward, reverse;
    private ImageButton ibHome, ibOrderTime, ib_cal;
    private int currentposition;
    private TextView tvTotalCnt;
    private TextView tvOrderTime;
    private TextView tv_order_title;

    private String result_txt;
    private String uid, udt;
    private String saveItemcd, saveQty;
    private String remainTime, enableTime;

    private MangoPreferences pref = new MangoPreferences(this);
    ArrayList<OrderItem> mItems = new ArrayList<>();
    ArrayList<OrderItem> mItemsForList = new ArrayList<>();

    ImageButton btnCalendarLeft;
    ImageButton btnCalendarRight;

    TextView tvOrderDate, tv_order_total_amount;

    Integer month, year, day, days, hour;
    String strMonth, strDay;
    String strToday, strFirstDay;

    Calendar calendar;

    Button btnAddItem;
    Button btnOrderLast;
    private Button btn_order_favorites, btn_order_cancel;

    OrderTask orderTask;
    OrderCancelTask orderCancelTask;
    OrderListTask orderListTask;
    OrderSaveTask orderSaveTask;

    MakeOrderFragment makeOrderFragment;
    ListOrderFragment listOrderFragment;
    OrderAdapter orderAdapter;

    RecyclerView target;

    TextView tvOrderSend;
    LinearLayout lyOrder, lyOrderTotal, ly_calendar;

    private ArrayList<OrderItem> mOrderItemSelected = new ArrayList<OrderItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        days = calendar.getActualMaximum(Calendar.DATE);
        hour = calendar.get(Calendar.HOUR_OF_DAY);

        strMonth = String.format("%02d", month);
        strDay = String.format("%02d", day);
        strToday = year.toString() + strMonth + strDay;
        strFirstDay = year.toString() + strMonth + "01";

        uid = pref.getString("uid", "");
        udt = pref.getString("udt", strToday);

//        orderTask = new OrderTask();
//        orderTask.execute(API_ACTION, GBN_ORDER, uid, udt);

        btnAddItem = findViewById(R.id.btn_add_item);
        btnOrderLast = findViewById(R.id.btn_order_last);

        btnCalendarLeft = findViewById(R.id.btn_calendar_left);
        btnCalendarRight = findViewById(R.id.btn_calendar_right);

        ibHome = findViewById(R.id.ib_home);
        ibOrderTime = findViewById(R.id.ib_order_time);
        ib_cal = findViewById(R.id.ib_cal);

        tvOrderDate = findViewById(R.id.tv_order_date);
        tvTotalCnt = findViewById(R.id.tv_total_cnt);
        tvOrderTime = findViewById(R.id.tv_order_time);

        btn_order_cancel = findViewById(R.id.btn_order_cancel);

        btnAddItem.setOnClickListener(this);
        btnOrderLast.setOnClickListener(this);
        btn_order_cancel.setOnClickListener(this);

        btnCalendarLeft.setOnClickListener(this);
        btnCalendarRight.setOnClickListener(this);
        ibHome.setOnClickListener(this);
        ibOrderTime.setOnClickListener(this);
        ib_cal.setOnClickListener(this);

        tvOrderDate.setText(year.toString()+"."+month.toString());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        forward = findViewById(R.id.forward);
        reverse = findViewById(R.id.reverse);
        mAdapter = new CalendarAdapter(calendarList);
        recyclerView.setHasFixedSize(true);

        tvOrderSend = findViewById(R.id.tv_order_send);
        tvOrderSend.setOnClickListener(this);

        lyOrder = findViewById(R.id.ly_order);
        lyOrderTotal = findViewById(R.id.ly_order_total);
        ly_calendar = findViewById(R.id.ly_calendar);

        tv_order_title = (TextView) findViewById(R.id.tv_order_title);
        tv_order_title.setText(pref.getString("ownerNm", "") + " 주문등록");


        tv_order_title.setPaintFlags(tv_order_title.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);
        tvOrderDate.setPaintFlags(tvOrderDate.getPaintFlags()| Paint.FAKE_BOLD_TEXT_FLAG);

        btn_order_favorites = (Button) findViewById(R.id.btn_order_favorites);
        btn_order_favorites.setOnClickListener(this);

        orderAdapter = new OrderAdapter(OrderActivity.this, mItems, OrderActivity.this);

        tv_order_total_amount = findViewById(R.id.tv_order_total_amount);


        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentposition = getCurrentItem();
                int bottom = recyclerView.getAdapter().getItemCount()-1;
                if (bottom-currentposition <4)
                    currentposition=bottom-1;
                else
                    currentposition+=4;
                setCurrentItem(currentposition, 1);
            }
        });
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentposition = getCurrentItem();
                setCurrentItem(currentposition-5, 0);
            }
        });

        // horizontal RecyclerView
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView,
                                           int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        int totalItemCount = mLayoutManager.getChildCount();
                        for (int i = 0; i < totalItemCount; i++){
                            View childView = recyclerView.getChildAt(i);
                            TextView childTextView = (TextView) (childView.findViewById(R.id.day_1));
                            String childTextViewText = (String) (childTextView.getText());

//                            if (childTextViewText.equals("일"))
//                                childTextView.setTextColor(Color.RED);
//                            else
//                                childTextView.setTextColor(Color.WHITE);

                        }
                    }
                });

        mAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(mAdapter);

        setCurrentItem(day, 3);

        mItems.clear();
        orderTask = new OrderTask();
        orderTask.execute(API_ACTION, GBN_ORDER, uid, udt);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        makeOrderFragment = new MakeOrderFragment();
        transaction.replace(R.id.frame_order, makeOrderFragment);
        transaction.commit();

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                mAdapter.clearSelectedItem();

                MyCalendar calendar = calendarList.get(position);
                TextView childTextView = (TextView) (view.findViewById(R.id.day_1));
                LinearLayout llCalendar = (LinearLayout) (view.findViewById(R.id.ly_calendar));
                //llCalendar.setBackgroundResource(R.drawable.shape_rect_calendar);

                //Animation startRotateAnimation = AnimationUtils.makeInChildBottomAnimation(getApplicationContext());
                //childTextView.startAnimation(startRotateAnimation);
                //childTextView.setTextColor(Color.CYAN);

                String strYMD = calendar.getYear()+String.format("%02d", Long.parseLong(calendar.getMonth()))+String.format("%02d", Long.parseLong(calendar.getDate()));

                //Toast.makeText(getApplicationContext(), strYMD + " is selected!", Toast.LENGTH_SHORT).show();

                uid = pref.getString("uid", "");
                udt = pref.getString("udt", strYMD);

                Log.e("today ::: ", strToday);
                Log.e("udt ::: ", udt);

                // 오늘
                if(strToday.equals(udt)) {
                    btn_order_cancel.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    params.weight = 170.0f;
                    lyOrder.setLayoutParams(params);
                    lyOrderTotal.setVisibility(View.VISIBLE);

                    mItems.clear();
                    orderTask = new OrderTask();
                    orderTask.execute(API_ACTION, GBN_ORDER, uid, udt);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    makeOrderFragment = new MakeOrderFragment();
                    transaction.replace(R.id.frame_order, makeOrderFragment);
                    transaction.commit();

                } else {
                    btn_order_cancel.setVisibility(View.GONE);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    params.weight = 170.0f;
                    lyOrder.setLayoutParams(params);
                    lyOrderTotal.setVisibility(View.GONE);

                    mItemsForList.clear();
                    orderListTask = new OrderListTask();
                    orderListTask.execute(API_ACTION, GBN_ORDER_LIST, uid, udt);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    listOrderFragment = new ListOrderFragment();
                    transaction.replace(R.id.frame_order, listOrderFragment);
                    transaction.commit();
                }
            }

            @Override
            public void onLongClick(View view, int position) {
//                MyCalendar calendar = calendarList.get(position);
//
//                TextView childTextView = (TextView) (view.findViewById(R.id.day_1));
//                childTextView.setTextColor(Color.GREEN);
//
//                Toast.makeText(getApplicationContext(), calendar.getDate()+"/" + calendar.getDay()+"/" +calendar.getMonth()+"   selected!", Toast.LENGTH_SHORT).show();
            }
        }));

        prepareCalendarData(strToday, days, day-1);

    }

    private int getCurrentItem(){
        return ((LinearLayoutManager)recyclerView.getLayoutManager())
                .findLastCompletelyVisibleItemPosition();
    }

    private void setCurrentItem(int position, int incr){
        position=position+incr;
        if (position <0)
            position=0;
        recyclerView.smoothScrollToPosition(position);
    }

    /**
     * Prepares sample data to provide data set to adapter
     */
    private void prepareCalendarData(String startDate, int days, int prevDays) {

        calendarList.clear();

        // run a for loop for all the next 30 days of the current month starting today
        // initialize mycalendarData and get Instance
        // getnext to get next set of date etc.. which can be used to populate MyCalendarList in for loop
        myCalendarData m_calendar = new myCalendarData(-prevDays, startDate);
        for ( int i=0; i <days; i++) {
            MyCalendar calendar = new MyCalendar(m_calendar.getWeekDay(), String.valueOf(m_calendar.getDay()), String.valueOf(m_calendar.getMonth()), String.valueOf(m_calendar.getYear()),i);
            m_calendar.getNextWeekDay(1);
            calendarList.add(calendar);
        }
        // notify adapter about data set changes
        // so that it will render the list with new data
        mAdapter.setYm(startDate);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        switch(view.getId()) {
            case R.id.btn_order_cancel:
                AlertDialog.Builder adOrderCancel = new AlertDialog.Builder(OrderActivity.this);
                adOrderCancel.setTitle("주문취소");
                adOrderCancel.setMessage("주문을 취소 하시겠습니까?");
                adOrderCancel.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        orderCancelTask = new OrderCancelTask();
                        orderCancelTask.execute(API_ACTION, GBN_ORDER_CANCEL, uid, udt);
                    }
                });

                adOrderCancel.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                adOrderCancel.show();

                break;
            case R.id.ib_cal:
                if(ly_calendar.getVisibility() == View.VISIBLE) {
                    ly_calendar.setVisibility(View.GONE);
                    lyOrder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,340f));
                } else {
                    ly_calendar.setVisibility(View.VISIBLE);
                    lyOrder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,200f));
                }
                break;
            case R.id.btn_order_favorites:
                intent = new Intent(OrderActivity.this, FavoritesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, ACTIVITY_LAST);
                break;
            case R.id.ib_order_time:
                intent = new Intent(OrderActivity.this, OrderTimeActivity.class);

                intent.putExtra("EXTRA_ORDER_TIME",  "금일 " + pref.getString("stTm", "") + "시 ~ 익일 " + pref.getString("edTm", "") + "시");

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, ACTIVITY_LAST);
                break;
            case R.id.tv_order_send:

                int stH = Integer.parseInt(pref.getString("stTm", ""));
                int edH = Integer.parseInt(pref.getString("edTm", ""));

                if (hour < stH && hour > edH) {
                    new android.app.AlertDialog.Builder(OrderActivity.this)
                        .setMessage("주문 가능시간이 아닙니다.\n주문가능시간 : 금일 " + pref.getString("stTm", "") + "시 ~ 익일 " + pref.getString("edTm", "") + "시")
                        .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    break;
                }

                boolean possibleOrder = true;

                if (pref.getString("loanCkYn", "").equals("1")) {
                    int loanAmt = Integer.parseInt(pref.getString("loanAmt", "0"));
                    int amt = Integer.parseInt(pref.getString("amt", "0"));
                    int orderAmt = Integer.parseInt(tv_order_total_amount.getText().toString().replace(",", ""));
                    if ((loanAmt - (orderAmt + amt)) < 0) {
                        possibleOrder = false;
                    }
                }

                if (!possibleOrder) {
                    new android.app.AlertDialog.Builder(OrderActivity.this)

                            .setMessage("주문가능금액이 초과 되었습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    break;
                }

                orderSaveTask = new OrderSaveTask();
                String strItemcd;
                String strQty;
                StringBuilder sb = new StringBuilder();
                StringBuilder sbCd = new StringBuilder();
                for(int i=0; i<mItems.size(); i++) {
                    OrderItem oi = mItems.get(i);

                    if(!oi.getQty().matches(" *")) {
                        sb.append(oi.getQty());
                        sbCd.append(oi.getCd());

                        if(i != (mItems.size()-1)) {
                            sb.append(",");
                            sbCd.append(",");
                        }
                    }

                    Log.e("sb ::: ", sb.toString());
                    Log.e("sbCd ::: ", sbCd.toString());
                }

                orderSaveTask.execute(API_ACTION_MOBILE, GBN_ORDER_SEND, uid, udt, sbCd.toString(), sb.toString());
                break;
            case R.id.btn_order_last:
                intent = new Intent(OrderActivity.this, OrderLastActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, ACTIVITY_LAST);
                break;
            case R.id.ib_home:
                finish();
                break;
            case R.id.btn_calendar_left:

                if (month <= 1) {//checking the month
                    month = 12;
                    year--;
                } else {
                    month--;
                }

                //update current time
                calendar.set(Calendar.MONTH,month-1);
                calendar.set(Calendar.YEAR,year);

                tvOrderDate.setText(year.toString() + "." + month.toString());

                days = calendar.getActualMaximum(Calendar.DATE);

                mAdapter.clearSelectedItem();
                mAdapter.notifyDataSetChanged();
                prepareCalendarData(year.toString()+String.format("%02d", month)+"01", days, 0);

                orderAdapter.clearData();
                orderAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_calendar_right:
                if (month > 11) {//checking the month
                    month = 1;
                    year++;
                } else {
                    month++;
                }

                //update current time
                calendar.set(Calendar.MONTH,month-1);
                calendar.set(Calendar.YEAR,year);
                tvOrderDate.setText(year.toString() + "." + month.toString());
                days = calendar.getActualMaximum(Calendar.DATE);

                mAdapter.clearSelectedItem();
                mAdapter.notifyDataSetChanged();
                prepareCalendarData(year.toString()+String.format("%02d", month)+"01", days, 0);

                orderAdapter.clearData();
                orderAdapter.notifyDataSetChanged();

                break;

            case R.id.btn_add_item:
                intent = new Intent(OrderActivity.this, AddItemActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, ACTIVITY_CONSTANT);
                break;
        }
    }

    private class OrderCancelTask extends AsyncTask<String, Void, Boolean> {

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

                    for(int i = 0; i < array.length(); i++) {
                        JSONObject listObj = array.getJSONObject(i);

                        String msg = listObj.get("msg").toString();
                        String code = listObj.get("code").toString();

                        switch (code) {
                            case "200":
                                msg = "주문이 취소 되었습니다.";
                                orderAdapter.clearInputs();
                                break;
                            case "400":
                                msg = "주문 취소에 실패했습니다.";
                                break;
                        }

                        show(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if ((OrderActivity.this != null) && (!OrderActivity.this.isFinishing())) {
                    new android.app.AlertDialog.Builder(OrderActivity.this)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
        }
    }


    private class OrderSendTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String gbn = params[1];
            String uid = params[2];
            String udt = params[3];
            String itemcd = params[4];
            String qty = params[5];
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));
                pairs.add(new BasicNameValuePair("udt", udt));
                pairs.add(new BasicNameValuePair("itemcd", itemcd));
                pairs.add(new BasicNameValuePair("qty", qty));

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
                        OrderItem orderItem = new OrderItem();
                        orderItem.setName(listObj.has("item") ? listObj.get("item").toString() : "");
                        orderItem.setQty(listObj.has("qty") ? listObj.get("qty").toString() : "");
                        orderItem.setUnit(listObj.has("unit") ? listObj.get("unit").toString() : "");
                        orderItem.setPrice(listObj.has("price") ? listObj.get("price").toString() : "");

                        mItems.add(orderItem);
                    }

                    tvTotalCnt.setText("주문목록 (총 품목수 : " + mItems.size() + "개)");

                    orderAdapter = new OrderAdapter(OrderActivity.this, mItems, OrderActivity.this);
                    target = findViewById(R.id.rv_make_order);
                    target.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
                    target.setAdapter(orderAdapter);

                    target.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), target, new RecyclerTouchListener.ClickListener() {

                        @Override
                        public void onClick(View view, int position) {
                            OrderItem orderItem = mItems.get(position);
                            ImageButton ibOrderDeleteItem = (ImageButton) (view.findViewById(R.id.ib_order_delete_item));
                            ibOrderDeleteItem.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLongClick(View view, int position) {
                            OrderItem orderItem = mItems.get(position);
                            ImageButton ibOrderDeleteItem = (ImageButton) (view.findViewById(R.id.ib_order_delete_item));
                            ibOrderDeleteItem.setVisibility(View.VISIBLE);
                        }
                    }));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if ((OrderActivity.this != null) && (!OrderActivity.this.isFinishing())) {
                    new android.app.AlertDialog.Builder(OrderActivity.this)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
        }
    }

    private class OrderTask extends AsyncTask<String, Void, Boolean> {

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

                    for(int i = 0; i < array.length(); i++) {
                        JSONObject listObj = array.getJSONObject(i);
                        OrderItem orderItem = new OrderItem();
                        orderItem.setName(listObj.has("item") ? listObj.get("item").toString() : "");
                        orderItem.setQty(listObj.has("qty") ? listObj.get("qty").toString() : "");
                        orderItem.setUnit(listObj.has("unit") ? listObj.get("unit").toString() : "");
                        orderItem.setPrice(listObj.has("price") ? listObj.get("price").toString() : "");
                        orderItem.setCd(listObj.has("itemcd") ? listObj.get("itemcd").toString() : "");
                        orderItem.setUrl(listObj.has("url") ? listObj.get("url").toString() : "");

                        if(i==0) {
                            pref.putString("stTm", listObj.has("stTm") ? listObj.get("stTm").toString() : "");
                            pref.putString("edTm", listObj.has("edTm") ? listObj.get("edTm").toString() : "");
//                            pref.putString("loanAmt", listObj.has("loanAmt") ? listObj.get("loanAmt").toString() : "");
//                            pref.putString("amt", listObj.has("amt") ? listObj.get("amt").toString() : "");
//                            pref.putString("loanCkYn", listObj.has("loanCkYn") ? listObj.get("loanCkYn").toString() : "");
                        }
                        mItems.add(orderItem);
                    }

                    tvTotalCnt.setText("주문목록 (총 품목수 : " + mItems.size() + "개)");

                    tvOrderTime.setText(" 주문가능시간 : " + pref.getString("stTm", "") + " ~ " + pref.getString("edTm", ""));

                    orderAdapter = new OrderAdapter(OrderActivity.this, mItems, OrderActivity.this);
                    target = findViewById(R.id.rv_make_order);
                    target.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
                    target.setAdapter(orderAdapter);

                    target.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), target, new RecyclerTouchListener.ClickListener() {

                        @Override
                        public void onClick(View view, int position) {
                            OrderItem orderItem = mItems.get(position);
                            TextView ibOrderDeleteItem = (TextView) (view.findViewById(R.id.ib_order_delete_item));
                            ibOrderDeleteItem.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLongClick(View view, int position) {
                            OrderItem orderItem = mItems.get(position);
                            TextView ibOrderDeleteItem = (TextView) (view.findViewById(R.id.ib_order_delete_item));
                            ibOrderDeleteItem.setVisibility(View.VISIBLE);
                        }
                    }));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if ((OrderActivity.this != null) && (!OrderActivity.this.isFinishing())) {
                    new android.app.AlertDialog.Builder(OrderActivity.this)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
        }
    }

    private class OrderListTask extends AsyncTask<String, Void, Boolean> {

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

                    for(int i = 0; i < array.length(); i++) {
                        JSONObject listObj = array.getJSONObject(i);
                        OrderItem item = new OrderItem();
                        item.setName(listObj.has("item") ? listObj.get("item").toString() : "");
                        item.setQty(listObj.has("cnt") ? listObj.get("cnt").toString() : "");
                        item.setUnit(listObj.has("unit") ? listObj.get("unit").toString() : "");
                        item.setPrice(listObj.has("price") ? listObj.get("price").toString() : "");
                        item.setCd(listObj.has("itemcd") ? listObj.get("itemcd").toString() : "");

                        mItemsForList.add(item);
                    }

                    tvTotalCnt.setText("주문목록 (총 품목수 : " + mItemsForList.size() + "개)");

                    ItemAdapter itemAdapter = new ItemAdapter(OrderActivity.this, mItemsForList);
                    RecyclerView target = findViewById(R.id.rv_list_order);
                    target.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
                    target.setAdapter(itemAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if ((OrderActivity.this != null) && (!OrderActivity.this.isFinishing())) {
                    new android.app.AlertDialog.Builder(OrderActivity.this)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
        }
    }

    private class OrderSaveTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String uri = params[0];
            String gbn = params[1];
            String uid = params[2];
            String udt = params[3];
            String itemcd = params[4];
            String qty = params[5];

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(uri);
                ArrayList<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("gbn", gbn));
                pairs.add(new BasicNameValuePair("uid", uid));
                pairs.add(new BasicNameValuePair("udt", udt));
                pairs.add(new BasicNameValuePair("itemcd", itemcd));
                pairs.add(new BasicNameValuePair("qty", qty));

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

                        String msg = listObj.get("msg").toString();
                        String code = listObj.get("code").toString();

                        switch (code) {
                            case "200":
                                msg = "주문 등록 되었습니다.";
                                break;
                            case "400":
                                msg = "주문 등록에 실패했습니다.";
                                break;
                        }

                        show(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if ((OrderActivity.this != null) && (!OrderActivity.this.isFinishing())) {
                    new android.app.AlertDialog.Builder(OrderActivity.this)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
        }
    }

    public void show(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("주문등록");
        builder.setMessage(message);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ACTIVITY_CONSTANT:
                if(resultCode == RESULT_OK) {
                    mOrderItemSelected = (ArrayList<OrderItem>) data.getSerializableExtra("EXTRA_SELECTED_ITEM");
                    orderAdapter.appendData(mOrderItemSelected);

                    tvTotalCnt.setText("주문목록 (총 품목수 : " + orderAdapter.getItemCount() + "개)");
                }
                break;
            case ACTIVITY_LAST:
                if(resultCode == RESULT_OK) {
                    mOrderItemSelected = (ArrayList<OrderItem>) data.getSerializableExtra("EXTRA_SELECTED_ITEM");

                    orderAdapter.clearData();
                    orderAdapter.appendData(mOrderItemSelected);

                    tvTotalCnt.setText("주문목록 (총 품목수 : " + orderAdapter.getItemCount() + "개)");
                }
                break;
        }
    }
}