package main;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class HeatDisplay extends JFrame {

    JPanel gridContainer = new JPanel();

    Surface surface;
    int surfaceWidth, surfaceLength;

    HeatDisplay(Surface surface) {
        this.surface = surface;
        surfaceWidth = surface.width;
        surfaceLength = surface.length;
        initDisplay();
    }

    void initDisplay() {
        // Panel setup
        gridContainer.setLayout(new GridLayout(surface.width, surface.length));
        gridContainer.setBorder(new CompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                new LineBorder(Color.BLACK, 2)
        ));
        add(gridContainer);


        setTitle("Heat Propagation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    void updateGrid() {
        gridContainer.removeAll();
        Region[][] regions = surface.surface;
        JLabel[][] cells = new JLabel[surfaceWidth][surfaceLength];
        for (int i = 0; i < surfaceWidth; i++) {
            for (int j = 0; j < surfaceLength; j++) {
                double temperature = regions[i][j].getTemperature();
                Color regionColor = temperatureToColor(temperature);
                JPanel regionPanel = new JPanel();
                regionPanel.setBackground(regionColor);
                gridContainer.add(regionPanel);
            }
        }
        gridContainer.revalidate();
    }

    Color temperatureToColor(double temperature) {
        int redValue = (int) (100 + temperature);
        int greenValue = 0;
        int blueValue = (int) (temperature);
        return new Color(redValue, greenValue, blueValue);
    }

}
