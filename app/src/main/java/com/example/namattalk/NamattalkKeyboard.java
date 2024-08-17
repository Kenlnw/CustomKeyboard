package com.example.namattalk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NamattalkKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboard;
    private Keyboard.Key enterKey;
    private Vibrator vibrator;
    private StringBuilder composingText = new StringBuilder();
    private boolean isCaps = false;

    private boolean isVowelOrY(char c) {
        return "aiueoy".indexOf(c) != -1;
    }

    private static final Map<String, String> romajiToKanaMap = new HashMap<>();
    static {
        romajiToKanaMap.put("a", "あ");
        romajiToKanaMap.put("i", "い");
        romajiToKanaMap.put("u", "う");
        romajiToKanaMap.put("e", "え");
        romajiToKanaMap.put("o", "お");
        romajiToKanaMap.put("ka", "か");
        romajiToKanaMap.put("ki", "き");
        romajiToKanaMap.put("ku", "く");
        romajiToKanaMap.put("ke", "け");
        romajiToKanaMap.put("ko", "こ");
        romajiToKanaMap.put("sa", "さ");
        romajiToKanaMap.put("shi", "し");
        romajiToKanaMap.put("su", "す");
        romajiToKanaMap.put("se", "せ");
        romajiToKanaMap.put("so", "そ");
        romajiToKanaMap.put("ta", "た");
        romajiToKanaMap.put("chi", "ち");
        romajiToKanaMap.put("tsu", "つ");
        romajiToKanaMap.put("te", "て");
        romajiToKanaMap.put("to", "と");
        romajiToKanaMap.put("na", "な");
        romajiToKanaMap.put("ni", "に");
        romajiToKanaMap.put("nu", "ぬ");
        romajiToKanaMap.put("ne", "ね");
        romajiToKanaMap.put("no", "の");
        romajiToKanaMap.put("ha", "は");
        romajiToKanaMap.put("hi", "ひ");
        romajiToKanaMap.put("fu", "ふ");
        romajiToKanaMap.put("he", "へ");
        romajiToKanaMap.put("ho", "ほ");
        romajiToKanaMap.put("ma", "ま");
        romajiToKanaMap.put("mi", "み");
        romajiToKanaMap.put("mu", "む");
        romajiToKanaMap.put("me", "め");
        romajiToKanaMap.put("mo", "も");
        romajiToKanaMap.put("ya", "や");
        romajiToKanaMap.put("yu", "ゆ");
        romajiToKanaMap.put("yo", "よ");
        romajiToKanaMap.put("ra", "ら");
        romajiToKanaMap.put("ri", "り");
        romajiToKanaMap.put("ru", "る");
        romajiToKanaMap.put("re", "れ");
        romajiToKanaMap.put("ro", "ろ");
        romajiToKanaMap.put("wa", "わ");
        romajiToKanaMap.put("wo", "を");
        romajiToKanaMap.put("ga", "が");
        romajiToKanaMap.put("gi", "ぎ");
        romajiToKanaMap.put("gu", "ぐ");
        romajiToKanaMap.put("ge", "げ");
        romajiToKanaMap.put("go", "ご");
        romajiToKanaMap.put("za", "ざ");
        romajiToKanaMap.put("ji", "じ");
        romajiToKanaMap.put("zu", "ず");
        romajiToKanaMap.put("ze", "ぜ");
        romajiToKanaMap.put("zo", "ぞ");
        romajiToKanaMap.put("da", "だ");
        romajiToKanaMap.put("ji", "ぢ");
        romajiToKanaMap.put("zu", "づ");
        romajiToKanaMap.put("de", "で");
        romajiToKanaMap.put("do", "ど");
        romajiToKanaMap.put("ba", "ば");
        romajiToKanaMap.put("bi", "び");
        romajiToKanaMap.put("bu", "ぶ");
        romajiToKanaMap.put("be", "べ");
        romajiToKanaMap.put("bo", "ぼ");
        romajiToKanaMap.put("pa", "ぱ");
        romajiToKanaMap.put("pi", "ぴ");
        romajiToKanaMap.put("pu", "ぷ");
        romajiToKanaMap.put("pe", "ぺ");
        romajiToKanaMap.put("po", "ぽ");
        romajiToKanaMap.put("kya", "きゃ");
        romajiToKanaMap.put("kyu", "きゅ");
        romajiToKanaMap.put("kyo", "きょ");
        romajiToKanaMap.put("gya", "ぎゃ");
        romajiToKanaMap.put("gyu", "ぎゅ");
        romajiToKanaMap.put("gyo", "ぎょ");
        romajiToKanaMap.put("sha", "しゃ");
        romajiToKanaMap.put("shu", "しゅ");
        romajiToKanaMap.put("sho", "しょ");
        romajiToKanaMap.put("ja", "じゃ");
        romajiToKanaMap.put("ju", "じゅ");
        romajiToKanaMap.put("jo", "じょ");
        romajiToKanaMap.put("cha", "ちゃ");
        romajiToKanaMap.put("chu", "ちゅ");
        romajiToKanaMap.put("cho", "ちょ");
        romajiToKanaMap.put("ja", "ぢゃ");
        romajiToKanaMap.put("ju", "ぢゅ");
        romajiToKanaMap.put("jo", "ぢょ");
        romajiToKanaMap.put("nya", "にゃ");
        romajiToKanaMap.put("nyu", "にゅ");
        romajiToKanaMap.put("nyo", "にょ");
        romajiToKanaMap.put("hya", "ひゃ");
        romajiToKanaMap.put("hyu", "ひゅ");
        romajiToKanaMap.put("hyo", "ひょ");
        romajiToKanaMap.put("bya", "びゃ");
        romajiToKanaMap.put("byu", "びゅ");
        romajiToKanaMap.put("byo", "びょ");
        romajiToKanaMap.put("pya", "ぴゃ");
        romajiToKanaMap.put("pyu", "ぴゅ");
        romajiToKanaMap.put("pyo", "ぴょ");
        romajiToKanaMap.put("mya", "みゃ");
        romajiToKanaMap.put("myu", "みゅ");
        romajiToKanaMap.put("myo", "みょ");
        romajiToKanaMap.put("rya", "りゃ");
        romajiToKanaMap.put("ryu", "りゅ");
        romajiToKanaMap.put("ryo", "りょ");
        romajiToKanaMap.put("va", "ゔぁ");
        romajiToKanaMap.put("vu", "ゔ");
        romajiToKanaMap.put("vo", "ゔぉ");

    }

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        keyboard = new Keyboard(this, R.xml.qwerty);

        int keyHeight = getResources().getDimensionPixelSize(R.dimen.key_height);
        int keyGap = getResources().getDimensionPixelSize(R.dimen.key_gap);

        List<Keyboard.Key> keys = keyboard.getKeys();
        for (Keyboard.Key key : keys) {
            key.height = keyHeight;
            key.gap = keyGap;
        }

        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        enterKey = findEnterKey();

        return kv;
    }

    private Keyboard.Key findEnterKey() {
        List<Keyboard.Key> keys = keyboard.getKeys();
        for (Keyboard.Key key : keys) {
            if (key.codes[0] == -4) { // Enter key code
                return key;
            }
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onPress(int primaryCode) {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Deprecated in API 26
                vibrator.vibrate(20);
            }
        }
    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        InputConnection ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch (primaryCode)
        {
            case Keyboard.KEYCODE_DELETE:
                handlebackspace(ic);
                break;
            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                keyboard.setShifted(isCaps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                if ("確定".equals(enterKey.label))
                {
                    commitText(ic);
                    enterKey.label = "検索";
                    kv.invalidateAllKeys();
                } else if ("検索".equals(enterKey.label)) {
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                }
                break;
            default:
               handleCharacter(primaryCode, ic);
        }
    }

    private void handleCharacter(int primaryCode, InputConnection ic) {
        char code = (char) primaryCode;
        composingText.append(code);
        ic.setComposingText(composingText, 1);
        if (composingText.length() == 1) {
            enterKey.label = "確定";
            kv.invalidateAllKeys();
        }
        convertToJapanese(ic);
    }

    private void convertToJapanese(InputConnection ic) {
        String japaneseText = convertRomajiToKana(composingText.toString());
        ic.setComposingText(japaneseText, 1);
    }

    private String convertRomajiToKana(String input) {
        StringBuilder kanaResult = new StringBuilder();
        StringBuilder buffer = new StringBuilder();

        for (char c : input.toCharArray()) {
            buffer.append(c);
            String bufferString = buffer.toString();
            String kana = romajiToKanaMap.get(bufferString);



            if (kana != null) {
                kanaResult.append(kana);
                buffer.setLength(0); // Clear buffer
            } else if (buffer.length() > 2) {
                kanaResult.append(buffer.charAt(0)); // Add first char as it doesn't match
                buffer.deleteCharAt(0); // Remove the first char and try again
            }

            // Handle "nn" to "ん"
            if (bufferString.equals("nn")) {
                kanaResult.append("ん");
                buffer.setLength(0);
            } else if (buffer.length() == 2 && buffer.charAt(0) == 'n') {
                kanaResult.append("ん");
                buffer.deleteCharAt(0); // Remove the "n" and continue with the next character
            } else if (buffer.length() == 2 && buffer.charAt(0) == buffer.charAt(1) && bufferString.matches("([kstnhmrgzbpd])\\1")) {
                kanaResult.append("っ");
                buffer.deleteCharAt(1);
            }
        }

        kanaResult.append(buffer); // Append any leftover characters
        return kanaResult.toString();
    }



    private void handlebackspace(InputConnection ic) {
        if (composingText.length() > 0) {
            composingText.setLength(composingText.length() - 1);
            String currentText = composingText.toString();
            ic.setComposingText(convertRomajiToKana(currentText), 1);
        } else {
            ic.deleteSurroundingText(1, 0);
        }
    }

    private void commitText(InputConnection ic) {
        if (composingText.length() > 0) {
            String text = composingText.toString();
            text = convertRomajiToKana(text);
            ic.commitText(text, 1);
            composingText.setLength(0);
        }
    }


    private void playClick(int i) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch(i)
        {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {
        // For example, delete the last word
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.beginBatchEdit();
            ic.deleteSurroundingText(findLastWordLength(), 0);
            ic.endBatchEdit();
        }
    }

    private int findLastWordLength() {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return 0;

        CharSequence text = ic.getTextBeforeCursor(50, 0);
        if (text == null) return 0;

        int lastSpace = text.toString().lastIndexOf(' ');
        return (lastSpace == -1) ? text.length() : text.length() - lastSpace - 1;
    }


    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}