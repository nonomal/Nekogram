package tw.nekomimi.nekogram;

import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

public class TabsDialogs {
    public static ArrayList<TLRPC.TL_dialog> dialogsAll = new ArrayList<>();
    public static ArrayList<TLRPC.TL_dialog> dialogsUsers = new ArrayList<>();
    public static ArrayList<TLRPC.TL_dialog> dialogsGroups = new ArrayList<>();
    public static ArrayList<TLRPC.TL_dialog> dialogsChannels = new ArrayList<>();
    public static ArrayList<TLRPC.TL_dialog> dialogsBots = new ArrayList<>();
    public static ArrayList<TLRPC.TL_dialog> dialogsAdmin = new ArrayList<>();

    private int currentAccount;
    private static volatile TabsDialogs[] Instance = new TabsDialogs[UserConfig.MAX_ACCOUNT_COUNT];
    public TabsDialogs(int num) {
        currentAccount = num;
    }
    public static TabsDialogs getInstance(int num) {
        TabsDialogs localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (MessagesController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new TabsDialogs(num);
                }
            }
        }
        return localInstance;
    }

    public void cleanup() {
        dialogsAll.clear();
        dialogsUsers.clear();
        dialogsGroups.clear();
        dialogsChannels.clear();
        dialogsBots.clear();
        dialogsAdmin.clear();
    }

    public void deleteDialog(TLRPC.TL_dialog dialog) {
        dialogsAll.remove(dialog);
        dialogsUsers.remove(dialog);
        dialogsGroups.remove(dialog);
        dialogsChannels.remove(dialog);
        dialogsBots.remove(dialog);
        dialogsAdmin.remove(dialog);
    }

    public void sortDialogs(MessagesController instance, TLRPC.TL_dialog dialog, int high_id, int lower_id) {
        dialogsAll.add(dialog);
        if (lower_id != 0 && high_id != 1) {
            if (DialogObject.isChannel(dialog)) {
                TLRPC.Chat chat = instance.getChat(-lower_id);
                if (chat != null) {
                    if (chat.megagroup) {
                        dialogsGroups.add(dialog);
                    } else {
                        dialogsChannels.add(dialog);
                    }
                }
                if (chat != null && (chat.creator || ChatObject.hasAdminRights(chat)))
                    dialogsAdmin.add(dialog);
            } else if(lower_id < 0){
                dialogsGroups.add(dialog);
            } else {
                TLRPC.User user = instance.getUser((int) dialog.id);
                if (user != null) {
                    if (user.bot)
                        dialogsBots.add(dialog);
                    else
                        dialogsUsers.add(dialog);
                }
            }
        } else {
            TLRPC.EncryptedChat encryptedChat = instance.getEncryptedChat(high_id);
            if (encryptedChat != null)
                dialogsUsers.add(dialog);
        }
    }

    public ArrayList<TLRPC.TL_dialog> getDialogs(int type) {
        switch (type) {
            case TabsHelper.DialogType.All:
                return dialogsAll;
            case TabsHelper.DialogType.Users:
                return dialogsUsers;
            case TabsHelper.DialogType.Groups:
                return dialogsGroups;
            case TabsHelper.DialogType.Channels:
                return dialogsChannels;
            case TabsHelper.DialogType.Bots:
                return dialogsBots;
            case TabsHelper.DialogType.Admin:
                return dialogsAdmin;
            default:
                return null;
        }
    }
}
