package com.mytaxi.android_demo;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.mytaxi.android_demo.activities.MainActivity;

import java.lang.Iterable;
import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;
import static org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class LoginParameterizedTest {

    @Rule public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    @Rule public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"", ""},
                {"  ", "  "},
                {"POP", "OPO"},
                {"!\"#$%&'()*+,- ./:;<=>?@[\\]^_`{|}~", "!\"#$%&'()*+,- ./:;<=>?@[\\]^_`{|}~"},
                {"312793837891237823178932789312", "312793837891237823178932789312"}
        });
    }

    private String mUser;
    private String mPass;

    private MainActivity mActivity = null;

    public LoginParameterizedTest(String user, String pass) {
        mUser = user;
        mPass = pass;
    }

    @Before
    public void setActivity() {
        mActivity = mainActivityTestRule.getActivity();
    }

    @Test
    public void testLoginScenarios() throws InterruptedException {
        try {
            onView(withId(R.id.edt_username)).perform(typeText(mUser));
            onView(withId(R.id.edt_password)).perform(typeText(mPass));
            sleep(500);
            onView(withId(R.id.btn_login)).perform(click());
            onView(withText(R.string.message_login_fail)).check(matches(isDisplayed()));
        }
        catch (NoMatchingViewException e){
            Assert.fail("Login failed message not displayed");
        }
    }
}