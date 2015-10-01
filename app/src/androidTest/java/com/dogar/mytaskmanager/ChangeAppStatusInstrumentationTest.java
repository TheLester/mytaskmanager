package com.dogar.mytaskmanager;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.dogar.mytaskmanager.activity.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ChangeAppStatusInstrumentationTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);
    @Test
    public void noOperandShowsComputationError() {
        assertTrue(true);
        onView(withId(R.id.process_list)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
//        onView(withId(R.id.operation_add_btn)).perform(click());
//        onView(withId(R.id.operation_result_text_view)).check(matches(withText(expectedResult)));
    }
}
