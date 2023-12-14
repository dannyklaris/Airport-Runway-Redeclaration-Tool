module com.group20seq.runway_redeclaration {
    requires javafx.controls;
    requires java.xml;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.commons.io;

    exports com.group20seq.runway_redeclaration.UI;
    exports com.group20seq.runway_redeclaration.Configs;
    exports com.group20seq.runway_redeclaration.Controllers;
    exports com.group20seq.runway_redeclaration;
}
