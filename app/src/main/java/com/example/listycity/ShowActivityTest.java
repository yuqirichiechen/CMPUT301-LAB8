package com.example.listycity;

import android.content.Intent;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ShowActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testActivitySwitch() {
        Intent intent = new Intent(activityRule.getActivity(), ShowActivity.class);
        intent.putExtra("CITY_NAME", "Edmonton, Alberta");
        activityRule.getActivity().startActivity(intent);

        intended(hasComponent(ShowActivity.class.getName()));
    }

    @Test
    public void testCityNameConsistency() {
        Intent intent = new Intent(activityRule.getActivity(), ShowActivity.class);
        intent.putExtra("CITY_NAME", "Edmonton, Alberta");
        try (ActivityScenario<ShowActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.city_name_text_view)).check(matches(withText("Edmonton, Alberta")));
        }
    }

    @Test
    public void testBackButton() {
        Intent intent = new Intent(activityRule.getActivity(), ShowActivity.class);
        intent.putExtra("CITY_NAME", "Edmonton, Alberta");
        try (ActivityScenario<ShowActivity> scenario = ActivityScenario.launch(intent)) {
            pressBack();
            intended(hasComponent(MainActivity.class.getName()));
        }
    }
}
