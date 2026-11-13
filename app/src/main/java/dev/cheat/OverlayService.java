package dev.cheat;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.*;

public class OverlayService extends Service {
    private WindowManager wm;
    private WindowManager.LayoutParams params;
    private LinearLayout root, header, tabs, content;
    private int panelColor = 0xF0161B18;
    private int accent = 0xFF63E65A;
    private String currentTab = "Show";

    @Override public void onCreate() {
        super.onCreate();
        wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        buildPanel();
        params = new WindowManager.LayoutParams(dp(360), dp(390),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.y = dp(90);
        wm.addView(root, params);
    }

    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density); }

    private void buildPanel() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(10), dp(8), dp(10), dp(10));
        root.setBackgroundColor(panelColor);

        header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8), dp(4), dp(4), dp(8));
        TextView pc = text("▣", 27, accent);
        pc.setGravity(Gravity.CENTER);
        RotateAnimation spin = new RotateAnimation(0, 360, 1, 0.5f, 1, 0.5f);
        spin.setDuration(4500);
        spin.setRepeatCount(RotateAnimation.INFINITE);
        pc.startAnimation(spin);
        header.addView(pc, new LinearLayout.LayoutParams(dp(42), dp(38)));

        TextView brand = text("DEV  |  OXIDE", 16, Color.WHITE);
        brand.setGravity(Gravity.CENTER);
        header.addView(brand, new LinearLayout.LayoutParams(0, dp(38), 1));

        TextView close = text("×", 25, 0xFFCCCCCC);
        close.setGravity(Gravity.CENTER);
        close.setOnClickListener(v -> stopSelf());
        header.addView(close, new LinearLayout.LayoutParams(dp(38), dp(38)));
        header.setOnTouchListener(new View.OnTouchListener() {
            int sx, sy; float tx, ty;
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    sx=params.x; sy=params.y; tx=e.getRawX(); ty=e.getRawY(); return true;
                }
                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    params.x=sx+(int)(e.getRawX()-tx); params.y=sy+(int)(e.getRawY()-ty);
                    wm.updateViewLayout(root, params); return true;
                }
                return e.getAction()==MotionEvent.ACTION_UP;
            }
        });
        root.addView(header);

        tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        String[] names = {"Show", "Shoot", "Memory", "Settings"};
        for (String name : names) {
            TextView tab = text(name, 11, Color.WHITE);
            tab.setGravity(Gravity.CENTER);
            tab.setPadding(dp(2), dp(12), dp(2), dp(12));
            tab.setBackgroundColor(name.equals(currentTab) ? 0xFF294329 : 0xFF202522);
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, dp(43), 1);
            tp.setMargins(dp(2), dp(4), dp(2), dp(8));
            tabs.addView(tab, tp);
            tab.setOnClickListener(v -> { currentTab=name; refreshTabs(); showContent(); });
        }
        root.addView(tabs);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(8), dp(6), dp(8), dp(6));
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        showContent();
    }

    private void refreshTabs() {
        for (int i=0; i<tabs.getChildCount(); i++) {
            TextView t=(TextView)tabs.getChildAt(i);
            t.setBackgroundColor(t.getText().toString().equals(currentTab) ? 0xFF294329 : 0xFF202522);
            t.setTextColor(t.getText().toString().equals(currentTab) ? accent : Color.WHITE);
        }
    }

    private void showContent() {
        content.removeAllViews();
        if (currentTab.equals("Show")) {
            section("DISPLAY");
            addInfo("Панель DEV активна");
            addInfo("Режим: демонстрационный");
            addInfo("Оверлей работает независимо от игры");
            addSwitch("Показывать статус", true);
            addSwitch("Компактный вид", false);
        } else if (currentTab.equals("Shoot")) {
            section("TOOLS");
            addInfo("Здесь будут безопасные инструменты интерфейса");
            addSwitch("Тестовый индикатор", false);
            addSwitch("Показывать уведомления", true);
        } else if (currentTab.equals("Memory")) {
            section("APP MEMORY");
            addInfo("Использование памяти самого DEV Cheat");
            addInfo("Состояние: запущено");
            addInfo("Игровая память не читается и не изменяется");
        } else {
            section("APPEARANCE");
            addInfo("Цвет панели");
            addColor("Белый", 0xF0E9E9ED);
            addColor("Серый", 0xF034363B);
            addColor("Фиолетовый", 0xF0251838);
            addColor("Зелёный", 0xF0161B18);
            addInfo("Оформление применяется сразу");
        }
    }

    private void section(String s) {
        TextView t=text(s,12,accent); t.setPadding(0,dp(8),0,dp(8)); content.addView(t);
    }
    private void addInfo(String s) {
        TextView t=text(s,13,0xFFD0D0D0); t.setPadding(dp(8),dp(9),dp(8),dp(9));
        t.setBackgroundColor(0x332FFFFFF); content.addView(t);
        LinearLayout.LayoutParams p=(LinearLayout.LayoutParams)t.getLayoutParams(); p.setMargins(0,dp(3),0,dp(3)); t.setLayoutParams(p);
    }
    private void addSwitch(String s, boolean checked) {
        Switch sw=new Switch(this); sw.setText(s); sw.setTextColor(Color.WHITE); sw.setChecked(checked);
        sw.setPadding(dp(8),dp(6),dp(8),dp(6)); content.addView(sw);
    }
    private void addColor(String s, int color) {
        Button b=new Button(this); b.setText(s); b.setTextColor(Color.WHITE);
        b.setOnClickListener(v->{ panelColor=color; root.setBackgroundColor(panelColor); });
        content.addView(b);
    }
    private TextView text(String s,int size,int color) {
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); return t;
    }
    @Override public void onDestroy() {
        if(root!=null) try { wm.removeView(root); } catch(Exception ignored) {}
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent i) { return null; }
}
