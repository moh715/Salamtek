package com.example.salamtek1;



import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HelloApplication extends Application {
    private Stage primaryStage;
    private DatabaseManager db = DatabaseManager.getInstance();
    private AccidentService accidentService = new AccidentService();
    private PaymentService paymentService = new PaymentService();
    private User currentUser;
    private Officer currentOfficer;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("SALAMTEK - Accident Management System");
        showLoginScreen();
        stage.show();
    }


    private void showLoginScreen() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        Label title = new Label("SALAMTEK");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Car Accident Management System");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        subtitle.setTextFill(Color.web("#e0e0e0"));

        Button userLoginBtn = createStyledButton("User Login", "#4CAF50");
        userLoginBtn.setOnAction(e -> showUserLoginForm());

        Button officerLoginBtn = createStyledButton("Officer Login", "#2196F3");
        officerLoginBtn.setOnAction(e -> showOfficerLoginForm());

        Button registerBtn = createStyledButton("Register New User", "#FF9800");
        registerBtn.setOnAction(e -> showRegistrationForm());

        root.getChildren().addAll(title, subtitle, userLoginBtn, officerLoginBtn, registerBtn);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
    }


    private void showUserLoginForm() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label title = new Label("User Login");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        TextField nationalIdField = new TextField();
        nationalIdField.setPromptText("National ID");
        nationalIdField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.RED);

        Button loginBtn = createStyledButton("Login", "#4CAF50");
        loginBtn.setOnAction(e -> {
            User user = db.getUser(nationalIdField.getText().trim());
            if (user != null && user.getPassword().equals(passwordField.getText())) {
                currentUser = user;
                showUserDashboard();
            } else {
                messageLabel.setText("Invalid credentials!");
            }
        });

        Button backBtn = createStyledButton("Back", "#757575");
        backBtn.setOnAction(e -> showLoginScreen());

        root.getChildren().addAll(title, nationalIdField, passwordField, messageLabel, loginBtn, backBtn);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
    }
    private void showRegistrationForm() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label title = new Label("User Registration");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        TextField nationalIdField = new TextField();
        nationalIdField.setPromptText("National ID (14 digits)");
        nationalIdField.setMaxWidth(300);

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setMaxWidth(300);

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setMaxWidth(300);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        Label messageLabel = new Label();

        Button registerBtn = createStyledButton("Register", "#4CAF50");
        registerBtn.setOnAction(e -> {
            try {
                db.addUser(new User(nationalIdField.getText().trim(), nameField.getText().trim(),
                        phoneField.getText().trim(), emailField.getText().trim(),
                        passwordField.getText()));
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("Registration successful!");
            } catch (Exception ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Error: " + ex.getMessage());
            }
        });
        Button backBtn = createStyledButton("Back", "#757575");
        backBtn.setOnAction(e -> showLoginScreen());
        root.getChildren().addAll(title, nationalIdField, nameField, phoneField, emailField, passwordField, messageLabel, registerBtn, backBtn);
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
    }

    private void showUserDashboard() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");


        HBox header = new HBox();
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #667eea;");
        Label headerLabel = new Label("Welcome, " + currentUser.getName());
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.WHITE);
        header.getChildren().add(headerLabel);
        root.setTop(header);

        VBox center = new VBox(20);
        center.setPadding(new Insets(40));
        center.setAlignment(Pos.CENTER);

        Button reportBtn = createLargeButton("Report Your Accident", "#FF5722");
        reportBtn.setOnAction(e -> showReportAccidentForm());

        Button paymentBtn = createLargeButton("Pay or Receive Bill", "#4CAF50");
        paymentBtn.setOnAction(e -> showPaymentCenter());

        Button viewBtn = createLargeButton("View My Accidents", "#2196F3");
        viewBtn.setOnAction(e -> showUserAccidents());

        Button logoutBtn = createLargeButton("Logout", "#757575");
        logoutBtn.setOnAction(e -> {
            currentUser = null;
            showLoginScreen();
        });

        center.getChildren().addAll(reportBtn, paymentBtn, viewBtn, logoutBtn);
        root.setCenter(center);

        Scene scene = new Scene(root, 700, 600);
        primaryStage.setScene(scene);
    }

    private void showReportAccidentForm() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f5f5f5;");
        Label title = new Label("Report Accident");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        TextField otherIdField = new TextField();
        otherIdField.setPromptText("Other Party's National ID");
        otherIdField.setMaxWidth(400);

        TextField myPlateField = new TextField();
        myPlateField.setPromptText("Your License Plate");
        myPlateField.setMaxWidth(400);
        TextField otherPlateField = new TextField();
        otherPlateField.setPromptText("Other Party's License Plate");
        otherPlateField.setMaxWidth(400);
        TextField latField = new TextField();
        latField.setPromptText("Latitude (e.g., 30.0444)");
        latField.setMaxWidth(400);

        TextField lonField = new TextField();
        lonField.setPromptText("Longitude (e.g., 31.2357)");
        lonField.setMaxWidth(400);

        Label messageLabel = new Label();

        Button submitBtn = createStyledButton("Submit Report", "#FF5722");
        submitBtn.setOnAction(e -> {
            try {
                double lat = Double.parseDouble(latField.getText().trim());
                double lon = Double.parseDouble(lonField.getText().trim());

                String accidentId = accidentService.reportAccident(
                        currentUser.getNationalId(),
                        otherIdField.getText().trim(),
                        myPlateField.getText().trim(),
                        otherPlateField.getText().trim(),
                        new Location(lat, lon)
                );

                showSuccessDialog("Accident Reported",
                        "Accident ID: " + accidentId + "\n" +
                                "An officer has been dispatched and will arrive shortly.");
                showUserDashboard();
            } catch (Exception ex) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Error: " + ex.getMessage());
            }
        });

        Button backBtn = createStyledButton("Back", "#757575");
        backBtn.setOnAction(e -> showUserDashboard());

        root.getChildren().addAll(title, otherIdField, myPlateField, otherPlateField,
                latField, lonField, messageLabel, submitBtn, backBtn);

        Scene scene = new Scene(root, 700, 600);
        primaryStage.setScene(scene);
    }


    private void showPaymentCenter() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label title = new Label("Payment Center");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        HashMap<String, Object> summary = paymentService.getPaymentSummary(currentUser.getNationalId());
        double owed = (double) summary.get("totalOwed");
        double available = (double) summary.get("totalAvailable");

        Label owedLabel = new Label(String.format("Amount you owe: %.2f EGP", owed));
        owedLabel.setFont(Font.font("Arial", 18));
        owedLabel.setTextFill(owed > 0 ? Color.RED : Color.BLACK);

        Label availableLabel = new Label(String.format("Amount available: %.2f EGP", available));
        availableLabel.setFont(Font.font("Arial", 18));
        availableLabel.setTextFill(available > 0 ? Color.GREEN : Color.BLACK);

        Button payBtn = createStyledButton("Pay Outstanding Bills", "#F44336");
        payBtn.setDisable(owed == 0);
        payBtn.setOnAction(e -> showPayBillsDialog((ArrayList<Payment>) summary.get("paymentsOwed")));

        Button withdrawBtn = createStyledButton("Withdraw Available Funds", "#4CAF50");
        withdrawBtn.setDisable(available == 0);
        withdrawBtn.setOnAction(e -> {
            paymentService.withdrawFunds(currentUser.getNationalId());
            showSuccessDialog("withdrawal Complete",
                    "funds will be transferred to your account within 2-3 business days.");
            showPaymentCenter();
        });

        Button backBtn = createStyledButton("Back", "#757575");
        backBtn.setOnAction(e -> showUserDashboard());

        root.getChildren().addAll(title, owedLabel, availableLabel, payBtn, withdrawBtn, backBtn);

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
    }

    private void showUserAccidents() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label title = new Label("My Accidents");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        ScrollPane scrollPane = new ScrollPane();
        VBox accidentsList = new VBox(10);

        ArrayList<Accident> accidents = accidentService.getUserAccidents(currentUser.getNationalId());

        for (Accident a : accidents) {
            VBox accidentBox = new VBox(5);
            accidentBox.setPadding(new Insets(15));
            accidentBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

            Label idLabel = new Label("Accident ID: " + a.getAccidentId());
            idLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            Label dateLabel = new Label("Date: " + a.getFormattedReportTime());
            Label statusLabel = new Label("Status: " + a.getStatus());

            accidentBox.getChildren().addAll(idLabel, dateLabel, statusLabel);

            if (a.getStatus() == AccidentStatus.COMPLETED || a.getStatus() == AccidentStatus.PAID) {
                boolean userAtFault = a.getAtFaultPartyId().equals(currentUser.getNationalId());
                Label faultLabel = new Label("At Fault: " + (userAtFault ? "You" : "Other Party"));
                Label costLabel = new Label(String.format("Cost: %.2f EGP", a.getTotalCost()));
                accidentBox.getChildren().addAll(faultLabel, costLabel);
            }

            accidentsList.getChildren().add(accidentBox);
        }

        scrollPane.setContent(accidentsList);
        scrollPane.setFitToWidth(true);

        Button backBtn = createStyledButton("Back", "#757575");
        backBtn.setOnAction(e -> showUserDashboard());

        root.getChildren().addAll(title, scrollPane, backBtn);

        Scene scene = new Scene(root, 700, 600);
        primaryStage.setScene(scene);
    }
    private void showOfficerLoginForm() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label title = new Label("Officer Login");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.RED);

        Button loginBtn = createStyledButton("Login", "#2196F3");
        loginBtn.setOnAction(e -> {
            Officer officer = db.authenticateOfficer(emailField.getText().trim(), passwordField.getText());
            if (officer != null) {
                currentOfficer = officer;
                showOfficerDashboard();
            } else {
                messageLabel.setText("Invalid credentials!");
            }
        });

        Button backBtn = createStyledButton("Back", "#757575");
        backBtn.setOnAction(e -> showLoginScreen());

        root.getChildren().addAll(title, emailField, passwordField, messageLabel, loginBtn, backBtn);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
    }

    private void showOfficerDashboard() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        HBox header = new HBox();
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2196F3;");
        Label headerLabel = new Label("Officer: " + currentOfficer.getName());
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.WHITE);
        header.getChildren().add(headerLabel);
        root.setTop(header);

        VBox center = new VBox(20);
        center.setPadding(new Insets(40));
        center.setAlignment(Pos.CENTER);

        Button viewBtn = createLargeButton("View Assigned Accidents", "#FF9800");
        viewBtn.setOnAction(e -> showOfficerAccidents());

        Button investigateBtn = createLargeButton("Complete Investigation", "#4CAF50");
        investigateBtn.setOnAction(e -> showInvestigationForm());

        Button logoutBtn = createLargeButton("Logout", "#757575");
        logoutBtn.setOnAction(e -> {
            currentOfficer = null;
            showLoginScreen();
        });
        center.getChildren().addAll(viewBtn, investigateBtn, logoutBtn);
        root.setCenter(center);
        Scene scene = new Scene(root, 700, 600);
        primaryStage.setScene(scene);
    }
    private void showOfficerAccidents() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label title = new Label("Assigned Accidents");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        ScrollPane scrollPane = new ScrollPane();
        VBox accidentsList = new VBox(10);

        ArrayList<Accident> accidents = accidentService.getOfficerAccidents(currentOfficer.getOfficerId());

        for (Accident a : accidents) {
            VBox accidentBox = new VBox(5);
            accidentBox.setPadding(new Insets(15));
            accidentBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");

            User p1 = db.getUser(a.getReporterNationalId());
            User p2 = db.getUser(a.getOtherPartyNationalId());

            Label idLabel = new Label("Accident ID: " + a.getAccidentId());
            idLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            Label statusLabel = new Label("Status: " + a.getStatus());
            Label party1Label = new Label("Party 1: " + p1.getName());
            Label party2Label = new Label("Party 2: " + p2.getName());

            accidentBox.getChildren().addAll(idLabel, statusLabel, party1Label, party2Label);
            accidentsList.getChildren().add(accidentBox);
        }
        scrollPane.setContent(accidentsList);
        scrollPane.setFitToWidth(true);
        Button backBtn = createStyledButton("Back", "#757575");
        backBtn.setOnAction(e -> showOfficerDashboard());
        root.getChildren().addAll(title, scrollPane, backBtn);
        Scene scene = new Scene(root, 700, 600);
        primaryStage.setScene(scene);
    }


    private void showInvestigationForm() {
        ArrayList<Accident> pendingAccidents = accidentService.getOfficerAccidents(currentOfficer.getOfficerId())
                .stream().filter(a -> a.getStatus() != AccidentStatus.COMPLETED &&
                        a.getStatus() != AccidentStatus.PAID)
                .collect(Collectors.toCollection(ArrayList::new));

        if (pendingAccidents.isEmpty()) {
            showErrorDialog("No Pending Investigations", "You have no accidents pending investigation.");
            return;
        }

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label title = new Label("Complete Investigation");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        ComboBox<String> accidentCombo = new ComboBox<>();
        for (Accident a : pendingAccidents) {
            accidentCombo.getItems().add(a.getAccidentId());
        }
        accidentCombo.setPromptText("Select Accident");

        Label infoLabel = new Label();
        RadioButton party1Radio = new RadioButton();
        RadioButton party2Radio = new RadioButton();
        ToggleGroup group = new ToggleGroup();
        party1Radio.setToggleGroup(group);
        party2Radio.setToggleGroup(group);

        ListView<String> partsListView = new ListView<>();
        partsListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        accidentCombo.setOnAction(e -> {
            String accidentId = accidentCombo.getValue();
            if (accidentId != null) {
                Accident a = db.getAccident(accidentId);
                User p1 = db.getUser(a.getReporterNationalId());
                User p2 = db.getUser(a.getOtherPartyNationalId());

                infoLabel.setText("Party 1: " + p1.getName() + "\nParty 2: " + p2.getName());
                party1Radio.setText("Party 1 (" + p1.getName() + ") is at fault");
                party2Radio.setText("Party 2 (" + p2.getName() + ") is at fault");

                // Load vehicle parts
                String plate = a.getReporterLicensePlate();
                Vehicle vehicle = db.getVehicleByLicensePlate(plate);
                if (vehicle != null) {
                    partsListView.getItems().clear();
                    partsListView.getItems().addAll(vehicle.getParts().keySet());
                }
            }
        });

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Investigation notes...");
        notesArea.setPrefRowCount(3);

        Button submitBtn = createStyledButton("Submit Investigation", "#4CAF50");
        submitBtn.setOnAction(e -> {
            String accidentId = accidentCombo.getValue();
            if (accidentId == null) {
                showErrorDialog("Error", "Please select an accident");
                return;
            }
            if (!party1Radio.isSelected() && !party2Radio.isSelected()) {
                showErrorDialog("Error", "Please select who is at fault");
                return;
            }

            Accident a = db.getAccident(accidentId);
            String atFaultId = party1Radio.isSelected() ?
                    a.getReporterNationalId() : a.getOtherPartyNationalId();

            ArrayList<String> selectedParts = new ArrayList<>(partsListView.getSelectionModel().getSelectedItems());

            try {
                accidentService.completeInvestigation(accidentId, atFaultId, selectedParts, notesArea.getText());
                showSuccessDialog("Investigation Complete", "Report filed successfully!");
                showOfficerDashboard();
            } catch (Exception ex) {
                showErrorDialog("Error", ex.getMessage());
            }
        });

        Button backBtn = createStyledButton("Back", "#757575");
        backBtn.setOnAction(e -> showOfficerDashboard());

        root.getChildren().addAll(title, accidentCombo, infoLabel,
                party1Radio, party2Radio,
                new Label("Select Damaged Parts:"), partsListView,
                new Label("Notes:"), notesArea,
                submitBtn, backBtn);

        Scene scene = new Scene(root, 700, 700);
        primaryStage.setScene(scene);
    }

    // ========== HELPER METHODS FOR PAY BILLS DIALOG ==========
    private void showPayBillsDialog(ArrayList<Payment> payments) {
        ArrayList<Payment> unpaid = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING ||
                        p.getStatus() == PaymentStatus.LATE)
                .collect(Collectors.toCollection(ArrayList::new));

        if (unpaid.isEmpty()) {
            showErrorDialog("No Bills", "You have no outstanding payments.");
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Pay Bills");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label title = new Label("Select Payment to Pay");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        ComboBox<String> paymentCombo = new ComboBox<>();
        for (Payment p : unpaid) {
            paymentCombo.getItems().add(String.format("%s - %.2f EGP (Due: %s)",
                    p.getPaymentId(), p.getTotalAmountDue(), p.getFormattedDueDate()));
        }

        ComboBox<String> methodCombo = new ComboBox<>();
        methodCombo.getItems().addAll("Phone Cash", "Fawry", "Bank Transfer");
        methodCombo.setPromptText("Select Payment Method");

        Button payBtn = createStyledButton("Pay Now", "#4CAF50");
        payBtn.setOnAction(e -> {
            int idx = paymentCombo.getSelectionModel().getSelectedIndex();
            String method = methodCombo.getValue();

            if (idx >= 0 && method != null) {
                Payment payment = unpaid.get(idx);
                paymentService.processPayment(payment.getPaymentId(), method);
                showSuccessDialog("Payment Successful",
                        String.format("Paid %.2f EGP via %s", payment.getTotalAmountDue(), method));
                dialog.close();
                showPaymentCenter();
            } else {
                showErrorDialog("Error", "Please select payment and method");
            }
        });

        root.getChildren().addAll(title, paymentCombo, methodCombo, payBtn);

        Scene scene = new Scene(root, 450, 250);
        dialog.setScene(scene);
        dialog.show();
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10 30; " +
                        "-fx-background-radius: 5; " +
                        "-fx-cursor: hand;", color));
        btn.setMinWidth(200);
        btn.setOnMouseEntered(e ->
                btn.setStyle(btn.getStyle() + "-fx-opacity: 0.8;"));
        btn.setOnMouseExited(e ->
                btn.setStyle(btn.getStyle().replace("-fx-opacity: 0.8;", "")));

        return btn;
    }

    private Button createLargeButton(String text, String color) {
        Button btn = createStyledButton(text, color);
        btn.setMinWidth(400);
        btn.setMinHeight(60);
        btn.setStyle(btn.getStyle() + "-fx-font-size: 18px;");
        return btn;
    }
    private void showSuccessDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
