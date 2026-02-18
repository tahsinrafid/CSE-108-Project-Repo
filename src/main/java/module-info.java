module com.example.examsystemproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.examsystemproject to javafx.fxml;
    exports com.example.examsystemproject;
}