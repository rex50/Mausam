package com.rex50.mausam.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
        import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    //location and weather Constants
    public static final int GPS_NOT_ENABLED = 5;
    public static final int NO_PERMISSION = 6;
    public static final int LAST_LOCATION_NOT_FOUND = 7;
    public static final int WEATHER_NOT_FOUND = 8;
    public static final int CITY_NOT_FOUND = 9;
    public static final int PAGE_NOT_FOUND = 404;

    //Internet connection Constants
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;

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

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

    @Nullable
    public static File getAppCacheDir(Context context){
        File dir = null;
        if(context != null)
             dir = context.getCacheDir();
        return dir;
    }

    public static boolean deleteCache(Context context) {
        try {
            if(context != null) {
                File dir = getAppCacheDir(context);
                return deleteDir(dir);
            }
        } catch (Exception e) {
            Log.e("deleteCache", "Error : ", e);
        }

        return false;
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if(children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static long folderSize(File dir){
        long length = 0;
        try{
            if(dir != null){
                //Check if device version is greater than API 25 then use newer apis available
                //else use reflections
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    return folderSize(Paths.get(dir.getPath()));
                }

                File[] files = dir.listFiles();
                if(files != null){
                    for (File file : files) {
                        if (file.isFile())
                            length += file.length();
                        else
                            length += folderSize(file);
                    }
                }
            }
        }catch (Exception e){
            Log.e("folderSize", "Error : ", e);
        }
        return length;
    }

    public static long folderSize(Path path) {

        final AtomicLong size = new AtomicLong(0);

        try {
            //Check if device version is greater than API 25 then use newer apis available
            //else use reflections
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                        size.addAndGet(attrs.size());
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {

                        Log.e("folderSize", "skipped: " + file + " (" + exc + ")");
                        // Skip folders that can't be traversed
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

                        if (exc != null)
                            Log.e("folderSize", "had trouble traversing: " + dir + " (" + exc + ")");
                        // Ignore errors traversing a folder
                        return FileVisitResult.CONTINUE;
                    }
                });
            }else {
                size.addAndGet(folderSize(new File(path.toString())));
            }
        } catch (IOException e) {
            throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
        }

        return size.get();
    }

}
