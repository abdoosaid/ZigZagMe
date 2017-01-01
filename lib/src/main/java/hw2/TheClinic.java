package hw2;


import java.util.ArrayList;

public class TheClinic {
    public static void main(String[] args) {
        ArrayList<Billing> bills = new ArrayList<>();
        Doctor doc1 = new Doctor("Kamel", "Pediatrician,", 100);
        Doctor doc2 = new Doctor("Yacine", "Obstetrician", 300);
        Patient pat1 = new Patient("Omer", "1d5vt65");
        Patient pat2 = new Patient("Mustafa", "1s8zc55");

        Billing bil1 = new Billing(doc1, pat1);
        Billing bil2 = new Billing(doc2, pat2);

        bills.add(bil1);
        bills.add(bil2);

        int totalIncome=0;
        for (Billing b:bills) {
            totalIncome+=b.income();
        }
        System.out.println(totalIncome);
    }
}

class Patient extends Person {
    String id="";
    public Patient(){
        super();
    }

    public Patient(String initialName){
        super(initialName);
    }

    public Patient(String initialName, String id){
        super(initialName);
        this.id=id;
    }


}

class Person {
    private String name;

    public Person(){
        this.name="No name yet";
    }

    public Person(String initialName){
        this.name=initialName;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getName() {
        return name;
    }

    public void writeOutput(){
        System.out.println("Name: "+name);
    }

    public boolean hasSameName(Person otherPerson){
        return this.name.equalsIgnoreCase(otherPerson.name);
    }
}

class Doctor extends Person {
    String speciality;
    double visitFee;
    public Doctor(){
        super();
        speciality="Not yet";
    }
    public Doctor(String initialName){
        super(initialName);
        speciality="Not yet";
    }
    public Doctor(String initialName, String spec){
        super(initialName);
        speciality=spec;
    }
    public Doctor(String initialName, String spec, double fee){
        super(initialName);
        speciality=spec;
        visitFee=fee;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
    public void setVisitFee(int visitFee) {
        this.visitFee = visitFee;
    }

    public String getSpeciality() {
        return speciality;
    }
    public double getVisitFee() {
        return visitFee;
    }

}

class Billing {
    Doctor doctor;
    Patient patient;
    public Billing(Doctor doc, Patient pat){
        this.doctor=doc;
        this.patient=pat;
    }
    public double income(){
        return doctor.getVisitFee();
    }
}