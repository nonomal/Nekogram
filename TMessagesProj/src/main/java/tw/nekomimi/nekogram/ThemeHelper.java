package tw.nekomimi.nekogram;

import android.os.Build;
import android.support.v4.graphics.ColorUtils;
import android.view.View;
import android.view.Window;

import org.telegram.ui.ActionBar.Theme;

public class ThemeHelper {

    public static void setupNavigationBar(Window window) {
        int color = Theme.getColor(NekoConfig.useMessagePanelColor ? Theme.key_chat_messagePanelBackground : Theme.key_actionBarDefault);
        setupNavigationBar(window, window.getDecorView(), color);
    }

    public static void setupNavigationBar(View view) {
        int color = Theme.getColor(NekoConfig.useMessagePanelColor ? Theme.key_chat_messagePanelBackground : Theme.key_actionBarDefault);
        setupNavigationBar(null, view, color);
    }

    public static void setupNavigationBar(Window window, View view, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (window != null) {
                if (NekoConfig.navigationBarTint) {
                    window.setNavigationBarColor(color);
                } else {
                    window.setNavigationBarColor(0xff000000);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int flags = view.getSystemUiVisibility();
                if (ColorUtils.calculateLuminance(color) > 0.5 && NekoConfig.navigationBarTint) {
                    flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                    view.setSystemUiVisibility(flags);
                } else {
                    flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                    view.setSystemUiVisibility(flags);
                }
            }
        }
    }
}
