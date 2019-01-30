package tw.nekomimi.nekogram;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.SerializedData;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class NekoConfig {

    private static boolean configLoaded;
    private static final Object sync = new Object();

    public static boolean useIPv6 = false;
    public static boolean hidePhone = true;
    public static boolean ignoreBlocked = false;

    public static int nameOrder = 1;

    static {
        loadConfig();
    }


    public static void saveConfig() {
        synchronized (sync) {
            try {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfing", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("useIPv6", useIPv6);
                editor.putBoolean("hidePhone", hidePhone);
                editor.putBoolean("ignoreBlocked", ignoreBlocked);
                editor.putInt("nameOrder", nameOrder);
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
            useIPv6 = preferences.getBoolean("useIPv6", false);
            hidePhone = preferences.getBoolean("hidePhone", true);
            ignoreBlocked = preferences.getBoolean("ignoreBlocked", false);
            nameOrder = preferences.getInt("nameOrder",1);
            configLoaded = true;
        }
    }

    public static void toggleIPv6() {
        useIPv6 = !useIPv6;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("useIPv6", useIPv6);
        editor.commit();
    }

    public static void toggleHidePhone() {
        hidePhone = !hidePhone;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hidePhone", hidePhone);
        editor.commit();
    }

    public static void toggleIgnoreBlocked() {
        ignoreBlocked = !ignoreBlocked;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("ignoreBlocked", ignoreBlocked);
        editor.commit();
    }

    public static void setNameOrder(int order) {
        nameOrder = order;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("nameOrder", nameOrder);
        editor.commit();
    }

    public static class ProxyInfo {

        public String address;
        public int port;
        public String username;
        public String password;
        public String secret;
        public String name;
        public String hash;
        public String provider;

        public long proxyCheckPingId;
        public long ping;
        public boolean checking;
        public boolean available;
        public long availableCheckTime;

        public ProxyInfo(String a, int p, String u, String pw, String s, String n, String h , String pr) {
            address = a;
            port = p;
            username = u;
            password = pw;
            secret = s;
            name = n;
            hash = h;
            provider = pr;
            if (address == null) {
                address = "";
            }
            if (password == null) {
                password = "";
            }
            if (username == null) {
                username = "";
            }
            if (secret == null) {
                secret = "";
            }
            if (name == null) {
                name = "";
            }
            if (hash == null) {
                hash = "";
            }
            if (provider == null) {
                provider = "";
            }
        }
    }


    public static ArrayList<ProxyInfo> proxyList = new ArrayList<>();
    private static boolean proxyListLoaded;
    public static ProxyInfo currentProxy;
    private static ProxyLoadTask currentTask;

    private static class ProxyLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {

        protected NativeByteBuffer doInBackground(Void... voids) {
            ByteArrayOutputStream outbuf = null;
            InputStream httpConnectionStream = null;

            try {
                URL downloadUrl = new URL("https://raw.githubusercontent.com/NekoInverter/NekoProxy/master/proxylist.json");
                URLConnection httpConnection = downloadUrl.openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                httpConnection.setConnectTimeout(5000);
                httpConnection.setReadTimeout(5000);
                httpConnection.connect();
                httpConnectionStream = httpConnection.getInputStream();

                outbuf = new ByteArrayOutputStream();

                byte[] data = new byte[1024 * 32];
                while (true) {
                    int read = httpConnectionStream.read(data);
                    if (read > 0) {
                        outbuf.write(data, 0, read);
                    } else if (read == -1) {
                        break;
                    } else {
                        break;
                    }
                }

                JSONObject jsonObject = new JSONObject(new String(outbuf.toByteArray(), "UTF-8"));
                JSONArray array = jsonObject.getJSONArray("proxies");
                int len = array.length();
                ArrayList<ProxyInfo> arrayList = new ArrayList<>(len);
                for (int a = 0; a < len; a++) {
                    String proxyAddr = array.getJSONObject(a).getString("server");
                    int proxyPort = array.getJSONObject(a).getInt("port");
                    String proxyName = array.getJSONObject(a).getString("name");
                    String proxyProvider = array.getJSONObject(a).getString("provider");
                    String proxyHash = array.getJSONObject(a).getString("hash");
                    String proxyType = array.getJSONObject(a).getString("type");
                    String proxySecret = null;
                    String proxyUser = null;
                    String proxyPass = null;
                    if(proxyType.equals("MTProxy")){
                        proxySecret = array.getJSONObject(a).getString("secret");
                        if(proxySecret.equals("butterflyday")){
                            Calendar calendar = Calendar.getInstance();
                            Date date = calendar.getTime();
                            String secret = new SimpleDateFormat("YYYYMMdd", Locale.ENGLISH).format(date.getTime());
                            MessageDigest md5 = null;
                            try {
                                md5 = MessageDigest.getInstance("MD5");
                                byte[] bytes = md5.digest(secret.getBytes());
                                StringBuilder result = new StringBuilder();
                                for (byte b : bytes) {
                                    String temp = Integer.toHexString(b & 0xff);
                                    if (temp.length() == 1) {
                                        temp = "0" + temp;
                                    }
                                    result.append(temp);
                                }
                                secret = result.toString();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                            proxySecret = secret;

                        }
                    } else if(proxyType.equals("SOCKS5")){
                        proxyUser = array.getJSONObject(a).getString("user");
                        proxyPass = array.getJSONObject(a).getString("pass");
                    }
                    arrayList.add(new ProxyInfo(proxyAddr,proxyPort,proxyUser,proxyPass,proxySecret,proxyName,proxyHash,proxyProvider));
                }
                proxyList = arrayList;
                saveProxyList();
                proxyListLoaded = false;
                loadProxyList();
            } catch (Throwable e) {
                FileLog.e(e);
            } finally {
                try {
                    if (httpConnectionStream != null) {
                        httpConnectionStream.close();
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                try {
                    if (outbuf != null) {
                        outbuf.close();
                    }
                } catch (Exception ignore) {

                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(final NativeByteBuffer result) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged);
            currentTask = null;
        }
    }
    public static void updateProxyList() {
        Utilities.stageQueue.postRunnable(() -> {
            if (currentTask != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("don't start task, current task = " + currentTask);
                }
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start proxy task");
            }
            ProxyLoadTask task = new ProxyLoadTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
            currentTask = task;
        });
    }

    public static void loadProxyList() {
        if (proxyListLoaded) {
            return;
        }
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        String proxyHash = preferences.getString("proxy_hash", "");

        proxyListLoaded = true;
        proxyList.clear();
        currentProxy = null;
        String list = preferences.getString("proxy_list", null);
        if (!TextUtils.isEmpty(list)) {
            byte[] bytes = Base64.decode(list, Base64.DEFAULT);
            SerializedData data = new SerializedData(bytes);
            int count = data.readInt32(false);
            for (int a = 0; a < count; a++) {
                ProxyInfo info = new ProxyInfo(
                        data.readString(false),
                        data.readInt32(false),
                        data.readString(false),
                        data.readString(false),
                        data.readString(false),
                        data.readString(false),
                        data.readString(false),
                        data.readString(false));
                proxyList.add(info);
                if (!TextUtils.isEmpty(proxyHash)) {
                    if (proxyHash.equals(info.hash)) {
                        currentProxy = info;
                    }
                }
            }
            data.cleanup();
        } else {
            updateProxyList();
        }
    }

    public static void saveProxyList() {
        SerializedData serializedData = new SerializedData();
        int count = proxyList.size();
        serializedData.writeInt32(count);
        for (int a = 0; a < count; a++) {
            ProxyInfo info = proxyList.get(a);
            serializedData.writeString(info.address != null ? info.address : "");
            serializedData.writeInt32(info.port);
            serializedData.writeString(info.username != null ? info.username : "");
            serializedData.writeString(info.password != null ? info.password : "");
            serializedData.writeString(info.secret != null ? info.secret : "");
            serializedData.writeString(info.name != null ? info.name : "");
            serializedData.writeString(info.hash != null ? info.hash : "");
            serializedData.writeString(info.provider != null ? info.provider : "");
        }
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        preferences.edit().putString("proxy_list", Base64.encodeToString(serializedData.toByteArray(), Base64.NO_WRAP)).commit();
        serializedData.cleanup();
    }

}
