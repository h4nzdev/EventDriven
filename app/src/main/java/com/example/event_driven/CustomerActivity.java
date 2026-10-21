package com.example.event_driven;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CustomerActivity extends AppCompatActivity {

    // Replace these with your Supabase project details.
    private static final String SUPABASE_URL =
            "https://teruulhhtuiqglftvjsq.supabase.co";

    private static final String SUPABASE_KEY =
            "sb_publishable_cYdPAq9Pe8znom_qHHeLQg_B_rr1pQi";

    private static final String CUSTOMER_ENDPOINT =
            SUPABASE_URL + "/rest/v1/customer";

    private EditText etCustomerId;
    private EditText etLastName;
    private EditText etFirstName;
    private EditText etMobileNo;
    private EditText etAddress;

    private LinearLayout customerListContainer;
    private TextView tvCustomerStatus;
    private Button btnSaveCustomer;
    private Button btnRefreshCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        etCustomerId = findViewById(R.id.etCustomerId);
        etLastName = findViewById(R.id.etLastName);
        etFirstName = findViewById(R.id.etFirstName);
        etMobileNo = findViewById(R.id.etMobileNo);
        etAddress = findViewById(R.id.etAddress);

        customerListContainer = findViewById(R.id.customerListContainer);
        tvCustomerStatus = findViewById(R.id.tvCustomerStatus);

        btnSaveCustomer = findViewById(R.id.btnSaveCustomer);
        btnRefreshCustomers = findViewById(R.id.btnRefreshCustomers);

        btnSaveCustomer.setOnClickListener(v -> addCustomer());

        btnRefreshCustomers.setOnClickListener(v -> loadCustomers());

        loadCustomers();
    }

    private void loadCustomers() {
        tvCustomerStatus.setText("Loading customers...");

        new CustomerTask("GET", null).execute();
    }

    private void addCustomer() {
        String customerId = etCustomerId.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String mobileNo = etMobileNo.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (customerId.isEmpty() || lastName.isEmpty()
                || firstName.isEmpty() || mobileNo.isEmpty()
                || address.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please fill in all fields.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        try {
            JSONObject customer = new JSONObject();
            customer.put("customerid", Integer.parseInt(customerId));
            customer.put("lastname", lastName);
            customer.put("firstname", firstName);
            customer.put("mobileno", mobileNo);
            customer.put("address", address);

            btnSaveCustomer.setEnabled(false);
            btnSaveCustomer.setText("Saving...");

            new CustomerTask("POST", customer).execute();

        } catch (NumberFormatException e) {
            Toast.makeText(
                    this,
                    "Customer ID must be a valid number.",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Could not prepare customer data.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private class CustomerTask extends AsyncTask<Void, Void, String> {

        private final String method;
        private final JSONObject customerData;
        private int responseCode = -1;

        CustomerTask(String method, JSONObject customerData) {
            this.method = method;
            this.customerData = customerData;
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection connection = null;

            try {
                String requestUrl = CUSTOMER_ENDPOINT;

                if (method.equals("GET")) {
                    requestUrl += "?select=*&order=customerid.asc";
                }

                URL url = new URL(requestUrl);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod(method);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                connection.setRequestProperty(
                        "apikey", SUPABASE_KEY
                );

                connection.setRequestProperty(
                        "Authorization", "Bearer " + SUPABASE_KEY
                );

                connection.setRequestProperty(
                        "Content-Type", "application/json"
                );

                if (method.equals("POST")) {
                    connection.setDoOutput(true);

                    connection.setRequestProperty(
                            "Prefer", "return=representation"
                    );

                    try (OutputStream output = connection.getOutputStream()) {
                        output.write(
                                customerData.toString().getBytes("UTF-8")
                        );
                    }
                }

                responseCode = connection.getResponseCode();

                InputStream inputStream;

                if (responseCode >= 200 && responseCode < 300) {
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = connection.getErrorStream();
                }

                if (inputStream == null) {
                    return "HTTP " + responseCode;
                }

                StringBuilder response = new StringBuilder();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"))) {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                return response.toString();

            } catch (Exception e) {
                return "Connection error: " + e.getMessage();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            btnSaveCustomer.setEnabled(true);
            btnSaveCustomer.setText("Add Customer");

            if (responseCode >= 200 && responseCode < 300) {

                if (method.equals("POST")) {
                    Toast.makeText(
                            CustomerActivity.this,
                            "Customer added successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    clearForm();
                    loadCustomers();

                } else {
                    displayCustomers(result);
                }

            } else {
                tvCustomerStatus.setText("Request failed.");

                Toast.makeText(
                        CustomerActivity.this,
                        "Supabase error (" + responseCode + "): "
                                + result,
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    private void displayCustomers(String response) {
        try {
            JSONArray customers = new JSONArray(response);

            customerListContainer.removeAllViews();

            if (customers.length() == 0) {
                tvCustomerStatus.setText("No customers found.");
                return;
            }

            tvCustomerStatus.setText(
                    "Total customers: " + customers.length()
            );

            for (int i = 0; i < customers.length(); i++) {
                JSONObject customer = customers.getJSONObject(i);

                String id = customer.optString("customerid", "N/A");
                String lastName = customer.optString("lastname", "");
                String firstName = customer.optString("firstname", "");
                String mobileNo = customer.optString("mobileno", "");
                String address = customer.optString("address", "");

                TextView customerView = new TextView(this);

                customerView.setText(
                        "Customer ID: " + id + "\n"
                                + "Name: " + firstName + " " + lastName + "\n"
                                + "Mobile: " + mobileNo + "\n"
                                + "Address: " + address
                );

                customerView.setTextSize(16);
                customerView.setTextColor(0xFF333333);
                customerView.setPadding(20, 20, 20, 20);
                customerView.setBackgroundColor(0xFFFFFFFF);

                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );

                layoutParams.setMargins(0, 0, 0, 16);
                customerView.setLayoutParams(layoutParams);

                customerListContainer.addView(customerView);
            }

        } catch (Exception e) {
            tvCustomerStatus.setText("Could not read customer data.");

            Toast.makeText(
                    this,
                    "Error reading customer records: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void clearForm() {
        etCustomerId.setText("");
        etLastName.setText("");
        etFirstName.setText("");
        etMobileNo.setText("");
        etAddress.setText("");
    }
}