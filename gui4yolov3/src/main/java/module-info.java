module nazari {
    requires javafx.controls;
    requires javafx.fxml;

    opens nazari to javafx.fxml;
    exports nazari;
}