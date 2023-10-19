package com.example.otpauth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnLogin;

    private String phoneNumber = "+919510324299";

    private final ActivityResultLauncher<Intent> requestActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                // Get SMS message content
                // Log.d(TAG, "onActivityResult: ");
                String message = result.getData().getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                // Extract one-time code from the message and complete verification
                // `sms` contains the entire text of the SMS message, so you will need
                // to parse the string.
                // String oneTimeCode = parseOneTimeCode(message); // define this function

                // send one time code to the server
            } else {
                String message = result.getData().getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        Intent intent = new Intent(MainActivity.this, OtpActivity.class);
        // intent.putExtra("PH",phoneNumber);
        startActivity(intent);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                // The user has been verified.
                                Toast.makeText(MainActivity.this, "Verification Done", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                // The verification failed.
                                Log.d("Auth", "onVerificationFailed: " + e.getMessage());
                                Toast.makeText(MainActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                Toast.makeText(MainActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, OtpActivity.class);
                                intent.putExtra("PH",phoneNumber);
                                startActivity(intent);
                                finish();
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.verifyPhoneNumber(options);
//                Intent intent = new Intent(MainActivity.this, OtpActivity.class);
//                startActivity(intent);
            }
        });

    }

}