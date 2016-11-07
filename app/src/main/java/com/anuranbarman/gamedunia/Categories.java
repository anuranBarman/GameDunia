package com.anuranbarman.gamedunia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by anuran on 27/10/16.
 */

public class Categories extends AppCompatActivity {
    Toolbar toolbar;
    ListView catList;
    CustomCatAdapter customCatAdapter;
    ArrayList<DataModel> my_data=new ArrayList<DataModel>();
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Shop by Game Genres");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        catList=(ListView)findViewById(R.id.catList);

        new FetchCat().execute("http://anuranbarman.com/gamedunia/categories.php");

    }


    class FetchCat extends AsyncTask<String,Void,Void>{

        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(Categories.this);
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Bringing Game Genres for you....");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                InputStream is = httpURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"));
                String response="";
                String line="";
                while((line=reader.readLine()) !=null){
                    response+=line;
                }
                JSONObject jsonObject=new JSONObject(response);
                Iterator<String> keys= jsonObject.keys();
                while (keys.hasNext())
                {
                    String keyValue = (String)keys.next();
                    String valueString = jsonObject.getString(keyValue);
                    DataModel data=new DataModel(valueString);
                    my_data.add(data);
                }
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

            customCatAdapter=new CustomCatAdapter(Categories.this,my_data);
            catList.setAdapter(customCatAdapter);
            customCatAdapter.notifyDataSetChanged();
            catList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //int catID=(int)(parent.getItemIdAtPosition(position)+1);
                    Intent intent=new Intent(Categories.this,GamesbyCategories.class);
                    TextView catName=(TextView)view.findViewById(R.id.catName);
                    String category=catName.getText().toString();
                    Bundle bundle=new Bundle();
                    bundle.putString("id",Long.toString(parent.getItemIdAtPosition(position)+1));
                    bundle.putString("category",category);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            progressDialog.dismiss();
        }

    }
}
