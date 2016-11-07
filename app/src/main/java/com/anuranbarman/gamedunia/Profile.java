package com.anuranbarman.gamedunia;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    TextView userNameText,userEmailText,userAddressText,defaultAddressText;
    ImageView editAddress;
    String userID;
    String addresstext,addresstextupdated;
    Button btnLogout;
    SharedPreferences sharedPreferences;
    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        userNameText=(TextView)view.findViewById(R.id.userNameText);
        userEmailText=(TextView)view.findViewById(R.id.userEmailText);
        userAddressText=(TextView)view.findViewById(R.id.userAddressText);
        defaultAddressText=(TextView)view.findViewById(R.id.defaultAddress);
        editAddress=(ImageView)view.findViewById(R.id.editAddressImage);
        btnLogout=(Button)view.findViewById(R.id.btnLogout);

        sharedPreferences=getActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE);
        userID=sharedPreferences.getString("userID",null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new ShowProfile().execute("http://anuranbarman.com/gamedunia/user.php");
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertdialog=new AlertDialog.Builder(getContext())
                        .setCancelable(false)
                        .setMessage("Are you sure, you want to Logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putBoolean("isLogged",false);
                                editor.commit();
                                dialog.dismiss();
                                Intent intent=new Intent(getActivity(),Login.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog=alertdialog.create();
                dialog.show();
            }
        });
        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog=new Dialog(getContext());
                dialog.setContentView(R.layout.address_editor);
                final EditText address=(EditText)dialog.findViewById(R.id.editaddress);
                addresstext=userAddressText.getText().toString();
                address.setText(addresstext);
                Button btnCancel=(Button)dialog.findViewById(R.id.btnCancelEdit);
                Button btnOK=(Button)dialog.findViewById(R.id.btnOKEdit);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addresstextupdated=address.getText().toString().trim();
                        new UpdateAddress().execute("http://anuranbarman.com/gamedunia/updateaddress.php");
                        dialog.dismiss();

                    }
                });

                dialog.show();
            }
        });

    }



    class ShowProfile extends AsyncTask<String,Void,Void> {
        ProgressDialog progressDialog;
        String userName,userEmail,userAddress;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Showing your profile information shortly....");
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
                userName=jsonObject.optString("user_name");
                userEmail=jsonObject.optString("user_email");
                userAddress=jsonObject.optString("user_address");


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
            userNameText.setText(userName);
            userEmailText.setText(userEmail);
            userAddressText.setText(userAddress);
            defaultAddressText.setText(userAddress);
            progressDialog.dismiss();
        }
    }




    class UpdateAddress extends AsyncTask<String,Void,Void> {
        ProgressDialog progressDialog;
        int success;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setTitle("Please Wait Gamer");
            progressDialog.setMessage("Updating your default delivery address....");
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
                String encoded_data= URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(userID,"UTF-8")+"&"+
                        URLEncoder.encode("user_address","UTF-8")+"="+URLEncoder.encode(addresstextupdated,"UTF-8");
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
            if (success==1){
                Toast.makeText(getContext(),"Default delivery address successfully updated",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(),"Something went wrong while updating address",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
