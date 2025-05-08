module org.example.colourdeficiency {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires java.desktop;

    opens org.example.colourdeficiency to javafx.fxml;
    exports org.example.colourdeficiency;
    exports org.example.colourdeficiency.controller;
    opens org.example.colourdeficiency.controller to javafx.fxml;
    exports org.example.colourdeficiency.models;
    opens org.example.colourdeficiency.models to javafx.fxml;
}