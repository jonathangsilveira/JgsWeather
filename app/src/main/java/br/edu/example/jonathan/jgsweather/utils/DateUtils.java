package br.edu.example.jonathan.jgsweather.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat mDateTimeFormat =
            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private DateUtils() {

    }

    public static String formatDateMedium(Date data) {
        return mDateFormat.format(data);
    }

    public static Date parseToDate(String valor) throws ParseException {
        return mDateFormat.parse(valor);
    }

    public static Date truncateDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date createDate(int year, int month, int dayOfMonth, int hour,
                                  int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, hour, minutes, seconds);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date createDate(int year, int month, int dayOfMonth) {
        return createDate(year, month, dayOfMonth, 0, 0, 0);
    }

    public static String formatDateTimeMedium(Date date) {
        return mDateTimeFormat.format(date);
    }

    public static boolean isYesterday(Date date) {
        Date now = new Date();
        Date today = truncateDate(now);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = truncateDate(date);
        return date.equals(calendar.getTime());
    }

    public static boolean isTomorrow(Date date) {
        Date now = new Date();
        Date today = truncateDate(now);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date = truncateDate(date);
        return date.equals(calendar.getTime());
    }

    public static boolean isToday(Date date) {
        Date now = new Date();
        Date today = truncateDate(now);
        date = truncateDate(date);
        return date.equals(today);
    }

    public static String getDay(Date date) {
        String dateForecast;
        if (isToday(date)) {
            dateForecast = "Hoje";
        } else if (isTomorrow(date)) {
            dateForecast = "Amanhã";
        } else if (isYesterday(date)) {
            dateForecast = "Ontem";
        } else {
            dateForecast = formatDateMedium(date);
        }
        return dateForecast;
    }

}
