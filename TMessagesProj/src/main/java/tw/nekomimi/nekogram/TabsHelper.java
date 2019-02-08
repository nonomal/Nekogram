package tw.nekomimi.nekogram;

import android.graphics.Color;

import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

public class TabsHelper {
    public static class DialogType {
        public static final int All = 0;
        public static final int Users = 6;
        public static final int Groups = 7;
        public static final int Channels = 8;
        public static final int Bots = 9;
        public static final int Admin = 10;
    }

    private int currentAccount;
    private static volatile TabsHelper[] Instance = new TabsHelper[UserConfig.MAX_ACCOUNT_COUNT];

    public static TabsHelper getInstance(int num) {
        TabsHelper localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (MessagesController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new TabsHelper(num);
                }
            }
        }
        return localInstance;
    }

    public TabsHelper(int num) {
        currentAccount = num;
    }

    public ArrayList<TLRPC.TL_dialog> dialogsUsers = new ArrayList<>();
    public ArrayList<TLRPC.TL_dialog> dialogsGroups = new ArrayList<>();
    public ArrayList<TLRPC.TL_dialog> dialogsChannels = new ArrayList<>();
    public ArrayList<TLRPC.TL_dialog> dialogsBots = new ArrayList<>();
    public ArrayList<TLRPC.TL_dialog> dialogsAdmin = new ArrayList<>();

    public static int getIntAlphaColor(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public void cleanup() {
        dialogsUsers.clear();
        dialogsGroups.clear();
        dialogsChannels.clear();
        dialogsBots.clear();
        dialogsAdmin.clear();
    }

    public void deleteDialog(TLRPC.TL_dialog dialog) {
        dialogsUsers.remove(dialog);
        dialogsGroups.remove(dialog);
        dialogsChannels.remove(dialog);
        dialogsBots.remove(dialog);
        dialogsAdmin.remove(dialog);
    }

    public void sortDialogs(TLRPC.TL_dialog dialog, int high_id, int lower_id) {
        if (lower_id != 0 && high_id != 1) {
            if (DialogObject.isChannel(dialog)) {
                TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-lower_id);
                if (chat != null) {
                    if (chat.megagroup) {
                        dialogsGroups.add(dialog);
                    } else {
                        dialogsChannels.add(dialog);
                    }
                }
                if (chat != null && (chat.creator || ChatObject.hasAdminRights(chat)))
                    dialogsAdmin.add(dialog);
            } else if (lower_id < 0) {
                dialogsGroups.add(dialog);
            } else {
                TLRPC.User user = MessagesController.getInstance(currentAccount).getUser((int) dialog.id);
                if (user != null) {
                    if (user.bot)
                        dialogsBots.add(dialog);
                    else
                        dialogsUsers.add(dialog);
                }
            }
        } else {
            TLRPC.EncryptedChat encryptedChat = MessagesController.getInstance(currentAccount).getEncryptedChat(high_id);
            if (encryptedChat != null)
                dialogsUsers.add(dialog);
        }
    }

    public ArrayList<TLRPC.TL_dialog> getDialogs(int type) {
        switch (type) {
            case DialogType.Users:
                return dialogsUsers;
            case DialogType.Groups:
                return dialogsGroups;
            case DialogType.Channels:
                return dialogsChannels;
            case DialogType.Bots:
                return dialogsBots;
            case DialogType.Admin:
                return dialogsAdmin;
            default:
                return null;
        }
    }
}
