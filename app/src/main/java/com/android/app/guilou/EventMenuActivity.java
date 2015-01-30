package com.android.app.guilou;

//import android.content.Intent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

//import android.view.View;


public class EventMenuActivity extends ActionBarActivity {

    Button btnAddEvent;
    Button btnListEvent;
    Button btnInviteEvent;
    User userPrinc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_menu);

        Intent i = getIntent();
        userPrinc = (User) i.getSerializableExtra("userPrinc");

        btnAddEvent = (Button) findViewById(R.id.buttonAddEvent);
        btnListEvent = (Button) findViewById(R.id.buttonSynchronizeEvent);
        btnInviteEvent = (Button) findViewById(R.id.buttonInviteEvent);

        btnAddEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventMenuActivity.this, AddEventActivity.class);
                i.putExtra("userPrinc", userPrinc);
                startActivity(i);
            }
        });

        btnListEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventMenuActivity.this, SynchronizeActivity.class);
                i.putExtra("userPrinc", userPrinc);
                startActivity(i);
            }
        });

        btnInviteEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventMenuActivity.this, InviteEventActivity.class);
                i.putExtra("userPrinc", userPrinc);
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
