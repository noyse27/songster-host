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
    private static final String PREFS = "songster-host";
    private static final String KEY_SERVER_URL = "server-url";

    private SharedPreferences prefs;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String serverUrl = prefs.getString(KEY_SERVER_URL, "");
        if (serverUrl == null || serverUrl.trim().isEmpty()) {
            showSetup("");
        } else {
            showHostApp(serverUrl);
        }
    }

    private void showSetup(String initialUrl) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(64, 56, 64, 56);
        root.setBackgroundColor(Color.rgb(9, 13, 31));

        TextView title = new TextView(this);
        title.setText("Songster Host");
        title.setTextColor(Color.WHITE);
        title.setTextSize(32);
        root.addView(title);

        TextView hint = new TextView(this);
        hint.setText("Songster-URL eingeben. Die App lädt danach automatisch den Hostmodus.");
        hint.setTextColor(Color.rgb(190, 198, 220));
        hint.setTextSize(18);
        hint.setPadding(0, 20, 0, 20);
        root.addView(hint);

        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setText(initialUrl);
        input.setHint("https://songster.example.de");
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.rgb(150, 160, 185));
        input.setTextSize(20);
        input.setSelectAllOnFocus(true);
        root.addView(input);

        Button connect = new Button(this);
        connect.setText("Verbinden");
        connect.setTextSize(18);
        connect.setAllCaps(false);
        connect.setPadding(16, 10, 16, 10);
        connect.setOnClickListener((View view) -> {
            String normalized = normalizeServerUrl(input.getText().toString());
            if (!normalized.isEmpty()) {
                prefs.edit().putString(KEY_SERVER_URL, normalized).apply();
                showHostApp(normalized);
            }
        });
        root.addView(connect);

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
            String current = prefs.getString(KEY_SERVER_URL, "");
            prefs.edit().remove(KEY_SERVER_URL).apply();
            showSetup(current == null ? "" : current);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
