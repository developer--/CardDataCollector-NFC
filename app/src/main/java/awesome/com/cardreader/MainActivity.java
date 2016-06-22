package awesome.com.cardreader;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import awesome.com.cardreader.async.NdefReaderTask;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.triangle.reader.PaymentCard;
import io.triangle.reader.TapProcessor;

public class MainActivity extends AppCompatActivity{

    /**
     * Adapter used to grab NFC information from the sensor.
     */
    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Grab a hold of the nfc sensor
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (this.nfcAdapter != null)
        {
            this.ensureSensorIsOn();

            // We'd like to listen to all incoming NFC tags that support the IsoDep interface
            nfcAdapter.enableForegroundDispatch(this,
                    PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0),
                    new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED) },
                    new String[][] { new String[] { IsoDep.class.getName() }});
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (this.nfcAdapter != null)
        {
            this.nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        // Is the intent for a new NFC tag discovery?
        if (intent != null && intent.getAction() == NfcAdapter.ACTION_TECH_DISCOVERED)
        {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            IsoDep isoDep = IsoDep.get(tag);

            // Does the tag support the IsoDep interface?
            if (isoDep == null)
            {
                return;
            }

            TapProcessor tapProcessor = new TapProcessor(MainActivity.this);
            tapProcessor.processIntent(intent);
        }
    }

    private void ensureSensorIsOn()
    {
        if(!this.nfcAdapter.isEnabled())
        {
            // Alert the user that NFC is off
            new AlertDialog.Builder(this)
                    .setTitle("NFC Sensor Turned Off")
                    .setMessage("In order to use this application, the NFC sensor must be turned on. Do you wish to turn it on?")
                    .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            // Send the user to the settings page and hope they turn it on
                            if (android.os.Build.VERSION.SDK_INT >= 16)
                            {
                                startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                            }
                            else
                            {
                                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        }
                    })
                    .setNegativeButton("Do Nothing", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            // Do nothing
                        }
                    })
                    .show();
        }
    }


}
