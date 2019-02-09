package tw.nekomimi.nekogram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.graphics.ColorUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.RecyclerListView;

public class NekoSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;

    private int rowCount;

    private int connectionRow;
    private int nekoProxyRow;
    private int ipv6Row;
    private int connection2Row;

    private int tabsRow;
    private int hideTabsRow;
    private int tabsToBottomRow;
    private int hideALlRow;
    private int hideUsersRow;
    private int hideGroupsRow;
    private int hideChannelsRow;
    private int hideBotsRow;
    private int hideAdminsRow;
    private int hideTabsCountersRow;
    private int tabsHeightRow;
    private int disableTabsScrollingRow;
    private int disableTabsInfiniteScrollingRow;
    private int tabs2Row;

    private int emojiRow;
    private int useSystemEmojiRow;
    private int singleBigEmojiRow;
    private int emoji2Row;

    private int navigationBarRow;
    private int navigationBarTintRow;
    private int useMessagePanelColorRow;
    private int navigationBar2Row;

    private int settingsRow;
    private int hidePhoneRow;
    private int inappCameraRow;
    private int ignoreBlockedRow;
    private int nameOrderRow;
    private int settings2Row;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        NekoConfig.loadProxyList();

        rowCount = 0;
        connectionRow = rowCount++;
        nekoProxyRow = rowCount++;
        ipv6Row = rowCount++;
        connection2Row = rowCount++;
        tabsRow = rowCount++;
        hideTabsRow = rowCount++;
        tabsToBottomRow = rowCount++;
        hideALlRow = rowCount++;
        hideUsersRow = rowCount++;
        hideGroupsRow = rowCount++;
        hideChannelsRow = rowCount++;
        hideBotsRow = rowCount++;
        hideAdminsRow = rowCount++;
        hideTabsCountersRow = rowCount++;
        disableTabsScrollingRow = rowCount++;
        disableTabsInfiniteScrollingRow = rowCount++;
        tabsHeightRow = rowCount++;
        tabs2Row = rowCount++;
        emojiRow = rowCount++;
        useSystemEmojiRow = rowCount++;
        singleBigEmojiRow = rowCount++;
        emoji2Row = rowCount++;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            navigationBarRow = rowCount++;
            navigationBarTintRow = rowCount++;
            useMessagePanelColorRow = rowCount++;
            navigationBar2Row = rowCount++;
        }
        settingsRow = rowCount++;
        hidePhoneRow = rowCount++;
        inappCameraRow = rowCount++;
        ignoreBlockedRow = rowCount++;
        nameOrderRow = rowCount++;
        settings2Row = rowCount++;

        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("NekoSettings", R.string.NekoSettings));

        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        listView.setAdapter(listAdapter);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (position == ipv6Row) {
                NekoConfig.toggleIPv6();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.useIPv6);
                }
                for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        ConnectionsManager.native_setUseIpv6(a, NekoConfig.useIPv6);
                    }
                }
            } else if (position == hidePhoneRow) {
                NekoConfig.toggleHidePhone();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.hidePhone);
                }
            } else if (position == inappCameraRow) {
                SharedConfig.toggleInappCamera();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.inappCamera);
                }
            } else if (position == ignoreBlockedRow) {
                NekoConfig.toggleIgnoreBlocked();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.ignoreBlocked);
                }
            } else if (position == useSystemEmojiRow) {
                SharedConfig.useSystemEmoji = !SharedConfig.useSystemEmoji;
                SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                editor.putBoolean("useSystemEmoji", SharedConfig.useSystemEmoji);
                editor.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.useSystemEmoji);
                }
            } else if (position == singleBigEmojiRow) {
                SharedConfig.allowBigEmoji = !SharedConfig.allowBigEmoji;
                SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                editor.putBoolean("allowBigEmoji", SharedConfig.allowBigEmoji);
                editor.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.allowBigEmoji);
                }
            } else if (position == nameOrderRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("NameOrder", R.string.NameOrder));
                CharSequence[] items = new CharSequence[]{
                        LocaleController.getString("FirstLast", R.string.FirstLast),
                        LocaleController.getString("LastFirst", R.string.LastFirst),
                };
                builder.setItems(items, (dialog, which) -> {
                    NekoConfig.setNameOrder(which + 1);
                    listAdapter.notifyItemChanged(nameOrderRow);
                });
                showDialog(builder.create());
            } else if (position == nekoProxyRow) {
                if (LocaleController.isRTL && x <= AndroidUtilities.dp(76) || !LocaleController.isRTL && x >= view.getMeasuredWidth() - AndroidUtilities.dp(76)) {
                    NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
                    boolean enabled = preferences.getBoolean("proxy_enabled", false) && !NekoConfig.proxyList.isEmpty();
                    if (NekoConfig.currentProxy == null) {
                        if (!NekoConfig.proxyList.isEmpty()) {
                            NekoConfig.currentProxy = NekoConfig.proxyList.get(0);

                            if (!enabled) {
                                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE).edit();
                                editor.putString("proxy_ip", NekoConfig.currentProxy.address);
                                editor.putString("proxy_pass", NekoConfig.currentProxy.password);
                                editor.putString("proxy_user", NekoConfig.currentProxy.username);
                                editor.putInt("proxy_port", NekoConfig.currentProxy.port);
                                editor.putString("proxy_secret", NekoConfig.currentProxy.secret);
                                editor.putString("proxy_hash", NekoConfig.currentProxy.hash);
                                editor.commit();
                            }
                        } else {
                            presentFragment(new NekoProxyActivity());
                            return;
                        }
                    }
                    enabled = !enabled;
                    checkCell.setChecked(enabled);

                    SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE).edit();
                    editor.putBoolean("proxy_enabled", enabled);
                    editor.commit();

                    ConnectionsManager.setProxySettings(enabled, NekoConfig.currentProxy.address, NekoConfig.currentProxy.port, NekoConfig.currentProxy.username, NekoConfig.currentProxy.password, NekoConfig.currentProxy.secret);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged);
                } else {
                    presentFragment(new NekoProxyActivity());
                }
            } else if (position == hideAdminsRow) {
                TabsConfig.toggleHideAdmins();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(TabsConfig.hideAdmins);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.refreshTabs, 3);
            } else if (position == hideALlRow) {
                TabsConfig.toggleHideALl();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(TabsConfig.hideALl);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.refreshTabs, 3);
            } else if (position == hideBotsRow) {
                TabsConfig.togglehideBots();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(TabsConfig.hideBots);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.refreshTabs, 3);
            } else if (position == hideChannelsRow) {
                TabsConfig.toggleHideChannels();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(TabsConfig.hideChannels);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.refreshTabs, 3);
            } else if (position == hideGroupsRow) {
                TabsConfig.toggleHideGroups();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(TabsConfig.hideGroups);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.refreshTabs, 3);
            } else if (position == hideTabsCountersRow) {
                TabsConfig.toggleHideTabsCounters();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(TabsConfig.hideTabsCounters);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.refreshTabs, 3);
            } else if (position == hideTabsRow) {
                TabsConfig.toggleHideTabs();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(TabsConfig.hideTabs);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.refreshTabs, 2);
            } else if (position == hideUsersRow) {
                TabsConfig.toggleHideUsers();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(TabsConfig.hideUsers);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.refreshTabs, 3);
            } else if (position == tabsToBottomRow) {
                TabsConfig.toggleTabsToBottom();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(TabsConfig.tabsToBottom);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.refreshTabs, 1);
            } else if (position == tabsHeightRow) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("TabsHeight", R.string.TabsHeight));
                final NumberPicker numberPicker = new NumberPicker(getParentActivity());
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(100);
                numberPicker.setValue(TabsConfig.tabsHeight);
                builder.setView(numberPicker);
                builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), (dialog, which) -> {
                    TabsConfig.setTabsHeight(numberPicker.getValue());
                    if (listAdapter != null) {
                        listAdapter.notifyItemChanged(position);
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.refreshTabs, 2);
                });
                showDialog(builder.create());
            } else if (position == disableTabsScrollingRow) {
                TabsConfig.toggleDisableTabsScrolling();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(TabsConfig.disableTabsScrolling);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.refreshTabs, 2);
            } else if (position == disableTabsInfiniteScrollingRow) {
                TabsConfig.toggleDisableTabsInfiniteScrolling();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(TabsConfig.disableTabsInfiniteScrolling);
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.refreshTabs, 2);
            } else if (position == navigationBarTintRow) {
                NekoConfig.toggleNavigationBarTint();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.navigationBarTint);
                }
                int color = Theme.getColor(NekoConfig.useMessagePanelColor ? Theme.key_chat_messagePanelBackground : Theme.key_actionBarDefault);
                Window window = getParentActivity().getWindow();
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
                        flags ^= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                        window.getDecorView().setSystemUiVisibility(flags);
                    }
                }
            } else if (position == useMessagePanelColorRow) {
                NekoConfig.toggleUseMessagePanelColor();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(NekoConfig.useMessagePanelColor);
                }
                int color = Theme.getColor(NekoConfig.useMessagePanelColor ? Theme.key_chat_messagePanelBackground : Theme.key_actionBarDefault);
                Window window = getParentActivity().getWindow();
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
                        flags ^= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                        window.getDecorView().setSystemUiVisibility(flags);
                    }
                }
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1: {
                    if (position == settings2Row) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                }
                case 2: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == nameOrderRow) {
                        String value;
                        switch (NekoConfig.nameOrder) {
                            case 2:
                                value = LocaleController.getString("LastFirst", R.string.LastFirst);
                                break;
                            case 1:
                            default:
                                value = LocaleController.getString("FirstLast", R.string.FirstLast);
                                break;
                        }
                        textCell.setTextAndValue(LocaleController.getString("NameOrder", R.string.NameOrder), value, false);
                    } else if (position == tabsHeightRow) {
                        textCell.setTextAndValue(LocaleController.getString("TabsHeight", R.string.TabsHeight), String.format("%d", TabsConfig.tabsHeight), false);
                    }
                    break;
                }
                case 3: {
                    TextCheckCell textCell = (TextCheckCell) holder.itemView;
                    if (position == ipv6Row) {
                        textCell.setTextAndCheck(LocaleController.getString("IPv6", R.string.IPv6), NekoConfig.useIPv6, false);
                    } else if (position == hidePhoneRow) {
                        textCell.setTextAndCheck(LocaleController.getString("HidePhone", R.string.HidePhone), NekoConfig.hidePhone, true);
                    } else if (position == inappCameraRow) {
                        textCell.setTextAndCheck(LocaleController.getString("DebugMenuEnableCamera", R.string.DebugMenuEnableCamera), SharedConfig.inappCamera, true);
                    } else if (position == useSystemEmojiRow) {
                        textCell.setTextAndCheck(LocaleController.getString("EmojiUseDefault", R.string.EmojiUseDefault), SharedConfig.useSystemEmoji, true);
                    } else if (position == singleBigEmojiRow) {
                        textCell.setTextAndCheck(LocaleController.getString("EmojiBigSize", R.string.EmojiBigSize), SharedConfig.allowBigEmoji, false);
                    } else if (position == ignoreBlockedRow) {
                        textCell.setTextAndCheck(LocaleController.getString("IgnoreBlocked", R.string.IgnoreBlocked), NekoConfig.ignoreBlocked, true);
                    } else if (position == hideUsersRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TabsHideUsers", R.string.TabsHideUsers), TabsConfig.hideUsers, true);
                    } else if (position == hideTabsRow) {
                        textCell.setTextAndCheck(LocaleController.getString("HideTabs", R.string.HideTabs), TabsConfig.hideTabs, true);
                    } else if (position == hideTabsCountersRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TabsHideCountersRow", R.string.TabsHideCountersRow), TabsConfig.hideTabsCounters, true);
                    } else if (position == hideGroupsRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TabsHideGroups", R.string.TabsHideGroups), TabsConfig.hideGroups, true);
                    } else if (position == hideChannelsRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TabsHideChannels", R.string.TabsHideChannels), TabsConfig.hideChannels, true);
                    } else if (position == hideBotsRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TabsHideBots", R.string.TabsHideBots), TabsConfig.hideBots, true);
                    } else if (position == hideALlRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TabsHideAll", R.string.TabsHideAll), TabsConfig.hideALl, true);
                    } else if (position == hideAdminsRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TabsHideAdmins", R.string.TabsHideAdmins), TabsConfig.hideAdmins, true);
                    } else if (position == tabsToBottomRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TabsToBottom", R.string.TabsToBottom), TabsConfig.tabsToBottom, true);
                    } else if (position == disableTabsScrollingRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TabsDisableScrolling", R.string.TabsDisableScrolling), TabsConfig.disableTabsScrolling, true);
                    } else if (position == disableTabsInfiniteScrollingRow) {
                        textCell.setTextAndCheck(LocaleController.getString("TabsDisableInfiniteScrolling", R.string.TabsDisableInfiniteScrolling), TabsConfig.disableTabsInfiniteScrolling, true);
                    } else if (position == navigationBarTintRow) {
                        textCell.setTextAndCheck(LocaleController.getString("NavigationBarTint", R.string.NavigationBarTint), NekoConfig.navigationBarTint, true);
                    } else if (position == useMessagePanelColorRow) {
                        textCell.setTextAndCheck(LocaleController.getString("UseMessagePanelColor", R.string.UseMessagePanelColor), NekoConfig.navigationBarTint, false);
                    }
                    break;
                }
                case 4: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == settingsRow) {
                        headerCell.setText(LocaleController.getString("Settings", R.string.Settings));
                    } else if (position == emojiRow) {
                        headerCell.setText(LocaleController.getString("Emoji", R.string.Emoji));
                    } else if (position == connectionRow) {
                        headerCell.setText(LocaleController.getString("Connection", R.string.Connection));
                    } else if (position == tabsRow) {
                        headerCell.setText(LocaleController.getString("TabsSettings", R.string.TabsSettings));
                    } else if (position == navigationBarRow) {
                        headerCell.setText(LocaleController.getString("NavigationBar", R.string.NavigationBar));
                    }
                    break;
                }
                case 5: {
                    NotificationsCheckCell checkCell = (NotificationsCheckCell) holder.itemView;
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
                    if (position == nekoProxyRow) {
                        boolean enabled = preferences.getBoolean("proxy_enabled", false) && !NekoConfig.proxyList.isEmpty();
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("NekoProxy", R.string.NekoProxy), LocaleController.getString("NekoProxyLearnMore", R.string.NekoProxyLearnMore), enabled, true);
                    }
                    break;
                }
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == hidePhoneRow || position == inappCameraRow || position == ignoreBlockedRow ||
                    position == useSystemEmojiRow || position == singleBigEmojiRow || position == ipv6Row ||
                    position == nameOrderRow || position == nekoProxyRow || position == hideAdminsRow ||
                    position == hideALlRow || position == hideBotsRow || position == hideChannelsRow ||
                    position == hideGroupsRow || position == hideTabsCountersRow || position == hideTabsRow ||
                    position == hideUsersRow || position == tabsToBottomRow || position == tabsHeightRow ||
                    position == disableTabsScrollingRow || position == disableTabsInfiniteScrollingRow ||
                    position == navigationBarTintRow || position == useMessagePanelColorRow;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 1:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 2:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new NotificationsCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 6:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == settings2Row || position == emoji2Row || position == connection2Row || position == tabs2Row || position == navigationBar2Row) {
                return 1;
            } else if (position == nameOrderRow || position == tabsHeightRow) {
                return 2;
            } else if (position == ipv6Row || position == hidePhoneRow || position == inappCameraRow ||
                    position == ignoreBlockedRow || position == useSystemEmojiRow || position == singleBigEmojiRow || position == hideAdminsRow ||
                    position == hideALlRow || position == hideBotsRow || position == hideChannelsRow || position == hideGroupsRow ||
                    position == hideTabsCountersRow || position == hideTabsRow || position == hideUsersRow || position == tabsToBottomRow ||
                    position == disableTabsScrollingRow || position == disableTabsInfiniteScrollingRow || position == navigationBarTintRow ||
                    position == useMessagePanelColorRow) {
                return 3;
            } else if (position == settingsRow || position == connectionRow || position == emojiRow || position == tabsRow || position == navigationBarRow) {
                return 4;
            } else if (position == nekoProxyRow) {
                return 5;
            }
            return 6;
        }
    }

    @Override
    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{
                new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyCell.class, TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextDetailSettingsCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite),
                new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray),

                new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue),
                new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconBlue),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem),

                new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector),

                new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider),

                new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow),

                new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText),

                new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2),
                new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack),
                new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked),

                new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2),
                new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack),
                new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked),

                new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader),

                new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2),
        };
    }
}
