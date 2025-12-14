package com.example.salamtek1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SalamtekEnhancedConsole {
    private static Scanner scanner = new Scanner(System.in);
    private static AccidentService accidentService = new AccidentService();
    private static PaymentService paymentService = new PaymentService();
    private static DatabaseManager db = DatabaseManager.getInstance();
    private static Person currentPerson; // Polymorphism - can be User or Officer

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Login (User/Officer)");
            System.out.println("2. Register New User");
            System.out.println("3. Exit");
            System.out.print("Choose: ");

            String choice = getValidInput();

            switch (choice) {
                case "1": handleLogin(); break;
                case "2": handleRegistration(); break;
                case "3": running = false; System.out.println("\nStay safe!"); break;
                default: System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }

    private static String getValidInput() {
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            System.out.print("Input cannot be empty. Try again: ");
            input = scanner.nextLine().trim();
        }
        return input;
    }


    private static void handleLogin() {
        System.out.println("\n========== LOGIN ==========");
        System.out.print("Enter ID/Email: ");
        String identifier = getValidInput();

        System.out.print("Enter Password: ");
        String password = getValidInput();

        try {
            // Polymorphism in action - Person can be User or Officer
            currentPerson = db.authenticatePerson(identifier, password);
            System.out.println("\n Welcome, " + currentPerson.getName() + " (" + currentPerson.getRole() + ")");

            // Runtime polymorphism - call appropriate menu based on actual type
            if (currentPerson instanceof User) {
                showUserMenu((User) currentPerson);
            } else if (currentPerson instanceof Officer) {
                showOfficerMenu((Officer) currentPerson);
            }
        } catch (InvalidCredentialsException e) {
            System.out.println("✗ " + e.getMessage());
        }
    }

    private static void handleRegistration() {
        System.out.println("\n========== REGISTRATION ==========");

        try {
            System.out.print("National ID (14 digits): ");
            String nationalId = getValidInput();

            if (db.userExists(nationalId)) {
                System.out.println("✗ User already exists!");
                return;
            }

            System.out.print("Full Name: ");
            String name = getValidInput();

            System.out.print("Phone (11 digits, starts with 01): ");
            String phone = getValidInput();

            System.out.print("Email: ");
            String email = getValidInput();

            System.out.print("Password (min 6 characters): ");
            String password = getValidInput();

            // Create user - validation happens inside
            User newUser = new User(nationalId, name, phone, email, password);
            db.addUser(newUser); // This will validate and save

            System.out.println("\n✓ Registration successful!");

        } catch (ValidationException e) {
            System.out.println("✗ Validation Error: " + e.getMessage());
        }
    }

    // ==================== USER MENU ====================

    private static void showUserMenu(User user) {
        boolean active = true;
        while (active) {
            System.out.println("\n========== USER MENU ==========");
            System.out.println("User: " + user.getName());
            System.out.println("1. Report Accident");
            System.out.println("2. Pay or Receive Bill");
            System.out.println("3. View My Accidents");
            System.out.println("4. Logout");
            System.out.print("Choose: ");

            String choice = getValidInput();

            try {
                switch (choice) {
                    case "1": reportAccident(user); break;
                    case "2": paymentMenu(user); break;
                    case "3": viewAccidents(user); break;
                    case "4": active = false; currentPerson = null; break;
                    default: System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage());
            }
        }
    }

    private static void reportAccident(User user) throws Exception {
        System.out.println("\n========== REPORT ACCIDENT ==========");
        System.out.print("Other party's National ID: ");
        String otherId = getValidInput();

        System.out.print("Your license plate: ");
        String myPlate = getValidInput().toUpperCase();

        System.out.print("Other party's license plate: ");
        String otherPlate = getValidInput().toUpperCase();

        System.out.print("Latitude (e.g., 30.0444): ");
        double lat = Double.parseDouble(getValidInput());

        System.out.print("Longitude (e.g., 31.2357): ");
        double lon = Double.parseDouble(getValidInput());

        // Exception handling in action
        try {
            String accId = accidentService.reportAccident(
                    user.getNationalId(), otherId, myPlate, otherPlate, new Location(lat, lon)
            );

            Accident acc = db.getAccident(accId);
            Officer off = db.getOfficer(acc.getAssignedOfficerId());
            Hub hub = db.getHub(off.getHubId());

            System.out.println("\n✓ ACCIDENT REPORTED!");
            System.out.println("Accident ID: " + accId);
            System.out.println("Officer " + off.getName() + " dispatched from " + hub.getCityName());

        } catch (IllegalArgumentException e) {
            System.out.println("✗ " + e.getMessage());
        }
    }

    private static void viewAccidents(User user) {
        System.out.println("\n========== MY ACCIDENTS ==========");
        ArrayList<Accident> accidents = accidentService.getUserAccidents(user.getNationalId());

        if (accidents.isEmpty()) {
            System.out.println("No accidents on record.");
            return;
        }

        for (Accident a : accidents) {
            System.out.println("\n─────────────────────────────────");
            System.out.println("ID: " + a.getAccidentId());
            System.out.println("Date: " + a.getFormattedReportTime());
            System.out.println("Status: " + a.getStatus());

            if (a.getStatus() == AccidentStatus.COMPLETED || a.getStatus() == AccidentStatus.PAID) {
                boolean userAtFault = a.getAtFaultPartyId().equals(user.getNationalId());
                System.out.println("At Fault: " + (userAtFault ? "You" : "Other Party"));
                System.out.println("Cost: " + a.getTotalCost() + " EGP");
            }
        }
    }

    private static void paymentMenu(User user) {
        System.out.println("\n========== PAYMENT CENTER ==========");
        HashMap<String, Object> summary = paymentService.getPaymentSummary(user.getNationalId());
        double owed = (double) summary.get("totalOwed");
        double available = (double) summary.get("totalAvailable");

        System.out.println("Amount you owe: " + owed + " EGP");
        System.out.println("Amount available: " + available + " EGP\n");

        System.out.println("1. Pay Bills");
        System.out.println("2. Withdraw Funds");
        System.out.println("3. Back");
        System.out.print("Choose: ");

        String choice = getValidInput();

        try {
            switch (choice) {
                case "1":
                    if (owed > 0) payBills(user, (ArrayList<Payment>) summary.get("paymentsOwed"));
                    break;
                case "2":
                    if (available > 0) withdrawFunds(user);
                    break;
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void payBills(User user, ArrayList<Payment> payments) throws IllegalArgumentException {
        ArrayList<Payment> unpaid = (ArrayList<Payment>) payments.stream()
                .filter(p -> p.getStatus() != PaymentStatus.PAID)
                .collect(Collectors.toList());

        if (unpaid.isEmpty()) {
            System.out.println("No outstanding bills.");
            return;
        }

        System.out.println("\n========== PAY BILLS ==========");
        for (int i = 0; i < unpaid.size(); i++) {
            Payment p = unpaid.get(i);
            System.out.println((i+1) + ". " + p.getPaymentId() + " - " +
                    p.getTotalAmountDue() + " EGP (Due: " + p.getFormattedDueDate() + ")");
            if (p.isLate()) {
                System.out.println("   ⚠ LATE! Late fee: " + p.getLateFee() + " EGP");
            }
        }

        System.out.print("\nPay #: ");
        int choice = Integer.parseInt(getValidInput());

        if (choice > 0 && choice <= unpaid.size()) {
            System.out.println("\nPayment Method: 1.Phone Cash  2.Fawry  3.Bank");
            System.out.print("Choose: ");
            String method = switch(getValidInput()) {
                case "1" -> "Phone Cash";
                case "2" -> "Fawry";
                case "3" -> "Bank Transfer";
                default -> "Unknown";
            };

            Payment p = unpaid.get(choice-1);
            paymentService.processPayment(p.getPaymentId(), method);
            System.out.println("\n✓ Payment successful! Paid " + p.getTotalAmountDue() + " EGP");
        }
    }

    private static void withdrawFunds(User user) {
        System.out.println("\n========== WITHDRAW FUNDS ==========");
        double available = db.getTotalAvailable(user.getNationalId());
        System.out.println("Available: " + available + " EGP");

        System.out.print("Bank account: ");
        String account = getValidInput();

        System.out.print("Confirm? (yes/no): ");
        if (getValidInput().equalsIgnoreCase("yes")) {
            paymentService.withdrawFunds(user.getNationalId());
            System.out.println("\n✓ Withdrawal successful!");
        }
    }

    // ==================== OFFICER MENU ====================

    private static void showOfficerMenu(Officer officer) {
        boolean active = true;
        while (active) {
            System.out.println("\n========== OFFICER MENU ==========");
            System.out.println("Officer: " + officer.getName());
            System.out.println("Status: " + (officer.isAvailable() ? "Available" : "On Assignment"));
            System.out.println("\n1. View Assigned Accidents");
            System.out.println("2. Complete Investigation");
            System.out.println("3. Logout");
            System.out.print("Choose: ");

            String choice = getValidInput();

            try {
                switch (choice) {
                    case "1": viewOfficerAccidents(officer); break;
                    case "2": completeInvestigation(officer); break;
                    case "3": active = false; currentPerson = null; break;
                    default: System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage());
            }
        }
    }

    private static void viewOfficerAccidents(Officer officer) throws IllegalArgumentException {
        System.out.println("\n========== ASSIGNED ACCIDENTS ==========");
        ArrayList<Accident> accidents = accidentService.getOfficerAccidents(officer.getOfficerId());

        if (accidents.isEmpty()) {
            System.out.println("No assigned accidents.");
            return;
        }

        for (Accident a : accidents) {
            System.out.println("\n─────────────────────────────────");
            System.out.println("ID: " + a.getAccidentId());
            System.out.println("Status: " + a.getStatus());
            System.out.println("Party 1: " + db.getUser(a.getReporterNationalId()).getName());
            System.out.println("Party 2: " + db.getUser(a.getOtherPartyNationalId()).getName());
        }
    }

    private static void completeInvestigation(Officer officer) throws Exception {
        System.out.println("\n========== COMPLETE INVESTIGATION ==========");

        ArrayList<Accident> accidents = (ArrayList<Accident>) accidentService.getOfficerAccidents(officer.getOfficerId())
                .stream().filter(a -> a.getStatus() != AccidentStatus.COMPLETED &&
                        a.getStatus() != AccidentStatus.PAID)
                .collect(Collectors.toList());

        if (accidents.isEmpty()) {
            System.out.println("No pending investigations.");
            return;
        }

        for (int i = 0; i < accidents.size(); i++) {
            System.out.println((i+1) + ". " + accidents.get(i).getAccidentId());
        }

        System.out.print("\nSelect #: ");
        int choice = Integer.parseInt(getValidInput());

        if (choice < 1 || choice > accidents.size()) return;

        Accident a = accidents.get(choice-1);
        a.setStatus(AccidentStatus.UNDER_INVESTIGATION);
        db.updateAccident(a);

        User p1 = db.getUser(a.getReporterNationalId());
        User p2 = db.getUser(a.getOtherPartyNationalId());

        System.out.println("\nParty 1: " + p1.getName() + " (" + a.getReporterLicensePlate() + ")");
        System.out.println("Party 2: " + p2.getName() + " (" + a.getOtherPartyLicensePlate() + ")");
        System.out.print("\nWho is at fault? (1 or 2): ");
        String faultChoice = getValidInput();

        String atFault = faultChoice.equals("1") ? p1.getNationalId() : p2.getNationalId();
        String damagedPlate = faultChoice.equals("1") ? a.getOtherPartyLicensePlate() : a.getReporterLicensePlate();

        Vehicle vehicle = db.getVehicleByLicensePlate(damagedPlate);
        System.out.println("\nDamaged Vehicle: " + vehicle.getFullName());

        ArrayList<String> partNames = new ArrayList<>(vehicle.getParts().keySet());
        System.out.println("\nAvailable parts:");
        for (int i = 0; i < partNames.size(); i++) {
            System.out.println((i+1) + ". " + vehicle.getParts().get(partNames.get(i)));
        }

        System.out.print("\nDamaged parts (e.g., 1,2,3): ");
        String[] parts = getValidInput().split(",");
        ArrayList<String> damagedParts = new ArrayList<>();
        for (String p : parts) {
            try {
                int idx = Integer.parseInt(p.trim()) - 1;
                if (idx >= 0 && idx < partNames.size()) {
                    damagedParts.add(partNames.get(idx));
                }
            } catch (NumberFormatException e) {}
        }

        System.out.print("\nNotes: ");
        String notes = getValidInput();

        accidentService.completeInvestigation(a.getAccidentId(), atFault, damagedParts, notes);
        System.out.println("\n✓ Investigation complete!");
    }
}
