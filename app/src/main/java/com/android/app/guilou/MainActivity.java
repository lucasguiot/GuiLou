package com.android.app.guilou;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;


public class MainActivity extends ActionBarActivity {

    AlertDialog builder;
    EditText editUserName;
    EditText editPassword;
    TextView textAddUserClick;
    Button btnConfirm;
    Encrypt encrypt;

    public class GetUser extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {
            try {
                /**
                 * Permet d'afficher le chargement sur le thread principal
                 */
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        builder.show();
                    }
                });

                Connection c = Jsoup.connect("http://guilou.orgfree.com/connection.php?login="
                        + params[0] + "&mdp=" + params[1]); //+ "&event=" + params[1]);
                /*Toast.makeText(MainActivity.this, "login="
                        + params[0] + "&mdp=" + params[1], Toast.LENGTH_LONG).show();*/

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
                Toast.makeText(MainActivity.this,
                        "Le nom d'utilisateur et/ou le mot de passe ne sont pas correct",
                        Toast.LENGTH_LONG).show();
            } else{
                try {
                    JSONArray array = new JSONArray(s);
                    JSONObject json = array.getJSONObject(0);

                    if(json.get("login").equals(editUserName.getText().toString().trim())
                        && json.get("mdp").equals(
                            encrypt.encryptPassword(editPassword.getText().toString()))){

                        Toast.makeText(MainActivity.this,
                                "Mot de passe valide",
                                Toast.LENGTH_LONG).show();

                        builder.dismiss();

                        User userPrinc = new User(Integer.parseInt(json.get("userId").toString()),
                                json.get("login").toString());

                        Intent i = new Intent(MainActivity.this, MenuActivity.class);
                        i.putExtra("userPrinc", userPrinc);
                        startActivity(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        builder = new AlertDialog.Builder(this).create();
        View v = View.inflate(this, R.layout.layout_progress_bar, null);
        builder.setView(v);
        builder.setTitle("Chargement");
        builder.setCancelable(false);

        btnConfirm = (Button) findViewById(R.id.buttonConfirm);
        editUserName = (EditText) findViewById(R.id.editUserName);
        editPassword = (EditText) findViewById(R.id.editPassword);

        textAddUserClick = (TextView) findViewById(R.id.textAddUserClick);

        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editUserName.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editPassword.getWindowToken(), 0);

                String user = editUserName.getText().toString();
                String password = editPassword.getText().toString();

                if(user.isEmpty() && password.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Veuillez saisir les informations de connection",
                            Toast.LENGTH_LONG).show();
                } else if(user.isEmpty()){
                    Toast.makeText(MainActivity.this,
                            "Veuillez saisir une information pour le nom d'utilisateur",
                            Toast.LENGTH_LONG).show();
                } else if(password.isEmpty()){
                    Toast.makeText(MainActivity.this,
                            "Veuillez saisir une information pour le mot de passe",
                            Toast.LENGTH_LONG).show();
                } else {
                    encrypt = new Encrypt();
                    GetUser result = new GetUser();
                    result.execute(user, encrypt.encryptPassword(password));
                }

            }
        });

        textAddUserClick.setClickable(true);
        textAddUserClick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddUserActivity.class);
                startActivity(i);
            }
        });


        //JSONObject json = new JSONObject(resultat);

        //String res = AppManager.getUser(1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
