package it.survey.survey_is;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.okta.appauth.android.OktaAppAuth;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import it.survey.survey_is.model.Sondaggio;

public class Questionario extends ListActivity {

    private Sondaggio sondaggio;
    private OktaAppAuth mOktaAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_questionario);

        mOktaAuth = OktaAppAuth.getInstance(this);

        sondaggio = (Sondaggio) getIntent().getSerializableExtra("sondaggio");
        TextView testoTitolo = (TextView) findViewById(R.id.textArgomento);
        testoTitolo.setText(sondaggio.getTitle());

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = ((SurveyApplication)getApplicationContext()).getUserName();

                final String xml = SurveysUtils.createXml(sondaggio, userName);
                Log.d("xml", xml);

                final RequestQueue queue = Volley.newRequestQueue(Questionario.this);
                String url = getResources().getString(R.string.url_salvataggio);
                StringRequest postRequest = new AuthorizedStringRequest(Questionario.this, Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                                Toast.makeText(Questionario.this, "sondaggio salvato", 1000).show();
                                Intent i = new Intent(getApplicationContext(), SurveysListContent.class);
                                startActivity(i);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                if (error.getMessage() != null) {
                                    Log.d("Error.Message", error.getMessage());
                                } else {
                                    Log.d("Error.Message", error.toString());
                                }

                                int status = 0;
                                if (error != null && error.networkResponse != null) {
                                    status = error.networkResponse.statusCode;
                                }
                                // Handle 30x
                                if (status == HttpURLConnection.HTTP_MOVED_PERM ||
                                        status == HttpURLConnection.HTTP_MOVED_TEMP ||
                                        status == HttpURLConnection.HTTP_SEE_OTHER) {
                                    final String location = error.networkResponse.headers.get("Location");
                                    Log.d("Location", location);

                                    StringRequest stringRequest = new AuthorizedStringRequest(Questionario.this, Request.Method.GET, location,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Log.d("Response", response);
                                                    Toast.makeText(Questionario.this, "sondaggio salvato", 1000).show();
                                                    Intent i = new Intent(getApplicationContext(), SurveysListContent.class);
                                                    startActivity(i);
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            if (error.getMessage() != null) {
                                                Log.d("Error.Message", error.getMessage());
                                            } else {
                                                Log.d("Error.Message", error.toString());
                                            }
                                            Toast.makeText(Questionario.this, "sondaggio salvato", 1000).show();
                                            Intent i = new Intent(getApplicationContext(), SurveysListContent.class);
                                            startActivity(i);
                                            //Toast.makeText(Questionario.this, "Errore nel salvataggio del sondaggio", 1000).show();
                                        }
                                    });

                                    // Add the request to the RequestQueue.
                                    queue.add(stringRequest);
                                } else {
                                    Toast.makeText(Questionario.this, "Errore nel salvataggio del sondaggio", 1000).show();
                                }
                            }
                        }
                ) {
                    @Override
                    public String getBodyContentType() {
                        return "application/xml; charset=" + getParamsEncoding();
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return xml == null ? null : xml.getBytes(getParamsEncoding());
                        } catch (UnsupportedEncodingException uee) {
                            return null;
                        }
                    }
                };
                queue.add(postRequest);

            }
        });

        QuestionarioAdapter adapter = new QuestionarioAdapter(this, sondaggio.getDomande());
        setListAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mOktaAuth.isUserLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), OktaLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_surveys_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch(id) {
            case R.id.action_logout:
                mOktaAuth.logout();

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.url_logout)));
                intent.putExtra(CustomTabsIntent.EXTRA_TITLE_VISIBILITY_STATE, CustomTabsIntent.NO_TITLE);
                startActivity(intent);

                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
