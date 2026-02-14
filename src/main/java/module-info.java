module com.example.examsystemproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.examsystemproject to javafx.fxml;
    exports com.example.examsystemproject;
}