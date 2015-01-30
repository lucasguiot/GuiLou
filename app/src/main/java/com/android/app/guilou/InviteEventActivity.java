package com.android.app.guilou;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;


public class InviteEventActivity extends ActionBarActivity {

    AlertDialog builder;
    private ListView mainFriendListView;
    private ArrayAdapter<String> listAdapter ;
    ListAdapter adapt;
    ArrayList<User> ami;
    ArrayList<Evenement> event;
    ArrayList<String> eventName;
    User userPrinc;
    Button btnConfirm;
    String participants = "";
    int compteurNBPart;
    Spinner spinnerEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_event);

        Intent i = getIntent();
        userPrinc = (User) i.getSerializableExtra("userPrinc");

        event = new ArrayList<Evenement>();
        eventName = new ArrayList<String>();
        spinnerEvent = (Spinner) findViewById(R.id.spinnerEvent);
        btnConfirm = (Button) findViewById(R.id.buttonInviteConfirm);

        builder = new AlertDialog.Builder(this).create();
        View v = View.inflate(this, R.layout.layout_progress_bar, null);
        builder.setView(v);
        builder.setTitle("Chargement");
        builder.setCancelable(false);

        mainFriendListView = (ListView) findViewById( R.id.mainFriendListView );
        ami = new ArrayList<>();
        adapt = new ListAdapter(InviteEventActivity.this, R.layout.simplerow, ami);

        GetEventByCreator getEvent = new GetEventByCreator();
        getEvent.execute("" + userPrinc.getId());

        //listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow);
        GetAmi getAmi = new GetAmi();
        getAmi.execute("" + userPrinc.getId());

        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                compteurNBPart = 0;

                for (int i = 0; i < mainFriendListView.getChildCount(); i++) {
                    mainFriendListView.getItemAtPosition(i).toString();
                    CheckBox check = (CheckBox) mainFriendListView.getChildAt(i).findViewById(R.id.checkInvite);
                    check.getText();

                    if (check.isChecked()) {
                        User u = (User) mainFriendListView.getItemAtPosition(i);
                        participants += u.getId() + ", ";
                        compteurNBPart++;
                    }
                }

                if(participants.length() > 0) {
                    int eventId = 0;
                    participants = participants.substring(0, participants.length() - 2);

                    for(int i = 0; i < event.size(); i++){
                        if(event.get(i).getNom().equals(spinnerEvent.getSelectedItem().toString())){
                            eventId = event.get(i).getEventId();
                        }
                    }

                    AddParticipant add = new AddParticipant();
                    add.execute("" + compteurNBPart, participants, "" + eventId);
                } else {
                    Toast.makeText(InviteEventActivity.this,
                            "Veuillez sélectionner un participant au moins",
                            Toast.LENGTH_LONG).show();
                }

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invite_event, menu);
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

    public class ListAdapter extends ArrayAdapter<User> {

        public ListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public ListAdapter(Context context, int resource, List<User> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {

                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.simplerow, null);

            }

            User p = getItem(position);

            if (p != null) {

                TextView tt = (TextView) v.findViewById(R.id.rowTextView);

                if (tt != null) {
                    tt.setText(p.getLogin());
                }

            }

            return v;

        }
    }


    public class GetAmi extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {
            try {
                /**
                 * Permet d'afficher le chargement sur le thread principal
                 */
                InviteEventActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        builder.show();
                    }
                });

                Connection c = Jsoup.connect("http://guilou.orgfree.com/getAmi.php?id="
                        + params[0]);

                c.timeout(10000);
                String resultat = c.get().getElementsByClass("resultat").html();
                return resultat;
            }
            catch (Exception e)
            {
                return "Erreur : " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            if(s.equals("null")){
                builder.dismiss();
                Toast.makeText(InviteEventActivity.this,
                        "Vous n'avez aucun ami",
                        Toast.LENGTH_LONG).show();
            } else{
                try {
                    JSONArray array = new JSONArray(s);
                    for(int i = 0; i < array.length(); i++){
                        JSONObject json = array.getJSONObject(i);

                        User u = new User(Integer.parseInt(json.get("amiId").toString()),
                                json.get("login").toString());

                        ami.add(i, u);
                    }

                    mainFriendListView.setAdapter(adapt);


                    builder.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }



    public class AddParticipant extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {
            try {
                /**
                 * Permet d'afficher le chargement sur le thread principal
                 */
                InviteEventActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        builder.show();
                    }
                });

                Connection c = Jsoup.connect("http://guilou.orgfree.com/addParticipant.php?nbPart="
                        + params[0] + "&userId=" + params[1] + "&eventId=" + params[2]);

                c.timeout(10000);
                String resultat = c.get().getElementsByClass("resultat").html();
                return resultat;
            }
            catch (Exception e)
            {
                return "Erreur : " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            builder.dismiss();

            Intent i = new Intent(InviteEventActivity.this, EventMenuActivity.class);
            i.putExtra("userPrinc", userPrinc);
            startActivity(i);
        }
    }

    public class GetEventByCreator extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {
            try {
                /**
                 * Permet d'afficher le chargement sur le thread principal
                 */
                InviteEventActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        builder.show();
                    }
                });

                Connection c = Jsoup.connect("http://guilou.orgfree.com/getEventByCreator.php?creatorId="
                        + params[0]);

                c.timeout(10000);
                String resultat = c.get().getElementsByClass("resultat").html();
                return resultat;
            }
            catch (Exception e)
            {
                return "Erreur : " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            if(s.equals("null")){
                builder.dismiss();
                Toast.makeText(InviteEventActivity.this,
                        "Vous n'avez crée aucun événement",
                        Toast.LENGTH_LONG).show();
            } else{
                try {
                    JSONArray array = new JSONArray(s);
                    JSONObject json = array.getJSONObject(0);

                    for(int i = 0; i < array.length(); i++){
                        JSONObject j = array.getJSONObject(i);
                        event.add(new Evenement(Integer.parseInt(j.get("EventId").toString()),
                                j.get("nom").toString(),
                                Integer.parseInt(j.get("creatorId").toString())));
                        eventName.add(j.get("nom").toString());
                    }

                    spinnerEvent.setAdapter(new ArrayAdapter<String>(InviteEventActivity.this,
                            android.R.layout.simple_spinner_item, eventName));

                    builder.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
