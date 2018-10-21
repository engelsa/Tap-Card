package com.example.alex.tapthat;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class FileSystem {

    public static String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("file.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("data fetch", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("data fetch", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("file.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void updateInformation(Context context, String newData) {
        Contact[] contacts = Contact.getAll(FileSystem.readFromFile(context));
        Contact[] contactList;
        String[] contactCodes;
        Contact newContact = new Contact(newData);
        boolean match = false;
        for (int i = 0; i < contacts.length; i++) {
            if (contacts[i].getDeviceId().equals(newContact.getDeviceId())) {
                contacts[i] = newContact;
                match = true;
            }
        }
        if (!match) {
            contactList = new Contact[contacts.length + 1];
            for (int i = 0; i < contacts.length; i++) {
                contactList[i] = contacts[i];
            }
            contactList[contacts.length] = newContact;
        } else {
            contactList = contacts;
        }

        boolean sorting;
        do {
            sorting = false;
            for (int i = 0; i < contactList.length; i++) {
                for (int x = i + 1; x < contactList.length; x++) {
                    if (contactList[i].getLastName().compareTo(contactList[x].getLastName()) < 0) {
                        Contact temp = contactList[i];
                        contactList[i] = contactList[x];
                        contactList[x] = temp;
                        sorting = true;
                    }
                }
            }
        } while (sorting);

        //TODO: update GUI... maybe

        contactCodes = new String[contactList.length];
        for (int i = 0; i < contactList.length; i++) {
            contactCodes[i] = contactList[i].toString();
        }

        FileSystem.writeToFile(Arrays.toString(contactCodes), context);
    }

    public static void deleteContact(String deviceId, Context context) {
        Contact[] contacts = Contact.getAll(FileSystem.readFromFile(context));
        String compilation = "";

        for (int i = 0; i < contacts.length; i++) {
            if (!contacts[i].getDeviceId().equals(deviceId)) {
                compilation += contacts[i].toString();
            }
        }

        FileSystem.writeToFile(compilation, context);
    }
}