package nz.ac.massey.cgwatkin.simpledialer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Vector;

public class MainActivity extends Activity {

    /**
     * @value DIALER_BUTTON_IDS Vector that stores the button IDs for all number buttons of the Simple Dialer.
     */
    private static final Vector<Integer> DIALER_BUTTON_IDS;
    static {
        DIALER_BUTTON_IDS = new Vector<Integer>();
        DIALER_BUTTON_IDS.add(R.id.button_0);
        DIALER_BUTTON_IDS.add(R.id.button_1);
        DIALER_BUTTON_IDS.add(R.id.button_2);
        DIALER_BUTTON_IDS.add(R.id.button_3);
        DIALER_BUTTON_IDS.add(R.id.button_4);
        DIALER_BUTTON_IDS.add(R.id.button_5);
        DIALER_BUTTON_IDS.add(R.id.button_6);
        DIALER_BUTTON_IDS.add(R.id.button_7);
        DIALER_BUTTON_IDS.add(R.id.button_8);
        DIALER_BUTTON_IDS.add(R.id.button_9);
        DIALER_BUTTON_IDS.add(R.id.button_star);
        DIALER_BUTTON_IDS.add(R.id.button_hash);
    };

    /**
     * Function called when activity initialised.
     *
     * Steps:
     * * Check if opened by dial intent, if true initialise TextView text as number from intent.
     * * Else if instance state was saved, initialise TextView text as number from previous state.
     * * Setup buttons in view.
     *
     * Note: Starting app from external intent was not functioning so code was commented out.
     *
     * @see MainActivity::setupButtons() The method which sets up the buttons.
     * @param savedInstanceState The saved state from previous run.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        /*if (Intent.ACTION_DIAL.equals(intent.getAction()) && intent.getType() == "tel") {
            ((TextView) findViewById(R.id.number_to_call)).setText(intent.getDataString());
        }
        else*/ if (savedInstanceState != null) {
            ((TextView) findViewById(R.id.number_to_call)).setText(savedInstanceState.getString("number_to_call"));
        }
        setupButtons();
    }

    /**
     * Funciton called when activity paused.
     *
     * @param outState Bundle to store state of app.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("number_to_call", (String) ((TextView) findViewById(R.id.number_to_call)).getText());
        super.onSaveInstanceState(outState);
    }

    /**
     * Function called when permission request ends.
     *
     * Checks if permission request for calling was granted. If true, start call. If false, show dialog.
     *
     * @param requestCode The code of the permission that was requested.
     * @param permissions The permission names.
     * @param grantResults The results of the permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCall();
            }
            else {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= 21) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Permission required")
                        .setMessage("Call permission is required to make calls.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing, permission will be requested when call button clicked again.
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }

    /**
     * Sets up the buttons of the main view.
     *
     * Sets up listeners for all buttons in the main view of the Simple Dialer.
     */
    private void setupButtons() {
        setupDialerButtons();
        ((Button) findViewById(R.id.button_call)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCall();
            }
        });
        final TextView textViewNumberToCall = (TextView) findViewById(R.id.number_to_call);
        Button deleteButton = (Button) findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNumberToCall = ((String) textViewNumberToCall.getText())
                        .substring(0, textViewNumberToCall.getText().length() - 1);
                textViewNumberToCall.setText(newNumberToCall);
            }
        });
        deleteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                textViewNumberToCall.setText("");
                return true;
            }
        });
    }

    /**
     * Sets up the dialer buttons of the main view.
     *
     * Sets up listeners for all input buttons of the Simple Dialer. Allows buttons to modify main TextView of app.
     */
    private void setupDialerButtons() {
        final TextView textViewNumberToCall = (TextView) findViewById(R.id.number_to_call);
        for (Integer id : DIALER_BUTTON_IDS) {
            Button button = (Button) findViewById(id);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newNumberToCall = textViewNumberToCall.getText() + (String) ((Button) v).getText();
                    textViewNumberToCall.setText(newNumberToCall);
                }
            });
        }
    }

    /**
     * Starts call intent from text content of main TextView.
     *
     * Uses the input number to start a call intent.
     */
    private void startCall() {
        String numberToCall = (String)((TextView) findViewById(R.id.number_to_call)).getText();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numberToCall));
        if (Build.VERSION.SDK_INT > 22) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            }
            else {
                requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, 0);
            }
        }
        else {
            startActivity(intent);
        }
    }
}

