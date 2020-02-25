package com.hyunki.origin_weather_app.adapter;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.model.Forecast;
import com.hyunki.origin_weather_app.model.util.DateUtil;
import com.hyunki.origin_weather_app.model.util.TempUtil;
import com.squareup.picasso.Picasso;

import java.util.Locale;

class ForecastViewHolder extends RecyclerView.ViewHolder {

    private TextView dateTextView;
    private TextView tempTextView;
    private ImageView iconImageView;
    private TextView conditionTextView;
    private TextView conditionDetailTextView;

    ForecastViewHolder(View itemView) {
        super(itemView);
        dateTextView = itemView.findViewById(R.id.my_weather_list_item_date_textView);
        tempTextView = itemView.findViewById(R.id.my_weather_list_item_temp_textView);
        iconImageView = itemView.findViewById(R.id.my_weather_list_item_imageView);
        conditionTextView = itemView.findViewById(R.id.my_weather_list_item_condition_textView);
        conditionDetailTextView = itemView.findViewById(R.id.my_weather_list_item_condition_detail_textView);
    }

    void bind(Forecast forecast) {

        dateTextView.setText(DateUtil.getFormattedDate(forecast.getDate()));
        conditionTextView.setText(forecast.getWeather().get(0).getMain());
        conditionDetailTextView.setText(forecast.getWeather().get(0).getDescription());
        int temp = TempUtil.getFahrenheitFromKelvin(forecast.getTemp().getTempKelvin());
        tempTextView.setText(String.format(Locale.US,"%d%s", temp, "℉"));

        String icon = forecast.getWeather().get(0).getIcon();
        String iconUri = String.format("https://openweathermap.org/img/wn/%s@2x.png", icon);
        Picasso.get().load(iconUri).into(iconImageView);
    }

}
