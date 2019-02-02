package tw.nekomimi.nekogram;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

public class MessageHelper {

    private static volatile MessageHelper[] Instance = new MessageHelper[UserConfig.MAX_ACCOUNT_COUNT];
    private int currentAccount;
    private int reqId;
    private int mergeReqId;
    private int lastReqId;
    private int messagesSearchCount[] = new int[]{0, 0};

    public MessageHelper(int num) {
        currentAccount = num;
    }

    public static MessageHelper getInstance(int num) {
        MessageHelper localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (MessageHelper.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new MessageHelper(num);
                }
            }
        }
        return localInstance;
    }

    public void deleteUserChannelHistoryWithSearch(final long dialog_id, final TLRPC.User user) {
        deleteUserChannelHistoryWithSearch(dialog_id, user, false);
    }

    public void deleteUserChannelHistoryWithSearch(final long dialog_id, final TLRPC.User user, final boolean internal) {
        boolean firstQuery = !internal;
        if (reqId != 0) {
            ConnectionsManager.getInstance(currentAccount).cancelRequest(reqId, true);
            reqId = 0;
        }
        if (mergeReqId != 0) {
            ConnectionsManager.getInstance(currentAccount).cancelRequest(mergeReqId, true);
            mergeReqId = 0;
        }
        if (firstQuery) {
            TLRPC.InputPeer inputPeer = MessagesController.getInstance(currentAccount).getInputPeer((int) dialog_id);
            if (inputPeer == null) {
                return;
            }
            final TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
            req.peer = inputPeer;
            req.limit = 1;
            req.q = "";
            if (user != null) {
                req.from_id = MessagesController.getInstance(currentAccount).getInputUser(user);
                req.flags |= 1;
            }
            req.filter = new TLRPC.TL_inputMessagesFilterEmpty();
            mergeReqId = ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() {
                @Override
                public void run(final TLObject response, final TLRPC.TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mergeReqId = 0;
                            if (response != null) {
                                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                                messagesSearchCount[0] = res.count;
                                FileLog.d("NekoRunner: Total " + String.valueOf(messagesSearchCount[0]));
                                deleteUserChannelHistoryWithSearch(dialog_id, user, true);
                            }
                        }
                    });
                }
            }, ConnectionsManager.RequestFlagFailOnServerErrors);
        } else {
            final TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
            req.peer = MessagesController.getInstance(currentAccount).getInputPeer((int) dialog_id);
            if (req.peer == null) {
                return;
            }
            req.limit = 100;
            req.q = "";
            req.offset_id = 0;
            if (user != null) {
                req.from_id = MessagesController.getInstance(currentAccount).getInputUser(user);
                req.flags |= 1;
            }
            req.filter = new TLRPC.TL_inputMessagesFilterEmpty();
            final int currentReqId = ++lastReqId;
            reqId = ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() {
                @Override
                public void run(final TLObject response, final TLRPC.TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (currentReqId == lastReqId) {
                                reqId = 0;
                                if (response != null) {
                                    TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                                    messagesSearchCount[1] = res.messages.size();
                                    for (int a = 0; a < res.messages.size(); a++) {
                                        TLRPC.Message message = res.messages.get(a);
                                        if (message instanceof TLRPC.TL_messageEmpty || message.action instanceof TLRPC.TL_messageActionHistoryClear || message instanceof TLRPC.TL_messageService) {
                                            res.messages.remove(a);
                                            a--;
                                        }
                                    }
                                    ArrayList<Integer> ids = new ArrayList<>();
                                    ArrayList<Long> random_ids = new ArrayList<>();
                                    int channelId = 0;
                                    for (int a = 0; a < res.messages.size(); a++) {
                                        TLRPC.Message message = res.messages.get(a);
                                        ids.add(message.id);
                                        if (message.random_id != 0) {
                                            random_ids.add(message.random_id);
                                        }
                                        channelId = message.to_id.channel_id;
                                    }
                                    MessagesController.getInstance(currentAccount).deleteMessages(ids, random_ids, null, channelId, true);
                                    messagesSearchCount[0] = messagesSearchCount[0] - messagesSearchCount[1];
                                    FileLog.d("NekoRunner: Found " + String.valueOf(messagesSearchCount[1]));
                                    FileLog.d("NekoRunner: After " + String.valueOf(messagesSearchCount[0]));
                                    if (messagesSearchCount[0] > 0) {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        deleteUserChannelHistoryWithSearch(dialog_id, user, true);
                                    }
                                }
                            }
                        }
                    });
                }
            }, ConnectionsManager.RequestFlagFailOnServerErrors);
        }
    }

}
