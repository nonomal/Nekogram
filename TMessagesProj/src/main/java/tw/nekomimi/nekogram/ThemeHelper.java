package tw.nekomimi.nekogram;

import android.os.Build;
import android.support.v4.graphics.ColorUtils;
import android.view.View;
import android.view.Window;

import org.telegram.ui.ActionBar.Theme;

public class ThemeHelper {
    public static void setupNavigationBar(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = Theme.getColor(NekoConfig.useMessagePanelColor ? Theme.key_chat_messagePanelBackground : Theme.key_actionBarDefault);
            if (NekoConfig.navigationBarTint) {
                window.setNavigationBarColor(color);
            } else {
                window.setNavigationBarColor(0xff000000);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ColorUtils.calculateLuminance(color) > 0.5 && NekoConfig.navigationBarTint) {
                    int flags = window.getDecorView().getSystemUiVisibility();
                    flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                    window.getDecorView().setSystemUiVisibility(flags);
                } else {
                    int flags = window.getDecorView().getSystemUiVisibility();
                    flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                    window.getDecorView().setSystemUiVisibility(flags);
                }
            }
        }
    }
}
