package sagex.miniclient.android.ui.keymaps;

import android.view.KeyEvent;

import sagex.miniclient.SageCommand;

public class PluginListKeyMap extends KeyMap {
    public PluginListKeyMap(KeyMap parent) {
        super(parent);
    }

    @Override
    public void initializeKeyMaps() {
        super.initializeKeyMaps();

        // Key Mapping for Plugin List
        KEYMAP.put(KeyEvent.KEYCODE_DPAD_LEFT, SageCommand.REW_2);
        KEYMAP.put(KeyEvent.KEYCODE_DPAD_RIGHT, SageCommand.FF_2);

        // bring up options on a long press left
        LONGPRESS_KEYMAP.put(KeyEvent.KEYCODE_DPAD_LEFT, SageCommand.OPTIONS);
    }

    @Override
    public boolean shouldCancelLongPress(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            return true;
        }
        return parent.shouldCancelLongPress(keyCode);
    }

    @Override
    public boolean isNavigationKey(int keyCode) {
        if (
                keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
                        keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            return false;
        }
        return parent.isNavigationKey(keyCode);
    }

}
