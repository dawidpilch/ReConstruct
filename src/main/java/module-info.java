module com.reconstruct {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires javafx.web;
    requires commons.math3;

    opens com.reconstruct to javafx.fxml;
    opens com.reconstruct.view to javafx.fxml;

    exports com.reconstruct;
    exports com.reconstruct.view;
}