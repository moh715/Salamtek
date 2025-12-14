module com.example.salamtek1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires jdk.jdi;
//    requires com.example.salamtek1;


    opens com.example.salamtek1 to javafx.fxml;
    exports com.example.salamtek1;
}