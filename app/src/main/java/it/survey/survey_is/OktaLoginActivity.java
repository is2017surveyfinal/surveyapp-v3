package it.survey.survey_is;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.okta.appauth.android.OktaAppAuth;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import net.openid.appauth.AuthorizationException;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class OktaLoginActivity extends Activity implements InternetConnectivityListener {

    private OktaAppAuth mOktaAuth;
    private InternetAvailabilityChecker mInternetAvailabilityChecker;

    private ImageButton btnRegister;
    private ImageButton btnLogin;

    private boolean oktaOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okta_login);

        InternetAvailabilityChecker.init(this);
        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);

        mOktaAuth = OktaAppAuth.getInstance(this);

        if (mOktaAuth.isUserLoggedIn()) {
            Log.i(TAG, "User is already authenticated, proceeding to protected activity");
            startActivity(new Intent(getApplicationContext(), SurveysListContent.class));

            finish();
            return;
        }

        btnLogin = (ImageButton) findViewById(R.id.btnLogin);
        btnLogin.setEnabled(false);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent completionIntent = new Intent(getApplicationContext(), SurveysListContent.class);
                Intent cancelIntent = new Intent(getApplicationContext(), OktaLoginActivity.class);
                cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                mOktaAuth.login(
                        getApplicationContext(),
                        PendingIntent.getActivity(getApplicationContext(), 0, completionIntent, 0),
                        PendingIntent.getActivity(getApplicationContext(), 0, cancelIntent, 0)
                );
            }
        });

        btnRegister = (ImageButton) findViewById(R.id.btnRegister);
        btnRegister.setEnabled(false);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(i);
            }
        });

        initOkta();
    }

    private void initOkta() {
        mOktaAuth.init(
                this,
                new OktaAppAuth.OktaAuthListener() {
                    @Override
                    public void onSuccess() {
                        // Handle a successful initialization (e.g. display login button)
                        btnLogin.setEnabled(true);
                        btnRegister.setEnabled(true);
                        oktaOk = true;
                    }

                    @Override
                    public void onTokenFailure(@NonNull AuthorizationException ex) {
                        // Handle a failed initialization
                        Log.e("onTokenFailure", ex.toString());
                        oktaOk = false;
                    }
                }
        );
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (!isConnected) {
            Toast.makeText(this, "Internet non disponibile", 1000).show();
            btnLogin.setEnabled(false);
            btnRegister.setEnabled(false);
        } else if (oktaOk) {
            btnLogin.setEnabled(true);
            btnRegister.setEnabled(true);
        } else {
            initOkta();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
    }
}
