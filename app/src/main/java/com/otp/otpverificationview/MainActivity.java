package com.otp.otpverificationview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.otp.otpverificationviewlib.OtpVerificationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OtpVerificationView otpVerificationView = findViewById(R.id.otpView);
        otpVerificationView.setListener(new OtpVerificationView.OnTextChangListener() {
            @Override
            public void afterTextChanged(String text, int textCount) {
                if (text.length() == textCount)
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
