package com.hyunki.origin_weather_app.model.util;

public class DateUtil {

    public static String getFormattedDate(String date){
        return ( (date.charAt(5) == '0'? date.substring(6,7):date.substring(5,7)) + "/" + date.substring(8,10) );
    }
}
