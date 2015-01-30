package com.android.app.guilou;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.Calendar;
import java.util.Date;


public class AddEventActivity extends ActionBarActivity {

    AlertDialog builder;
    EditText editName;
    EditText editDescr;
    DatePicker dateBegin;
    DatePicker dateEnd;

    TimePicker timeBegin;
    TimePicker timeEnd;

    Button btnConfirm;

    User userPrinc;

    public class AddEvent extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {
            try {
                /**
                 * Permet d'afficher le chargement sur le thread principal
                 */
                AddEventActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        builder.show();
                    }
                });

                Connection c = Jsoup.connect("http://guilou.orgfree.com/addEvent.php?name="
                        + params[0] + "&desc=" + params[1] + "&creaD=" +
                        params[2] + "&endD=" + params[3] +
                        "&creatorId=" + params[4]);

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
            if (s.equals("true")){     // On ajoute
                Toast.makeText(AddEventActivity.this,
                        "Les informations sont correct, l\'événement a été ajouté à la base de données",
                        Toast.LENGTH_LONG).show();

                Intent i = new Intent(AddEventActivity.this, EventMenuActivity.class);
                i.putExtra("userPrinc", userPrinc);
                startActivity(i);
            } else {
                builder.dismiss();
                Toast.makeText(AddEventActivity.this,
                        "Il y a eu une erreur, veuillez essayer ultérieurement",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Intent i = getIntent();
        userPrinc = (User) i.getSerializableExtra("userPrinc");

        builder = new AlertDialog.Builder(this).create();
        View v = View.inflate(this, R.layout.layout_progress_bar, null);
        builder.setView(v);
        builder.setTitle("Chargement");
        builder.setCancelable(false);

        //editBeginDate = (EditText) findViewById(R.id.editEventBeginDate);
        dateBegin = (DatePicker) findViewById(R.id.dpBegin);
        dateEnd = (DatePicker) findViewById(R.id.dpEnd);

        timeBegin = (TimePicker) findViewById(R.id.timePickerBegin);
        timeEnd = (TimePicker) findViewById(R.id.timePickerEnd);

        timeBegin.setIs24HourView(true);
        timeBegin.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        timeEnd.setIs24HourView(true);
        timeEnd.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        btnConfirm = (Button) findViewById(R.id.buttonAddEventConfirm);

        editName = (EditText) findViewById(R.id.editEventName);
        editDescr = (EditText) findViewById(R.id.editEventDescription);

        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String name = editName.getText().toString();
                String descr = editDescr.getText().toString();

                int dayB = dateBegin.getDayOfMonth();
                int monthB = dateBegin.getMonth();
                int yearB = dateBegin.getYear();

                int hourB = timeBegin.getCurrentHour();
                int minuteB = timeBegin.getCurrentMinute();

                int dayE = dateEnd.getDayOfMonth();
                int monthE = dateEnd.getMonth();
                int yearE = dateEnd.getYear();

                int hourE = timeEnd.getCurrentHour();
                int minuteE = timeEnd.getCurrentMinute();

                Date dateB = new Date(yearB, monthB, dayB, hourB, minuteB);
                Date dateE = new Date(yearE, monthE, dayE, hourE, minuteE);

                monthB = monthB + 1;
                monthE = monthE + 1;

                if(dateB.before(dateE)){
                    if(name.contains("\'")) {
                        name = checkSimpleQuote(name);
                    }

                    if(descr.contains("\'")) {
                        descr = checkSimpleQuote(descr);
                    }

                    AddEvent result = new AddEvent();
                    String dateResultB = yearB + "-" + monthB + "-" + dayB + " " + hourB
                            + ":" + minuteB;
                    String dateResultE = yearE + "-" + monthE + "-" + dayE + " " + hourE
                            + ":" + minuteE;
                    result.execute(name, descr, dateResultB, dateResultE, "" + userPrinc.getId());
                } else {
                    Toast.makeText(AddEventActivity.this,
                            "La date de fin est inférieur à la date de début !!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_event, menu);
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

    public String checkSimpleQuote(String s){
        String start;
        String end;
        String res = s;

        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) == '\''){
                start = s.substring(0, i);
                end = s.substring(i, s.length());
                res = start + "\\" + end;
            }
        }

        return res;
    }

}
