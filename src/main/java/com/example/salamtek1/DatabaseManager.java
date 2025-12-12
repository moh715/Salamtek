package com.example.salamtek1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
class DatabaseManager {
    private static DatabaseManager instance;
      private Map<String, User> users = new HashMap<>();
private Map<String, Officer> officers = new HashMap<>();
  private Map<String, Hub> hubs = new HashMap<>();
       private Map<String, Vehicle> vehicles = new HashMap<>();
    private Map<String, Accident> accidents = new HashMap<>();
 private Map<String, Payment> payments = new HashMap<>();
      private Map<String, String> licensePlateToVehicle = new HashMap<>();
    private int nextAccidentId = 1, nextPaymentId = 1;
    private DatabaseManager() { initializeSampleData(); }
      public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

 public void addUser(User user) { users.put(user.getNationalId(), user); }
 public User getUser(String nationalId) { return users.get(nationalId); }
public boolean userExists(String nationalId) { return users.containsKey(nationalId); }
   public User authenticateUser(String nationalId, String password) {
    User user = users.get(nationalId);
    return (user != null && user.checkPassword(password)) ? user : null;
    }
    public void addOfficer(Officer officer) {
        officers.put(officer.getOfficerId(), officer);
        Hub hub = hubs.get(officer.getHubId());
        if (hub != null) hub.addOfficer(officer.getOfficerId());
    }

    public Officer getOfficer(String officerId) { return officers.get(officerId); }
    public Officer authenticateOfficer(String email, String password) {
        for (Officer officer : officers.values()) {
            if (officer.getEmail().equals(email) && officer.checkPassword(password)) {
                return officer;
            }
        }
        return null;
    }
    public ArrayList<Officer> getAvailableOfficersAtHub(String hubId) {
        return officers.values().stream()
                .filter(o -> o.getHubId().equals(hubId) && o.isAvailable())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void addHub(Hub hub) { hubs.put(hub.getHubId(), hub); }
    public Hub getHub(String hubId) { return hubs.get(hubId); }

    public Hub findNearestHub(Location location) {
        Hub nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (Hub hub : hubs.values()) {
            double distance = location.distanceTo(hub.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = hub;
            }
        }
        return nearest;
    }

    public void addVehicle(Vehicle vehicle) { vehicles.put(vehicle.getVehicleId(), vehicle); }
    public void registerLicensePlate(String licensePlate, String vehicleId) {
        licensePlateToVehicle.put(licensePlate.toUpperCase(), vehicleId);
    }

    public Vehicle getVehicleByLicensePlate(String licensePlate) {
        String vehicleId = licensePlateToVehicle.get(licensePlate.toUpperCase());
        return vehicleId != null ? vehicles.get(vehicleId) : null;
    }
 public String addAccident(Accident accident) {
        String id = "ACC" + String.format("%04d", nextAccidentId++);
        accidents.put(id, accident);
        return id;
    }

  
    public Accident getAccident(String accidentId) { return accidents.get(accidentId); }
    public void updateAccident(Accident accident) { accidents.put(accident.getAccidentId(), accident); }

    public ArrayList<Accident> getAccidentsByUser(String nationalId) {
        return accidents.values().stream()
                .filter(a -> a.getReporterNationalId().equals(nationalId) ||
                        a.getOtherPartyNationalId().equals(nationalId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Accident> getAccidentsByOfficer(String officerId) {
        return accidents.values().stream()
                .filter(a -> officerId.equals(a.getAssignedOfficerId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String addPayment(Payment payment) {
        String id = "PAY" + String.format("%04d", nextPaymentId++);
        payments.put(id, payment);
        return id;
    }

    public Payment getPayment(String paymentId) { return payments.get(paymentId); }
        public void updatePayment(Payment payment) { payments.put(payment.getPaymentId(), payment); }

    public ArrayList<Payment> getPaymentsByPayer(String nationalId) {
        return payments.values().stream()
                .filter(p -> p.getPayerNationalId().equals(nationalId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Payment> getPaymentsByRecipient(String nationalId) {
        return payments.values().stream()
                .filter(p -> p.getRecipientNationalId().equals(nationalId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void initializeSampleData() {
        try {
            addHub(new Hub("HUB001", "Cairo - Nasr City", new Location(30.0444, 31.2357)));
            addHub(new Hub("HUB002", "Cairo - Heliopolis", new Location(30.0908, 31.3219)));

            addOfficer(new Officer("OFF001", "Ahmed Hassan", "ahmed.hassan@police.gov.eg", "pass123", "HUB001"));
            addOfficer(new Officer("OFF002", "Mohamed Ali", "mohamed.ali@police.gov.eg", "pass123", "HUB001"));

            addUser(new User("29012011234567", "Karim Ahmed", "01012345678", "karim@email.com", "user123"));
            addUser(new User("29506201234568", "Nour Mohamed", "01098765432", "nour@email.com", "user123"));

    Vehicle hyundai = new Vehicle("VEH001", "Hyundai", "Avante", 2018);
        hyundai.addPart("Front Bumper", 2500.0);
        hyundai.addPart("Left Headlight", 3000.0);
        addVehicle(hyundai);
        registerLicensePlate("ABC123", "VEH001");

    Vehicle toyota = new Vehicle("VEH002", "Toyota", "Corolla", 2020);
    toyota.addPart("Front Bumper", 3000.0);
    addVehicle(toyota);
    registerLicensePlate("XYZ789", "VEH002");
    } catch (SalamtekException e) {
        System.out.println("Error initializing data: " + e.getMessage());
        }
    }
}
