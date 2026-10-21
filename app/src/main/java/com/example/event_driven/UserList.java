
        package com.example.event_driven;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserList extends AppCompatActivity {

    private LinearLayout userListContainer;
    private TextView tvUserStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        userListContainer = findViewById(R.id.userListContainer);
        tvUserStatus = findViewById(R.id.tvUserStatus);

        displaySavedUser();
    }

    private void displaySavedUser() {

        // Use the exact same preferences filename as RegisterActivity.
        SharedPreferences preferences =
                getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String firstName = preferences.getString("first_name", "");
        String lastName = preferences.getString("last_name", "");
        String email = preferences.getString("email", "");

        userListContainer.removeAllViews();

        if (firstName.isEmpty() && lastName.isEmpty() && email.isEmpty()) {
            tvUserStatus.setText("No registered user found.");
            return;
        }

        tvUserStatus.setText("Registered User");

        // Create a card to display the saved user.
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 24, 24, 24);
        card.setBackgroundColor(0xFFFFFFFF);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 16);
        card.setLayoutParams(cardParams);

        TextView nameView = new TextView(this);
        nameView.setText("Name: " + firstName + " " + lastName);
        nameView.setTextSize(18);
        nameView.setTextColor(0xFF222222);

        TextView emailView = new TextView(this);
        emailView.setText("Email: " + email);
        emailView.setTextSize(16);
        emailView.setTextColor(0xFF555555);

        card.addView(nameView);
        card.addView(emailView);

        userListContainer.addView(card);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh the list whenever this screen becomes visible.
        if (userListContainer != null && tvUserStatus != null) {
            displaySavedUser();
        }
    }
}
