module com.example.salamtek1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.salamtek1 to javafx.fxml;
    exports com.example.salamtek1;
}