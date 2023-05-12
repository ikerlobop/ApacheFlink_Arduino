import com.fazecast.jSerialComm.SerialPort;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class SerialPortService {
    
    private SerialPort serialPort;
    private BufferedReader input;
    private boolean running;
    
    @PostConstruct
    public void initialize() throws Exception {
        // Obtén una lista de puertos seriales disponibles
        SerialPort[] portNames = SerialPort.getCommPorts();
        if (portNames.length == 0) {
            throw new Exception("No se ha encontrado ningún puerto serial disponible");
        }

        // Configura la conexión con el primer puerto serial disponible
        serialPort = portNames[0];
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        serialPort.setBaudRate(9600);

        // Abre la conexión serial y establece la entrada de datos
        if (serialPort.openPort()) {
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            running = true;
        } else {
            throw new Exception("No se puede abrir el puerto serial");
        }
    }

    @PreDestroy
    public void cleanup() throws IOException {
        // Cierra la conexión serial y la entrada de datos
        running = false;
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
        if (input != null) {
            input.close();
        }
    }

    public void readSerialData() throws IOException {
        // Lee los datos de la entrada serial en un bucle mientras el servicio está en ejecución
        while (running) {
            String data = input.readLine();
            if (data != null && !data.isEmpty()) {
                // Aquí puedes procesar los datos como necesites
                // Por ejemplo, puedes imprimir los datos en la consola
                System.out.println(data);
            }
        }
    }
}
