package com.ahmet.barberbooking.Common;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveSettings {

    private SharedPreferences mPreferencesDarkMode;
    private SharedPreferences mPreferencesLanguage;

    public SaveSettings(Context context){
        mPreferencesDarkMode = context.getSharedPreferences("darkMode", Context.MODE_PRIVATE);
        mPreferencesLanguage = context.getSharedPreferences("language", context.MODE_PRIVATE);
    }

    // this Method Will Save the Night Mode State / true or false
    public void setNightModeState(Boolean state){

        SharedPreferences.Editor editor = mPreferencesDarkMode.edit();
        editor.putBoolean(Common.KEY_DARK_MODE, state);
        editor.commit();
    }

    // this Method Will Load the Night Mode State / true or false
    public Boolean getNightModeState(){
        Boolean state = mPreferencesDarkMode.getBoolean(Common.KEY_DARK_MODE, false);
        return state;
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // this Method Will Save the Language State / true or false
    public void setLanguageState(String state){

        SharedPreferences.Editor editor = mPreferencesLanguage.edit();
        editor.putString(Common.KEY_LANGUAGE, state);
        editor.commit();
    }

    // this Method Will Load the Language State / true or false
    public String getLanguageState(){
        String state = mPreferencesLanguage.getString(Common.KEY_LANGUAGE,"");
        return state;
    }
}
