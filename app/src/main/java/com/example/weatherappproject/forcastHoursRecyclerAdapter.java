package com.example.weatherappproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class forcastHoursRecyclerAdapter extends RecyclerView.Adapter<forcastHoursRecyclerAdapter.myViewHolder>{

    Context context;
    ArrayList<forcastHoursRecyclerModel> forcastArrayList;

    public forcastHoursRecyclerAdapter(Context context, ArrayList<forcastHoursRecyclerModel> forcastArrayList) {
        this.context = context;
        this.forcastArrayList = forcastArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forcast_single_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        forcastHoursRecyclerModel model = forcastArrayList.get(position);
        holder.tempTV.setText(String.valueOf(model.forcastHourTemp)+" °C");
        Picasso.get().load("https:".concat(model.forcastIcon)).into(holder.iconIV);
        holder.conditionTV.setText(model.forcastCondition);
        holder.windTV.setText(String.valueOf(model.forcastWind)+" KPH");

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");

        try{
            Date t = input.parse(model.forcastHour);
            holder.timeTV.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return forcastArrayList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView timeTV, tempTV, conditionTV, windTV;
        ImageView iconIV;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            timeTV = itemView.findViewById(R.id.timeTV);
            tempTV = itemView.findViewById(R.id.temperatureTV);
            conditionTV = itemView.findViewById(R.id.conditionTV);
            iconIV = itemView.findViewById(R.id.iconIV);
            windTV = itemView.findViewById(R.id.windTV);
        }
    }
}
