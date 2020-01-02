package com.example.pranoy.finalapp;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    public class Database extends SQLiteOpenHelper {


        public static final String DB_NAME = "libraryApp.db";
        public static final int DB_VERSION = 1;
        public static final String TABLE = "branch_table";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String ADDRESS = "address";
        public static final String POSTAL_CODE = "postalCode";
        public static final String PHONE = "telephone";
        public static final String MONDAY = "monday";
        public static final String TUESDAY = "tuesday";
        public static final String WEDNESDAY = "wednesday";
        public static final String THURSDAY = "thursday";
        public static final String FRIDAY = "friday";
        public static final String SATURDAY = "saturday";
        public static final String SUNDAY = "sunday";


        public static final String CREATE_TABLE_BRANCH = "CREATE TABLE " + TABLE +
                "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + NAME + " TEXT," + ADDRESS + " TEXT," + POSTAL_CODE + " TEXT," + PHONE + " TEXT,"
                + MONDAY + " TEXT," + TUESDAY + " TEXT," + WEDNESDAY + " TEXT," + THURSDAY + " TEXT," + FRIDAY + " TEXT," + SATURDAY + " TEXT," + SUNDAY + " TEXT,)";


        public Database(Context context) {
            super(context, DB_NAME, null, DB_VERSION);

            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            sqLiteDatabase.execSQL(CREATE_TABLE_BRANCH);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE);

            onCreate(sqLiteDatabase);
        }

        public boolean insertData(String name, String address, String postal_code, String telephone_number,
                                  String monday, String tuesday, String wednesday, String thursday,
                                  String friday, String saturday, String sunday ){

            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME, name);
            contentValues.put(ADDRESS, address);
            contentValues.put(POSTAL_CODE, postal_code);
            contentValues.put(PHONE, telephone_number);
            contentValues.put(MONDAY, monday);
            contentValues.put(TUESDAY, tuesday);
            contentValues.put(WEDNESDAY, wednesday);
            contentValues.put(THURSDAY, thursday);
            contentValues.put(FRIDAY, friday);
            contentValues.put(SATURDAY, saturday);
            contentValues.put(SUNDAY, sunday);

            long result = sqLiteDatabase.insert(TABLE, null, contentValues);
            if(result == -1){
                return false;
            }else{
                return true;
            }
        }

        public Cursor getAllData(){

            SQLiteDatabase db = this.getWritableDatabase();

            Cursor res = db.rawQuery("SELECT * FROM " + TABLE, null);

            return res;
        }

    }

    private static ArrayList<String> arrayList_names = new ArrayList<>();
    private static ArrayList<String> arrayList_time = new ArrayList<>();
    private static ArrayList<Integer> arrayList_ID = new ArrayList<>();
    private Database database;


    public ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView)findViewById(R.id.listView);
        database = new Database(this);
        Cursor cursor;
        cursor = database.getAllData();
        readCSV();

        while(cursor.moveToNext()){
            String name = cursor.getString(1);
            int id = Integer.parseInt(cursor.getString(0));
            String hours = "";
            if (GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.MONDAY) hours = cursor.getString(5);
            else if (GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.TUESDAY) hours = cursor.getString(6);
            else if (GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.WEDNESDAY) hours = cursor.getString(7);
            else if (GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.THURSDAY) hours = cursor.getString(8);
            else if (GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.FRIDAY) hours = cursor.getString(9);
            else if (GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY)  hours = cursor.getString(10);
            else hours = cursor.getString(11);
            arrayList_names.add(name);
            arrayList_time.add(hours);
            arrayList_ID.add(id);
        }


        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, arrayList_names);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text = (String)adapterView.getItemAtPosition(i);
                itemSelected(i, text);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                adapter.getFilter().filter(s);

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }


    public void readCSV(){

        InputStream is = getResources().openRawResource(R.raw.branch_general_profile);
        InputStream is2 = getResources().openRawResource(R.raw.hours_of_operation);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(is2, Charset.forName("UTF-8")));
        String line = "";
        String line2 = "";

        try {

            bufferedReader.readLine();
            reader2.readLine();

            while ((line = bufferedReader.readLine()) != null && (line2 = reader2.readLine()) != null){


                String[] tokens = line.split(",");
                String[] tokens2 = line2.split(",");

                database.insertData(tokens[0], tokens[1], tokens[3],
                        tokens[2], tokens2[1], tokens2[2], tokens2[3],
                        tokens2[4], tokens2[5], tokens2[6], tokens2[7]);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void itemSelected(int position, String name){

        for(int i = 0; i < arrayList_names.size(); i++){
            if(arrayList_names.get(i).contains(name)){
                position = i;
                break;
            }
        }

        String query = "SELECT * FROM branch_table WHERE id = " + arrayList_ID.get(position).toString();
        SQLiteDatabase sqLiteDatabase = database.getReadableDatabase();
        Cursor cur = sqLiteDatabase.rawQuery(query, null);
        ArrayList<String> arrayList = new ArrayList<>();
        if (cur.moveToFirst()){
            do{
                arrayList.add(cur.getString(cur.getColumnIndex("name")));
                arrayList.add(cur.getString(cur.getColumnIndex("address")));
                arrayList.add(cur.getString(cur.getColumnIndex("telephone")));
                arrayList.add(cur.getString(cur.getColumnIndex("postalCode")));
                arrayList.add(cur.getString(cur.getColumnIndex("monday")));
                arrayList.add(cur.getString(cur.getColumnIndex("tuesday")));
                arrayList.add(cur.getString(cur.getColumnIndex("wednesday")));
                arrayList.add(cur.getString(cur.getColumnIndex("thursday")));
                arrayList.add(cur.getString(cur.getColumnIndex("friday")));
                arrayList.add(cur.getString(cur.getColumnIndex("saturday")));
                arrayList.add(cur.getString(cur.getColumnIndex("sunday")));
                arrayList.add(cur.getString(cur.getColumnIndex("id")));
            }while(cur.moveToNext());
        }
        Intent intent = new Intent(this, ValueSetters.class);
        intent.putExtra("data", arrayList);
        startActivity(intent);
    }

}