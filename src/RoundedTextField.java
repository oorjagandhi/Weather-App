import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedTextField extends JTextField {
    private Shape shape;
    private int radius;
    private Color borderColor = Color.LIGHT_GRAY; // Default border color
    private int borderWidth = 2; // Default border width

    public RoundedTextField(int radius) {
        super();
        this.radius = radius;
        setOpaque(false); // As we paint a rounded background, we don't want to paint the background.
    }

    // Setter for border color
    public void setBorderColor(Color color) {
        borderColor = color;
        repaint(); // Repaint the component with the new border color
    }

    // Setter for border width
    public void setBorderWidth(int width) {
        borderWidth = width;
        repaint(); // Repaint the component with the new border width
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(borderWidth));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        return shape.contains(x, y);
    }
}
