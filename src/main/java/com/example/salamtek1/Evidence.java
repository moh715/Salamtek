package com.example.salamtek1;

public abstract class Evidence {
    // data fields
    // Mohamed Mustafa Task
    protected String description;
    protected Customer wrong; // the one who is wrong according to this evidence
    protected Officer officer;
    // constructor

    public Evidence(String description, Customer w, Officer o) {
        this.description = description;
        wrong = w;
        officer = o;
    }

    // setters getters
    public String getDescription() {
        return description;
    }

    public Customer getWrong() {
        return wrong;
    }

    public void setWrong(Customer wrong) {
        this.wrong = wrong;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Officer getOfficer() {
        return officer;
    }

    public void setOfficer(Officer officer) {
        this.officer = officer;
    }
    // methods
    public abstract String getEvidenceType();

}