package com.example.otpauth;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class OtpActivity extends AppCompatActivity {

    private TextView tvOtp;
    private String phoneNum = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        tvOtp = findViewById(R.id.tvOtp);
        phoneNum = getIntent().getStringExtra("PH");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 3);
        } else {

        }
        PackageManager packageManager = getPackageManager();
        boolean isSmsRetrieverSupported = packageManager.hasSystemFeature("android.hardware.telephony.sms.consent");
        Toast.makeText(this, ""+ isSmsRetrieverSupported, Toast.LENGTH_SHORT).show();
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsVerificationReceiver, intentFilter);
        Task<Void> task = SmsRetriever.getClient(OtpActivity.this).startSmsUserConsent("+919510324299");

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OtpActivity.this, "SmsRetriever Failed", Toast.LENGTH_SHORT).show();
            }
        });
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(OtpActivity.this, "SmsRetriever success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidMessageArrived = false;

    private final ActivityResultLauncher<Intent> requestActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                // Get SMS message content
                String message = result.getData().getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                String otp = message.substring(0, 6);
                tvOtp.setText(otp);
                unregisterReceiver(smsVerificationReceiver);
                // Extract one-time code from the message and complete verification
            } else {
                String message = result.getData().getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
            }
        }
    });

    private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isValidMessageArrived) return;
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                    if (smsRetrieverStatus != null) {
                        switch (smsRetrieverStatus.getStatusCode()) {
                            case CommonStatusCodes.SUCCESS:
                                // Get consent intent
                                isValidMessageArrived = true;
                                Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                                try {
                                    requestActivityLauncher.launch(consentIntent);
                                } catch (ActivityNotFoundException e) {
                                }
                                break;
                            case CommonStatusCodes.TIMEOUT:
                                isValidMessageArrived = false;
                                Toast.makeText(context, "in Receiver timeout", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } else {
                        Toast.makeText(context, "smsRetrieverStatus null", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "extras null", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };
}