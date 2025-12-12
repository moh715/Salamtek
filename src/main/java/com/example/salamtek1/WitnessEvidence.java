package com.example.salamtek1;

public class WitnessEvidence extends Evidence {
    private Customer witness;
    private String testimony;

    public WitnessEvidence(String description, Customer wrong, Officer officer, Customer witness, String testimony) {
        super(description, wrong, officer);
        this.witness = witness;
        this.testimony = testimony;
    }

    public Customer getWitness() {
        return witness;
    }

    public void setWitness(Customer witness) {
        this.witness = witness;
    }

    public String getTestimony() {
        return testimony;
    }

    public void setTestimony(String testimony) {
        this.testimony = testimony;
    }

    @Override
    public String getEvidenceType() {
        return "witness";
    }

    @Override
    public String toString() {
        return "witness: " + witness+ "\n"
                + "testimony: " + testimony + "\n"
                + description + "\n"
                + "the one who is wrong: " + wrong + "\n by Officer: " + officer;
    }
}