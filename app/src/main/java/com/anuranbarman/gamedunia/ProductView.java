package com.anuranbarman.gamedunia;


import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.nio.charset.StandardCharsets;


public class ProductView extends AppCompatActivity {
    TextView gameTitle,gamePrice,gameDesc;
    ImageView gameImage;
    Button btnAddCart;
    String url="http://www.anuranbarman.com/gamedunia/game_images/";
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    String productTitle,productID,userID,quantity,productImage,productPrice;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_view);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String[] info=getIntent().getExtras().getStringArray("info");
        getSupportActionBar().setTitle(info[0]);
        gameTitle=(TextView)findViewById(R.id.pv_game_name);
        gamePrice=(TextView)findViewById(R.id.pv_game_price);
        gameDesc=(TextView)findViewById(R.id.pv_game_desc);
        gameImage=(ImageView)findViewById(R.id.game_image);
        btnAddCart=(Button)findViewById(R.id.btnAddCart);
        sharedPreferences=getSharedPreferences("mypref",Context.MODE_PRIVATE);

        productID=info[4];
        productTitle=info[0];
        productImage=info[3];
        productPrice=info[1];
        userID=sharedPreferences.getString("userID",null);
        quantity="1";
        gameTitle.setText(info[0]);
        gamePrice.setText(info[1]);
        gameDesc.setText(info[2]);
        Picasso.with(this).load(url+info[3]).into(gameImage);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddtoCart().execute("http://www.anuranbarman.com/gamedunia/addtocart.php");
            }
        });
    }

    class AddtoCart extends AsyncTask<String,Void,Void>{
        int success=-1;
        ProgressDialog progressDialog;
        String encoded_data;
        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(ProductView.this);
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Updating your cart...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream os=httpURLConnection.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                encoded_data= URLEncoder.encode("product_id","UTF-8")+"="+URLEncoder.encode(productID,"UTF-8")+"&"+
                        URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(userID,"UTF-8")+"&"+
                        URLEncoder.encode("product_title","UTF-8")+"="+URLEncoder.encode(productTitle,"UTF-8")+"&"+
                        URLEncoder.encode("quantity","UTF-8")+"="+URLEncoder.encode(quantity,"UTF-8")+"&"+
                        URLEncoder.encode("product_image","UTF-8")+"="+URLEncoder.encode(productImage,"UTF-8")+"&"+
                        URLEncoder.encode("product_price","UTF-8")+"="+URLEncoder.encode(productPrice,"UTF-8");
                writer.write(encoded_data);
                writer.flush();
                writer.close();
                os.close();
                InputStream is = httpURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"));
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if(success==1){
                Toast.makeText(ProductView.this,"Successfully updated your cart",Toast.LENGTH_LONG).show();
            }else if(success==0){
                Toast.makeText(ProductView.this,"Something went wrong.Please try again",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(ProductView.this,"You have already added this product to your cart",Toast.LENGTH_LONG).show();
            }
        }
    }
}
