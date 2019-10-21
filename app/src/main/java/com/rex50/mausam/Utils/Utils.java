package com.rex50.mausam.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static boolean containsNumericValues(String text){
        Pattern p = Pattern.compile("[0-9]");
        Matcher m = p.matcher(text);
        return m.find();
    }

    private static boolean containsSpecialChars(String text){
        Pattern p = Pattern.compile("[^A-Za-z0-9 ]");
        Matcher m = p.matcher(text);
        return m.find();
    }

    public static void validateText(String text,TextValidationInterface listner){
        if(text.trim().isEmpty()) {
            listner.empty();
        } else if(containsNumericValues(text)){
            listner.containNumber();
        } else if(containsSpecialChars(text)){
            listner.containSpecialChars();
        } else {
            listner.correct();
        }
    }

    public interface TextValidationInterface{
        void correct();
        void containNumber();
        void containSpecialChars();
        void empty();
    }

}
