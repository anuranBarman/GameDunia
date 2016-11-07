package com.anuranbarman.gamedunia;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
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
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment{
    String gameTitle,gamePrice,gameDesc,gameImage,gameID;
    String userID;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private List<MyData> data_list;
    private GridLayoutManager gridLayoutManager;
    private final String link="http://anuranbarman.com/gamedunia/products.php";
    SliderLayout imageSlider;
    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_home,container,false);
        recyclerView=(RecyclerView)rootView.findViewById(R.id.recyler_view);
        imageSlider=(SliderLayout)rootView.findViewById(R.id.slider);
        //btn=(Button)rootView.findViewById(R.id.btn);
        gridLayoutManager=new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("mypref",Context.MODE_PRIVATE);
        userID=sharedPreferences.getString("userID",null);
        return rootView;
    }

    @Override
    public void onStop() {
        imageSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Buy Hot Games of the Year",R.drawable.banner1);
        file_maps.put("Game Dunia-a name you can Trust",R.drawable.banner2);
        file_maps.put("Gamers don't Die,They Respawn",R.drawable.banner3);
        file_maps.put("Games by Consoles,Genres,Studios", R.drawable.banner4);
        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(getContext());
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {

                        }
                    });

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            imageSlider.addSlider(textSliderView);
        }
        imageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        imageSlider.setCustomAnimation(new DescriptionAnimation());
        imageSlider.setDuration(4000);
        imageSlider.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        data_list=new ArrayList<>();
        adapter=new CustomAdapter(getActivity(),data_list);
        recyclerView.setAdapter(adapter);
        new ShowProducts().execute(link);

    }



    class ShowProducts extends AsyncTask<String,Void,Void>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Bringing Mindblowing Games for you....");
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
                JSONArray jsonArray=jsonObject.optJSONArray("products");
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






    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.Viewholder> {
        private Context context;
        private List<MyData> my_data;

        public CustomAdapter(Context context, List<MyData> my_data) {
            this.context = context;
            this.my_data = my_data;
        }

        @Override
        public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);
            return new Viewholder(itemView);
        }

        @Override
        public void onBindViewHolder(final Viewholder holder, final int position) {
            holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    String[] infoArray=new String[]{data_list.get(position).getGameTitle(),data_list.get(position).getGamePrice(),
                            data_list.get(position).getGameDesc(),data_list.get(position).getGameImage(),data_list.get(position).getGameID()};
                    bundle.putStringArray("info",infoArray);
                    Intent i = new Intent(getContext(),ProductView.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });
            holder.addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AddtoCart().execute("http://www.anuranbarman.com/gamedunia/addtocart.php",userID,data_list.get(position).getGameID(),data_list.get(position).getGameTitle(),data_list.get(position).getGamePrice(),data_list.get(position).getGameImage());
                }
            });
            holder.addToWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AddtoWishlist().execute("http://www.anuranbarman.com/gamedunia/addtowishlist.php",userID,data_list.get(position).getGameID(),data_list.get(position).getGameTitle(),data_list.get(position).getGamePrice(),data_list.get(position).getGameImage());
                    holder.addToWishlist.setBackgroundResource(R.drawable.wishlistselected);
                }
            });
            holder.gameTitle.setText(my_data.get(position).getGameTitle());
            holder.gamePrice.setText(my_data.get(position).getGamePrice());
            Picasso.with(context).load("http://www.anuranbarman.com/gamedunia/game_images/"+my_data.get(position).getGameImage()).into(holder.image);
        }

        @Override
        public int getItemCount() {
            return my_data.size();
        }

        public class Viewholder extends RecyclerView.ViewHolder {
            public TextView gameTitle,gamePrice;
            public ImageView image;
            Button addToCart,btnDetail;
            Button addToWishlist;

            public Viewholder(View itemView) {
                super(itemView);
                gameTitle=(TextView)itemView.findViewById(R.id.tvGameName);
                gamePrice=(TextView)itemView.findViewById(R.id.tvGamePrice);
                image=(ImageView)itemView.findViewById(R.id.image);
                addToCart=(Button)itemView.findViewById(R.id.btnAddCartShort);
                btnDetail=(Button)itemView.findViewById(R.id.btnDetails);
                addToWishlist=(Button)itemView.findViewById(R.id.btnWishList);
            }
        }


    }

    class AddtoCart extends AsyncTask<String,Void,Void>{
        int success=-1;
        ProgressDialog progressDialog;
        String encoded_data;
        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(getActivity());
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
                encoded_data= URLEncoder.encode("product_id","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"+
                        URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"+
                        URLEncoder.encode("product_title","UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"+
                        URLEncoder.encode("quantity","UTF-8")+"="+URLEncoder.encode("1","UTF-8")+"&"+
                        URLEncoder.encode("product_image","UTF-8")+"="+URLEncoder.encode(params[5],"UTF-8")+"&"+
                        URLEncoder.encode("product_price","UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8");
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
                Toast.makeText(getActivity(),"Successfully updated your cart",Toast.LENGTH_LONG).show();
            }else if(success==0){
                Toast.makeText(getActivity(),"Something went wrong.Please try again",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getActivity(),"You have already added this product to your cart",Toast.LENGTH_LONG).show();
            }
        }
    }

    class AddtoWishlist extends AsyncTask<String,Void,Void>{
        int success=-1;
        ProgressDialog progressDialog;
        String encoded_data;
        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Updating your wishlist...");
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
                encoded_data= URLEncoder.encode("product_id","UTF-8")+"="+URLEncoder.encode(params[2],"UTF-8")+"&"+
                        URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8")+"&"+
                        URLEncoder.encode("product_title","UTF-8")+"="+URLEncoder.encode(params[3],"UTF-8")+"&"+
                        URLEncoder.encode("quantity","UTF-8")+"="+URLEncoder.encode("1","UTF-8")+"&"+
                        URLEncoder.encode("product_image","UTF-8")+"="+URLEncoder.encode(params[5],"UTF-8")+"&"+
                        URLEncoder.encode("product_price","UTF-8")+"="+URLEncoder.encode(params[4],"UTF-8");
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
                Toast.makeText(getActivity(),"Successfully updated your wishlist",Toast.LENGTH_LONG).show();
            }else if(success==0){
                Toast.makeText(getActivity(),"Something went wrong.Please try again",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getActivity(),"You already have this product in your wishlist",Toast.LENGTH_LONG).show();
            }
        }
    }
}
