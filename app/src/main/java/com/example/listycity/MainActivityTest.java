package com.example.listycity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private MainActivity activity;

    @Before
    public void setUp() {
        activity = new MainActivity();
    }

    @Test
    public void testHasCity() {
        activity.dataList.add("Edmonton, Alberta");


        assertTrue(activity.hasCity("Edmonton, Alberta"));


        assertFalse(activity.hasCity("Calgary, Alberta"));
    }

    @Test
    public void testDeleteCity() {
        activity.dataList.add("Edmonton, Alberta");


        assertTrue(activity.hasCity("Edmonton, Alberta"));


        activity.deleteCity("Edmonton, Alberta");
        assertFalse(activity.hasCity("Edmonton, Alberta"));
    }

    @Test
    public void testCountCities() {
        activity.dataList.clear();


        assertEquals(0, activity.countCities());


        activity.dataList.add("Edmonton, Alberta");
        activity.dataList.add("Calgary, Alberta");
        assertEquals(2, activity.countCities());
    }


}
