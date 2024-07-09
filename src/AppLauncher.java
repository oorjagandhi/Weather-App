import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        // Calling invokeLater() which makes updates to the GUI thread-safe
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                // Make the GUI visible
                new WeatherAppGui().setVisible(true);

//                System.out.println(WeatherApp.getLocationData("Tokyo"));

//                System.out.println(WeatherApp.getCurrentTime());

            }
        });

    }

}
