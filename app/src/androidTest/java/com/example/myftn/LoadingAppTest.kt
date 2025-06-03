package com.example.myftn
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId

@RunWith(AndroidJUnit4::class)
class LoadingAppTest {

    @Test
    fun appLoadsSuccessfully() {
        // Launch the activity
        ActivityScenario.launch(MainActivity::class.java).use {
            // Check if the "message input" area is displayed
            onView(withId(R.id.messageEditText)).check(matches(isDisplayed()))
        }
    }
}
