package com.example.ayush.todopractise;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    myDatabase database;
    ListView listview;
    DatePicker date;
    LinearLayout linearLayout;
    ArrayList<Data> list;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listview = (ListView)findViewById(R.id.listview);
        linearLayout = (LinearLayout)findViewById(R.id.linearlayout1);
        list = new ArrayList<>();
        database=new myDatabase(this);
        refreshList(0);
        if(database.rowcount()==0){

            Toast.makeText(this, R.string.no_data_to_display, Toast.LENGTH_SHORT).show();
            //If user has not entered any tasks, then a toast is produced
        }



        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Data data = list.get(position);
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_populate);
                dialog.show();
                // A dialog is populated on selection of the item where the user can edit the task.
                final EditText title = (EditText)dialog.findViewById(R.id.populated_title);
                final EditText description = (EditText)dialog.findViewById(R.id.populated_description);
                final EditText date = (EditText)dialog.findViewById(R.id.populate_date);
                Button cancel = (Button)dialog.findViewById(R.id.cancel);
                Button update = (Button)dialog.findViewById(R.id.update);
                title.setText(data.getTitle());
                description.setText(data.getDescription());
                date.setText(data.getDate());
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                //On click of the cancel button, the dialog is dismissed
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newtitle = title.getText().toString().trim();
                        String newdesc = description.getText().toString().trim();
                        String newdate = date.getText().toString().trim();

                        boolean status = database.updatedata(newtitle,newdesc,newdate,list.get(position).getId());
                        dialog.dismiss();
                        refreshList(2);
                        if(!status){
                            Toast.makeText(MainActivity.this, R.string.cant_update_task, Toast.LENGTH_SHORT).show();
                            //updates the task. If the method returns false, then a toast is created which lets the user know that the task hasn't been updated.
                        }
                    }
                });


            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //When the user long-clicks on an item, an AlertDialog is created which asks the user whether he wants to set the task as complete or delete the task
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_title).setMessage("Do you wish to set the task as completed or delete the task").setPositiveButton("Set as complete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.changestatus(list.get(position).getId());
                        Toast.makeText(MainActivity.this,"Task set as complete",Toast.LENGTH_SHORT);
                        //The respective task is set as complete
                        refreshList(0);
                    }
                }).setNegativeButton("Delete Task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean status = database.deletetask(list.get(position).getId());
                        if(status){
                            Toast.makeText(MainActivity.this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
                            refreshList(0);
                            //If the task has been deleted, a toast is shown which notifies the user
                        }else{
                            Toast.makeText(MainActivity.this, R.string.error_in_deleting_task, Toast.LENGTH_SHORT).show();
                            //in case there was an error in deleting th task, the respective toast notifies te user
                        }
                    }
                }).show();
                return true;

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        //inflates the menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.settings){
            Intent intent = new Intent(getApplicationContext(),Settings.class);
            startActivity(intent);
            //takes the user to the settings activity
        }
        if(id==R.id.add){
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog);
            dialog.setTitle(R.string.Add_Task);
            dialog.show();
            // A dialog is popluted on press of the add button which allows the user to enter a new task
            final EditText title = (EditText)dialog.findViewById(R.id.title);
            final EditText description = (EditText)dialog.findViewById(R.id.desc);
            date = (DatePicker)dialog.findViewById(R.id.datepicker);
            final Calendar calendar = Calendar.getInstance();
            Button save = (Button)dialog.findViewById(R.id.save);
            Button cancel = (Button)dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String gettitle = title.getText().toString().trim();
                    String getdesc = description.getText().toString().trim();
                    int day = date.getDayOfMonth();
                    int month = date.getMonth()+1;
                    int year = date.getYear();
                    if(gettitle.isEmpty()){
                        title.setError("Title can not be empty. Enter Title First.");
                        //In case the title is empty...
                    }
                    if(gettitle.length()>=15){
                        title.setError("Title should be less than 15 characters");
                        //In case the title is more than 15 characters...
                    }
                    if(getdesc.length()>=40){
                        description.setError("Description should be less than 40 characters");
                        //In case the description is more than 40 characters
                    }
                    String inputDateStr = String.format("%s/%s/%s", day, month, year);
                    Date inputDate = null;
                    try {
                        inputDate = new SimpleDateFormat("yyyy/MM/dd").parse(inputDateStr);
                        //formats the date to be added to the textView
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    calendar.setTime(inputDate);
                    final String getfinaldate = year+"-"+month+"-"+day;
                    if(gettitle.length()<20 && getdesc.length()<40 && !gettitle.isEmpty()){
                        boolean r=database.insert_Data(gettitle,getdesc,getfinaldate);
                        if(r==true)
                        {
                            Toast.makeText(MainActivity.this, R.string.task_inserted, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            refreshList(2);
                            //inserts the data into the database
                        }
                        else {
                            Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }
        if(id==R.id.complete){
            Intent intent = new Intent(MainActivity.this,Main2Activity.class);
            startActivity(intent);
            //takes the user to the activity in which all completed tasks are displayed
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList(0);
        //when the user returns from another activity
    }

    public void refreshList(int a){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //getting values from shared preference
        String sortorder = sharedPreferences.getString("sort","New Tasks first");
        list = database.getAlldata(sortorder);
        customAdapter=new CustomAdapter(this,list,a);
        listview.setAdapter(customAdapter);
        //repopulates the listview if there has been any changes
    }

}
