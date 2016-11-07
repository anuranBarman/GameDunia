package com.anuranbarman.gamedunia;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by anuran on 26/10/16.
 */
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
    public void onBindViewHolder(Viewholder holder, int position) {
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        Button addToCart;

        public Viewholder(View itemView) {
            super(itemView);
            gameTitle=(TextView)itemView.findViewById(R.id.tvGameName);
            gamePrice=(TextView)itemView.findViewById(R.id.tvGamePrice);
            image=(ImageView)itemView.findViewById(R.id.image);
            addToCart=(Button)itemView.findViewById(R.id.btnAddCartShort);
        }
    }


}
