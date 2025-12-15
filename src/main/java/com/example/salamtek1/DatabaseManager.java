package com.example.salamtek1;

import com.example.salamtek1.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

class DatabaseManager {
    private static DatabaseManager instance;
    private static final String DATA_DIRECTORY = "salamtek_data/";

    private HashMap<String, User> users;
    private HashMap<String, Officer> officers;
    private HashMap<String, Hub> hubs;
    private HashMap<String, Vehicle> vehicles;
    private HashMap<String, Accident> accidents;
    private HashMap<String, Payment> payments;
    private HashMap<String, String> licensePlateToVehicle;
    private int nextAccidentId = 1;
    private int nextPaymentId = 1;

    private DatabaseManager() {
        users = new HashMap<>();
        officers = new HashMap<>();
        hubs = new HashMap<>();
        vehicles = new HashMap<>();
        accidents = new HashMap<>();
        payments = new HashMap<>();
        licensePlateToVehicle = new HashMap<>();

        // Create data directory if it doesn't exist
        new File(DATA_DIRECTORY).mkdirs();

        // Try to load data from files, if it fails, use sample data
        if (!loadDataFromFiles()) {
            initializeSampleData();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }


    private boolean loadDataFromFiles() {
        try {
            loadUsers();
            loadOfficers();
            loadHubs();
            loadVehicles();
            System.out.println(" Data loaded from files successfully");
            return true;
        } catch (IOException e) {
            System.out.println(" Could not load data from files, using sample data");
            return false;
        }
    }

    public void saveAllData() {
        try {
            saveUsers();
            saveOfficers();
            saveHubs();
            saveVehicles();
            saveAccidents();
            savePayments();
            System.out.println("✓ All data saved successfully");
        } catch (IOException e) {
            System.err.println("✗ Error saving data: " + e.getMessage());
        }
    }

    private void saveUsers() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIRECTORY + "users.txt"))) {
            for (User user : users.values()) {
                writer.println(user.toFileFormat());
            }
        }
    }
    private void loadUsers() throws IOException {
        File file = new File(DATA_DIRECTORY + "users.txt");
        if (!file.exists()) throw new IOException("Users file not found");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals("USER") && parts.length >= 6) {
                    User user = new User(parts[1], parts[2], parts[3], parts[4], parts[5]);
                    users.put(user.getNationalId(), user);
                }
            }
        }
    }

    private void saveOfficers() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIRECTORY + "officers.txt"))) {
            for (Officer officer : officers.values()) {
                writer.println(officer.toFileFormat());
            }
        }
    }
    private void loadOfficers() throws IOException {
        File file = new File(DATA_DIRECTORY + "officers.txt");
        if (!file.exists()) throw new IOException("Officers file not found");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals("OFFICER") && parts.length >= 6) {
                    Officer officer = new Officer(parts[1], parts[2], parts[3], parts[4], parts[5]);
                    officers.put(officer.getOfficerId(), officer);
                }
            }
        }
    }
    private void saveHubs() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIRECTORY + "hubs.txt"))) {
            for (Hub hub : hubs.values()) {
                writer.println(hub.toFileFormat());
            }
        }
    }
    private void loadHubs() throws IOException {
        File file = new File(DATA_DIRECTORY + "hubs.txt");
        if (!file.exists()) throw new IOException("Hubs file not found");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals("HUB") && parts.length >= 4) {
                    Location location = Location.fromFileFormat(parts[3]);
                    Hub hub = new Hub(parts[1], parts[2], location);
                    hubs.put(hub.getHubId(), hub);
                }
            }
        }
    }

    private void saveVehicles() throws IOException {
        try (PrintWriter writer = new PrintWriter(
                new FileWriter(DATA_DIRECTORY + "vehicles.txt"))) {

            for (HashMap.Entry<String, Vehicle> entry : vehicles.entrySet()) {
                writer.println(
                        entry.getValue().toFileFormat() + "|" + entry.getKey()
                );
            }
        }
    }
    private void loadVehicles() throws IOException {
        File file = new File(DATA_DIRECTORY + "vehicles.txt");
        if (!file.exists()) throw new IOException("Vehicles file not found");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals("VEHICLE") && parts.length >= 6) {
                    Vehicle vehicle = new Vehicle(parts[1], parts[2], parts[3], Integer.parseInt(parts[4]));
                    registerLicensePlate(parts[6], parts[1]);
                    // Load parts if they exist
                    for (int i = 5; i < parts.length; i++) {
                        String[] partData = parts[i].split(":");
                        if (partData.length == 2) {
                            vehicle.addPart(partData[0], Double.parseDouble(partData[1]));
                        }
                    }
                    vehicles.put(vehicle.getVehicleId(), vehicle);
                }
            }
        }
    }

    private void saveAccidents() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIRECTORY + "accidents.txt"))) {
            for (Accident accident : accidents.values()) {
                writer.println(accident.toFileFormat());
            }
        }
    }

    private void savePayments() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIRECTORY + "payments.txt"))) {
            for (Payment payment : payments.values()) {
                writer.println(payment.toFileFormat());
            }
        }
    }



    public void addUser(User user) throws ValidationException {
        user.validate(); // input validation before adding
        users.put(user.getNationalId(), user);
        try {
            saveUsers(); // Persist to file immediately
        } catch (IOException e) {
            System.err.println("Warning: Could not save user to file");
        }
    }

    public User getUser(String nationalId) throws IllegalArgumentException {
        User user = users.get(nationalId);
        if (user == null) {
            throw new IllegalArgumentException("User: "+ nationalId+ "not found");
        }
        return user;
    }

    public boolean userExists(String nationalId) {
        return users.containsKey(nationalId);
    }

    public Person authenticatePerson(String identifier, String password) throws InvalidCredentialsException {
        // Try as user first
        User user = users.get(identifier);
        if (user != null && user.authenticate(identifier, password)) {
            return user;
        }
        // try as officer
        for (Officer officer : officers.values()) {
            if (officer.authenticate(identifier, password)) {
                return officer;
            }
        }

        throw new InvalidCredentialsException("Invalid credentials provided");
    }



    public void addOfficer(Officer officer) throws ValidationException {
        officer.validate();
        officers.put(officer.getOfficerId(), officer);
        Hub hub = hubs.get(officer.getHubId());
        if (hub != null) {
            hub.addOfficer(officer.getOfficerId());
        }
        try {
            saveOfficers();
        } catch (IOException e) {
            System.err.println("Warning: Could not save officer to file");
        }
    }

    public Officer getOfficer(String officerId) throws IllegalArgumentException {
        Officer officer = officers.get(officerId);
        if (officer == null) {
            throw new IllegalArgumentException("Officer: "+ officerId+ "not found");
        }
        return officer;
    }

    public ArrayList<Officer> getAvailableOfficersAtHub(String hubId) {
        return (ArrayList<Officer>) officers.values().stream()
                .filter(o -> o.getHubId().equals(hubId) && o.isAvailable())
                .collect(Collectors.toList());
    }


    public void addHub(Hub hub) {
        hubs.put(hub.getHubId(), hub);
    }

    public Hub getHub(String hubId) throws IllegalArgumentException {
        Hub hub = hubs.get(hubId);
        if (hub == null) {
            throw new IllegalArgumentException("Hub: "+ hubId+ "not found");
        }
        return hub;
    }

    public Hub findNearestHub(Location location) throws NoOfficersAvailableException {
        Hub nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Hub hub : hubs.values()) {
            double distance = location.distanceTo(hub.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = hub;
            }
        }

        if (nearest == null) {
            throw new NoOfficersAvailableException("System");
        }

        return nearest;
    }

    public void addVehicle(Vehicle vehicle) throws ValidationException {
        vehicle.validate();
        vehicles.put(vehicle.getVehicleId(), vehicle);
    }

    public Vehicle getVehicle(String vehicleId) {
        return vehicles.get(vehicleId);
    }

    public void registerLicensePlate(String licensePlate, String vehicleId) {
        licensePlateToVehicle.put(licensePlate.toUpperCase(), vehicleId);
    }

    public Vehicle getVehicleByLicensePlate(String licensePlate) throws IllegalArgumentException {
        String vehicleId = licensePlateToVehicle.get(licensePlate.toUpperCase());
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle with license plate " +  licensePlate+ " not found");
        }
        return vehicles.get(vehicleId);
    }
    public String addAccident(Accident accident) throws ValidationException {
        accident.validate();
        String id = "ACC" + String.format("%04d", nextAccidentId++);
        accidents.put(id, accident);
        try {
            saveAccidents();
        } catch (IOException e) {
            System.err.println("Warning: Could not save accident to file");
        }
        return id;
    }

    public Accident getAccident(String accidentId) throws IllegalArgumentException {
        Accident accident = accidents.get(accidentId);
        if (accident == null) {
            throw new IllegalArgumentException("Accident: "+ accidentId+ "not found");
        }
        return accident;
    }

    public void updateAccident(Accident accident) {
        accidents.put(accident.getAccidentId(), accident);
        try {
            saveAccidents();
        } catch (IOException e) {
            System.err.println("Warning: Could not save accident to file");
        }
    }

    public ArrayList<Accident> getAccidentsByUser(String nationalId) {
        return (ArrayList<Accident>) accidents.values().stream()
                .filter(a -> a.getReporterNationalId().equals(nationalId) ||
                        a.getOtherPartyNationalId().equals(nationalId))
                .collect(Collectors.toList());
    }
    public ArrayList<Accident> getAccidentsByOfficer(String officerId) {
        return (ArrayList<Accident>) accidents.values().stream()
                .filter(a -> officerId.equals(a.getAssignedOfficerId()))
                .collect(Collectors.toList());
    }

    public String addPayment(Payment payment) throws ValidationException {
        payment.validate();
        String id = "PAY" + String.format("%04d", nextPaymentId++);
        payments.put(id, payment);
        try {
            savePayments();
        } catch (IOException e) {
            System.err.println("Warning: Could not save payment to file");
        }
        return id;
    }

    public Payment getPayment(String paymentId) throws IllegalArgumentException {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment"+ paymentId+ "not found");
        }
        return payment;
    }


    public void updatePayment(Payment payment) {
        payments.put(payment.getPaymentId(), payment);
        try {
            savePayments();
        } catch (IOException e) {
            System.err.println("Warning: Could not save payment to file");
        }
    }

    public ArrayList<Payment> getPaymentsByPayer(String nationalId) {
        return (ArrayList<Payment>) payments.values().stream()
                .filter(p -> p.getPayerNationalId().equals(nationalId))
                .collect(Collectors.toList());
    }

    public ArrayList<Payment> getPaymentsByRecipient(String nationalId) {
        return (ArrayList<Payment>) payments.values().stream()
                .filter(p -> p.getRecipientNationalId().equals(nationalId))
                .collect(Collectors.toList());
    }

    public double getTotalAvailable(String nationalId) {
        return payments.values().stream()
                .filter(p -> p.getRecipientNationalId().equals(nationalId) &&
                        p.getStatus() == PaymentStatus.AVAILABLE)
                .mapToDouble(Payment::getAmount)
                .sum();
    }



    private void initializeSampleData() {
        try {
            addHub(new Hub("HUB001", "Cairo - Nasr City", new Location(30.0444, 31.2357)));
            addHub(new Hub("HUB002", "Cairo - Heliopolis", new Location(30.0908, 31.3219)));
            addHub(new Hub("HUB003", "Giza - Mohandessin", new Location(30.0626, 31.2003)));

            addOfficer(new Officer("OFF001", "Ahmed Hassan", "ahmed.hassan@police.gov.eg", "pass123", "HUB001"));
            addOfficer(new Officer("OFF002", "Mohamed Ali", "mohamed.ali@police.gov.eg", "pass123", "HUB001"));
            addOfficer(new Officer("OFF003", "Sara Ibrahim", "sara.ibrahim@police.gov.eg", "pass123", "HUB002"));

            addUser(new User("11111111111111", "Karim Ahmed", "01012345678", "karim@email.com", "123456"));
            addUser(new User("22222222222222", "Nour Mohamed", "01098765432", "nour@email.com", "123456"));
            addUser(new User("28801151234569", "Youssef Ibrahim", "01187654321", "youssef@email.com", "user123"));

            Vehicle hyundai = new Vehicle("VEH001", "Hyundai", "Avante", 2018);
            hyundai.addPart("Front Bumper", 2500.0);
            hyundai.addPart("Left Headlight", 3000.0);
            hyundai.addPart("Right Headlight", 3000.0);
            hyundai.addPart("Hood", 4000.0);
            hyundai.addPart("Front Windshield", 2000.0);
            addVehicle(hyundai);
            registerLicensePlate("123", "VEH001");

            Vehicle toyota = new Vehicle("VEH002", "Toyota", "Corolla", 2020);
            toyota.addPart("Front Bumper", 3000.0);
            toyota.addPart("Left Headlight", 3500.0);
            toyota.addPart("Right Headlight", 3500.0);
            toyota.addPart("Hood", 4500.0);
            addVehicle(toyota);
            registerLicensePlate("789", "VEH002");

            System.out.println("Sample data initialized");
            saveAllData(); // Save sample data to files
        } catch (ValidationException e) {
            System.err.println("Error initializing data: " + e.getMessage());
        }
    }


    public Officer authenticateOfficer(String trim, String text) {
        for (Officer officer : officers.values()) {
            if (officer.authenticate(trim, text)) {
                return officer;
            }
        }
        return null;
    }
}