package dev.cheat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {
    private static final int REQUEST_OVERLAY = 1001;
    private EditText keyInput;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(28, 28, 28, 28);
        root.setBackgroundColor(Color.rgb(13, 14, 18));

        TextView title = new TextView(this);
        title.setText("DEV  |  OXIDE");
        title.setTextColor(Color.WHITE);
        title.setTextSize(27);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView info = new TextView(this);
        info.setText("Введите ключ активации");
        info.setTextColor(0xFFB8B8C2);
        info.setTextSize(15);
        info.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(-1, -2);
        ip.setMargins(0, 24, 0, 12);
        root.addView(info, ip);

        keyInput = new EditText(this);
        keyInput.setSingleLine(true);
        keyInput.setHint("Ключ");
        keyInput.setHintTextColor(0xFF888894);
        keyInput.setTextColor(Color.WHITE);
        keyInput.setPadding(16, 8, 16, 8);
        keyInput.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF8B63D9));
        root.addView(keyInput, new LinearLayout.LayoutParams(-1, -2));

        Button activate = new Button(this);
        activate.setText("АКТИВИРОВАТЬ");
        root.addView(activate, new LinearLayout.LayoutParams(-1, -2));

        TextView note = new TextView(this);
        note.setText("DEV | OXIDE  •  версия 2.0");
        note.setTextColor(0xFF777783);
        note.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(-1, -2);
        np.setMargins(0, 20, 0, 0);
        root.addView(note, np);

        activate.setOnClickListener(v -> {
            if (!"DevNiko".equals(keyInput.getText().toString().trim())) {
                Toast.makeText(this, "Неверный ключ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Settings.canDrawOverlays(this)) {
                startService(new Intent(this, OverlayService.class));
                Toast.makeText(this, "Активация успешна", Toast.LENGTH_SHORT).show();
            } else {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())), REQUEST_OVERLAY);
            }
        });
        setContentView(root);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OVERLAY && Settings.canDrawOverlays(this)
                && "DevNiko".equals(keyInput.getText().toString().trim())) {
            startService(new Intent(this, OverlayService.class));
        }
    }
}
