package com.example.salamtek1;

import com.example.salamtek1.Authenticatable;
import com.example.salamtek1.Persistable;

abstract class Person implements Authenticatable, Persistable {
    // Protected fields can be accessed by child classes (User, Officer)
    // This is Encapsulation - controlling access to data
    protected String name;
    protected String email;
    protected String password;

    public Person(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Abstract method - child classes MUST implement this
    // This is Abstraction - defining what needs to be done, not how
    public abstract String getRole();

    // Concrete method that all persons share
    public String getName() { return name; }
    public String getEmail() { return email; }
    protected String getPassword() { return password; }

    // Default implementation of authenticate - can be overridden (Polymorphism)
    @Override
    public boolean authenticate(String credential, String password) {
        return this.password.equals(password);
    }

    // Template method pattern - defines structure, details filled by subclasses
    public String getDisplayInfo() {
        return String.format("[%s] %s - %s", getRole(), name, getIdentifier());
    }
}
