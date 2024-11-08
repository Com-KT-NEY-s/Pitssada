package Arquivos;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Lucro {

    private static final String FILE_PATH = "lucro.txt"; // Caminho do arquivo de lucro
    private double totalDiario = 0.0; // Acumulador para o total diário
    
    private static final LocalTime horaSimulada = LocalTime.of(5, 0);

    // Método para registrar o lucro de cada pedido
    public void escreveLucro(double precoFinal) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dataHoraFormatada = "[" + now.format(formatter) + "]";
        
        // Adiciona o preço do pedido ao total diário
        totalDiario += precoFinal;
        
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             PrintWriter pw = new PrintWriter(fw)) {
             
            // Escreve o pedido no arquivo com data e hora
            pw.println(dataHoraFormatada + " Preço: " + precoFinal);
            
            // Verifica se é meia-noite para registrar o total diário e iniciar uma nova lista
            if (LocalTime.now().equals(horaSimulada)) {
                pw.println("Total do dia: " + totalDiario); // Registra o total diário
                pw.println(); // Quebra de linha para separar os dias
                totalDiario = 0.0; // Reinicia o acumulador para o novo dia
            }
            
            System.out.println("Registro de lucro adicionado com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao gravar no arquivo: " + e.getMessage());
        }
    }
}
