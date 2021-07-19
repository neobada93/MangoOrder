package com.todo.order.view.talk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.todo.order.MainActivity;
import com.todo.order.R;
import com.todo.order.config.MangoPreferences;
import com.todo.order.util.StringUtil;
import com.todo.order.view.common.BasicActivity;
import com.todo.order.view.common.SoftKeyboardDectectorView;

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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.todo.order.config.MangoPreferences.GBN_TALK_SEND;
import static com.todo.order.config.ServerAddress.API_QUESTION_INSERT;
import static com.todo.order.config.ServerAddress.API_QUESTION_LIST;

public class TalkActivity extends BasicActivity implements View.OnClickListener {

    LinearLayout top,clickFile,sendBtn,header;
    private MangoPreferences pref;
    String result_txt;
    ArrayList<TalkItem> mItem = new ArrayList<>();
    RecyclerView rvTalk;
    TalkAdapter qAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    Uri fileUri;
    String uploadFilePath;
    String uploadFilePath2;
    int serverResponseCode = 0;
    EditText sendMsg;
    ImageView clickFile2;
    ImageButton ibSend;
    RelativeLayout body;
    private InputMethodManager imm;
    private boolean flag = false;
    private ImageButton ibHome;
    private Boolean isEmptyContent;
    private Boolean isEmptyFile;

    private String uid;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        //prev = findViewById(R.id.news_back);
        //setting = findViewById(R.id.news_setting);
        ibHome = findViewById(R.id.ib_home_activity_talk);
        pref = new MangoPreferences(this);
        rvTalk = findViewById(R.id.rv_talk);
        top = findViewById(R.id.top_talk);
        clickFile = findViewById(R.id.click_file);
        sendBtn = findViewById(R.id.send_btn);
        sendMsg = findViewById(R.id.send_msg);
        ibSend = findViewById(R.id.ib_send);
        //header = findViewById(R.id.header);
        body = findViewById(R.id.body_talk);
        clickFile2 = findViewById(R.id.click_file2);
        mLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        rvTalk.setLayoutManager(mLayoutManager);
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(80);
        rvTalk.addItemDecoration(spaceDecoration);

        ibHome.setOnClickListener(this);

        isEmptyContent = false;
        isEmptyFile = false;

        uid = pref.getString("uid", "");

        /*rvTalk.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                overallXScroll = overallXScroll + dy;
                if(overallXScroll > 0) {
                    top.setBackgroundColor(Color.rgb(255,255,255));
                } else {
                    top.setBackgroundColor(Color.rgb(238,229,224));

                }

            }
        });*/

        //prev.setOnClickListener(this);
        //setting.setOnClickListener(this);
        clickFile.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        ibSend.setOnClickListener(this);

        sendMsg.setOnFocusChangeListener((view, b) -> {
            if(b) {
                //sendBtn.setBackgroundResource(R.drawable.select_question_box5);
                //sendTv.setTextColor(Color.rgb(255,255,255));
            } else {
                //sendBtn.setBackgroundResource(R.drawable.select_question_box2);
                //sendTv.setTextColor(Color.argb(40,255,0,0));
            }
        });

        itemInit();
        QuestionList q = new QuestionList();
        q.execute(API_QUESTION_LIST, GBN_TALK_SEND, uid);


        final SoftKeyboardDectectorView softKeyboardDecector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDecector, new FrameLayout.LayoutParams(-1, -1));

        softKeyboardDecector.setOnShownKeyboard(() -> {
            //키보드 등장할 때
//            header.setVisibility(View.GONE);
//
//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) body.getLayoutParams();	// 레이아웃의 weight 값 변경가능
//            params.weight = 100;
//            body.setLayoutParams(params);

            Log.e("Keyboard", "show");
        });

        softKeyboardDecector.setOnHiddenKeyboard(() -> {
            // 키보드 사라질 때
//            header.setVisibility(View.VISIBLE);
//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) body.getLayoutParams();	// 레이아웃의 weight 값 변경가능
//            params.weight = 92;
//            body.setLayoutParams(params);
            Log.e("Keyboard", "hide");
        });
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);


    }


    private void itemInit() {
        TalkItem item = new TalkItem();
        item.setFile("q_test");
        item.setPosition(2);
        item.setContent("안녕하세요. 망고입니다.\n무엇을 도와드릴까요?");
        mItem.add(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TalkActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.ib_home_activity_talk:
                finish();
                break;
            case R.id.news_back :
                onBackPressed();
                break;
            case R.id.click_file :
                SelectImage();
//                CropImage.activity(null)
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(this);
                break;
            case R.id.ib_send :
                String content = sendMsg.getText().toString();

                isEmptyContent = content.trim().isEmpty();
                isEmptyFile = StringUtil.nullValue(uploadFilePath2).isEmpty();

                if (isEmptyContent && isEmptyFile) {
                    new android.app.AlertDialog.Builder(TalkActivity.this)
                            .setMessage("이미지를 선택하거나 메세지를 입력해 주세요.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    break;
                }

                if(flag) {
                    break;
                }
                flag = true;

                Insert i = new Insert();
                i.execute(API_QUESTION_INSERT, GBN_TALK_SEND, uid, content);
                break;
        }
    }

    private void SelectImage() {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(TalkActivity.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[i].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    //startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);
                    startActivityForResult(intent, SELECT_FILE);
                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private class Insert extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            isEmptyContent = params[1].trim().isEmpty();
            isEmptyFile = StringUtil.nullValue(uploadFilePath2).isEmpty();

            // 파일이 없고 메세지만 있을때
            if (isEmptyFile && !isEmptyContent) {
                return backgroundByEmpty(params);
            } else {
                return backgroundByExist(params);
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (StringUtil.nullValue(uploadFilePath2).isEmpty() && !isEmptyContent) {
                executeByEmpty(result);
            } else {
                executeByExist(result);
            }
        }

        private Boolean backgroundByEmpty(String... params) {
            String url = params[0];
            String gbn = params[1];
            String uid = params[2];
            String content = params[3];
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 720;

            try{
                URL url1 = new URL(url);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url1.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"gbn\""+lineEnd+lineEnd+gbn);

                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uid\""+lineEnd+lineEnd+uid);

                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"message\""+lineEnd+lineEnd);

                Log.e("test","content = "+ content);

                dos.writeUTF(content);

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                //close the streams //
                dos.flush();
                dos.close();

                if(serverResponseMessage.equals("OK")){
                    Log.e("test","okokok");
                } else {
                    new android.app.AlertDialog.Builder(TalkActivity.this)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }

            } catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }

        private void executeByEmpty(Boolean result) {
            if(result) {
                mHandler.sendEmptyMessage(1);
                QuestionList q = new QuestionList();
                q.execute(API_QUESTION_LIST, GBN_TALK_SEND, uid);
            } else {
                flag = false;
                new android.app.AlertDialog.Builder(TalkActivity.this)
                        .setMessage("통신에 실패하였습니다.")
                        .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
            }
        }

        private Boolean backgroundByExist(String... params) {
            String url = params[0];
            String gbn = params[1];
            String uid = params[2];
            String content = params[3];
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 720;

            try{
                File f = new File(uploadFilePath2);
                FileInputStream fileInputStream = new FileInputStream(f);
                uploadFilePath = f.getPath();
                Log.e("test","u1 =" + uploadFilePath);
                uploadFilePath = uploadFilePath.substring(uploadFilePath.lastIndexOf("/"));
                uploadFilePath = URLEncoder.encode(uploadFilePath,"UTF-8");
                Log.e("test","u2 =" + uploadFilePath);

                URL url1 = new URL(url);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url1.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);


                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"gbn\""+lineEnd+lineEnd+gbn);

                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uid\""+lineEnd+lineEnd+uid);

                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"message\""+lineEnd+lineEnd);

                Log.e("test","content = "+ content);

                dos.writeUTF(content);

                String test = URLConnection.guessContentTypeFromName(f.getPath());

                Log.e("test","test = "+test);

                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"fileType\""+lineEnd+lineEnd+test);

                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"ext\""+lineEnd+lineEnd+uploadFilePath.substring(uploadFilePath.lastIndexOf(".")+1));

                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"img\";filename=\""+uploadFilePath+"\"");
                dos.writeBytes("Content-Type: "+test + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

                if(serverResponseMessage.equals("OK")){
                    Log.e("test","okokok");
                } else {
                    new android.app.AlertDialog.Builder(TalkActivity.this)
                            .setMessage("통신에 실패하였습니다.")
                            .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }

            } catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }

        private void executeByExist(Boolean result) {
            if(result) {
                Log.e("Image Upload excute", "Success");
                mHandler.sendEmptyMessage(1);
                QuestionList q = new QuestionList();
                q.execute(API_QUESTION_LIST, GBN_TALK_SEND, uid);
            } else {
                flag = false;
                Log.e("Image Upload excute", "Error");
            }
        }

    }


    private class QuestionList extends AsyncTask<String, Void, Boolean> {

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
        protected void onPostExecute(Boolean aBoolean) {

            if(aBoolean) {
                try {
                    JSONArray array = new JSONArray(result_txt);
                    for(int i=0; i<array.length(); i++) {
                        TalkItem item = new TalkItem();
                        JSONObject data = array.getJSONObject(i);
                        item.setPosition(data.getInt("talkgbn"));
                        item.setContent(data.getString("cont"));
                        item.setRegdt(data.getString("regdt"));
                        item.setUrl(data.getString("url"));
                        mItem.add(item);
                    }

                    qAdapter = new TalkAdapter(TalkActivity.this, mItem);
                    rvTalk.setAdapter(qAdapter);
                    mHandler.sendEmptyMessage(0);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                new android.app.AlertDialog.Builder(TalkActivity.this)
                        .setMessage("통신에 실패하였습니다.")
                        .setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss()).show();
            }
        }
    }


    public class RecyclerDecoration extends RecyclerView.ItemDecoration {

        private final int divHeight;


        public RecyclerDecoration(int divHeight) {
            this.divHeight = divHeight;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1)

                outRect.bottom = divHeight;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                Uri selectedImageUri = data.getData();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                Uri tempUri = getImageUri(getApplicationContext(), bmp);
                File finalFile = new File(getRealPathFromURI(tempUri));
                uploadFilePath2 = finalFile.getPath();
            } else if(requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                File finalFile = new File(getRealPathFromURI(selectedImageUri));
                uploadFilePath2 = finalFile.getPath();
            }
        }

//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                fileUri = result.getUri();
//                uploadFilePath2 = fileUri.getPath();
//                //clickFile2.setBackgroundResource(R.drawable.icon_photo_on);
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public void click() {
        mHandler.sendEmptyMessage(2);
    }

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0 :
                    rvTalk.scrollToPosition(qAdapter.getItemCount()-1);
                    break;
                case 1:
                    uploadFilePath2 = null;
                    uploadFilePath = null;
                    fileUri = null;
                    sendMsg.setText("");
                    //clickFile2.setBackgroundResource(R.drawable.icon_photo_off);
                    mItem.clear();
                    itemInit();
                    flag = false;
                    break;
                case 2:
                    imm.hideSoftInputFromWindow(sendMsg.getWindowToken(), 0);
                    break;

            }
        }
    };
}
