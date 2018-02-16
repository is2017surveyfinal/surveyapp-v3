package it.survey.survey_is.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.okta.appauth.android.AuthStateManager;
import com.okta.appauth.android.OktaAppAuth;

import net.openid.appauth.AuthorizationException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


public class AuthorizedImageRequest extends ImageRequest {

    private AuthStateManager mAuthStateManager;
    private OktaAppAuth mOktaAuth;

    public AuthorizedImageRequest(@NonNull Context context, String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight, ImageView.ScaleType scaleType, Bitmap.Config decodeConfig, Response.ErrorListener errorListener) {
        super(url, listener, maxWidth, maxHeight, scaleType, decodeConfig, errorListener);
        this.mAuthStateManager = AuthStateManager.getInstance(context);
        mOktaAuth = OktaAppAuth.getInstance(context);
    }

    public AuthorizedImageRequest(@NonNull Context context, String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight, Bitmap.Config decodeConfig, Response.ErrorListener errorListener) {
        super(url, listener, maxWidth, maxHeight, decodeConfig, errorListener);
        this.mAuthStateManager = AuthStateManager.getInstance(context);
        mOktaAuth = OktaAppAuth.getInstance(context);
    }

    private class TokenResult {
        public String accessToken = null;
        public String idToken = null;
        public AuthorizationException authorizationException = null;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> superHeaders = super.getHeaders();

        final CountDownLatch latch = new CountDownLatch(1);
        final TokenResult tokenResult = new TokenResult();

        mOktaAuth.refreshAccessToken(new OktaAppAuth.OktaAuthListener() {
            public void onSuccess() {
                tokenResult.accessToken = mAuthStateManager.getCurrent().getAccessToken();
                tokenResult.idToken = mAuthStateManager.getCurrent().getIdToken();
                tokenResult.authorizationException = null;
                latch.countDown();
            }

            public void onTokenFailure(@NonNull AuthorizationException ex) {
                tokenResult.accessToken = null;
                tokenResult.idToken = null;
                tokenResult.authorizationException = ex;
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch(InterruptedException ie) {
            if (ie.getMessage() != null) {
                Log.d("AuthorizedStringRequest", ie.getMessage());
            } else {
                Log.d("AuthorizedStringRequest", ie.toString());
            }
        }

        if (tokenResult.authorizationException != null) {
            throw new AuthFailureError("Token refresh failed when performing action", tokenResult.authorizationException);
        }
        if (tokenResult.accessToken != null) {
            Map<String, String> headers = new HashMap<>();
            headers.putAll(superHeaders);

            headers.put("Authorization", "Bearer " + tokenResult.accessToken);
            headers.put("X-Okta-User-Agent", "Android/" + Build.VERSION.SDK_INT + " " + "com.okta.appauth.android" + "/" + "0.1.0");

            return headers;
        }

        return superHeaders;
    }

    public boolean hasRefreshToken() {
        return this.mAuthStateManager.getCurrent().getRefreshToken() != null;
    }

}
