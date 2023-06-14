package it.codewiththeitalians.weeeeeeeee;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

public class PizzaProgressBarUiInstaller {

    @SuppressWarnings({ "MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration" })
    public static ComponentUI createUI(JComponent c) {
        c.setBorder(JBUI.Borders.empty().asUIResource());
        return new PizzaProgressBarUI();
    }
}
