package com.travelrights.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.travelrights.model.AirportResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jithi on 04-06-2018.
 */

public class PrefManager {

    public static final String PREFERENCES = "preference";
    public static final String Airport = "airport";
    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private static PrefManager instance = null;

    public static PrefManager getInstance(Context context) {
        if(instance == null) {
            instance = new PrefManager(context);
        }
        else
        {
            instance.context=null;
            instance.context=context;

        }
        return instance;
    }
    private PrefManager(Context context) {
        super();
        this.context = context;
        sharedpreferences=context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        editor=sharedpreferences.edit();
    }

    public String getSharedString(String KEY, String defValue) {
        if(!isValid(sharedpreferences))
            sharedpreferences =context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if(!isValid(editor))
            editor = sharedpreferences.edit();
        return sharedpreferences.getString(KEY, defValue);
    }

    public Integer getSharedInteger(String KEY, Integer defValue) {
        if(!isValid(sharedpreferences))
            sharedpreferences =context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if(!isValid(editor))
            editor = sharedpreferences.edit();
        return sharedpreferences.getInt(KEY, defValue);
    }

    public void putSharedString(String KEY, String value) {
        if(!isValid(sharedpreferences))
            sharedpreferences =context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if(!isValid(editor))
            editor = sharedpreferences.edit();
        editor.putString(KEY, value).apply();

    }

    public void putSharedInteger(String KEY, Integer value) {
        if(!isValid(sharedpreferences))
            sharedpreferences =context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if(!isValid(editor))
            editor = sharedpreferences.edit();
        editor.putInt(KEY, value).apply();

    }


    public boolean getSharedBoolean(String KEY, boolean defValue) {
        if(!isValid(sharedpreferences))
            sharedpreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if(!isValid(editor))
            editor = sharedpreferences.edit();
        return sharedpreferences.getBoolean(KEY, defValue);
    }

    public void putSharedBoolean(String KEY, boolean value) {
        if(!isValid(sharedpreferences))
            sharedpreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if(!isValid(editor))
            editor = sharedpreferences.edit();
        editor.putBoolean(KEY, value).apply();
    }

    private Long getSharedLong(String KEY) {
        if(!isValid(sharedpreferences))
            sharedpreferences =context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if(!isValid(editor))
            editor = sharedpreferences.edit();
        return sharedpreferences.getLong(KEY,0L);
    }

    private void putSharedLong(String KEY, long value) {
        if(!isValid(sharedpreferences))
            sharedpreferences =context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if(!isValid(editor))
            editor = sharedpreferences.edit();
        editor.putLong(KEY, value).apply();
    }

    public void clearSharedAll() {
        if(!isValid(editor))
        {
            if(!isValid(sharedpreferences))
                sharedpreferences =context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
        }
        editor.clear().apply();
    }
    public Boolean isValid(Object text)
    {
        if(text!=null)

            return  true;
        return  false;

    }

    public Boolean isValid(int count)
    {
        if(count>0)
            return  true;
        return  false;

    }
    public Boolean isValid(String text)
    {
        if(text!=null)
            if(!text.trim().equalsIgnoreCase(""))
                return  true;
        return  false;

    }
    public Boolean isValid(List list)
    {
        if(list!=null)
            if(list.size()>0)
                return  true;
        return  false;

    }


}
