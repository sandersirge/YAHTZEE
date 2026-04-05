/**
 * Module descriptor for the Yahtzee JavaFX application.
 *
 * @author sandersirge
 */
module projekt.yahtzee {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    exports projekt.yahtzee;
    exports projekt.yahtzee.model;
    exports projekt.yahtzee.model.combos;
    exports projekt.yahtzee.controller.game;
    exports projekt.yahtzee.controller.data;
    exports projekt.yahtzee.controller.ui;
    exports projekt.yahtzee.util;

    opens projekt.yahtzee to javafx.fxml;
    opens projekt.yahtzee.ui.scenes to javafx.fxml;
    opens projekt.yahtzee.ui.dialogs to javafx.fxml;
    opens projekt.yahtzee.ui.components to javafx.fxml;
    opens projekt.yahtzee.ui.handlers to javafx.fxml;
    opens projekt.yahtzee.ui.navigation to javafx.fxml;
}
