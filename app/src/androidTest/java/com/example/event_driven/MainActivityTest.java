package com.example.event_driven;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// Explicit imports for static methods
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

// Explicit import for your R file (resolves tvDashboardTitle, etEmail, etOtp1, etc.)
import com.example.event_driven.R;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUpLocalPreferences() {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("first_name", "Hanz");
        editor.putString("last_name", "Magbal");
        editor.putString("email", "hanzhmagbal@gmail.com");
        editor.putString("password", "Hanz#123");
        editor.apply();
    }

    @Test
    public void testLoginWithSharedPreferences() {
        SystemClock.sleep(1500);

        // 1. Enter Email
        Espresso.onView(withId(R.id.etEmail))
                .perform(ViewActions.typeText("hanzhmagbal@gmail.com"), ViewActions.closeSoftKeyboard());
        SystemClock.sleep(1000);

        // 2. Enter Password
        Espresso.onView(withId(R.id.etPassword))
                .perform(ViewActions.typeText("Hanz#123"), ViewActions.closeSoftKeyboard());
        SystemClock.sleep(1000);

        // 3. Click Login
        Espresso.onView(withId(R.id.btnLogin))
                .perform(ViewActions.click());

        // Wait for OtpActivity to open and generate the random OTP
        SystemClock.sleep(2000);

        // 4. Catch/Read the exact random OTP from SharedPreferences
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences otpPrefs = context.getSharedPreferences("OtpPrefs", Context.MODE_PRIVATE);
        String dynamicOtp = otpPrefs.getString("latest_otp", "1234"); // Fallback if missing

        // 5. Type each digit into the 4 separate boxes automatically
        Espresso.onView(withId(R.id.etOtp1)).perform(ViewActions.typeText(String.valueOf(dynamicOtp.charAt(0))), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.etOtp2)).perform(ViewActions.typeText(String.valueOf(dynamicOtp.charAt(1))), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.etOtp3)).perform(ViewActions.typeText(String.valueOf(dynamicOtp.charAt(2))), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.etOtp4)).perform(ViewActions.typeText(String.valueOf(dynamicOtp.charAt(3))), ViewActions.closeSoftKeyboard());
        SystemClock.sleep(1000);

        // 6. Click Verify OTP
        Espresso.onView(withId(R.id.btnVerifyOtp))
                .perform(ViewActions.click());

        SystemClock.sleep(2000);

        // 7. Verify Dashboard title is displayed
        Espresso.onView(withId(R.id.tvDashboardTitle))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testLoginWithRenderApi() {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Wait for Render cold start
        SystemClock.sleep(15000);

        // 1. Enter Render API Email
        Espresso.onView(withId(R.id.etEmail))
                .perform(ViewActions.typeText("vfrance_ccs@uspf.edu.ph"), ViewActions.closeSoftKeyboard());
        SystemClock.sleep(1000);

        // 2. Enter Password
        Espresso.onView(withId(R.id.etPassword))
                .perform(ViewActions.typeText("france#123"), ViewActions.closeSoftKeyboard());
        SystemClock.sleep(1000);

        // 3. Click Login
        Espresso.onView(withId(R.id.btnLogin))
                .perform(ViewActions.click());

        SystemClock.sleep(5000);

        // 4. Catch/Read the exact random OTP from SharedPreferences
        SharedPreferences otpPrefs = context.getSharedPreferences("OtpPrefs", Context.MODE_PRIVATE);
        String dynamicOtp = otpPrefs.getString("latest_otp", "1234");

        // 5. Type each digit into the 4 separate boxes automatically
        Espresso.onView(withId(R.id.etOtp1)).perform(ViewActions.typeText(String.valueOf(dynamicOtp.charAt(0))), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.etOtp2)).perform(ViewActions.typeText(String.valueOf(dynamicOtp.charAt(1))), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.etOtp3)).perform(ViewActions.typeText(String.valueOf(dynamicOtp.charAt(2))), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.etOtp4)).perform(ViewActions.typeText(String.valueOf(dynamicOtp.charAt(3))), ViewActions.closeSoftKeyboard());
        SystemClock.sleep(1000);

        // 6. Click Verify OTP
        Espresso.onView(withId(R.id.btnVerifyOtp))
                .perform(ViewActions.click());

        SystemClock.sleep(3000);

        // 7. Verify Dashboard title is displayed
        Espresso.onView(withId(R.id.tvDashboardTitle))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        SystemClock.sleep(1500);

        // 1. Type wrong email
        Espresso.onView(withId(R.id.etEmail))
                .perform(ViewActions.typeText("wrongemail@uspf.edu.ph"), ViewActions.closeSoftKeyboard());
        SystemClock.sleep(1000);

        // 2. Type wrong password
        Espresso.onView(withId(R.id.etPassword))
                .perform(ViewActions.typeText("wrongpassword123"), ViewActions.closeSoftKeyboard());
        SystemClock.sleep(1000);

        // 3. Click login
        Espresso.onView(withId(R.id.btnLogin))
                .perform(ViewActions.click());

        SystemClock.sleep(3000);

        // 4. Verify we are still on the login screen
        Espresso.onView(withId(R.id.btnLogin))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testOtpVerificationWithIncorrectCode() {
        SystemClock.sleep(1500);

        // 1. Perform valid local login to reach OTP screen
        Espresso.onView(withId(R.id.etEmail))
                .perform(ViewActions.typeText("hanzhmagbal@gmail.com"), ViewActions.closeSoftKeyboard());
        SystemClock.sleep(1000);

        Espresso.onView(withId(R.id.etPassword))
                .perform(ViewActions.typeText("Hanz#123"), ViewActions.closeSoftKeyboard());
        SystemClock.sleep(1000);

        Espresso.onView(withId(R.id.btnLogin))
                .perform(ViewActions.click());

        SystemClock.sleep(2000);

        // 2. Intentionally enter an incorrect 4-Digit OTP ("9999")
        Espresso.onView(withId(R.id.etOtp1)).perform(ViewActions.typeText("9"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.etOtp2)).perform(ViewActions.typeText("9"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.etOtp3)).perform(ViewActions.typeText("9"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.etOtp4)).perform(ViewActions.typeText("9"), ViewActions.closeSoftKeyboard());
        SystemClock.sleep(1000);

        // 3. Click Verify OTP
        Espresso.onView(withId(R.id.btnVerifyOtp))
                .perform(ViewActions.click());

        SystemClock.sleep(2000);

        // 4. Verify we remain stuck on the OTP verification screen
        Espresso.onView(withId(R.id.btnVerifyOtp))
                .check(matches(isDisplayed()));
    }
}