package com.anuranbarman.gamedunia;

import android.content.Intent;
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

public class Register extends AppCompatActivity {
    Toolbar toolbar;
    EditText etEmail,etPass,etName,etAddress;
    Button register;
    String email,pass,name,address;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create an Account");
        etEmail=(EditText)findViewById(R.id.registerEmail);
        etPass=(EditText)findViewById(R.id.registerPassword);
        etName=(EditText)findViewById(R.id.registerName);
        etAddress=(EditText)findViewById(R.id.registerAddress);
        register=(Button)findViewById(R.id.btnRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=etEmail.getText().toString().trim();
                pass=etPass.getText().toString().trim();
                name=etName.getText().toString().trim();
                address=etAddress.getText().toString().trim();
                if(email.equals("") || pass.equals("") || name.equals("") || address.equals("")){
                    Toast.makeText(Register.this,"Please fill up all required fields.",Toast.LENGTH_SHORT).show();
                    return;
                }

                new BackgroundTask().execute(email,pass,name,address);
            }
        });
    }



    class BackgroundTask extends AsyncTask<String,String,Integer> {

        int success;

        @Override
        protected Integer doInBackground(String... params) {
            String surl="http://anuranbarman.com/gamedunia/register.php";
            String emailT,passT,nameT,addressT;
            emailT=params[0];
            passT=params[1];
            nameT=params[2];
            addressT=params[3];

            try {
                URL url=new URL(surl);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os=httpURLConnection.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                String data= URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(nameT,"UTF-8")+"&"+
                        URLEncoder.encode("user_email","UTF-8")+"="+URLEncoder.encode(emailT,"UTF-8")+"&"+
                        URLEncoder.encode("user_pass","UTF-8")+"="+URLEncoder.encode(passT,"UTF-8")+"&"+
                        URLEncoder.encode("user_address","UTF-8")+"="+URLEncoder.encode(addressT,"UTF-8");
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
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            if(success==1){
                Toast.makeText(Register.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Register.this,Login.class);
                startActivity(i);
                finish();
            }else if(success==0){
                Toast.makeText(Register.this,"Username and Email are already in use",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(Register.this,"Registration went wrong.",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
