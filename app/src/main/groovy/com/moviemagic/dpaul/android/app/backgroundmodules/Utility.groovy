/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Environment
import android.support.v7.preference.PreferenceManager
import com.moviemagic.dpaul.android.app.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import groovy.transform.CompileStatic

@CompileStatic
class Utility {
    private static final String LOG_TAG = JsonParse.class.getSimpleName()

    /**
     * This utility method returns the current date
     * @return Today's date in yyyy-MM-dd HH:mm:ss.SSSZ format
     */
    static String getTodayDate() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ")
        final String todayDate = simpleDateFormat.format(new Date())
        LogDisplay.callLog(LOG_TAG, "Today date stamp-> $todayDate", LogDisplay.UTILITY_LIST_LOG_FLAG)
        return todayDate
    }

    /**
     * This utility method returns the current date in simple yyyy-MM-dd format
     * @return Today's date in yyyy-MM-dd format
     */
    static String getSimpleTodayDate() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
        final String todayDate = simpleDateFormat.format(new Date())
        LogDisplay.callLog(LOG_TAG, "Today date -> $todayDate", LogDisplay.UTILITY_LIST_LOG_FLAG)
        return todayDate
    }

    /**
     * This utility method returns the date which is 3 days ahead of today's date  in simple yyyy-MM-dd format
     * @return Date which is 3 days ahead of Today's date in yyyy-MM-dd format
     */
    static String getSimpleFiveDayFutureDate() {
        // Set the calendar to current date
        final Calendar calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 5)
        final Date date = calendar.getTime()
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
        final String fiveDayFutureDate = simpleDateFormat.format(date)
        LogDisplay.callLog(LOG_TAG, "Three days future date stamp-> $fiveDayFutureDate", LogDisplay.UTILITY_LIST_LOG_FLAG)
        return fiveDayFutureDate
    }

    /**
     * This utility method returns the timestamp 10 days prior to current date
     * @return Date 10 day's prior to Today's date in yyyy-MM-dd HH:mm:ss.SSSZ format
     */
    static String getTenDayPriorDate() {
        // Set the calendar to current date
        final Calendar calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -10)
        final Date date = calendar.getTime()
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ")
        final String tenDayPriorDate = simpleDateFormat.format(date)
        LogDisplay.callLog(LOG_TAG, "Ten days prior date stamp-> $tenDayPriorDate", LogDisplay.UTILITY_LIST_LOG_FLAG)
        return tenDayPriorDate
    }

    /**
     * This utility method converts the date representation of milliseconds to regular date format
     * @param timeInMillis Date represented in milliseconds
     * @return Formatted date value
     */
    static String convertMilliSecsToOrigReleaseDate(final long timeInMillis) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
        final String dateString = simpleDateFormat.format(new Date(timeInMillis))
        return dateString
    }

    /**
     * This utility method formats the date for friendly user display
     * @param date Date to be formatted
     * @return Formatted date value
     */
    static String formatFriendlyDate(final String date) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy")
        if(date.size() == 10) {
            if (date.getAt(4) == '-' && date.getAt(7) == '-') {
                final String dateString = simpleDateFormat.format(new SimpleDateFormat("yyyy-MM-dd").parse(date))
                return dateString
            } else
                return date
        } else
            return date
    }

    /**
     * This method returns the day name (Today, Tomorrow, Monday, Tuesday, etc)
     * @param ctx Context
     * @param date Release date of the movie
     * @return Formatted day name
     */
    static String getDayNameForNotification(final Context ctx, final String date) {
        final String[] dayOfWeek = ['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday']

        final GregorianCalendar currDateCalendar = new GregorianCalendar()
        final int currentDayOfYear = currDateCalendar.get(Calendar.DAY_OF_YEAR)
        final GregorianCalendar releaseCalendar = new GregorianCalendar()
        releaseCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date))
        final int releaseDayOfYear = releaseCalendar.get(Calendar.DAY_OF_YEAR)
        String dayName = null
        if(currentDayOfYear == releaseDayOfYear) {
            dayName = ctx.getString(R.string.notification_day_name_today)
        } else if (currentDayOfYear < 365) {
            if (releaseDayOfYear == currentDayOfYear + 1) {
                dayName = ctx.getString(R.string.notification_day_name_tomorrow)
            } else {
                dayName = 'on ' + dayOfWeek[(releaseCalendar.get(Calendar.DAY_OF_WEEK)) - 1]
            }
        } else if (!currDateCalendar.isLeapYear(currDateCalendar.get(Calendar.YEAR)) && currentDayOfYear == 365) {
            if (releaseDayOfYear == 1) {
                dayName = ctx.getString(R.string.notification_day_name_tomorrow)
            } else {
                dayName = 'on ' + dayOfWeek[(releaseCalendar.get(Calendar.DAY_OF_WEEK)) - 1]
            }
        } else if (currDateCalendar.isLeapYear(currDateCalendar.get(Calendar.YEAR)) && currentDayOfYear == 365) {
            if (releaseDayOfYear == 366) {
                dayName = ctx.getString(R.string.notification_day_name_tomorrow)
            } else {
                dayName = 'on ' + dayOfWeek[(releaseCalendar.get(Calendar.DAY_OF_WEEK)) - 1]
            }
        } else if (currDateCalendar.isLeapYear(currDateCalendar.get(Calendar.YEAR)) && currentDayOfYear == 366) {
            if (releaseDayOfYear == 1) {
                dayName = ctx.getString(R.string.notification_day_name_tomorrow)
            } else {
                dayName = 'on ' + dayOfWeek[(releaseCalendar.get(Calendar.DAY_OF_WEEK)) - 1]
            }
        } else { // Fallback option to ensure 'null' is not shown in the notification!
            dayName = ''
        }
        return dayName
    }

    /**
     * This utility method converts the minutes to hour and minutes
     * @param ctx Application context
     * @param runTime Minute value
     * @return Converted hour and minute value
     */
    public static String formatRunTime(final Context ctx, final int runTime) {
        final int hourVal
        final def minVal
        hourVal = runTime / 60 as Integer
        minVal = runTime % 60
        return String.format(ctx.getString(R.string.movie_run_time),hourVal,minVal)
    }

    /**
     * This utility method formats the dollar value in US currency
     * @param val Dollar value to be formatted
     * @return Formattd dollar value in us currency
     */
    public static String formatCurrencyInDollar(final int val) {
        final NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US)
        final def formattedValue = formatter.format(val)
        return formattedValue
    }

    /**
     * This utility method converts the date representation of milliseconds to formatted date
     * @param timeInMilliSeconds Date represented in milliseconds
     * @return Formatted friendly display date
     */
    public static String formatMilliSecondsToDate(final long timeInMilliSeconds) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy")
        final String dateString = formatter.format(new Date(timeInMilliSeconds))
        return dateString
    }

    /**
     * This utility method read the shared preference for theme settings and returns true if theme is set as 'Dynamic Theme'
     * @param context The application Context
     * @return True if the theme is set as 'Dynamic Theme' otherwise False
     */
    public static boolean isDynamicTheme(final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        final String themeType = sharedPreferences.getString(context.getString(R.string.pref_theme_key),
                                    context.getString(R.string.pref_theme_default_value))
        if(themeType.equals(context.getString(R.string.pref_theme_default_value))) {
            // If default app theme (i.e. Dynamic then return true
            return true
        } else {
            // If Static theme is set then return false
            return false
        }
    }

    /**
     * This utility method determines if the application can download data (i.e. internet connection is available or
     * if user selected download only on WiFi & user is connected to internet using WiFi)
     * @param context The application Context
     * @return Returns true if all set to download data otherwise returns False
     */
    public static boolean isReadyToDownload(final Context context) {
        final boolean isUserOnline = isOnline(context)
        final boolean isUserSelectedOnlyOnWifi = isOnlyWifi(context)

        if(isUserOnline) {
            if((!isUserSelectedOnlyOnWifi) || (isUserSelectedOnlyOnWifi && GlobalStaticVariables.WIFI_CONNECTED)) {
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    }

    /**
     * This utility method read the shared preference checks if user selected to use reduce data
     * @param context The application Context
     * @return Returns the Reduce Data flag
     */
    public static boolean isReducedDataOn(final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        final boolean reduceDataFlag = sharedPreferences.getBoolean(context.getString(R.string.pref_reduce_data_use_key),false)
        return reduceDataFlag
    }

    /**
     * This utility method read the shared preference and checks if user selected to use WiFi only for loading data
     * @param context The application Context
     * @return Returns the WiFiFlag
     */
    public static boolean isOnlyWifi(final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        final boolean wifiFlag = sharedPreferences.getBoolean(context.getString(R.string.pref_wifi_download_key),false)
        return wifiFlag
    }

    /**
     * This utility method checks if the mobile is connected to a network to perfrom network operation. It also
     * sets the WiFi or Mobile data flag accordingly based on the network type
     * @param ctx Application Context
     * @return True if WiFi or Mobile network is available otherwise returns False
     */
    public static boolean isOnline(final Context ctx) {
        final ConnectivityManager connMgr = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        final NetworkInfo activeInfo = connMgr.getActiveNetworkInfo()
        if (activeInfo != null && activeInfo.isConnected()) {
            GlobalStaticVariables.WIFI_CONNECTED = activeInfo.getType() == ConnectivityManager.TYPE_WIFI
            GlobalStaticVariables.MOBILE_CONNECTED = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE
            return true
        } else {
            GlobalStaticVariables.WIFI_CONNECTED = false
            GlobalStaticVariables.MOBILE_CONNECTED = false
            return false
        }
    }

    /**
     * Checks if external storage is available for read and write
     * @return True if external storage is available otherwise False
     */
    public static boolean isExternalStorageWritable() {
        final String state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true
        }
        return false
    }

    /**
     * This method format the date and timestamp which is used for file name
     * @return
     */
    public static String dateTimeForFileName() {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
        return timeStamp
    }

    /**
     * This utility method determines the mpaa for different countries - at this moment it only supports US & UK
     * @param mpaa MPAA indicator
     * @param locale Country
     * @return Logo to be displayed as determined by mpaa indicator and locale
     */
    public static int getIconResourceForMpaaRating(final String mpaa, final String locale) {
        //Remove any white spaces from mpaa and locale string
        if(mpaa) {
            mpaa.replaceAll("\\s", "")
        }
        if(locale) {
            locale.replaceAll("\\s", "")
        }

        switch (locale) {
            case 'GB':
                if (mpaa == 'U') {
                    return R.drawable.mpaa_uk_u
                } else if (mpaa == 'PG') {
                    return R.drawable.mpaa_uk_pg
                } else if (mpaa == '12A') {
                    return R.drawable.mpaa_uk_12a
                } else if (mpaa == '12') {
                    return R.drawable.mpaa_uk_12
                } else if (mpaa == '15') {
                    return R.drawable.mpaa_uk_15
                } else if (mpaa == '18') {
                    return R.drawable.mpaa_uk_18
                } else if (mpaa == 'R18') {
                    return R.drawable.mpaa_uk_r18
                }
                break
            case 'US':
                if (mpaa == 'G') {
                    return R.drawable.mpaa_us_g
                } else if (mpaa == 'PG') {
                    return R.drawable.mpaa_us_pg
                } else if (mpaa == 'PG-13') {
                    return R.drawable.mpaa_us_pg13
                } else if (mpaa == 'R') {
                    return R.drawable.mpaa_us_r
                } else if (mpaa == 'NC-17') {
                    return R.drawable.mpaa_us_nc17
                }
                break
            default:
                LogDisplay.callLog(LOG_TAG, "Unknown locale-> $locale", LogDisplay.UTILITY_LIST_LOG_FLAG)
        }
        return -1
    }
}