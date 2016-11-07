package com.anuranbarman.gamedunia;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
 * A simple {@link Fragment} subclass.
 */
public class Orders extends Fragment {

    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    List<OrderDataModel> my_data;
    LinearLayoutManager linearLayoutManager;
    String userID;
    int success;
    public Orders() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_orders, container, false);
        recyclerView=(RecyclerView)view.findViewById(R.id.recyler_view_order);
        linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("mypref",Context.MODE_PRIVATE);
        userID=sharedPreferences.getString("userID",null);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        my_data=new ArrayList<OrderDataModel>();
        orderAdapter=new OrderAdapter(getContext(),my_data);
        recyclerView.setAdapter(orderAdapter);
        new ShowOrder().execute("http://anuranbarman.com/gamedunia/orders.php");


    }

    public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{
        private Context context;
        private List<OrderDataModel> data_list;

        public OrderAdapter(Context context, List<OrderDataModel> data_list) {
            this.context = context;
            this.data_list = data_list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card,parent,false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
                holder.productTitle.setText(data_list.get(position).getProduct_title());
                holder.productPrice.setText(data_list.get(position).getProduct_price()+"");
                holder.productQuantity.setText(data_list.get(position).getQuantity()+"");
                if(data_list.get(position).getStatus().equals("Placed")){
                    holder.status.setTextColor(Color.BLUE);
                    holder.btnCancel.setVisibility(View.VISIBLE);
                }else if(data_list.get(position).getStatus().equals("Processing")){
                    holder.status.setTextColor(Color.MAGENTA);
                    holder.btnCancel.setVisibility(View.VISIBLE);
                }else if(data_list.get(position).getStatus().equals("Dispatched")){
                    holder.status.setTextColor(Color.parseColor("#b9441c"));
                    holder.btnCancel.setVisibility(View.GONE);
                }else if(data_list.get(position).getStatus().equals("Delivered")){
                    holder.status.setTextColor(Color.parseColor("#215d1c"));
                    holder.btnCancel.setVisibility(View.GONE);
                }else if(data_list.get(position).getStatus().equals("Cancelled")){
                    holder.itemView.setEnabled(false);
                    holder.productImage.setEnabled(false);
                    holder.btnCancel.setVisibility(View.GONE);
                }
                holder.status.setText("Order Status : "+data_list.get(position).getStatus());
                holder.order_id.setText("Order ID : #"+data_list.get(position).getOrderID());
                holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog=new Dialog(getContext());
                        dialog.setContentView(R.layout.cancel_order_dialog);
                        Button cancelorder=(Button)dialog.findViewById(R.id.btnOKCancel);
                        Button cancelcancelorder=(Button)dialog.findViewById(R.id.btnCancelCancelOrder);
                        final EditText editText=(EditText)dialog.findViewById(R.id.cancelreasonedittext);
                        cancelcancelorder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        cancelorder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(editText.getText().toString().equals("")){
                                    editText.setText("Reason of cancellation is required.Please specify the reason in short");
                                    editText.setTextColor(Color.RED);
                                    return;
                                }
                                int order_id=data_list.get(position).getOrderID();
                                new CancelOrder().execute("http://anuranbarman.com/gamedunia/cancel.php",order_id+"");
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });
            Picasso.with(context).load("http://www.anuranbarman.com/gamedunia/game_images/"+data_list.get(position).getProduct_image()).into(holder.productImage);

        }

        @Override
        public int getItemCount() {
            return data_list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView productTitle,productQuantity,productPrice,status,order_id;
            ImageView productImage;
            Button btnCancel;
            public ViewHolder(View itemView) {
                super(itemView);


                productTitle=(TextView)itemView.findViewById(R.id.orderGameName);
                productQuantity=(TextView)itemView.findViewById(R.id.orderquntityText);
                productPrice=(TextView)itemView.findViewById(R.id.orderGamePrice);
                productImage=(ImageView)itemView.findViewById(R.id.ordergameImage);
                status=(TextView)itemView.findViewById(R.id.statusText);
                order_id=(TextView)itemView.findViewById(R.id.orderIDText);
                btnCancel=(Button)itemView.findViewById(R.id.btnCancelOrder);

            }
        }
    }


    class ShowOrder extends AsyncTask<String,Void,Void> {
        ProgressDialog progressDialog;
        String gameName,gameImage,gameStatus;
        int gamePrice,gameQuantity,orderID,productID,user_id;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Showing your orders information shortly....");
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
                JSONArray jsonArray=jsonObject.optJSONArray("orders");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject1=jsonArray.optJSONObject(i);
                    user_id=Integer.parseInt(jsonObject1.optString("user_id"));
                    orderID=Integer.parseInt(jsonObject1.optString("order_id"));
                    productID=Integer.parseInt(jsonObject1.optString("product_id"));
                    gameName=jsonObject1.optString("product_title");
                    gamePrice=Integer.parseInt(jsonObject1.optString("product_price"));
                    gameImage=jsonObject1.optString("product_image");
                    gameQuantity=Integer.parseInt(jsonObject1.optString("quantity"));
                    gameStatus=jsonObject1.optString("status");
                    OrderDataModel data=new OrderDataModel(orderID,productID,gameImage,gamePrice,gameName,gameQuantity,user_id,gameStatus);
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
            orderAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }
    }



    class CancelOrder extends AsyncTask<String,Void,Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Cancelling your order....");
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
                String encoded_data= URLEncoder.encode("order_id","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8");
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
            progressDialog.dismiss();
            if(success==1){
                Toast.makeText(getContext(),"Order cancelled successfully",Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(getContext(),"Something went wrong while cancelling order.",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
