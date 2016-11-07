package com.anuranbarman.gamedunia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
 * Created by anuran on 28/10/16.
 */

public class Cart extends AppCompatActivity {

    String gameName,gameImage,userID;
    int gamePrice,quantity,product_id;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    List<CartDataModel> my_data;
    CartAdapter cartAdapter;
    private final String link="http://anuranbarman.com/gamedunia/cart.php";
    SharedPreferences sharedPreferences;
    Toolbar toolbar;
    int qty,success;
    boolean isOneSelected;
    TextView totalAmount;
    Button btnPlaceOrder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Your Cart");
        isOneSelected=false;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView=(RecyclerView)findViewById(R.id.recyler_view_cart);
        totalAmount=(TextView)findViewById(R.id.totalAmountText);
        btnPlaceOrder=(Button)findViewById(R.id.btnPlaceOrder);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        my_data=new ArrayList<CartDataModel>();
        cartAdapter=new CartAdapter(this,my_data);
        recyclerView.setAdapter(cartAdapter);
        sharedPreferences=getSharedPreferences("mypref",Context.MODE_PRIVATE);
        userID=sharedPreferences.getString("userID",null);
        new ShowCart().execute(link);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<cartAdapter.data_list.size();i++){
                    new PlaceOrder().execute("http://anuranbarman.com/gamedunia/placeorder.php",i+"");
                }

            }
        });
    }





    class ShowCart extends AsyncTask<String,Void,Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(Cart.this);
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Showing your cart information shortly....");
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
                String encoded_data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(userID,"UTF-8");
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
                JSONArray jsonArray=jsonObject.optJSONArray("cart");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject1=jsonArray.optJSONObject(i);
                    product_id=Integer.parseInt(jsonObject1.optString("product_id"));
                    gameName=jsonObject1.optString("title");
                    gamePrice=Integer.parseInt(jsonObject1.optString("product_price"));
                    gameImage=jsonObject1.optString("product_image");
                    quantity=Integer.parseInt(jsonObject1.optString("quantity"));
                    CartDataModel data=new CartDataModel(product_id,gameName,gameImage,gamePrice,quantity);
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
            cartAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
            new TotalAmount().execute("http://anuranbarman.com/gamedunia/totalamount.php",userID);
        }
    }

    public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Viewholder> {
        private Context context;
        private List<CartDataModel> data_list;

        public CartAdapter(Context context, List<CartDataModel> data_list) {
            this.context = context;
            this.data_list = data_list;
        }

        @Override
        public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card,parent,false);
            return new Viewholder(itemView);
        }

        @Override
        public void onBindViewHolder(Viewholder holder, final int position) {
            final int pro_id=data_list.get(position).getProduct_id();
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DeleteItem().execute("http://anuranbarman.com/gamedunia/deleteitem.php",pro_id+"",position+"");
                }
            });
            holder.gameTitle.setText(data_list.get(position).getGameName());
            holder.gamePrice.setText(data_list.get(position).getPrice()+"");
            holder.quantityText.setText(data_list.get(position).getQuantity()+"");
            List<String> list=new ArrayList<String>();
            list.add("1");
            list.add("2");
            list.add("3");
            list.add("4");
            list.add("5");
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,list);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.quantity.setAdapter(arrayAdapter);
            holder.quantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionspin, long id) {
                        qty=Integer.parseInt(parent.getItemAtPosition(positionspin).toString());
                    if (qty >1){
                        new UpdateCart().execute("http://anuranbarman.com/gamedunia/updatecart.php",pro_id+"",position+"");

                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            Picasso.with(context).load("http://www.anuranbarman.com/gamedunia/game_images/"+data_list.get(position).getImage()).into(holder.gameImage);

        }

        @Override
        public int getItemCount() {
            return data_list.size();
        }

        public class Viewholder extends RecyclerView.ViewHolder {
            public TextView gameTitle,gamePrice,quantityText;
            public Spinner quantity;
            public ImageView gameImage;
            public Button btnDelete;
            public Viewholder(View itemView) {
                super(itemView);
                btnDelete=(Button)itemView.findViewById(R.id.deleteItem);
                gameTitle=(TextView)itemView.findViewById(R.id.cartGameName);
                gamePrice=(TextView)itemView.findViewById(R.id.cartGamePrice);
                quantityText=(TextView)itemView.findViewById(R.id.quntityText);
                quantity=(Spinner) itemView.findViewById(R.id.cartQuantitySpinner);
                gameImage=(ImageView)itemView.findViewById(R.id.gameImage);
            }
        }
    }


    class UpdateCart extends AsyncTask<String,Void,Void> {
        ProgressDialog progressDialog;
        String pos;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(Cart.this);
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Updating your cart information shortly....");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                pos=params[2];
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os=httpURLConnection.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                String encoded_data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(userID,"UTF-8")+"&"+
                        URLEncoder.encode("quantity","UTF-8")+"="+URLEncoder.encode(qty+"","UTF-8")+"&"+
                        URLEncoder.encode("product_id","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8");
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
                success=Integer.parseInt(jsonObject.optString("success"));
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
            cartAdapter.data_list.remove(Integer.parseInt(pos));
            progressDialog.dismiss();
            if(success==1){
                Toast.makeText(Cart.this,"Quantity updated successfully",Toast.LENGTH_SHORT).show();
                isOneSelected=true;
            }else{
                Toast.makeText(Cart.this,"Something went wrong while updating quantity",Toast.LENGTH_SHORT).show();
            }
            new ShowCart().execute(link);
        }
    }

    class DeleteItem extends AsyncTask<String,Void,Void> {
        ProgressDialog progressDialog;
        String pos;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(Cart.this);
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Updating your cart information shortly....");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                pos=params[2];
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os=httpURLConnection.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                String encoded_data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(userID,"UTF-8")+"&"+
                        URLEncoder.encode("product_id","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8");
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
                success=Integer.parseInt(jsonObject.optString("success"));
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
            cartAdapter.data_list.remove(Integer.parseInt(pos));
            progressDialog.dismiss();
            if(success==1){
                Toast.makeText(Cart.this,"Item deleted successfully",Toast.LENGTH_SHORT).show();
                isOneSelected=true;
            }else{
                Toast.makeText(Cart.this,"Something went wrong while deleting item.",Toast.LENGTH_SHORT).show();
            }
            new ShowCart().execute(link);
        }
    }

    class TotalAmount extends AsyncTask<String,Void,Void> {
        ProgressDialog progressDialog;
        int total;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(Cart.this);
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Updating your cart information shortly....");
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
                String encoded_data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8");
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
                total=jsonObject.optInt("total");
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
            totalAmount.setText(total+"");
            progressDialog.dismiss();

        }
    }

    class PlaceOrder extends AsyncTask<String,Void,Void>{
        int success=-1;
        ProgressDialog progressDialog;
        String encoded_data;
        int position;
        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(Cart.this);
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Placing your orders...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                position=Integer.parseInt(params[1]);
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream os=httpURLConnection.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                encoded_data= URLEncoder.encode("product_id","UTF-8")+"="+URLEncoder.encode(cartAdapter.data_list.get(position).getProduct_id()+"","UTF-8")+"&"+
                        URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(userID,"UTF-8")+"&"+
                        URLEncoder.encode("product_title","UTF-8")+"="+URLEncoder.encode(cartAdapter.data_list.get(position).getGameName()+"","UTF-8")+"&"+
                        URLEncoder.encode("quantity","UTF-8")+"="+URLEncoder.encode(cartAdapter.data_list.get(position).getQuantity()+"","UTF-8")+"&"+
                        URLEncoder.encode("product_image","UTF-8")+"="+URLEncoder.encode(cartAdapter.data_list.get(position).getImage()+"","UTF-8")+"&"+
                        URLEncoder.encode("product_price","UTF-8")+"="+URLEncoder.encode(cartAdapter.data_list.get(position).getPrice()+"","UTF-8")+"&"+
                        URLEncoder.encode("status","UTF-8")+"="+URLEncoder.encode("Placed","UTF-8");
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
                Toast.makeText(Cart.this,"Successfully Placed your Orders",Toast.LENGTH_LONG).show();
            }else if(success==0){
                Toast.makeText(Cart.this,"Something went wrong.Please try again",Toast.LENGTH_LONG).show();
            }
        }
    }
}
