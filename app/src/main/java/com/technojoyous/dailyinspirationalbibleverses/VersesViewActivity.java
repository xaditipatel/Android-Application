package com.technojoyous.dailyinspirationalbibleverses;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.BottomNavigationView;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import database.ExternalDataBaseHelper;

public class VersesViewActivity extends AppCompatActivity {

    HashMap<Integer,String> mapVerses;
    private Intent intentVersesCategories;
    private Intent intentnavItem;
    private String tableName="";
    private SQLiteDatabase sqliteDatabase;
    private ExternalDataBaseHelper dbExternalDataBaseHelper;
    private int versesCount = 0, versePosition = 1;
    private Context  myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verses_view);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        intentVersesCategories = new Intent();
        intentnavItem = new Intent();
        tableName = this.getIntent().getExtras().getString("tableName");
        tableName = tableName.substring(0, 1).toLowerCase() + tableName.substring(1);

        myContext=this.getApplicationContext();

        this.setTitle(tableName.substring(0, 1).toUpperCase() + tableName.substring(1));

        dbExternalDataBaseHelper = new ExternalDataBaseHelper(this);
        sqliteDatabase = dbExternalDataBaseHelper.openDataBase();

        mapVerses = new  HashMap<Integer,String>();
        mapVerses = dbExternalDataBaseHelper.fetchAllVerses(tableName);

        versesCount = mapVerses.size();
        setVerse(myContext,versePosition,mapVerses);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    intentnavItem.setClass(getApplicationContext(), VersesCategoriesActivity.class);
                    startActivity(intentnavItem);
                    return true;
                case R.id.navigation_next:
                    int tempVerseNext = versePosition+1;
                    if(tempVerseNext == versesCount){
                        Toast.makeText(myContext, new StringBuilder().append("Last Verse"), Toast.LENGTH_LONG).show();}
                    else {
                        versePosition = tempVerseNext;
                        setVerse(myContext, versePosition, mapVerses);
                    }
                    return true;
                case R.id.navigation_previous:
                    int tempVersePre = versePosition-1;
                    if(tempVersePre == 0){
                        Toast.makeText(myContext, new StringBuilder().append("First Verse"), Toast.LENGTH_LONG).show();}
                    else {
                        versePosition =  tempVersePre;
                        setVerse(myContext, versePosition, mapVerses);
                    }
                    return true;
                 case R.id.navigation_share:
                    TextView tv = (TextView) findViewById(R.id.verseView);
                    String getVerse = tv.getText().toString();
                    shareVerse(getVerse);
                   // Toast.makeText(myContext, new StringBuilder().append("Share Verse"), Toast.LENGTH_LONG).show();
                    return true;
            }
            return false;
        }

    };

    private void setVerse(Context context, int position, HashMap<Integer,String> mapVerses) {

        TextView tv = (TextView) findViewById(R.id.verseView);

        String verse = mapVerses.get(position);
        String verseHeading = verse.split("@")[0];
        String verseInfo = verse.split("@")[1];
        String finalVerse = verseHeading + "\n \n" + verseInfo;
        //Toast.makeText(context, new StringBuilder().append(mapVerses.get(position)),Toast.LENGTH_LONG).show();

        Typeface fontVerse = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");

        SpannableString spannableString = new SpannableString(finalVerse);
        spannableString.setSpan(new CustomTypeFaceSpan("",fontVerse), 0, verseHeading.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, verseHeading.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new CustomTypeFaceSpan("",fontVerse), verseHeading.length()+1, finalVerse.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv.setText(spannableString);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(25);
    }

    private void shareVerse(String text){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        //sharingIntent.putExtra( android.content.Intent.EXTRA_SUBJECT,"Subject Here");

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(sharingIntent, "Share Verse Using"));
    }
}

