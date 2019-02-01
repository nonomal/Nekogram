package tw.nekomimi.nekogram;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

public class TabsConfig {

    private static boolean configLoaded;
    private static final Object sync = new Object();

    public static boolean hideTabs;
    public static boolean tabsToBottom;
    public static boolean hideALl;
    public static boolean hideUsers;
    public static boolean hideGroups;
    public static boolean hideChannels;
    public static boolean hideBots;
    public static boolean hideAdmins;
    public static boolean hideTabsCounters;
    public static int chatsTabCounterSize = AndroidUtilities.isTablet() ? 13 : 11;
    public static int currentTab;

    static {
        loadConfig();
    }


    public static void saveConfig() {
        synchronized (sync) {
            try {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfing", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("hideTabs", hideTabs);
                editor.putBoolean("tabsToBottom", tabsToBottom);
                editor.putBoolean("hideALl", hideALl);
                editor.putBoolean("hideUsers", hideUsers);
                editor.putBoolean("hideGroups", hideGroups);
                editor.putBoolean("hideChannels", hideChannels);
                editor.putBoolean("hideBots", hideBots);
                editor.putBoolean("hideAdmins", hideAdmins);
                editor.putBoolean("hideTabsCounters", hideTabsCounters);
                editor.putInt("currentTab", currentTab);
                editor.commit();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }

            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
            hideTabs = preferences.getBoolean("hideTabs", false);
            tabsToBottom = preferences.getBoolean("tabsToBottom", false);
            hideALl = preferences.getBoolean("hideALl", false);
            hideUsers = preferences.getBoolean("hideUsers", false);
            hideGroups = preferences.getBoolean("hideGroups", false);
            hideChannels = preferences.getBoolean("hideChannels", false);
            hideBots = preferences.getBoolean("hideBots", false);
            hideAdmins = preferences.getBoolean("hideAdmins", false);
            hideTabsCounters = preferences.getBoolean("hideTabsCounters", false);
            currentTab = preferences.getInt("currentTab",0);
            configLoaded = true;
        }
    }

    public static void toggleHideTabs() {
        hideTabs = !hideTabs;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideTabs", hideTabs);
        editor.commit();
    }

    public static void toggleTabsToBottom() {
        tabsToBottom = !tabsToBottom;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("tabsToBottom", tabsToBottom);
        editor.commit();
    }

    public static void toggleHideALl() {
        hideALl = !hideALl;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideALl", hideALl);
        editor.commit();
    }

    public static void toggleHideUsers() {
        hideUsers = !hideUsers;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideUsers", hideUsers);
        editor.commit();
    }

    public static void toggleHideGroups() {
        hideGroups = !hideGroups;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideGroups", hideGroups);
        editor.commit();
    }

    public static void togglehideBots() {
        hideBots = !hideBots;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideBots", hideBots);
        editor.commit();
    }

    public static void toggleHideAdmins() {
        hideAdmins = !hideAdmins;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideAdmins", hideAdmins);
        editor.commit();
    }

    public static void toggleHideChannels() {
        hideChannels = !hideChannels;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideChannels", hideChannels);
        editor.commit();
    }

    public static void toggleHideTabsCounters() {
        hideTabsCounters = !hideTabsCounters;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideTabsCounters", hideTabsCounters);
        editor.commit();
    }

    public static void setCurrentTab(int tab) {
        currentTab = tab;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("currentTab", currentTab);
        editor.commit();
    }
}
