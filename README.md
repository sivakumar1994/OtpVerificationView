# OtpVerificationView

Provides a widget for enter OTP verification view

![2097193539468445493](https://user-images.githubusercontent.com/34672633/43193214-0308d398-901d-11e8-94c2-8b640ee32058.png)

###  Usage:


###  Step 1:

Add OtpVerification View in your layout.
###  XML:
    <com.otp.otpverificationviewlib.OtpVerificationView
        android:id="@+id/otpView"
        android:layout_width="150dp"
        android:layout_height="wrap_content"
        app:OTPInputTypePassword="true"
        app:OTPLineWidth="2dp"
        app:OTPTextColor="#000000"
        app:OTPTextCount="4" />
        
###  Java:
     OtpVerificationView otpVerificationView = findViewById(R.id.otpView);
        otpVerificationView.setListener(new OtpVerificationView.OnTextChangListener() {
            @Override
            public void afterTextChanged(String text, int textCount) {
                if (text.length() == textCount)
                    //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
        
###  License:
Copyright 2017 Chaos Leong

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
