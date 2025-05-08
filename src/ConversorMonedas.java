import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.google.gson.Gson;

public class ConversorMonedas {

    static class ExchangeResponse {
        String result;
        Map<String, Double> conversion_rates;
    }

    public static double obtenerTasa(String de, String a) throws Exception {
        String url = String.format("https://v6.exchangerate-api.com/v6/89ab00729117a90d037b11ce/latest/%s", de);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();

        Gson gson = new Gson();
        ExchangeResponse exchange = gson.fromJson(json, ExchangeResponse.class);

        if (!"success".equals(exchange.result) || exchange.conversion_rates == null || !exchange.conversion_rates.containsKey(a)) {
            throw new RuntimeException("X Error en la respuesta de la API.");
        }

        return exchange.conversion_rates.get(a);
    }

    public static void mostrarMenu() {
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║         $ CONVERSOR DE MONEDAS 2025 $        ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║  1) USD => ARS                                 ║");
        System.out.println("║  2) ARS => USD                                 ║");
        System.out.println("║  3) USD => BRL                                 ║");
        System.out.println("║  4) BRL => USD                                 ║");
        System.out.println("║  5) USD => COP                                 ║");
        System.out.println("║  6) COP => USD                                 ║");
        System.out.println("║  7) USD => CLP                                 ║");
        System.out.println("║  8) CLP => USD                                 ║");
        System.out.println("║  9) Ver historial de conversiones             ║");
        System.out.println("║ 10) Salir                                     ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.print("=> Ingrese una opción: ");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<String> historial = new ArrayList<>();
        int opcion;

        do {
            mostrarMenu();
            opcion = sc.nextInt();

            if (opcion >= 1 && opcion <= 8) {
                System.out.print("$ Ingrese el monto a convertir: ");
                double monto = sc.nextDouble();
                String de = "", a = "";

                switch (opcion) {
                    case 1 -> { de = "USD"; a = "ARS"; }
                    case 2 -> { de = "ARS"; a = "USD"; }
                    case 3 -> { de = "USD"; a = "BRL"; }
                    case 4 -> { de = "BRL"; a = "USD"; }
                    case 5 -> { de = "USD"; a = "COP"; }
                    case 6 -> { de = "COP"; a = "USD"; }
                    case 7 -> { de = "USD"; a = "CLP"; }
                    case 8 -> { de = "CLP"; a = "USD"; }
                }

                try {
                    double tasa = obtenerTasa(de, a);
                    double resultado = monto * tasa;
                    LocalDateTime ahora = LocalDateTime.now();
                    DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String timestamp = ahora.format(formato);

                    String conversion = String.format("[%s] %.2f %s => %.2f %s", timestamp, monto, de, resultado, a);
                    System.out.println("\n Conversión exitosa: " + conversion);
                    System.out.println("---------------------------------------------------");
                    historial.add(conversion);

                } catch (Exception e) {
                    System.out.println("! Error: " + e.getMessage());
                }

            } else if (opcion == 9) {
                System.out.println("\n $ Historial de Conversiones:");
                if (historial.isEmpty()) {
                    System.out.println("   (Aún no hay conversiones registradas)");
                } else {
                    historial.forEach(conv -> System.out.println("   * " + conv));
                }
                System.out.println("---------------------------------------------------");

            } else if (opcion != 10) {
                System.out.println("! Opción inválida. Intente de nuevo.");
            }

        } while (opcion != 10);

        System.out.println("\n Gracias por usar el conversor $. ¡Nos vemos!");
    }
}
