import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {

    // Construtor
    public WeatherAppGUI() {
        super();
        setTitle("Weather App");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        addGuiComponents();
    }

    // Função para adicionar os componentes na janela
    private void addGuiComponents(){

        // Campo de texto da busca
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15,15,350,45);
        searchTextField.setFont(new Font("Dialog",Font.PLAIN,24));

        add(searchTextField);

        // Imagem de como está o tempo
        JLabel weatherConditionImage = new JLabel(loadImage("src/weather_app_images/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        // Texto da temperatura
        JLabel temperatureText = new JLabel("10 C°");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog",Font.BOLD,48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // Descrição do tempo
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,430,450,36);
        weatherConditionDesc.setFont(new Font("Dialog",Font.PLAIN,32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // Imagem da umidade
        JLabel humidityImage = new JLabel(loadImage("src/weather_app_images/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        // Texto da umidade
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog",Font.PLAIN,16));
        humidityText.setHorizontalAlignment(SwingConstants.CENTER);
        add(humidityText);

        // Imagem windSpeed
        JLabel windSpeedImage = new JLabel(loadImage("src/weather_app_images/windspeed.png"));
        windSpeedImage.setBounds(220,500,74,66);
        add(windSpeedImage);

        // Texto de windSpeed
        JLabel windSpeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windSpeedText.setBounds(310,500,85,55);
        windSpeedText.setFont(new Font("Dialog",Font.PLAIN,16));
        windSpeedText.setHorizontalAlignment(SwingConstants.CENTER);
        add(windSpeedText);

        // ===== Lógica do botão de busca ====//

        // Criando o botão
        JButton searchButton = new JButton(loadImage("src/weather_app_images/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);

        // Ação do botão de busca

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String userInput = searchTextField.getText();

                // Verificando se a entrada do usuário (userInput) está vazia,
                if(userInput.replaceAll("\\s", "").isEmpty()){
                    return;
                }

                // Recebendo os dados
                JSONObject weatherData = WeatherApp.getWeatherData(userInput);

                // Atualizando a imagem conforme a condição do tempo
                assert weatherData != null;
                String weatherCondition = (String) weatherData.get("weather_condition");

                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/weather_app_images/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/weather_app_images/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/weather_app_images/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/weather_app_images/snow.png"));
                        break;
                }

                // Updates dos campos de textos

                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C°");

                weatherConditionDesc.setText(weatherCondition);

                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                Object windSpeedObj = weatherData.get("windspeed");
                if (windSpeedObj != null) {
                    double wind_speed = (Double) windSpeedObj;
                    windSpeedText.setText("<html><b>WindSpeed</b> " + wind_speed + "km/h</html>");
                } else {
                    // Caso não haja a informação de umidade
                    windSpeedText.setText("<html><b>WindSpeed</b> N/A</html>");
                }
            }
        });

        // Adicionando o botão
        add(searchButton);
    }

    // Função que carrega as imagens na label
    private ImageIcon loadImage(String resourcePath){
        try{
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Could not found resource");
        return null;
    }
}