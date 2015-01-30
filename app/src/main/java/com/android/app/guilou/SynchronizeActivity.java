package com.android.app.guilou;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class SynchronizeActivity extends ActionBarActivity {

    AlertDialog builder;
    User userPrinc;
    ArrayList<Evenement> event;
    Button syncReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize);

        builder = new AlertDialog.Builder(this).create();
        View v = View.inflate(this, R.layout.layout_progress_bar, null);
        builder.setView(v);
        builder.setTitle("Chargement");
        builder.setCancelable(false);

        Intent i = getIntent();
        userPrinc = (User) i.getSerializableExtra("userPrinc");

        event = new ArrayList<Evenement>();

        Synchronize synchronize = new Synchronize();
        synchronize.execute("" + userPrinc.getId());

        syncReturn = (Button) findViewById(R.id.buttonSynchReturn);
        syncReturn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(SynchronizeActivity.this, MenuActivity.class);
                i.putExtra("userPrinc", userPrinc);
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_synchronize, menu);
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

    public class Synchronize extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {
            try {
                /**
                 * Permet d'afficher le chargement sur le thread principal
                 */
                SynchronizeActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        builder.show();
                    }
                });

                Connection c = Jsoup.connect("http://guilou.orgfree.com/synchronize.php?userId="
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
                Toast.makeText(SynchronizeActivity.this,
                        "Vous n'avez aucun événement à synchroniser",
                        Toast.LENGTH_LONG).show();
            } else{
                try {
                    JSONArray array = new JSONArray(s);

                    for(int i = 0; i < array.length(); i++){
                        JSONObject json = array.getJSONObject(i);

                        int id = Integer.parseInt(json.get("EventId").toString());
                        String nom = json.get("nom").toString();
                        String desc = json.get("description").toString();

                        // Date de création
                        String dateCr = json.get("creationDate").toString().substring(0, 10);
                        String heureCr = json.get("creationDate").toString().substring(11, 16);

                        String[] tabDateCr = dateCr.split("-");
                        String[] tabHeureCr = heureCr.split(":");

                        Date crea = new Date(Integer.parseInt(tabDateCr[0]) - 1900,
                                Integer.parseInt(tabDateCr[1]) - 1, Integer.parseInt(tabDateCr[2]),
                                Integer.parseInt(tabHeureCr[0]), Integer.parseInt(tabHeureCr[1]));


                        //Date de Fin
                        String dateEnd = json.get("endDate").toString().substring(0, 10);
                        String heureEnd = json.get("endDate").toString().substring(11, 16);

                        String[] tabDateEnd = dateEnd.split("-");
                        String[] tabHeureEnd = heureEnd.split(":");

                        Date end = new Date(Integer.parseInt(tabDateEnd[0]) - 1900,
                                Integer.parseInt(tabDateEnd[1]) - 1, Integer.parseInt(tabDateEnd[2]),
                                Integer.parseInt(tabHeureEnd[0]), Integer.parseInt(tabHeureEnd[1]));

                        int creId = Integer.parseInt(json.get("creatorId").toString());


                        event.add(new Evenement(id, nom, desc, crea, end, creId));
                    }

                    int i;
                    for(i = 0; i < event.size(); i++){
                        Intent intent = new Intent(Intent.ACTION_INSERT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra(CalendarContract.Events.TITLE, event.get(i).getNom());
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                event.get(i).creationDate.getTime());
                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                event.get(i).endDate.getTime());
                        intent.putExtra(CalendarContract.Events.ALL_DAY, false);// periodicity
                        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.get(i).desc);

                        startActivity(intent);
                    }

                    builder.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
