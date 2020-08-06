package group17.cmpt276.iteration3.Model;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import group17.cmpt276.iteration3.R;

/**
 * Inspection Date is a class that models the dates used in the program
 * includes features to output the date in correct format based on how long ago the inspection occurred
 * includes both update dates and inspection dates
 */
public class Date {

    private static final String TAG = "Inspection Date";
    private int year;
    private int month;
    private int day;
    private int hour, minute, seconds;
    private List<String> months = new ArrayList<>();

    public Date(int year, int month, int day, Context context) {
        this.year = year;
        this.month = month;
        this.day = day;
        setMonth(context);
    }

    //create a new date from a string (used for the downloads)
    public Date(String dateTimeAsString){
        this.year = Integer.parseInt(dateTimeAsString.substring(0,4));
        this.month = Integer.parseInt(dateTimeAsString.substring(5,7));
        this.day = Integer.parseInt(dateTimeAsString.substring(8,10));
        this.hour = Integer.parseInt(dateTimeAsString.substring(11,13));
        this.minute = Integer.parseInt(dateTimeAsString.substring(14,16));
        this.seconds = Integer.parseInt(dateTimeAsString.substring(17,19));
    }

    //return the current date as a string
    public String getCurrentDateAsString(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime currDate = LocalDateTime.now();
        return  dtf.format(currDate);
    }

    public static String getCurrentDateAsString(String date){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime currDate = LocalDateTime.now();
        return dtf.format(currDate);
    }

    //builds a string to save a date object
    public String getSaveTag(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.year).append('-');
        if(this.month/10 == 0){
            stringBuilder.append('0').append(this.month).append('-');
        }
        else{
            stringBuilder.append(this.month).append('-');
        }
        if(this.day/10 == 0){
            stringBuilder.append('0').append(this.day).append('T');
        }
        else{
            stringBuilder.append(this.day).append('T');
        }
        if(this.hour/10 == 0){
            stringBuilder.append('0').append(this.hour).append(':');
        }
        else{
            stringBuilder.append(this.hour).append(':');
        }
        if(this.minute/10 == 0){
            stringBuilder.append('0').append(this.minute).append(':');
        }
        else{
            stringBuilder.append(this.minute).append(':');
        }
        if(this.seconds/10 == 0){
            stringBuilder.append('0').append(this.seconds);
        }
        else{
            stringBuilder.append(this.seconds);
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "InspectionDate{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", seconds=" + seconds +
                '}';
    }

    private void setMonth(Context context){
        Resources res = context.getResources();

        months.add(res.getString(R.string.month_jan));
        months.add(res.getString(R.string.month_feb));
        months.add(res.getString(R.string.month_mar));
        months.add(res.getString(R.string.month_apr));
        months.add(res.getString(R.string.month_may));
        months.add(res.getString(R.string.month_jun));
        months.add(res.getString(R.string.month_jul));
        months.add(res.getString(R.string.month_aug));
        months.add(res.getString(R.string.month_sep));
        months.add(res.getString(R.string.month_oct));
        months.add(res.getString(R.string.month_nov));
        months.add(res.getString(R.string.month_dec));
    }

    //make a date object a string for printing to ui
    public String makeActivityString(Context context){
        Resources res = context.getResources();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime currDate = LocalDateTime.now();
        int days = currDate.getDayOfMonth() - this.day +
                (currDate.getMonthValue() - this.month)*30 +
                (currDate.getYear() - this.year)*365;
        String str;
        if(days <= 30){
            str = days + res.getString(R.string.days_ago);
        }
        else if(days <= 365){
            str = months.get(this.month - 1) + " " + this.day;
        }
        else{
            str = months.get(this.month - 1) +" " + this.year;
        }
        return str;
    }
    public String getFullDate(){
        return months.get(getMonth()-1) +" "+ getDay() +", "+getYear();
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSeconds() {
        return seconds;
    }
}




