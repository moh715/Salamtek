package com.example.salamtek1;

public abstract class Person {
    // TOPIC: ENCAPSULATION (protected fields allow child access)
    protected String name;
    protected String email;
    protected String password;

    public Person(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
    public abstract String getRole();

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public boolean checkPassword(String inputPass) {
        return this.password.equals(inputPass);
    }
}

