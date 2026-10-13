package com.example.event_driven;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle edge-to-edge window system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Handle Login Click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = etEmail.getText().toString().trim();
                String inputPassword = etPassword.getText().toString().trim();

                if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // STEP 1: Check SharedPreferences first
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String savedEmail = prefs.getString("email", "");
                String savedPassword = prefs.getString("password", "");

                if (!savedEmail.isEmpty() && savedEmail.equalsIgnoreCase(inputEmail) && savedPassword.equals(inputPassword)) {
                    String firstName = prefs.getString("first_name", "");
                    String lastName = prefs.getString("last_name", "");
                    String fullName = firstName + " " + lastName;

                    Toast.makeText(MainActivity.this, "Welcome, " + fullName + "!", Toast.LENGTH_SHORT).show();

                    // Route local login through OtpActivity to keep flow uniform
                    Intent intent = new Intent(MainActivity.this, OtpActivity.class);
                    intent.putExtra("USER_NAME", fullName);
                    startActivity(intent);
                    finish();
                    return; // Found locally, stop execution here!
                }

                // STEP 2: If not in SharedPreferences, check Render API in a background thread
                validateLoginFromRender(inputEmail, inputPassword);
            }
        });

        // Handle Register Click
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validateLoginFromRender(String email, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://actusertest.onrender.com/users");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(response.toString());
                    boolean loginSuccessful = false;
                    String fullName = "";

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject user = jsonArray.getJSONObject(i);
                        String apiEmail = user.getString("email");

                        if (apiEmail.equalsIgnoreCase(email)) {
                            loginSuccessful = true;
                            fullName = user.getString("first_name") + " " + user.getString("last_name");
                            break;
                        }
                    }

                    final boolean success = loginSuccessful;
                    final String name = fullName;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (success) {
                                Toast.makeText(MainActivity.this, "Welcome, " + name + "!", Toast.LENGTH_SHORT).show();

                                // Route API login through OtpActivity
                                Intent intent = new Intent(MainActivity.this, OtpActivity.class);
                                intent.putExtra("USER_NAME", name);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Network error or user not found.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}