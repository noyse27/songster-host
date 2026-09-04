package de.adolar.songsterhost;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String PREFS = "adolar-host";
    private static final String LEGACY_PREFS = "songster-host";
    private static final String LEGACY_KEY_SERVER_URL = "server-url";
    private static final String GAME_SONGSTER = "songster";
    private static final String GAME_BLOEKI = "bloeki";
    private static final String KEY_SONGSTER_SERVER_URL = "songster-server-url";
    private static final String KEY_BLOEKI_SERVER_URL = "bloeki-server-url";

    private SharedPreferences prefs;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        migrateLegacySongsterUrl();
        showGamePicker();
    }

    private void migrateLegacySongsterUrl() {
        SharedPreferences legacyPrefs = getSharedPreferences(LEGACY_PREFS, MODE_PRIVATE);
        String legacyUrl = legacyPrefs.getString(LEGACY_KEY_SERVER_URL, "");
        String existingSongsterUrl = prefs.getString(KEY_SONGSTER_SERVER_URL, "");
        if (legacyUrl != null && !legacyUrl.trim().isEmpty() && (existingSongsterUrl == null || existingSongsterUrl.trim().isEmpty())) {
            prefs.edit().putString(KEY_SONGSTER_SERVER_URL, legacyUrl).apply();
        }
    }

    private String serverUrlKey(String game) {
        return GAME_BLOEKI.equals(game) ? KEY_BLOEKI_SERVER_URL : KEY_SONGSTER_SERVER_URL;
    }

    private String gameTitle(String game) {
        return GAME_BLOEKI.equals(game) ? "blöki" : "Songster";
    }

    private String gameExampleUrl(String game) {
        return GAME_BLOEKI.equals(game) ? "https://bloeki.example.de" : "https://songster.example.de";
    }

    private TextView createText(String text, int color, float textSize) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(color);
        view.setTextSize(textSize);
        return view;
    }

    private Button createButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(20);
        button.setAllCaps(false);
        button.setPadding(16, 12, 16, 12);
        return button;
    }

    private LinearLayout createRoot() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(64, 56, 64, 56);
        root.setBackgroundColor(Color.rgb(9, 13, 31));
        return root;
    }

    private void showGamePicker() {
        webView = null;
        LinearLayout root = createRoot();

        TextView title = createText("Adolar Host", Color.WHITE, 34);
        root.addView(title);

        TextView hint = createText("Was soll dieses Anzeigegerät hosten?", Color.rgb(190, 198, 220), 19);
        hint.setPadding(0, 20, 0, 20);
        root.addView(hint);

        Button songster = createButton("Songster");
        songster.setOnClickListener((View view) -> selectGame(GAME_SONGSTER));
        root.addView(songster);

        Button bloeki = createButton("blöki");
        bloeki.setOnClickListener((View view) -> selectGame(GAME_BLOEKI));
        root.addView(bloeki);

        TextView footer = createText("Menü öffnet diese Auswahl jederzeit erneut.", Color.rgb(150, 160, 185), 14);
        footer.setPadding(0, 24, 0, 0);
        root.addView(footer);

        setContentView(root);
    }

    private void selectGame(String game) {
        String serverUrl = prefs.getString(serverUrlKey(game), "");
        if (serverUrl == null || serverUrl.trim().isEmpty()) {
            showSetup(game, "");
        } else {
            showHostApp(serverUrl);
        }
    }

    private void showSetup(String game, String initialUrl) {
        webView = null;
        LinearLayout root = createRoot();

        String titleText = gameTitle(game) + " Host";
        TextView title = createText(titleText, Color.WHITE, 32);
        root.addView(title);

        TextView hint = createText(gameTitle(game) + "-URL eingeben. Die App lädt danach automatisch den Hostmodus.", Color.rgb(190, 198, 220), 18);
        hint.setPadding(0, 20, 0, 20);
        root.addView(hint);

        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setText(initialUrl);
        input.setHint(gameExampleUrl(game));
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.rgb(150, 160, 185));
        input.setTextSize(20);
        input.setSelectAllOnFocus(true);
        root.addView(input);

        Button connect = createButton("Verbinden");
        connect.setOnClickListener((View view) -> {
            String normalized = normalizeServerUrl(input.getText().toString());
            if (!normalized.isEmpty()) {
                prefs.edit().putString(serverUrlKey(game), normalized).apply();
                showHostApp(normalized);
            }
        });
        root.addView(connect);

        Button back = createButton("Andere App wählen");
        back.setOnClickListener((View view) -> showGamePicker());
        root.addView(back);

        setContentView(root);
        input.requestFocus();
    }

    private void showHostApp(String serverUrl) {
        webView = new WebView(this);
        webView.setBackgroundColor(Color.BLACK);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        setContentView(webView);
        webView.loadUrl(serverUrl + "/host-app");
    }

    private String normalizeServerUrl(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.isEmpty()) return "";
        if (!value.startsWith("http://") && !value.startsWith("https://")) value = "https://" + value;
        while (value.endsWith("/")) value = value.substring(0, value.length() - 1);
        return value;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            showGamePicker();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && webView != null) {
            showGamePicker();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
