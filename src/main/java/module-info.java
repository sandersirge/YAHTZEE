module projekt.yahtzee {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens projekt.yahtzee to javafx.fxml;
    exports projekt.yahtzee;
}
