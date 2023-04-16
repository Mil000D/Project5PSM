/**
 * @author Miłosz Demendecki s24611
 */

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * I have created an example in Excel with charts and data copied from this program
 */
public class MoonSimulation extends JFrame{
    private static final ArrayList<Double> xEarth = new ArrayList<>();
    private static final ArrayList<Double> yEarth = new ArrayList<>();
    private static final ArrayList<Double> xMoon = new ArrayList<>();
    private static final ArrayList<Double> yMoon = new ArrayList<>();
    private static final ArrayList<Double> xMoonRelativeToSun = new ArrayList<>();
    private static final ArrayList<Double> yMoonRelativeToSun = new ArrayList<>();

    public static void main(String[] args) {
        createFrame();
    }

    /**
     * Function that adds records from list specified as parameter to text area,
     * so user can easily copy records and paste it to e.g. excel
     */
    public static void addListToArea(ArrayList<Double> list, String str, JTextArea textArea, JButton button, int rows){
        for (int i = 0; i < rows; i++) {
            textArea.append(list.get(i).toString().replace(".",",") + "\n");
            button.setText(str);
        }
    }

    /**
     * Function that calculates x and y for both Moon and Earth - Moon in relation to Earth and Earth with relation to Sun
     */
    public static void calculationsForSimulation(int rows, double dt, boolean earth) {
        double G = 6.6743 * Math.pow(10, -11);
        double M;
        double R;
        double Mm = 7.347 * Math.pow(10, 22);

        if(earth) {
            double Ms = 1.989 * Math.pow(10, 30);
            double Res = 1.5 * Math.pow(10, 8) * 1000;
            M = Ms;
            R = Res;
        } else {
            double Me = 5.972 * Math.pow(10, 24);
            double Rem = 384400 * 1000;
            M = Me;
            R = Rem;
        }

        double Xe = 0;
        double Ye = R;
        double Vx_E = Math.sqrt(G * M / R);
        double Vy_E = 0;

        for (int i = 0; i < rows; i++) {
            if(earth) {
                xEarth.add(Xe);
                yEarth.add(Ye);
            } else {
                xMoon.add(Xe);
                yMoon.add(Ye);
            }

            double Wx = -Xe;
            double Wy = -Ye;
            double W_len = Math.sqrt(Math.pow(Wx, 2) + Math.pow(Wy, 2));

            double Ux = Wx / W_len;
            double Uy = Wy / W_len;

            double A = G * M / Math.pow(W_len, 2);
            double Ax = A * Ux;
            double Ay = A * Uy;

            double Xe_2 = Xe + Vx_E * dt / 2;
            double Ye_2 = Ye + Vy_E * dt / 2;

            double Wx_2 = -Xe_2;
            double Wy_2 = -Ye_2;
            double W_len2 = Math.sqrt(Math.pow(Wx_2, 2) + Math.pow(Wy_2, 2));

            double Ux_2 = Wx_2 / W_len2;
            double Uy_2 = Wy_2 / W_len2;

            double A_2 =  G * M / Math.pow(W_len2, 2);
            double Ax_2 = A_2 * Ux_2;
            double Ay_2 = A_2 * Uy_2;

            double Vx_E2 = Vx_E + Ax * dt / 2;
            double Vy_E2 = Vy_E + Ay * dt / 2;

            double Dx_E = Vx_E2 * dt;
            double Dy_E = Vy_E2 * dt;
            double Dvx_E = Ax_2 * dt;
            double Dvy_E = Ay_2 * dt;

            Xe += Dx_E;
            Ye += Dy_E;
            Vx_E += Dvx_E;
            Vy_E += Dvy_E;
        }
    }

    /**
     * In constructor x and y for Moon in relation to Sun are calculated
     * (I had to downscale x of Earth several times for motion of Moon to be visible on chart)
     */
    public MoonSimulation(int rows, double dt) {
        calculationsForSimulation(rows, dt, true);
        calculationsForSimulation(rows, dt, false);
        for(int i = 0; i < xMoon.size(); i++){
            xMoonRelativeToSun.add(xMoon.get(i) + xEarth.get(i) / 20);
            yMoonRelativeToSun.add(yMoon.get(i) + yEarth.get(i) / 20);
        }

        setTitle("MoonSimulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 300);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        for (int i = 1; i <= 6; i++) {
            JTextArea textArea = new JTextArea();
            JButton copyButton = new JButton();
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            textArea.setEditable(false);
            switch (i) {
                case 1 -> addListToArea(xEarth,"Copy xEarth", textArea, copyButton, rows);
                case 2 -> addListToArea(yEarth,"Copy yEarth", textArea, copyButton, rows);
                case 3 -> addListToArea(xMoon, "Copy xMoon", textArea, copyButton, rows);
                case 4 -> addListToArea(yMoon, "Copy yMoon", textArea, copyButton, rows);
                case 5 -> addListToArea(xMoonRelativeToSun, "Copy xMoonRelativeToSun", textArea, copyButton, rows);
                case 6 -> addListToArea(yMoonRelativeToSun, "Copy yMoonRelativeToSun", textArea, copyButton, rows);
            }
            copyButton.addActionListener(e -> {
                textArea.selectAll();
                textArea.copy();
            });
            JPanel scrollPanel = new JPanel(new BorderLayout());
            scrollPanel.add(copyButton, BorderLayout.NORTH);
            scrollPanel.add(scrollPane, BorderLayout.CENTER);
            panel.add(scrollPanel);
        }
        getContentPane().add(panel);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    /**
     * Function that creates user interface
     */
    public static void createFrame(){
        JFrame startFrame = new JFrame("Moon motion");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label1 = new JLabel("Provide number of rows:");
        JTextField textField = new JTextField();

        JLabel label2 = new JLabel("Provide Δt value:");
        JTextField deltaTField = new JTextField();

        JButton calculateButton = new JButton("Click me to perform calculations");
        calculateButton.addActionListener(e -> {
            try {
                new MoonSimulation(Integer.parseInt(textField.getText()),
                         Double.parseDouble(deltaTField.getText()));
            } catch (NumberFormatException ex){
                System.out.println("Try again, remember to pass all arguments");
            }
        });
        startFrame.setResizable(true);
        panel.add(label1);
        panel.add(textField);

        panel.add(label2);
        panel.add(deltaTField);

        panel.add(calculateButton);

        startFrame.getContentPane().add(panel);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setLocationRelativeTo(null);
        startFrame.setVisible(true);
        startFrame.pack();
    }
}