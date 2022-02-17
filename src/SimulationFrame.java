import javax.swing.*;

public class SimulationFrame extends JFrame {


    SimulationFrame(){
        this.add(new SimulationPanel());
        this.setTitle("Evolution Generator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

    }
}
