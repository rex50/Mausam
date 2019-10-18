package com.rex50.mausam.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static boolean containsNumericValues(String text){
        Pattern p = Pattern.compile("[0-9]");
        Matcher m = p.matcher(text);
        return m.find();
    }

    public static boolean containsSpecialChars(String text){
        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(text);
        return m.find();
    }

    public static void validateText(String text,TextValidationInterface listner){
        if(text.trim().isEmpty()) {
            listner.empty();
        } else if(containsNumericValues(text)){
            listner.cointainNumber();
        } else if(containsSpecialChars(text)){
            listner.cointainSpecialChars();
        } else {
            listner.correct();
        }
    }

    public interface TextValidationInterface{
        void correct();
        void cointainNumber();
        void cointainSpecialChars();
        void empty();
    }

}
