module com.app.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires jdk.xml.dom;
    requires java.naming;

    opens com.app to javafx.fxml;
    exports com.app;
    exports com.app.controllers;
    opens com.app.controllers to javafx.fxml;
    exports com.app.models;
    opens com.app.models to javafx.fxml;
    exports com.app.utils;
    opens com.app.utils to javafx.fxml;
    exports com.app.controllers.factories;
    opens com.app.controllers.factories to javafx.fxml;
    exports com.app.controllers.controllerInterfaces;
    opens com.app.controllers.controllerInterfaces to javafx.fxml;
    exports com.app.timer;
    opens com.app.timer to javafx.fxml;
}