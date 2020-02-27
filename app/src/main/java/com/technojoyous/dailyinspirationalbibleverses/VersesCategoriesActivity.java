package com.technojoyous.dailyinspirationalbibleverses;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.util.ArrayList;

import database.ExternalDataBaseHelper;

public class VersesCategoriesActivity extends AppCompatActivity {

    ListView listVersesCategories;
    private ExternalDataBaseHelper dbExternalDataBaseHelper;
    private SQLiteDatabase sqliteDatabase;
    private Intent intentVersesCategories;
    private int totVersesCategories;
    private ArrayList<String> listVersesTables;
    private String verseTableName, dailyVerse, dailyVerseHeading, randomVerseHeading, randomVerse;
    TextView randomVerseHeadingView, randomVerseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verses_categories);

        listVersesCategories= (ListView) findViewById(R.id.versesCategories);
        dbExternalDataBaseHelper = new ExternalDataBaseHelper(this);

        try {
            dbExternalDataBaseHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sqliteDatabase = dbExternalDataBaseHelper.openDataBase();
        displayVersesCategories(sqliteDatabase);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        intentVersesCategories = new Intent();
        listVersesCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                String tableName = (String) parent.getItemAtPosition(position);
                tableName = tableName.substring(0, 1).toLowerCase() + tableName.substring(1);

                intentVersesCategories.setClass(getApplicationContext(), VersesViewActivity.class).putExtra("tableName",tableName);
                startActivity(intentVersesCategories);
            }

        });

        /******************  GET RANDOM VERSE ****************** */
        listVersesTables = dbExternalDataBaseHelper.fetchAllVersesCategories(sqliteDatabase);
        totVersesCategories = listVersesTables.size();

        verseTableName = listVersesTables.get(dbExternalDataBaseHelper.generateRamdomNumber(totVersesCategories));
        Log.d ("Category", verseTableName);
        dailyVerse = dbExternalDataBaseHelper.fetchVerse(verseTableName);
        Log.d ("Category", dailyVerse);

        dailyVerseHeading = "Today's Verse";
        randomVerseHeading = dailyVerse.split("@")[0];
        randomVerse = dailyVerse.split("@")[1];


        randomVerseHeadingView = (TextView) findViewById(R.id.randomVerseHeading);

        SpannableString spannableStringForHeading = new SpannableString(dailyVerseHeading);
        spannableStringForHeading.setSpan(new StyleSpan(Typeface.BOLD), 0, dailyVerseHeading.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        randomVerseHeadingView.setText(spannableStringForHeading);

        randomVerseView = (TextView) findViewById(R.id.randomVerse);
        randomVerseView.setText("\n" + randomVerseHeading + "\n\n" + randomVerse + "\n");
        randomVerseView.setVisibility(View.GONE);
    }

    private void displayVersesCategories(SQLiteDatabase db){

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, dbExternalDataBaseHelper.fetchAllVersesCategories(db));
        listVersesCategories.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alert = new AlertDialog.Builder(VersesCategoriesActivity.this);
        alert.setTitle("RATE US");
        alert.setMessage("Provide feedback and Rate the App! Help us to improve the App");
        alert.setNeutralButton("Exit App",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                dialog.dismiss();
                finish();
            }
        });

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + getPackageName())));
                }

                dialog.dismiss();
                finish();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alert.create();
        alert.show();

    }

    public void toggle_contents(View v){

        if(randomVerseView.isShown()){
            AnimationView.slide_up(this, randomVerseView);
            randomVerseView.setVisibility(View.GONE);
        }
        else{
            randomVerseView.setVisibility(View.VISIBLE);
            AnimationView.slide_down(this, randomVerseView);
        }
    }
}
