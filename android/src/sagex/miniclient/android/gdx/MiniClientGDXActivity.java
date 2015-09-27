package sagex.miniclient.android.gdx;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.io.IOException;

import sagex.miniclient.MgrServerInfo;
import sagex.miniclient.MiniClientConnection;
import sagex.miniclient.ServerInfo;
import sagex.miniclient.android.R;
import sagex.miniclient.uibridge.UIFactory;
import sagex.miniclient.uibridge.UIManager;

import static sagex.miniclient.android.AppUtil.confirmExit;
import static sagex.miniclient.android.AppUtil.getMACAddress;
import static sagex.miniclient.android.AppUtil.hideSystemUI;

/**
 * Created by seans on 20/09/15.
 */
public class MiniClientGDXActivity extends AndroidApplication {
    public static final String ARG_SERVER_INFO = "server_info";

    private static final String TAG = "GDXMINICLIENT";
    FrameLayout surfaceHolder;

    View pleaseWait = null;
    TextView plaseWaitText = null;

    MiniClientRenderer mgr;

    private MiniClientConnection client;
    private View miniClientView;

    public MiniClientGDXActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideSystemUI(this);

        setContentView(R.layout.miniclientgl_layout);
        surfaceHolder=(FrameLayout)findViewById(R.id.surface);
        pleaseWait = findViewById(R.id.waitforit);
        plaseWaitText = (TextView)findViewById(R.id.pleaseWaitText);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        mgr = new MiniClientRenderer(this);
        miniClientView =initializeForView(mgr, config);
        miniClientView.setFocusable(true);
        miniClientView.setFocusableInTouchMode(true);
        miniClientView.setOnTouchListener(null);
        miniClientView.setOnClickListener(null);
        miniClientView.setOnKeyListener(null);
        miniClientView.setOnDragListener(null);
        miniClientView.setOnFocusChangeListener(null);
        miniClientView.setOnGenericMotionListener(null);
        miniClientView.setOnHoverListener(null);
        miniClientView.setOnTouchListener(null);
        surfaceHolder.addView(miniClientView);
        miniClientView.requestFocus();

        ServerInfo si = (ServerInfo) getIntent().getSerializableExtra(ARG_SERVER_INFO);
        if (si==null) {
            Log.e(TAG, "Missing SERVER INFO in Intent: " + ARG_SERVER_INFO );
            finish();
        }

        plaseWaitText.setText("Connecting to " + si.address + "...");
        setConnectingIsVisible(true);

        startMiniClient(si);
    }

    public void startMiniClient(final ServerInfo si) {
        System.setProperty("user.home", getCacheDir().getAbsolutePath());
        final UIFactory factory = new UIFactory() {
            @Override
            public UIManager<?> getUIManager(MiniClientConnection conn) {
                return mgr;
            }
        };
        MgrServerInfo info = new MgrServerInfo(si.address, (si.port==0)?31099:si.port, si.locatorID);
        client = new MiniClientConnection(si.address, getMACAddress(this), false, info, factory);
        mgr.setConnection(client);

        miniClientView.setOnTouchListener(new MiniclientTouchListener(this, client));
        miniClientView.setOnKeyListener(new MiniClientKeyListener(client));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connect();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void onBackPressed() {
        confirmExit(this);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Closing MiniClient Connection");

        try {
            if (client!=null) {
                client.close();
            }
        } catch (Throwable t) {
            Log.w(TAG, "Error shutting down client", t);
        }
        super.onDestroy();
    }

    public void setConnectingIsVisible(final boolean connectingIsVisible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pleaseWait.setVisibility((connectingIsVisible) ? View.VISIBLE : View.GONE);
            }
        });
    }
}