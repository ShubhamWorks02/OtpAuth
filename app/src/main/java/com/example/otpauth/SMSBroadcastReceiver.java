package com.example.otpauth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("receiver", "onReceive: called");
//        Bundle bundle = intent.getExtras();
//        if (bundle != null) {
//            Object[] pdus = (Object[]) bundle.get("pdus");
//            if (pdus != null) {
//                for (Object pdu : pdus) {
//                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
//                    String sender = smsMessage.getDisplayOriginatingAddress();
//                    String messageBody = smsMessage.getDisplayMessageBody();
//
//                    // If the sender is Firebase, and the message body contains an OTP,
//                    // extract the OTP and fill it in the EditText.
//                    if (sender.equals("+919510324299")) {
//                        String otp = messageBody.substring(0, 5);
//                    }
//                }
//            }
//        }
    }
}
