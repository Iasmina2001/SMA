package com.sma.lab11;

import static com.sma.lab11.MainActivity.SI_CODE;
import static com.sma.lab11.MainActivity.SU_CODE;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "smartwallet-login";
    private TextView tStatus;
    private TextView tDetail;
    private EditText eEmail;
    private EditText ePass;
    private ProgressBar pLoading;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tStatus = findViewById(R.id.tStatus);
        tDetail = findViewById(R.id.tDetail);
        eEmail = findViewById(R.id.eEmail);
        ePass = findViewById(R.id.ePass);
        pLoading = findViewById(R.id.pLoading);
        Button bSignIn = findViewById(R.id.bSignIn);
        Button bRegister = findViewById(R.id.bRegister);
        Button bSignOut = findViewById(R.id.bSignOut);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    AppState.get().setUserId(user.getUid());
                    setResult(RESULT_OK);
                    finish();
                }
                updateUI(user);
            }
        };

        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = eEmail.getText().toString();
                String password = ePass.getText().toString();
                signIn(email, password);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("email", eEmail.getText().toString());
                intent.putExtra("password", ePass.getText().toString());
                setResult(SI_CODE, intent);
                finish();
            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = eEmail.getText().toString();
                String password = ePass.getText().toString();
                createAccount(email, password);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                setResult(SU_CODE, intent);
                finish();
            }
        });

        bSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                            tStatus.setText("Authentication failed");
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = eEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            eEmail.setError("Required.");
            valid = false;
        } else {
            eEmail.setError(null);
        }

        String password = ePass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ePass.setError("Required.");
            valid = false;
        } else {
            ePass.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            tStatus.setText("Email user: " + user.getEmail());
            tDetail.setText("Firebase user: " + user.getUid());

            findViewById(R.id.lSignIn).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.bSignOut).setVisibility(View.VISIBLE);
        } else {
            tStatus.setText("Signed out");
            tDetail.setText(null);

            findViewById(R.id.lSignIn).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.bSignOut).setVisibility(View.GONE);
        }
    }

    private void showProgressDialog() {
        pLoading.setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
        pLoading.setVisibility(View.GONE);
    }
}