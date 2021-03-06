package com.todo.order.view.common;

import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.todo.order.R;
import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageActivity extends BasicActivity {

    LinearLayout news_back,download;
    ImageView iv;

    private File outputFile;
    private String folderName = "church";
    String path = "";
    String downPath = "";
    String fileName2 = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        news_back = findViewById(R.id.news_back);
        download = findViewById(R.id.download);
        iv = findViewById(R.id.main_image);


        try {
            path = getIntent().getStringExtra("path");
            downPath = getIntent().getStringExtra("download");
            fileName2 = getIntent().getStringExtra("fileName");
            Log.e("test","path = "+path);
            if(path.equals("q_test")) {
                Glide.with(ImageActivity.this).load(R.drawable.q_test).into(iv);
            } else {
                Glide.with(ImageActivity.this).load(path).into(iv);
            }

        } catch (Exception e) {

        }
        makeFolder();

        news_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("test","????????????");
                if(path.equals("q_test")) {
                    Toast.makeText(ImageActivity.this,"???????????? ??????????????????.",Toast.LENGTH_SHORT).show();
                } else {
                    makeFolder();
                    DownloadFilesTask g = new DownloadFilesTask();
                    g.execute(new String[]{downPath});
                }

            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
    }


    private class DownloadFilesTask extends AsyncTask<String, String, Long> {

        //?????? ??????????????? ???????????????.
        @Override
        protected Long doInBackground(String... string_url) { //3
            int count;
            long FileSize = -1;
            InputStream input = null;
            OutputStream output = null;
            URLConnection connection = null;
            String filePath = string_url[0];
            try {
                URL url = new URL(string_url[0]);
                connection = url.openConnection();
                connection.connect();


                //?????? ????????? ?????????
                FileSize = connection.getContentLength();

                //URL ??????????????? ???????????????????????? ?????? input stream
                input = new BufferedInputStream(url.openStream(), 8192);

                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = "church_" +System.currentTimeMillis();
                String ext  ="jpg";
                try {
                    ext = fileName2.substring(fileName2.lastIndexOf(".")+1);
                }
                catch (Exception e) {
                }
                outputFile= new File(path+"/"+folderName+"/"+fileName+"."+ext); //??????????????? ????????? ????????? File ?????? ??????

                // SD????????? ???????????? ?????? Output stream
                output = new FileOutputStream(outputFile);


                byte data[] = new byte[1024];
                long downloadedSize = 0;
                while ((count = input.read(data)) != -1) {
                    //???????????? BACK ?????? ????????? ????????????
                    if (isCancelled()) {
                        input.close();
                        return Long.valueOf(-1);
                    }

                    downloadedSize += count;

                    if (FileSize > 0) {
                        float per = ((float)downloadedSize/FileSize) * 100;
                        String str = "Downloaded " + downloadedSize + "KB / " + FileSize + "KB (" + (int)per + "%)";
                        publishProgress("" + (int) ((downloadedSize * 100) / FileSize), str);

                    }

                    //????????? ???????????? ???????????????.
                    output.write(data, 0, count);
                }
                // Flush output
                output.flush();

                // Close streams
                output.close();
                input.close();


            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }


            }
            return FileSize;
        }

        //?????? ???????????? ?????? ???
        @Override
        protected void onPostExecute(Long size) { //5
            super.onPostExecute(size);
            if ( size > 0) {
                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(FileProvider.getUriForFile(ImageActivity.this, "com.brighten.church.provider", outputFile));
                sendBroadcast(mediaScanIntent);
                Toast.makeText(getApplicationContext(), "?????????????????????.", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void makeFolder() {
        // ??????????????? ??????
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.e("test","path = "+ path);
        // ?????? ?????? ?????? ??? ?????? ??????
        File fileFolderPath = new File(path + "/" + folderName);
        fileFolderPath.mkdirs();
        int version = android.os.Build.VERSION.SDK_INT;

        if (version > 17) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }

    }
}
