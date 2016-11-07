package com.anuranbarman.gamedunia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by anuran on 29/10/16.
 */

public class Login extends AppCompatActivity {
    Toolbar toolbar;
    EditText email,pass;
    Button login,gotoRegister;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String useremail,password,textEmail,textPass;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        email=(EditText)findViewById(R.id.loginEmail);
        pass=(EditText)findViewById(R.id.loginPassword);
        login=(Button)findViewById(R.id.btnLogin);
        gotoRegister=(Button)findViewById(R.id.goToRegister);
        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Login.this,Register.class);
                startActivity(i);
                finish();
            }
        });
        sharedPreferences=getSharedPreferences("mypref", Context.MODE_PRIVATE);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textEmail=email.getText().toString();
                textPass=pass.getText().toString();
                new LoginTask().execute(textEmail,textPass);
            }
        });
    }

    class LoginTask extends AsyncTask<String,Integer,Integer> {
        String userID;
        int success;
        @Override
        protected Integer doInBackground(String... params) {

            useremail=params[0];
            password=params[1];
            String surl="http://anuranbarman.com/gamedunia/login.php";
            try {
                URL url=new URL(surl);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os=httpURLConnection.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                String data= URLEncoder.encode("user_email","UTF-8")+"="+URLEncoder.encode(useremail,"UTF-8")+"&"+
                        URLEncoder.encode("user_pass","UTF-8")+"="+ URLEncoder.encode(password,"UTF-8");
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();
                InputStream is=httpURLConnection.getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(is,"iso-8859-1"));
                String response="";
                String line="";
                while((line=reader.readLine()) !=null){
                    response+=line;
                }
                JSONObject jsonObject=new JSONObject(response);
                success=jsonObject.optInt("success");
                userID=jsonObject.optString("user_id");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(success==1){
                editor=sharedPreferences.edit();
                editor.putString("userID",userID);
                editor.putBoolean("isLogged",true);
                editor.commit();
                Intent i = new Intent(Login.this,MainActivity.class);
                startActivity(i);
                finish();
            }else if(success==0){
                Toast.makeText(Login.this,"Login failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
