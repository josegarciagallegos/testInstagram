package com.garcia.jose.testjson;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class MyClass {

    Context myContext;
    MainActivity.MyAsyncTask myTask;

    public MyClass(Context myContext, final MainActivity.MyAsyncTask myTask) {
        this.myContext = myContext;
        this.myTask = myTask;
    }

    public void saveImageToExternalStorage(Bitmap finalBitmap){

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File( root + "/InstaImages");
        myDir.mkdirs();

        Random generator = new Random();
        int n = 10000;
        int x = generator.nextInt(n);
        String fname = "Image-" + x + ".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) { file.delete(); }

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(myContext, "Ups! Algo ha salido mal...", Toast.LENGTH_SHORT).show();
            myTask.cancel(true);
        }

        MediaScannerConnection.scanFile(myContext, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            if(myBitmap == null)
            {
                Toast.makeText(myContext, "URL inválida ...", Toast.LENGTH_LONG).show();
                myTask.cancel(true);
            }

            return myBitmap;
        } catch (IOException e) {
            Toast.makeText(myContext, "Ups! Algo ha salido mal...", Toast.LENGTH_SHORT).show();
            myTask.cancel(true);
            return null;
        }
    }

    public String GetImage(String uri) {
        try {
            URL url = new URL(uri);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.connect();

            StringBuilder response = new StringBuilder();
            String inputLine;
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();

            CharSequence cs1 = "property=\"og:image\" content=\"";
            int a = response.indexOf(cs1.toString());
            String cadena = response.substring(a + 29, response.length()).toString();
            int b = cadena.indexOf(">");
            String link = cadena.substring(0, b - 3).toString();
            return link;

        } catch (IOException e) {
            Toast.makeText(myContext, "Ups! Algo ha salido mal...", Toast.LENGTH_SHORT).show();
            myTask.cancel(true);
            return null;
        }
    }

}
