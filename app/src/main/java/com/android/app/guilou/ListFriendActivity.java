package com.android.app.guilou;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.ArrayList;


public class ListFriendActivity extends ActionBarActivity {

    AlertDialog builder;
    private ListView mainFriendListView;
    private ArrayAdapter<String> listAdapter;
    ArrayList<User> ami;
    User userPrinc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_friend);

        builder = new AlertDialog.Builder(this).create();
        View v = View.inflate(this, R.layout.layout_progress_bar, null);
        builder.setView(v);
        builder.setTitle("Chargement");
        builder.setCancelable(false);

        Intent i = getIntent();
        userPrinc = (User) i.getSerializableExtra("userPrinc");

        mainFriendListView = (ListView) findViewById( R.id.mainFriendListView );
        listAdapter = new ArrayAdapter<String>(this, R.layout.simple_row_list_friend);
        GetAmi getAmi = new GetAmi();
        getAmi.execute("" + userPrinc.getId());
        ami = new ArrayList<>();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_friend, menu);
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

    public class GetAmi extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {
            try {
                /**
                 * Permet d'afficher le chargement sur le thread principal
                 */
                ListFriendActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        builder.show();
                    }
                });

                Connection c = Jsoup.connect("http://guilou.orgfree.com/getAmi.php?id="
                        + params[0]); //+ "&event=" + params[1]);

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
                Toast.makeText(ListFriendActivity.this,
                        "Vous n'avez aucun ami",
                        Toast.LENGTH_LONG).show();
            } else{
                try {
                    JSONArray array = new JSONArray(s);
                    for(int i = 0; i < array.length(); i++){
                        JSONObject json = array.getJSONObject(i);

                        /*Toast.makeText(ListFriendActivity.this,
                                "Voici : " + json.get("amiId") + " " + json.get("login"),
                                Toast.LENGTH_LONG).show();*/

                        User u = new User(Integer.parseInt(json.get("amiId").toString()),
                                json.get("login").toString());

                        ami.add(i, u);
                        listAdapter.add(u.getLogin());
                    }

                    mainFriendListView.setAdapter(listAdapter);

                    builder.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
