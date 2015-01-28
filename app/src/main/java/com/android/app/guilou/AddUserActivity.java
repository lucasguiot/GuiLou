package com.android.app.guilou;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;


public class AddUserActivity extends ActionBarActivity {

    AlertDialog builder;
    EditText editAddUser;
    EditText editAddPassword;
    EditText editAddPasswordC;
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
                AddUserActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        builder.show();
                    }
                });

                Connection c = Jsoup.connect("http://guilou.orgfree.com/addUser.php?login="
                        + params[0] + "&mdp=" + params[1]); //+ "&event=" + params[1]);
                /*Toast.makeText(MainActivity.this, "login="
                        + params[0] + "&mdp=" + params[1], Toast.LENGTH_LONG).show();*/

                c.timeout(10000);
                String resultat = c.get().getElementsByClass("resultat").html();
                return resultat;
            }
            catch (Exception e)
            {
                Toast.makeText(AddUserActivity.this,
                        e.getMessage(),
                        Toast.LENGTH_LONG).show();
                return "Erreur : " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            if(s.equals("null")){   // Erreur
                builder.dismiss();
                Toast.makeText(AddUserActivity.this,
                        "Il y a eu une erreur, veuillez essayer ultérieurement",
                        Toast.LENGTH_LONG).show();
            } else if(s.equals("false")) {    // Existe deja
                builder.dismiss();
                Toast.makeText(AddUserActivity.this,
                        "L'utilisateur " + editAddUser.getText().toString() +
                                " existe déjà. Veuillez changer de nom d'utilisateur",
                        Toast.LENGTH_LONG).show();
            } else{     // On ajoute
                Toast.makeText(AddUserActivity.this,
                        "L'identifiant est correct. Il a bien été ajouté à la base de données",
                        Toast.LENGTH_LONG).show();

                Intent i = new Intent(AddUserActivity.this, MainActivity.class);
                startActivity(i);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        builder = new AlertDialog.Builder(this).create();
        View v = View.inflate(this, R.layout.layout_progress_bar, null);
        builder.setView(v);
        builder.setTitle("Chargement");
        builder.setCancelable(false);

        btnConfirm = (Button) findViewById(R.id.buttonAddUserConfirm);
        editAddUser = (EditText) findViewById(R.id.editAddUser);
        editAddPassword = (EditText) findViewById(R.id.editAddPassword);
        editAddPasswordC = (EditText) findViewById(R.id.editAddPasswordC);

        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editAddUser.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editAddPassword.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editAddPasswordC.getWindowToken(), 0);

                String user = editAddUser.getText().toString();
                String password = editAddPassword.getText().toString();
                String passwordC = editAddPasswordC.getText().toString();

                if(user.isEmpty() || password.isEmpty() || passwordC.isEmpty()) {
                    Toast.makeText(AddUserActivity.this,
                            "Il manque des informations",
                            Toast.LENGTH_LONG).show();
                } else if(!password.toString().equals(passwordC.toString())){
                    Toast.makeText(AddUserActivity.this,
                            "Les mot de passe ne sont pas les mêmes",
                            Toast.LENGTH_LONG).show();
                } else {
                    encrypt = new Encrypt();
                    GetUser result = new GetUser();
                    result.execute(user, encrypt.encryptPassword(password));
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_user, menu);
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
