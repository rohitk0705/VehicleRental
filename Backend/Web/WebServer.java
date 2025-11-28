package Backend.Web;

import Backend.Common.*;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServer {
    private static FleetService service;

    public static void main(String[] args) throws IOException {
        service = new FleetService();
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Serve static files from the "docs" directory
        server.createContext("/", new StaticHandler());
        
        // API Endpoints
        server.createContext("/api/fleet", new FleetHandler());
        server.createContext("/api/add", new AddHandler());
        server.createContext("/api/rent", new RentHandler());
        server.createContext("/api/return", new ReturnHandler());
        server.createContext("/api/delete", new DeleteHandler());
        server.createContext("/api/edit", new EditHandler());
        server.createContext("/api/testdata", new TestDataHandler());

        server.setExecutor(null);
        System.out.println("Server started on http://localhost:" + port);
        server.start();
    }

    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String path = t.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            
            File file = new File("docs" + path);
            if (file.exists() && !file.isDirectory()) {
                String mime = "text/plain";
                if(path.endsWith(".html")) mime = "text/html";
                else if(path.endsWith(".css")) mime = "text/css";
                else if(path.endsWith(".js")) mime = "application/javascript";
                
                t.getResponseHeaders().set("Content-Type", mime);
                t.sendResponseHeaders(200, file.length());
                OutputStream os = t.getResponseBody();
                Files.copy(file.toPath(), os);
                os.close();
            } else {
                String response = "404 Not Found";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class FleetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            List<Vehicle> fleet = service.getFleet();
            StringBuilder json = new StringBuilder("[");
            for(int i=0; i<fleet.size(); i++){
                Vehicle v = fleet.get(i);
                json.append(String.format("{\"id\":\"%s\",\"type\":\"%s\",\"brand\":\"%s\",\"rented\":%b,\"extra\":\"%s\",\"price\":%.2f,\"rentalCount\":%d}",
                    escape(v.getId()), escape(v.getTypeName()), escape(v.getBrand()), v.isRented(), escape(v.getExtra()), v.getPrice(), v.getRentalCount()));
                if(i < fleet.size()-1) json.append(",");
            }
            json.append("]");
            
            byte[] response = json.toString().getBytes("UTF-8");
            t.getResponseHeaders().set("Content-Type", "application/json");
            t.sendResponseHeaders(200, response.length);
            OutputStream os = t.getResponseBody();
            os.write(response);
            os.close();
        }
        
        private String escape(String s) {
            if(s == null) return "";
            return s.replace("\"", "\\\"");
        }
    }

    static class AddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if("POST".equals(t.getRequestMethod())){
                Map<String, String> params = parseParams(t);
                String type = params.get("type");
                String id = params.get("id");
                String brand = params.get("brand");
                String extra = params.get("extra");
                double price = 0.0;
                try {
                    price = Double.parseDouble(params.get("price"));
                } catch (Exception e) {
                    price = 0.0;
                }

                if(service.existsId(id)){
                    sendResponse(t, 400, "ID already exists");
                    return;
                }

                Vehicle v = null;
                try {
                    switch (type) {
                        case "Car": v = new Car(id, brand, extra, price); break;
                        case "Bike": v = new Bike(id, brand, extra, price); break;
                        case "Truck": v = new Truck(id, brand, Double.parseDouble(extra), price); break;
                    }
                    if(v != null) {
                        service.addVehicle(v);
                        sendResponse(t, 200, "Added");
                    } else {
                        sendResponse(t, 400, "Invalid type");
                    }
                } catch (Exception e) {
                    sendResponse(t, 400, "Error: " + e.getMessage());
                }
            } else {
                sendResponse(t, 405, "Method Not Allowed");
            }
        }
    }

    static class RentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if("POST".equals(t.getRequestMethod())){
                Map<String, String> params = parseParams(t);
                String result = service.rentVehicle(params.get("id"));
                sendResponse(t, 200, result);
            }
        }
    }

    static class ReturnHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if("POST".equals(t.getRequestMethod())){
                Map<String, String> params = parseParams(t);
                String result = service.returnVehicle(params.get("id"));
                sendResponse(t, 200, result);
            }
        }
    }

    static class DeleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if("POST".equals(t.getRequestMethod())){
                Map<String, String> params = parseParams(t);
                boolean deleted = service.deleteVehicle(params.get("id"));
                if (deleted) {
                    sendResponse(t, 200, "Deleted");
                } else {
                    sendResponse(t, 404, "Vehicle not found");
                }
            }
        }
    }

    static class EditHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if("POST".equals(t.getRequestMethod())){
                Map<String, String> params = parseParams(t);
                String id = params.get("id");
                String brand = params.get("brand");
                String extra = params.get("extra");
                double price = 0.0;
                try {
                    price = Double.parseDouble(params.get("price"));
                } catch (Exception e) {
                    price = 0.0;
                }

                boolean updated = service.updateVehicle(id, brand, extra, price);
                if (updated) {
                    sendResponse(t, 200, "Updated");
                } else {
                    sendResponse(t, 404, "Vehicle not found or invalid data");
                }
            } else {
                sendResponse(t, 405, "Method Not Allowed");
            }
        }
    }

    static class TestDataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("POST".equals(t.getRequestMethod())) {
                service.loadTestData();
                sendResponse(t, 200, "Test data loaded");
            } else {
                sendResponse(t, 405, "Method Not Allowed");
            }
        }
    }

    private static Map<String, String> parseParams(HttpExchange t) throws IOException {
        InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        Map<String, String> map = new HashMap<>();
        if(query != null){
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if(idx > 0){
                    map.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), 
                            URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                }
            }
        }
        return map;
    }

    private static void sendResponse(HttpExchange t, int code, String msg) throws IOException {
        byte[] response = msg.getBytes("UTF-8");
        t.sendResponseHeaders(code, response.length);
        OutputStream os = t.getResponseBody();
        os.write(response);
        os.close();
    }
}
