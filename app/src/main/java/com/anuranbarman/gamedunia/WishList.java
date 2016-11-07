package com.anuranbarman.gamedunia;

import android.app.ProgressDialog;
import android.content.Context;

import android.content.SharedPreferences;
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


public class WishList extends Fragment {
    RecyclerView recyclerView;
    WishlistAdapter wishlistAdapter;
    LinearLayoutManager linearLayoutManager;
    List<WishlistDataModel> my_data;
    String userID;
    int success;

    public WishList() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_wish_list, container, false);
        recyclerView=(RecyclerView)view.findViewById(R.id.recyler_view_wishlist);
        linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("mypref",Context.MODE_PRIVATE);
        userID=sharedPreferences.getString("userID",null);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        my_data=new ArrayList<WishlistDataModel>();
        wishlistAdapter=new WishlistAdapter(getContext(),my_data);
        recyclerView.setAdapter(wishlistAdapter);
        new ShowWishlist().execute("http://anuranbarman.com/gamedunia/wishlist.php");

    }

    public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {
        private Context context;
        private List<WishlistDataModel> data_list;

        public WishlistAdapter(Context context, List<WishlistDataModel> data_list) {
            this.context = context;
            this.data_list = data_list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_card,parent,false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
                holder.productName.setText(data_list.get(position).getProduct_title());
                holder.productQuantity.setText(data_list.get(position).getProduct_quantity()+"");
                holder.productPrice.setText(data_list.get(position).getProduct_price()+"");
                holder.btnMovetoCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AddtoCart().execute("http://anuranbarman.com/gamedunia/movetocart.php",data_list.get(position).getProduct_id()+"",
                                data_list.get(position).getProduct_title(),data_list.get(position).getProduct_price()+"",
                                data_list.get(position).getProduct_image(),data_list.get(position).getProduct_quantity()+"",data_list.get(position).getWishlist_id()+"",position+"");
                    }
                });
            Picasso.with(context).load("http://www.anuranbarman.com/gamedunia/game_images/"+data_list.get(position).getProduct_image()).into(holder.productImage);
        }

        @Override
        public int getItemCount() {
            return data_list.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView productName,productPrice,productQuantity;
            ImageView productImage;
            Button btnMovetoCart;
            public ViewHolder(View itemView) {
                super(itemView);
                productName=(TextView)itemView.findViewById(R.id.wishlistGameName);
                productPrice=(TextView)itemView.findViewById(R.id.wishlistGamePrice);
                productQuantity=(TextView)itemView.findViewById(R.id.wishlistquntityText);
                productImage=(ImageView)itemView.findViewById(R.id.wishlistgameImage);
                btnMovetoCart=(Button)itemView.findViewById(R.id.btnMovetoCart);
            }
        }
    }



    class ShowWishlist extends AsyncTask<String,Void,Void> {
        ProgressDialog progressDialog;
        String gameName,gameImage;
        int gamePrice,gameQuantity,wishlistID,productID,user_id;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Showing your wishlist information shortly....");
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
                JSONArray jsonArray=jsonObject.optJSONArray("wishlist");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject1=jsonArray.optJSONObject(i);
                    user_id=Integer.parseInt(jsonObject1.optString("user_id"));
                    wishlistID=Integer.parseInt(jsonObject1.optString("wishlist_id"));
                    productID=Integer.parseInt(jsonObject1.optString("product_id"));
                    gameName=jsonObject1.optString("product_title");
                    gamePrice=Integer.parseInt(jsonObject1.optString("product_price"));
                    gameImage=jsonObject1.optString("product_image");
                    gameQuantity=Integer.parseInt(jsonObject1.optString("quantity"));

                    WishlistDataModel data=new WishlistDataModel(productID,gameImage,gamePrice,gameQuantity,gameName,user_id,wishlistID);
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
            wishlistAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }
    }


    class AddtoCart extends AsyncTask<String,Void,Void>{
        int success=-1;
        ProgressDialog progressDialog;
        String encoded_data;
        String productID,productTitle,productImage,productPrice,quantity,wishlistID;
        int pos;
        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(getContext());
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Updating your cart...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                productID=params[1];
                productTitle=params[2];
                productPrice=params[3];
                productImage=params[4];
                quantity=params[5];
                wishlistID=params[6];
                pos=Integer.parseInt(params[7]);
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
                        URLEncoder.encode("product_price","UTF-8")+"="+URLEncoder.encode(productPrice,"UTF-8")+"&"+
                        URLEncoder.encode("wishlist_id","UTF-8")+"="+URLEncoder.encode(wishlistID,"UTF-8");
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
            wishlistAdapter.data_list.remove(pos);
            wishlistAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
            if(success==1){
                Toast.makeText(getContext(),"Successfully moved the product to your cart",Toast.LENGTH_LONG).show();
            }else if(success==0){
                Toast.makeText(getContext(),"Something went wrong.Please try again",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getContext(),"You have already added this product to your cart",Toast.LENGTH_LONG).show();
            }
        }
    }
}
