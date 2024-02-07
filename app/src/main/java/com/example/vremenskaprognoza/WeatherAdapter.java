package com.example.vremenskaprognoza;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    /* Klasa sluzi za popunjavanje RecyclerView-a u njoj se definise kako ce izgledati
    elementi RV-a, kojim podacima ce se popunjavati i slicno */
    private Context context;
    /* Lista modal-a sa kojima cemo praviti kartice u RV */
    private ArrayList<WeatherModal> weatherModalArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherModal> weatherModalArrayList) {
        this.context = context;
        this.weatherModalArrayList = weatherModalArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /* Kada se kreira RV u aktivnosti pokrece se ova metoda, u njoj se definise koji xml fajl
        se koristi za definisanje elementa RV-a */
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.weather_rv_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        /* Metoda se pokrece kada se doda novi element u listu modala, u sustini samo
        postavlja komponente kartice na vrijednosti modala */
        WeatherModal modal = weatherModalArrayList.get(position);
        holder.cardTemp.setText(modal.getTemperature() + "°C");
        holder.cardWind.setText(modal.getWindSpeed() + "km/h");
        holder.timeText.setText(modal.getDay());
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.cardIcon);
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date d = input.parse(modal.getTime());
            holder.cardTime.setText(output.format(d));
        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        /* Vrace duzinu liste modala */
        return weatherModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        /* Klasa sluzi za inicijalizovanje komponenti kartice, koristi se u dosta metoda
        nadklase */
        private TextView cardTemp, cardTime, cardWind, timeText;
        private ImageView cardIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTemp = itemView.findViewById(R.id.cardTemperature);
            cardTime = itemView.findViewById(R.id.cardTime);
            cardWind = itemView.findViewById(R.id.cardWindSpeed);
            cardIcon = itemView.findViewById(R.id.cardIcon);
            timeText = itemView.findViewById(R.id.cardTimeTxt);
        }
    }
}
