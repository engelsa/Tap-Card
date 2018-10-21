package com.example.alex.tapthat;

import android.content.Context;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.IntentFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    public static boolean windowOpen = false;

    private String encode() {
        EditText firstName = findViewById(R.id.firstName);
        EditText lastName = findViewById(R.id.lastName);
        EditText email = findViewById(R.id.email);
        EditText phone = findViewById(R.id.phone);
        EditText linkedIn = findViewById(R.id.linkedIn);
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return String.format("%s<>%s<>%s<>%s<>%s<>%s\n", android_id, firstName.getText().toString(),
                lastName.getText().toString(),
                email.getText().toString(),
                phone.getText().toString(),
                linkedIn.getText().toString());
               // "%s<>%s<>%s<>%s<>%s<>%s\n", deviceId, firstName, lastName, email, phone, linkedIn)
    }

    static Context mainContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainContext = getApplicationContext();
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        /**
         *  FILE STORAGE BEGINNING
         */

        FileSystem.writeToFile("A<>John<>Redfern<>onthecob@corn.com<>8675309<>https://www.linkedin.com/in/eugene-lee-yang-a568664b", mainContext);

        startActivity(new Intent(mainContext, ScrollingActivity.class));


        Button viewCards = findViewById(R.id.button);

        viewCards.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(mainContext, ScrollingActivity.class));
            }
        });

        /**
         *  FILE STORAGE END
         */

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            Toast.makeText(mainContext, "NFC is available", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mainContext, "NFC is not available", Toast.LENGTH_LONG).show();
            return;
        }
    }

    protected void onNewIntent(Intent intent) {

        //Toast.makeText(this, "NFC intent received", Toast.LENGTH_LONG).show();

        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(mainContext, "NFC intent!", Toast.LENGTH_LONG).show();

            //if (true) {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if (parcelables != null && parcelables.length > 0) {
                    readTextFromMessage((NdefMessage) parcelables[0]);

                } else {
                    Toast.makeText(mainContext, "No NDEF messages found!", Toast.LENGTH_LONG).show();
                }
            //} else {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                NdefMessage ndefMessage = createNdefMessage(encode());

                writeNdefMessage(tag, ndefMessage);
            //}
        }

    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];

            String tagContent = getTextFromNdefRecord(ndefRecord);

            FileSystem.updateInformation(mainContext, tagContent);
            //if (!windowOpen) {
                startActivity(new Intent(mainContext, ScrollingActivity.class));
            //}
        } else {
            Toast.makeText(mainContext, "No NDEF records found!", Toast.LENGTH_LONG).show();
        }
    }

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(mainContext, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(mainContext, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }


    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    protected void onPause() {

        super.onPause();
        disableForegroundDispatchSystem();
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag not formatable", Toast.LENGTH_LONG).show();

            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            //Toast.makeText(this, "Ndef written", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {
        try {
            if (tag == null) {
                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_LONG).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if (ndef == null) {
                formatTag(tag, ndefMessage);
            } else {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Ndef not writable", Toast.LENGTH_LONG).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(this, "Info sent!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
    }

    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord", e.getMessage());
        }
        return null;
    }

    private NdefMessage createNdefMessage(String content) {
        NdefRecord ndefRecord = createTextRecord(content);

        NdefMessage ndefMessage = new NdefMessage(ndefRecord);

        return ndefMessage;
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }
}
