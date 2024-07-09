package com.evilc;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.WebDriver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.openqa.selenium.Cookie;

// Saves Cookies to disk, and loads them back in again...
// To save / load logged in state
public class CookieJar{
    private String cookieFile;

    public CookieJar(String cookieFile) {
        this.cookieFile = cookieFile;
    }

    public void saveCookies(WebDriver driver) {
        // create file named Cookies to store Login Information
        Set<Cookie> cookies = driver.manage().getCookies();
        try {
            Writer writer = new FileWriter(this.cookieFile);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(cookies, writer);
            writer.flush();    
        } catch (IOException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void loadCookies(WebDriver driver){
        Gson gson = new Gson();
        try {
            Set<Cookie> cookies = gson.fromJson(new FileReader(this.cookieFile), new TypeToken<HashSet<Cookie>>(){}.getType());
            for(Cookie cookie : cookies) {
                driver.manage().addCookie(cookie);
            }
            } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}