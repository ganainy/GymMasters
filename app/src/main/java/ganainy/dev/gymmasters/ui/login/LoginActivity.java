package ganainy.dev.gymmasters.ui.login;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.ui.main.MainActivity;
import ganainy.dev.gymmasters.ui.signup.SignUpActivity;
import ganainy.dev.gymmasters.utils.ApplicationViewModelFactory;
import ganainy.dev.gymmasters.utils.NetworkChangeReceiver;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 101;
    ScrollView parentScroll;
    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailTextInputLayout,passwordTextInputLayout;
    private Button signIn, signUp;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView semiTransparentBackgroundImage;
    private ProgressBar progressBar;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViewModel();

        initViews();

        loginViewModel.getEmailError().observe(this, errorHint ->
                {
                    String errorHintContentIfNotHandled = errorHint.getContentIfNotHandled();
                    if(errorHintContentIfNotHandled!=null&&errorHintContentIfNotHandled.isEmpty()){
                        emailTextInputLayout.setError("");
                    }else if(errorHintContentIfNotHandled != null){
                        emailTextInputLayout.setError(errorHintContentIfNotHandled);
                    }
                }
        );

        loginViewModel.getPasswordError().observe(this, errorHint ->
                {
                    String errorHintContentIfNotHandled = errorHint.getContentIfNotHandled();
                    if(errorHintContentIfNotHandled!=null&&errorHintContentIfNotHandled.isEmpty()){
                        passwordTextInputLayout.setError("");
                    }else if(errorHintContentIfNotHandled != null){
                        passwordTextInputLayout.setError(errorHintContentIfNotHandled);
                    }
                }
        );

        loginViewModel.getIsLoadingLiveData().observe(this, isLoading -> {
            if (isLoading) {
                semiTransparentBackgroundImage.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                semiTransparentBackgroundImage.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        });

        loginViewModel.getToastLiveData().observe(this, pairEvent -> {
            if (!pairEvent.getHasBeenHandled()) {
                Pair<Integer, Integer> toastPair = pairEvent.getContentIfNotHandled();
                Toast.makeText(
                        LoginActivity.this,
                        getApplication().getString(toastPair.first),
                        toastPair.second)
                        .show();
            }
        });

        loginViewModel.getNavigateToMainLiveData().observe(this, shouldNavigateToMainActivity -> {
            if (!shouldNavigateToMainActivity.getHasBeenHandled()) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });


        loginViewModel.getIsGoogleUserDataUploadedLiveData().observe(this, isGoogleUserDataUploaded -> {
            if (isGoogleUserDataUploaded) {
                //executes if account was added to db or not added if already existed
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                //onCancelled called when adding or checking if google account is in db*/
                Toast.makeText(LoginActivity.this, R.string.use_gym_master_account, Toast.LENGTH_LONG).show();
            }
        });


    }

    private void initViewModel() {
        ApplicationViewModelFactory applicationViewModelFactory = new ApplicationViewModelFactory(getApplication());
        loginViewModel = new ViewModelProvider(this, applicationViewModelFactory).get(LoginViewModel.class);
    }

    private void initViews() {
        parentScroll = findViewById(R.id.parentScroll);
        signIn = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signUpButton);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailEditText=findViewById(R.id.emailEditText);
        emailTextInputLayout=findViewById(R.id.emailTextInputLayout);
        passwordTextInputLayout=findViewById(R.id.passwordTextInputLayout);
        semiTransparentBackgroundImage=findViewById(R.id.semiTransparentBackgroundImage);
        progressBar=findViewById(R.id.progressBar);


        SignInButton googleSignInButton = findViewById(R.id.sign_in_button);
        googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });


        signUp.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        signIn.setOnClickListener(v ->loginViewModel.authenticateUser());


        //softkeyboard done action on password initiates login
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        loginViewModel.authenticateUser();
                    }
                    return false;
                }
        );


        // Configure sign-in to request the user's ID, email address, and basic
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // Set the dimensions of the sign-in button.
        SignInButton signInButton = (SignInButton) googleSignInButton;
        signInButton.setSize(SignInButton.SIZE_WIDE);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.validatePassword(s.toString());
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.validateEmail(s.toString());
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        loginViewModel.observeNetworkChanges();
    }

    @Override
    protected void onPause() {
       loginViewModel.unregisterNetworkReceiver();
        super.onPause();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            loginViewModel.handleGoogleSignIn(data);
        }
    }


}
