package com.example.alex.tapthat;

public class Contact {
    private String deviceId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String linkedIn;

    public static Contact[] getAll(String data) {
        if (!data.equals("")) {
            String[] codes = data.split("\n");
            Contact[] contacts = new Contact[codes.length];
            for (int i = 0; i < codes.length; i++) {
                contacts[i] = new Contact(codes[i]);
            }
            return contacts;
        } else {
            return new Contact[0];
        }
    }

    public Contact(String deviceId, String firstName, String lastName, String email, String phone, String linkedIn) {
        this.deviceId = deviceId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.linkedIn = linkedIn;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public Contact(String code) {
        this(code.split("<>")[0], code.split("<>")[1], code.split("<>")[2], code.split("<>")[3], code.split("<>")[4], code.split("<>")[5]);
    }

    public String toString() {
        return String.format("%s<>%s<>%s<>%s<>%s<>%s\n", deviceId, firstName, lastName, email, phone, linkedIn);
    }
}
