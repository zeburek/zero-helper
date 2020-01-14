module ru.zeburek.zerohelper {
    requires de.jensd.fx.glyphs.commons;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.logging;
    requires java.naming;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.swing;
    requires google.analytics.java;
    requires commons.codec;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    opens ru.zeburek.zerohelper to javafx.graphics;
    opens ru.zeburek.zerohelper.controllers to javafx.fxml;
    exports ru.zeburek.zerohelper;
}