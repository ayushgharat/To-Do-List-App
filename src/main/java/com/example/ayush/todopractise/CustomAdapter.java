package com.example.ayush.todopractise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by admin on 10/18/2017.
 */

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<Data> list;
    LayoutInflater mLayoutInflator;
    TextView title;
    TextView desc;
    TextView date;
    int i;
    ArrayList<Data> arraylist;
    TextView headerTitle;

    ImageView imageView;

    public CustomAdapter(Context context, List<Data> list, int i){
        this.context=context;
        this.list=list;
        this.i = i;
        mLayoutInflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arraylist = new ArrayList<>();
        arraylist.addAll(list);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = mLayoutInflator.inflate(R.layout.row,null);
        title = (TextView)convertView.findViewById(R.id.textView);
        desc = (TextView)convertView.findViewById(R.id.textView2);
        date = (TextView)convertView.findViewById(R.id.textView3);
        headerTitle = (TextView) convertView.findViewById(R.id.textView4);
        title.setText(list.get(position).getTitle());
        desc.setText(list.get(position).getDescription());
        Date inputDate=null;
        Calendar calendar = Calendar.getInstance();
        String getdate = list.get(position).getDate();
        String parts[] = getdate.split("-");
        String inputDateStr = String.format("%s/%s/%s", parts[0], parts[1],parts[2]);
        //Formats the date
        try {
            inputDate = new SimpleDateFormat("yyyy/MM/dd").parse(inputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(inputDate);
        String day = parts[2];
        String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
        String monthoftheyear = calendar.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.US);
        String year = parts[0];
        final String getfinaldate = dayOfWeek+", "+day+" "+monthoftheyear+" "+year;
        date.setText(getfinaldate);
        headerTitle.setText(day + "/" + monthoftheyear + "/" + year);
        imageView = (ImageView)convertView.findViewById(R.id.thumbsup);
        if(i==1){
            imageView.setImageResource(R.drawable.complete);
            //sets the image as complete if status is 1. Else it keeps the image as incomplete
        }else if(i==0){
            imageView.setImageResource(R.drawable.incomplete);
        }
        return convertView;
    }
}
