package it.survey.survey_is;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        final EditText txtNome = (EditText) findViewById(R.id.txtNome);
        final EditText txtCognome = (EditText) findViewById(R.id.txtCognome);
        final EditText txtMail = (EditText) findViewById(R.id.txtMail);
        final EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
        final Button cmdRegister = (Button) findViewById(R.id.cmdRegister);
        cmdRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject user = new JSONObject();

                if (txtNome.getText().toString().length() == 0) {
                    txtNome.setError("Nome obbligatorio!");
                }
                if (txtCognome.getText().toString().length() == 0) {
                    txtCognome.setError("Cognome obbligatorio!");
                }
                if (txtMail.getText().toString().length() == 0) {
                    txtMail.setError("Mail obbligatoria!");
                }
                if (txtPassword.getText().toString().length() == 0) {
                    txtPassword.setError("Password obbligatoria!");
                }

                try {
                    JSONObject profile = new JSONObject();
                    profile.put("firstName", txtNome.getText().toString());
                    profile.put("lastName", txtCognome.getText().toString());
                    profile.put("email", txtMail.getText().toString());
                    profile.put("login", txtMail.getText().toString());
                    JSONObject credentials = new JSONObject();
                    JSONObject password = new JSONObject();
                    credentials.put("password", password);
                    password.put("value", txtPassword.getText().toString());
                    user.put("profile", profile);
                    user.put("credentials", credentials);
                } catch (JSONException ex) {

                }

                final String jsonText = user.toString();

                final RequestQueue queue = Volley.newRequestQueue(RegistrationActivity.this);
                StringRequest registrationRequest = new StringRequest(Request.Method.POST,
                        getResources().getString(R.string.url_registration),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("JSON", response);
                                Toast.makeText(RegistrationActivity.this, "Registrazione effettuata con successo", 1000).show();
                                Intent i = new Intent(getApplicationContext(), OktaLoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String response = null;
                                String errorMessage = null;

                                try {
                                    try {
                                        response = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers));
                                    } catch (UnsupportedEncodingException e) {
                                        response = new String(error.networkResponse.data);
                                    }
                                } catch (Exception e) { }

                                if (response != null && response.length() > 0) {
                                    try {
                                        JSONObject json = new JSONObject(response);
                                        Log.d("Error.JSON", json.toString());

                                        if (json.has("errorSummary")) {
                                            errorMessage = json.getString("errorSummary");
                                        }
                                        if (json.has("errorCauses")) {
                                            JSONArray errorCauses = json.getJSONArray("errorCauses");
                                            if (errorCauses.length() > 0) {
                                                if (errorCauses.get(0)  instanceof JSONObject) {
                                                    JSONObject obj = (JSONObject)errorCauses.get(0);
                                                    if (obj.has("errorSummary")) {
                                                        errorMessage = obj.getString("errorSummary");
                                                    }
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        Log.e("JSON Parser", "Error parsing data " + e.toString());
                                    }
                                }

                                if (error.getMessage() != null) {
                                    Log.d("Error.Message", error.getMessage());
                                } else {
                                    Log.d("Error.Message", error.toString());
                                }

                                if (errorMessage != null) {
                                    Toast.makeText(RegistrationActivity.this, errorMessage, 1000).show();
                                } else {
                                    Toast.makeText(RegistrationActivity.this, "Errore nella registrazione", 1000).show();
                                }
                            }
                        }
                ) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=" + getParamsEncoding();
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return jsonText == null ? null : jsonText.getBytes(getParamsEncoding());
                        } catch (UnsupportedEncodingException uee) {
                            return null;
                        }
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();

                        headers.put("Authorization", "SSWS " + getResources().getString(R.string.api_key));

                        return headers;
                    }
                };
                queue.add(registrationRequest);
            }
        });
    }

}
