import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui() {
        // Set up our GUI and add a title
        super("Weather App");

        // Configure GUI to end the program's process once the window is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set the size of our GUI (in pixels)
        setSize(450, 650);

        // Load out GUI at the centre of the screen
        setLocationRelativeTo(null);

        // Make our layout manager null to manually position components within GUI
        setLayout(null);

        // Prevent any resize of the GUI
        setResizable(false);

        addGuiComponents();

        // Set the background color of the GUI
        getContentPane().setBackground(new Color(0x91BBFF));
    }

    private void addGuiComponents(){
        // Search field
        RoundedTextField searchField = new RoundedTextField(25);

        // Set the location and size of component
        searchField.setBounds(15, 15, 351, 45);

        // Change the font style and size
        searchField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchField);


        // Weather image
        JLabel weatherConditionImage = new JLabel(loadImage("./src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 100, 450, 217);
        add(weatherConditionImage);

        // Temperature text
        JLabel temperatureText = new JLabel("25°C");
        temperatureText.setBounds(0, 325, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        temperatureText.setForeground(Color.white);

        // Align text to the center
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // Weather condition description
        JLabel weatherConditionText = new JLabel("cloudy");
        weatherConditionText.setBounds(0, 380, 450, 36);
        weatherConditionText.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionText.setForeground(Color.white);

        weatherConditionText.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionText);

        // Humidity type
        JLabel humidityImage = new JLabel(loadImage("./src/assets/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        // Humidity text
        JLabel humidityText = new JLabel("<html><b>humidity</b> </br> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        humidityText.setForeground(Color.white);
        add(humidityText);

        // Windspeed image
        JLabel windSpeedImage = new JLabel(loadImage("./src/assets/windspeed.png"));
        windSpeedImage.setBounds(220,500,74,66);
        add(windSpeedImage);

        // Windspeed text
        JLabel windSpeedText = new JLabel("<html><b>wind speed</b> </br> 15 km/h</html>");
        windSpeedText.setBounds(310, 500, 100, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        windSpeedText.setForeground(Color.white);
        add(windSpeedText);

        // search button
        JButton searchButton = new JButton(loadImage("./src/assets/search.png"));

        // Change the cursor to a hand when hovering over the button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 50, 50);
        searchButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                // Get locaction from user
                String userInput = searchField.getText();

                // Validate input - remove whitespace
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                // Retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                // Check if weatherData is null
                if(weatherData == null){
                    // Handle the error, for example, by showing a dialog
                    JOptionPane.showMessageDialog(null, "Failed to fetch weather data. Please check your internet connection or try a different location.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Exit the method early to avoid accessing null weatherData
                }

                // Update GUI

                // Update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                // Depending on the condition, update the image
                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("./src/assets/clear.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("./src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("./src/assets/snow.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("./src/assets/cloudy.png"));
                        break;
                }

                // Update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + "°C");

                // Update weather condition text
                weatherConditionText.setText(weatherCondition);

                // Update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>humidity</b> </br>" + humidity + "%</html>");

                // Update windspeed text
                double windSpeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>wind speed</b> </br>" + windSpeed + " km/h</html>");

            }
        });
        add(searchButton);

    }

    // Create images in our GUI components
    private ImageIcon loadImage(String path){
        try{
            // Read the image from the path
            BufferedImage image = ImageIO.read(new File(path));

            // Returns an image icon so components can use it
            return new ImageIcon(image);
        } catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Error loading image");
        return null;
    }

}