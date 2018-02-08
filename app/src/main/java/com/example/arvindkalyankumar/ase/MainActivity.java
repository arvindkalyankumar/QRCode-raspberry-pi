package com.example.arvindkalyankumar.ase;

import android.annotation.SuppressLint;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;

import android.app.Activity;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Button login;
    public EditText username, password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        login = (Button) findViewById(R.id.login);
        username = (AutoCompleteTextView) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

           String mUsername = username.getText().toString();
          String mPassword = password.getText().toString();
          tryLogin(mUsername, mPassword);
            }
        });
}

    @SuppressLint("ShowToast")
    protected void tryLogin(String mUsername, String mPassword)
    {
        if (!validate()){
            //loginFailed();
            return;
        }

        final String mUsername1 = mUsername;
        final String mPassword1=mPassword;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection;
                OutputStreamWriter request = null;

                URL url = null;
                String response = null;

                try
                {
                    url = new URL("http://aatserver.appspot.com/login");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestMethod("POST");

                    JSONObject cred = new JSONObject();
                    cred.put("user",mUsername1);
                    cred.put("pw",mPassword1);

                    request = new OutputStreamWriter(connection.getOutputStream());
                    request.write(cred.toString());
                    request.flush();
                    request.close();
                    String line = "";
                    InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }

                    response = sb.toString();

                    //Toast.makeText(this,"Message from Server: \n"+ response, Toast.LENGTH_SHORT).show();
                    Log.d("Null response xxxx ",response);
                    if(!response.trim().equals("0")){
                        Intent intent = new Intent(getApplicationContext(),DashboardActivity.class);
                        intent.putExtra("session",response);
                        intent.putExtra("userid",mUsername1);
                        startActivity(intent);
                    }
                    else {
                        //loginFailed();
                    }
                    isr.close();
                    reader.close();

                }
                catch(IOException e)
                {
                    // Error
                }
                catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

    //public  void loginFailed(){
      //  Toast.makeText(this,"Login Failed",Toast.LENGTH_SHORT).show();
    //}

    public boolean validate(){
        boolean valid =true;
        String uname = username.getText().toString();
        String pwd = password.getText().toString();
        if ((uname.equals("")) || (pwd.equals(""))){
            Toast.makeText(this,"Invalid username or password",Toast.LENGTH_SHORT).show();
            valid=false;
        }
        return valid;
    }
}