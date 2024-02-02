package edu.eci.arep.ASE.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import edu.eci.arep.ASE.app.http.HTTPConnection;
import edu.eci.arep.ASE.app.services.ReadFile;

public class HTTPServer {

    /*
     * Inicia el servidor HTTP en el puerto 35000 y maneja las solicitudes entrantes.
     * Crea una instancia de la clase HTTPConnection para establecer una conexión con la API.
     * @throws IOException Si hay algún error de entrada/salida durante la ejecución.
     */
    public static void main(String[] args) throws IOException {
        HTTPConnection httpConnection = new HTTPConnection("https://www.omdbapi.com/", "d91dcd3");
        
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while(running){

            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            BufferedReader in = new BufferedReader(
                                 new InputStreamReader(
                                    clientSocket.getInputStream()));
            String inputLine;

            String firstLine = null;
                
            while ((inputLine = in.readLine()) != null) {
                if(firstLine == null){firstLine = inputLine;}

                System.out.println("Received: " + inputLine);
                
                if (!in.ready()) {
                    break;
                }
            }

            if(firstLine == null){continue;}

            String path = firstLine.split(" ")[1];
            ReadFile readFile = new ReadFile();

            if(path.startsWith("/movie")){
                readFile.getMovieData(clientSocket,httpConnection, path.split("/")[2]);

            }else if(path.equals("/")){
                readFile.lecturaArchivo(clientSocket, "/index.html");
            }else if(path.endsWith(".html") || path.endsWith(".js")){
                readFile.lecturaArchivo(clientSocket , path);
            }else if(path.endsWith(".css")){
                readFile.lecturaEstilos(clientSocket, path);
            }else if(path.endsWith(".jpg")){
                readFile.lecturaImagen(clientSocket, path);

            }

 
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    
    }
}
