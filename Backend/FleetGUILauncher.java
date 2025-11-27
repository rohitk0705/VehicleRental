package Backend;

import javax.swing.SwingUtilities;

public class FleetGUILauncher {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            FleetGUI gui = new FleetGUI();
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);
        });
    }
}
