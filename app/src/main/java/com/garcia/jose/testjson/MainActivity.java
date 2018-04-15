package com.garcia.jose.testjson;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.os.AsyncTask;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    Integer counter = 1;
    String myUrl = null;
    String urlValidText = "instagram";
    MyAsyncTask myTask = null;
    Bitmap myBitmap;
    MyClass myClass;
    FloatingActionButton fab;
    String myImage;
    EditText texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ActivityCompat.requestPermissions(this,
                new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE },0);

        fab = findViewById(R.id.fab);
        progressBar = findViewById(R.id.MyProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        texto = (EditText) findViewById(R.id.myURL);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fab.setVisibility(View.GONE);

                myUrl = texto.getText().toString();

                if(!myUrl.contains(urlValidText))
                {
                    Snackbar.make(findViewById(android.R.id.content), "URL inválida ...", Snackbar.LENGTH_LONG).show();
                    fab.setVisibility(View.VISIBLE);
                    return;
                }

                myTask = new MyAsyncTask();
                myClass = new MyClass(getApplicationContext(), myTask);

                counter = 1;
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
                myTask.execute(myUrl);
                fab.setEnabled(false);

                Log.d("myTag", "testing ...");
            }
        });
    }

    //AsyncTask<Params, Progress, Result>
    class MyAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... url) {

            if (url != null)
            {
                try {
                    Thread.sleep(1000);
                    publishProgress(counter);
                    Log.d("test", counter.toString());

                    myImage = myClass.GetImage(url[0]);

                    if(myImage == null){
                        myTask.cancel(true);
                    }
                    else {

                        myBitmap = myClass.getBitmapFromURL(myImage);
                        myClass.saveImageToExternalStorage(myBitmap);
                    }

                    if (isCancelled()) {
                        return null;
                    }

                } catch (InterruptedException e) {
                    Snackbar.make(findViewById(android.R.id.content), "Ups! Algo ha salido mal...", Snackbar.LENGTH_LONG).show();
                    fab.setEnabled(true);
                    fab.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            fab.setEnabled(true);
            fab.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            EditText texto = (EditText) findViewById(R.id.myURL);
            texto.setText("");
            Log.d("test", "Error");
            Snackbar.make(findViewById(android.R.id.content), "Ups! Algo ha salido mal...", Snackbar.LENGTH_LONG).show();
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            Log.d("test", "Reinicia");
            EditText texto = (EditText) findViewById(R.id.myURL);
            Snackbar.make(findViewById(android.R.id.content), "Guardado correctamente!!", Snackbar.LENGTH_LONG).show();
            texto.setText("");
            fab.setEnabled(true);
            fab.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            Log.d("test", "Tarea ejecutandose...");
            Toast.makeText(getApplicationContext(), "Espera ...", Toast.LENGTH_SHORT).show();

        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("test", "Ejecutandose..." + values[0]);
            progressBar.setProgress(values[0]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
