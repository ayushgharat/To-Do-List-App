package com.example.ayush.todopractise;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ayush on 10/18/2017.
 */

public class Main2Activity extends AppCompatActivity {

    ListView listView;
    ArrayList<Data> list;
    myDatabase database;
    CustomAdapter customAdapter;
    LinearLayout linearLayout;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.completedtask_list);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        listView = (ListView)findViewById(R.id.listview);
        list = new ArrayList<>();
        database=new myDatabase(this);
        linearLayout = (LinearLayout)findViewById(R.id.linearlayout);
        list = database.getcompletedtaskdata();

        if(database.completedrowcount()==0){
            TextView textView = new TextView(Main2Activity.this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    //select linearlayoutparam- set the width & height
                    ViewGroup.LayoutParams.MATCH_PARENT, 48));

            textView.setGravity(Gravity.CENTER);
            textView.setText(R.string.no_complete_task);
            //If there aren't any completed tasks, then a textView is dynamically created which tells the user that there are no completed tasks
            linearLayout.addView(textView);
        }
        customAdapter=new CustomAdapter(this,list,1);
        listView.setAdapter(customAdapter);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                //creates a dialog similair to the one in the main activity, instead it allows the task to be set as incomplete
                builder.setTitle(R.string.dialog_title).setMessage("Do you wish to set the task as completed or delete the task")
                        .setPositiveButton("Set as incomplete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.changestatus(list.get(position).getId());
                        Toast.makeText(Main2Activity.this,"Task set as incomplete",Toast.LENGTH_SHORT);
                        refreshList(0);
                    }
                }).setNegativeButton("Delete Task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean status = database.deletetask(list.get(position).getId());
                        if(status){
                            Toast.makeText(Main2Activity.this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
                            refreshList(0);
                            if(database.completedrowcount()==0){
                                //TODO: add image also here
                                TextView textView = new TextView(Main2Activity.this);
                                textView.setLayoutParams(new LinearLayout.LayoutParams(             //select linearlayoutparam- set the width & height
                                        ViewGroup.LayoutParams.MATCH_PARENT, 48));
                                textView.setGravity(Gravity.CENTER_VERTICAL);
                                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                                textView.setText("No completed tasks");
                                linearLayout.addView(textView);

                            }
                        }else{
                            Toast.makeText(Main2Activity.this, R.string.error_in_deleting_task, Toast.LENGTH_SHORT).show();
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
        menuInflater.inflate(R.menu.complete_menu,menu);
        //inflates menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.settings){
            Intent intent = new Intent(getApplicationContext(),Settings.class);
            startActivity(intent);
            //allows user to go to the settings page
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshList(int a){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //getting values from shared preference
        String sortorder = sharedPreferences.getString("sort","New Tasks first");
        list = database.getcompletedtaskdata();
        customAdapter=new CustomAdapter(this,list,a);
        listView.setAdapter(customAdapter);
    }
}
