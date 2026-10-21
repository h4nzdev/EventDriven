
package com.example.event_driven;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private Button btnCustomer;
    private Button btnUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnCustomer = findViewById(R.id.btnCustomer);
        btnUserList = findViewById(R.id.btnUserList);

        btnCustomer.setOnClickListener(v -> {
            startActivity(new Intent(
                    DashboardActivity.this,
                    CustomerActivity.class
            ));
        });

        btnUserList.setOnClickListener(v -> {
            startActivity(new Intent(
                    DashboardActivity.this,
                    UserList.class
            ));
        });
    }
}
