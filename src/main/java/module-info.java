module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;

    opens com.reconstruct to javafx.fxml;
    opens com.reconstruct.view to javafx.fxml;

    exports com.reconstruct;
    exports com.reconstruct.view;
}