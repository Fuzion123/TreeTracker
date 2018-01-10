package com.apps.frederik.treetracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// this activity and its layout is almost directly copied from: https://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ProgressDialog progressDialog;
    private final String SHARED_PREF_LOGIN = "com.apps.frederik.treetracker.shared.pref.login";
    private final String SHARED_PREF_EMAIL = "com.apps.frederik.treetracker.shared.pref.email";
    private final String SHARED_PREF_PASSWORD = "com.apps.frederik.treetracker.shared.pref.password";

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    ImageView _treeImage;
    AnimationDrawable _treeAnimation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.title_activity_login));

        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_signup);
        _treeImage = findViewById(R.id.tree_image);
        _treeImage.setBackgroundResource(R.drawable.tree_flipper);
        _treeAnimation = (AnimationDrawable) _treeImage.getBackground();

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Signing up is not yet implemented! :-)", Toast.LENGTH_LONG).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        // inserts email and password if last login was successful.
        SetEmailAndPasswordFromLastTime();
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus)
    {
        if (hasFocus)
            _treeAnimation.start();
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        // authenticating progress
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        progressDialog.show();

        // reads the user input fields
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        // checks with firebase database authentication, that the provided user and password is valid.
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            SaveEmailAndPassword();
                            onLoginSuccess();
                        } else {
                            onLoginFailed();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);

        // starts overview activity
        Intent overview = new Intent(LoginActivity.this, OverviewActivity.class);
        String userId = user.getUid();
        overview.putExtra(Globals.USERID, userId);
        overview.putExtra(Globals.EMAIL, _emailText.getText().toString());
        startActivity(overview);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
        if(progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog != null)
            progressDialog.dismiss();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError("Password needs to be at least 4 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }

    // using sharedprefs to save username and password of last successful authentication.
    private void SaveEmailAndPassword(){
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        SharedPreferences pref = getSharedPreferences(SHARED_PREF_LOGIN, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SHARED_PREF_EMAIL, email);
        editor.putString(SHARED_PREF_PASSWORD, password);
        editor.commit();
    }

    // using sharedprefs to save username and password of last successful authentication.
    private void SetEmailAndPasswordFromLastTime(){
        SharedPreferences pref = getSharedPreferences(SHARED_PREF_LOGIN, MODE_PRIVATE);
        String email = pref.getString(SHARED_PREF_EMAIL, null);
        String password = pref.getString(SHARED_PREF_PASSWORD, null);

        if(email == null || password == null) return;

        _emailText.setText(email);
        _passwordText.setText(password);

        // hides Keyboard ;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}