package com.anuranbarman.gamedunia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anuran on 27/10/16.
 */

public class GamesbyCategories extends AppCompatActivity {
    String gameTitle,gamePrice,gameDesc,gameImage,gameID;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private List<MyData> data_list=new ArrayList<MyData>();
    private GridLayoutManager gridLayoutManager;
    private final String link="http://anuranbarman.com/gamedunia/gamesbycategories.php";
    Toolbar toolbar;
    ProgressDialog progressDialog;
    String catID,category;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamesbycategories);
        catID= getIntent().getExtras().getString("id");
        category= getIntent().getExtras().getString("category");
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(category+" Games");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView=(RecyclerView)findViewById(R.id.recyler_view_cat_games);
        data_list=new ArrayList<MyData>();
        adapter=new CustomAdapter(GamesbyCategories.this,data_list);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new GamesbyCategories.RecyclerTouchListener(GamesbyCategories.this, recyclerView, new GamesbyCategories.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle=new Bundle();
                String[] infoArray=new String[]{data_list.get(position).getGameTitle(),data_list.get(position).getGamePrice(),
                        data_list.get(position).getGameDesc(),data_list.get(position).getGameImage(),data_list.get(position).getGameID()};
                bundle.putStringArray("info",infoArray);
                Intent i = new Intent(GamesbyCategories.this,ProductView.class);
                i.putExtras(bundle);
                startActivity(i);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        gridLayoutManager=new GridLayoutManager(GamesbyCategories.this,2);
        recyclerView.setLayoutManager(gridLayoutManager);

        new ShowProducts().execute(link);
    }



    class ShowProducts extends AsyncTask<String,Void,Void> {

        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(GamesbyCategories.this);
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Bringing Mindblowing Games for you....");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os=httpURLConnection.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                String encoded_data= URLEncoder.encode("cat_id","UTF-8")+"="+URLEncoder.encode(catID,"UTF-8");
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
                JSONArray jsonArray=jsonObject.optJSONArray("games");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject1=jsonArray.optJSONObject(i);

                    gameTitle=jsonObject1.optString("title");
                    gamePrice=jsonObject1.optString("price");
                    gameDesc=jsonObject1.optString("product_desc");
                    gameImage=jsonObject1.optString("product_image");
                    gameID=jsonObject1.optString("id");
                    MyData data=new MyData(gameTitle,gamePrice,gameDesc,gameImage,gameID);
                    data_list.add(data);
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
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private ClickListener clickListener;
        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){
            this.clickListener=clickListener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public static interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view,int position);
    }
}
