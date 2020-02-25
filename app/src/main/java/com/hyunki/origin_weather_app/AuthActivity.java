package com.hyunki.origin_weather_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "AuthActivity";

    private FirebaseAuth auth;

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        auth = FirebaseAuth.getInstance();
        SignInButton googleButton = findViewById(R.id.google_button);

        googleButton.setOnClickListener(view -> signIn());

        authListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                Log.d(TAG, "onCreate: " + auth.getCurrentUser().getUid());
                onAuthSuccess();
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
//        showProgressBar();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
//                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                        updateUI(null);
                    }

                    // [START_EXCLUDE]
//                        hideProgressBar();
                    // [END_EXCLUDE]
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser currentUser = auth.getCurrentUser();
//        updateUI(currentUser);
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(Objects.requireNonNull(account));
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        auth.signOut();

        // Google sign out
//        googleSignInClient.signOut().addOnCompleteListener(this,
//                task -> updateUI(null));
    }

    private void revokeAccess() {
        // Firebase sign out
        auth.signOut();

        // Google revoke access
//        googleSignInClient.revokeAccess().addOnCompleteListener(this,
//                task -> updateUI(null));
    }

//    private void updateUI(FirebaseUser user) {
////        hideProgressBar();
//        if (user != null) {
////            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
////            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
////            findViewById(R.id.signInButton).setVisibility(View.GONE);
////            findViewById(R.id.signOutAndDisconnect).setVisibility(View.VISIBLE);
//        } else {
////            mStatusTextView.setText(R.string.signed_out);
////            mDetailTextView.setText(null);
////
////            findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
////            findViewById(R.id.signOutAndDisconnect).setVisibility(View.GONE);
//        }
//    }
    private void onAuthSuccess(){
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
//        if (i == R.id.signInButton) {
//            signIn();
//        } else if (i == R.id.signOutButton) {
//            signOut();
//        } else if (i == R.id.disconnectButton) {
//            revokeAccess();
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}


