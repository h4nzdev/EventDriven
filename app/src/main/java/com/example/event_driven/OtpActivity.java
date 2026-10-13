package com.example.event_driven;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = "OtpActivityDebug";
    private String generatedOtp;

    private EditText etOtp1, etOtp2, etOtp3, etOtp4;
    private TextView tvTimer;
    private Button btnResend, btnVerifyOtp;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        tvTimer = findViewById(R.id.tvTimer);
        btnResend = findViewById(R.id.btnResend);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);

        setupOtpInputs();
        generateAndLogNewOtp();
        startTimer();

        // Handle Resend Click
        btnResend.setOnClickListener(v -> {
            generateAndLogNewOtp();
            clearInputs();
            startTimer();
            Toast.makeText(this, "New OTP sent! Check Logcat.", Toast.LENGTH_SHORT).show();
        });

        // Handle Verify Click
        btnVerifyOtp.setOnClickListener(v -> {
            String enteredOtp = etOtp1.getText().toString() +
                    etOtp2.getText().toString() +
                    etOtp3.getText().toString() +
                    etOtp4.getText().toString();

            if (enteredOtp.length() < 4) {
                Toast.makeText(this, "Please enter the complete 4-digit OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            if (enteredOtp.equals(generatedOtp)) {
                if (countDownTimer != null) countDownTimer.cancel();
                Toast.makeText(this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(OtpActivity.this, DashboardActivity.class);
                intent.putExtra("USER_NAME", getIntent().getStringExtra("USER_NAME"));
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid OTP. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateAndLogNewOtp() {
        Random random = new Random();
        generatedOtp = String.format("%04d", random.nextInt(10000));

        getSharedPreferences("OtpPrefs", MODE_PRIVATE)
                .edit()
                .putString("latest_otp", generatedOtp)
                .apply();

        Log.d(TAG, "========================================");
        Log.d(TAG, "GENERATED OTP CODE: " + generatedOtp);
        Log.d(TAG, "========================================");
    }

    private void startTimer() {
        btnResend.setEnabled(false);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Resend OTP in " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                tvTimer.setText("OTP expired. Click Resend.");
                btnResend.setEnabled(true);
                // Reset/refresh OTP automatically on expiration
                generateAndLogNewOtp();
                Toast.makeText(OtpActivity.this, "OTP expired and refreshed. Check Logcat.", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void clearInputs() {
        etOtp1.setText("");
        etOtp2.setText("");
        etOtp3.setText("");
        etOtp4.setText("");
        etOtp1.requestFocus();
    }

    private void setupOtpInputs() {
        // Auto-focus shifting logic across the 4 boxes
        etOtp1.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) etOtp2.requestFocus();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etOtp2.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) etOtp3.requestFocus();
                else if (s.length() == 0) etOtp1.requestFocus();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etOtp3.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) etOtp4.requestFocus();
                else if (s.length() == 0) etOtp2.requestFocus();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etOtp4.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) etOtp3.requestFocus();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}