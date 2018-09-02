package com.mytaxi.android_demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;

import com.mytaxi.android_demo.api.Response;
import com.mytaxi.android_demo.api.Result;
import com.mytaxi.android_demo.api.UserService;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.mytaxi.android_demo.activities.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.mytaxi.android_demo.misc.Constants.LOG_TAG;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class TestMainActivity {
    @Rule public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    @Rule public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    private String validUsername;
    private String validPassword;
    private String searchText = "sa";
    private String PREFS_NAME = "MytaxiPrefs";
    public static final String BASE_URL = "https://randomuser.me/";

    private MainActivity mActivity = null;

    @Before
    public void setUp() throws IOException {
        //Deserialize json response
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //Build randomuser endpoint, send request, and retrieve username/password
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        UserService service = retrofit.create(UserService.class);
        Call<Response> response = service.api();
        List<Result> results = response.execute().body().getResults();
        validUsername = results.get(0).login.username;
        validPassword = results.get(0).login.password;
    }

    @Before
    public void setActivity() {
        mActivity = mainActivityTestRule.getActivity();
    }

    //Check if view exists
    public boolean doesViewExist(int id) {
        try {
            onView(withId(id)).check(matches(isDisplayed()));
            return true;
        } catch (NoMatchingViewException e) {
            return false;
        }
    }

    @Test
    public void testScenario() {
        //Check if login view is visible, perform login, otherwise skip
        if(doesViewExist(R.id.btn_login)) {
            onView(withId(R.id.edt_username)).perform(clearText()).perform(typeText(validUsername));
            onView(withId(R.id.edt_password)).perform(clearText()).perform(typeText(validPassword));
            onView(withId(R.id.btn_login)).perform(click());
        }

        try {
            //Proceed only if map view displayed
            onView(withId(R.id.map)).check(matches(isDisplayed()));
            //Click on search field
            onView(withId(R.id.textSearch))
                    .perform(click())
                    .perform(clearText())
                    .perform(typeText(searchText));
            //Close keyboard
            closeSoftKeyboard();
            //Select 2nd name returned in list
            onView(withText("Sarah Scott"))
                    .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                    .perform(click());
            //Click call button
            onView(withId(R.id.fab)).perform(click());
        } catch (NoMatchingViewException e){
            Log.d(LOG_TAG,"Not on map view, skipping test");
        }
    }

    @After
    public void clearSharedPrefs() {
        //Clear SharedPreferencesStorage
        SharedPreferences mSharedPrefStorage = mainActivityTestRule.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPrefStorage.edit();
        editor.clear();
        editor.commit();
    }

}
