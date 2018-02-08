package com.example.arvindkalyankumar.ase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.pixplicity.sharp.Sharp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DashboardActivity extends AppCompatActivity {

    private Button qrcode,bonus,logout;
    private ImageView qrImage;
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Intent intent = this.getIntent();
        final String message = intent.getStringExtra("session");
        final String user = intent.getStringExtra("userid");

        qrImage = (ImageView)findViewById(R.id.qrView);
        qrcode = (Button)findViewById(R.id.qrbtn);
        qrcode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestQR(message);
                 }
        });

        bonus = (Button)findViewById(R.id.bonusBtn);
        bonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBonus(user);
            }
        });

        logout = (Button)findViewById(R.id.logoutBtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

    }

    public void requestQR(String msg) {
        try {
            String url = "http://aatserver.appspot.com/qr_code/"+msg;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            Log.d("Bonussss",response.toString());

            Bitmap bitmap = generateQRCode(response.toString());
            qrImage.setImageBitmap(bitmap);
        }
        catch(IOException e){
        }
        catch (WriterException e){
            e.printStackTrace();
        }
    }

    protected Bitmap generateQRCode(String str) throws WriterException{

        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE,WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
  }

    public void requestBonus(final String uname){
        try {
                    String url = "http://aatserver.appspot.com/bonus/"+uname;

                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");


                    System.out.println("\nSending 'GET' request to URL : " + url);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));

                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.d("Bonussss",response.toString());
                    Toast.makeText(this,response.toString(),Toast.LENGTH_LONG).show();
                }
                catch(IOException e){
                }
            }

        public void logoutUser(){
            Context context=getApplicationContext();
            Intent intnt = new Intent(context, MainActivity.class);
            intnt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intnt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startActivity(intnt);
    }
}
