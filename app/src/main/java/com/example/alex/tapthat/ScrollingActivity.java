package com.example.alex.tapthat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ScrollingActivity extends AppCompatActivity {

    private void callNumber(String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    public void updateCards() {
        //Loop for 5 items
        //setContentView(R.layout.content_scrolling);
        LinearLayout layoutHolder = getWindow().getDecorView().findViewById(R.id.layout);
        /*for (int i = 0; i < 5; i++) {
            Button button = new Button(this);//Creating Button
            button.setId(i);//Setting Id for using in future
            button.setText("Item " + i);
            button.setTextSize(15);
            button.setPadding(5, 5, 5, 5);//paading
            layoutHolder.addView(button);
            TextView tv=new TextView(this);
            tv.setText("Here we go!");
            layoutHolder.addView(tv);
            final int id = button.getId();
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),
                            "Button clicked index = " + id, Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }*/

        Contact[] contacts = Contact.getAll(FileSystem.readFromFile(MainActivity.mainContext)); //TODO: DON'T MAKE DISPLAYS FOR BLANK FIELDS
        for (int i = 0; i < contacts.length; i++) {
            TextView name = new TextView(this);
            name.setText(String.format("%s, %s", contacts[i].getLastName(), contacts[i].getFirstName()));
            name.setTextSize(25);
            Button call = new Button(this);
            call.setText("Call phone");
            call.setTextSize(15);
            call.setPadding(10,5,10,5);
            final String number = contacts[i].getPhone();
            call.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    callNumber(number);
                }
            });
            Button linkedIn = new Button(this);
            linkedIn.setText("Visit LinkedIn");
            linkedIn.setTextSize(15);
            linkedIn.setPadding(10,5,10,5);
            final String linkedInURL = contacts[i].getLinkedIn();
            linkedIn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(linkedInURL));
                    startActivity(browserIntent);
                }
            });
            Button email = new Button(this);
            email.setText("Send Email");
            email.setTextSize(15);
            email.setPadding(10,5,10,5);
            final String emailDestination = contacts[i].getEmail();
            email.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                    /* Fill it with Data */
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{emailDestination});

                    /* Send it off to the Activity-Chooser */
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
            });
            Button delete = new Button(this);
            delete.setText("Delete Contact");
            delete.setTextSize(15);
            delete.setPadding(10,5,10,5);
            final String deviceId = contacts[i].getDeviceId();
            delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    FileSystem.deleteContact(deviceId, MainActivity.mainContext);
                    startActivity(new Intent(MainActivity.mainContext, ScrollingActivity.class));
                    finish();
                }
            });
            layoutHolder.addView(name);
            layoutHolder.addView(call);
            layoutHolder.addView(linkedIn);
            layoutHolder.addView(email);
            layoutHolder.addView(delete);
        }

        //setContentView(R.layout.activity_scrolling);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MainActivity.windowOpen = true;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        updateCards();

        Button exit = findViewById(R.id.exit);

        exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.windowOpen = false;
                finish();
            }
        });

        //Toast.makeText(this, ActivityCache.data, Toast.LENGTH_LONG).show();

        //setContentView(R.layout.content_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /** fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         **/
    }
}
