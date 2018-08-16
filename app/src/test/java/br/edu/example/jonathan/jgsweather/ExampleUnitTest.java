package br.edu.example.jonathan.jgsweather;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class ExampleUnitTest {

    @Test
    public void test() {
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
    }

}