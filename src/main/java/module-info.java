module com.app.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires jdk.xml.dom;

    opens com.app to javafx.fxml;
    exports com.app;
    exports com.app.controllers;
    opens com.app.controllers to javafx.fxml;
    exports com.app.models;
    opens com.app.models to javafx.fxml;
}