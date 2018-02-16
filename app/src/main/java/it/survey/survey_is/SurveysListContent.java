package it.survey.survey_is;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.okta.appauth.android.OktaAppAuth;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationManagementActivity;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.RedirectUriReceiverActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.survey.survey_is.model.Sondaggio;

public class SurveysListContent extends ListActivity {
    private List<Sondaggio> lista = new ArrayList<>();

    private OktaAppAuth mOktaAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_surveys_list);

        mOktaAuth = OktaAppAuth.getInstance(this);


        mOktaAuth.getUserInfo(new OktaAppAuth.OktaAuthActionCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    String username = (String) jsonObject.get("preferred_username");
                    ((SurveyApplication)getApplicationContext()).setUserName(username);

                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(SurveysListContent.this);

                    StringRequest testRequest = new AuthorizedStringRequest(SurveysListContent.this, Request.Method.GET,
                            getResources().getString(R.string.url_test),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error.getMessage() != null) {
                                        Log.d("Error.Message", error.getMessage());
                                    } else {
                                        Log.d("Error.Message", error.toString());
                                    }
                                    Toast.makeText(SurveysListContent.this, "Errore nella chiamata di test", 1000).show();
                                }
                            }
                    );

                    queue.add(testRequest);

                    String url = getResources().getString(R.string.url_sondaggi) + username;

                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new AuthorizedStringRequest(SurveysListContent.this, Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //System.out.println(response);
                                    lista = SurveysUtils.parseXml(response);

                                    SurveyAdapter adapter = new SurveyAdapter(SurveysListContent.this, lista);
                                    setListAdapter(adapter);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error.getMessage() != null) {
                                        Log.d("Error.Message", error.getMessage());
                                    } else {
                                        Log.d("Error.Message", error.toString());
                                    }
                                    Toast.makeText(SurveysListContent.this, "Errore nel downlowd dei sondaggi", 1000).show();
                                }
                            }
                    );

                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTokenFailure(@NonNull AuthorizationException e) {
                Intent intent = new Intent(getApplicationContext(), OktaLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onFailure(int i, Exception e) {
                Intent intent = new Intent(getApplicationContext(), OktaLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });



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
