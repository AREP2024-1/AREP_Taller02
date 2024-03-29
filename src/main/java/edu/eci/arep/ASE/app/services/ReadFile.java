package edu.eci.arep.ASE.app.services;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import edu.eci.arep.ASE.app.http.HTTPConnection;


public class ReadFile {

    /*
     * Lee el archivos html y lo envia al cliente.
     * @param Socket del cliente al que se enviarán los datos del archivo leido.
     * @throws IOException Si hay algún error de entrada/salida durante la ejecución.
     */
    public void lecturaArchivo(Socket clientSocket, String path) throws IOException{

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String outputLine;

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html\r\n");
        out.println("\r\n");
        File file = new File("target/classes/public/"+ path.split("/")[1]);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));  
        while((outputLine = bufferedReader.readLine()) != null){
            out.println(outputLine);
            out.println("\n");
        }
         
        bufferedReader.close();
        out.close();
        
    }

    /*
     * Lee el archivo de estilos (css) y lo envia al cliente.
     * @param Socket del cliente al que se enviarán los datos del archivo leido.
     * @throws IOException Si hay algún error de entrada/salida durante la ejecución.
     */
    public void lecturaEstilos(Socket clientSocket, String path) throws IOException{

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String outputLine;

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/css\r\n");
        out.println("\r\n");
        File file = new File("target/classes/public/"+ path.split("/")[1]);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));  
        while((outputLine = bufferedReader.readLine()) != null){
            out.println(outputLine);
            out.println("\n");
        }

        bufferedReader.close();        
        out.close();
    }

    /*
     * Lee la imagen (jpg) y la envia al cliente.
     * @param Socket del cliente al que se enviarán los datos del archivo leido.
     * @param path Ruta de la imagen que se desea enviar.
     * @throws IOException Si hay algún error de entrada/salida durante la ejecución.
     */
    public void lecturaImagen(Socket clientSocket, String path) throws IOException{
        enviarImagen(clientSocket, path);
    }

    /*
     * Envía datos de película al cliente a través del socket proporcionado.
     * @param clientSocket Socket del cliente al que se enviarán los datos de la película.
     * @param httpConnection Instancia deL HTTPConnection utilizada para la conexión con la API.
     * @param title Título de la película de la que se desea recuperar los datos.
     * @throws IOException Si hay algún error de entrada/salida durante la ejecución.
     */
    public void getMovieData(Socket clientSocket, HTTPConnection httpConnection, String title) throws IOException{
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String outputLine = httpConnection.cacheMovie(title);

        out.println("HTTP/1.1 200 OK\r\n");
        //out.println("Content-Type: text/json\r\n");
        out.println("\r\n");
        out.println(outputLine);

        out.close();

    }

    /*
     * Envía un mensaje de error 404 al cliente a través del socket proporcionado 
     * cuando no se encuentra el archivo solicitado.
     * @param clientSocket Socket del cliente al que se enviará el mensaje de error.
     * @throws IOException Si hay algún error de entrada/salida durante la ejecución.
     */
    public void archivoNoEncontrado(Socket clientSocket) throws IOException{
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        
        String outputLine = "HTTP/1.1 404 Not Found\r\n"
        + "Content-Type: text/html\r\n"
        + "\r\n"
        + "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "<meta charset=\"UTF-8\">\n"
        + "<title>Archivo-No-Encontrado</title>\n"
        + "</head>\n"
        + "<body>\n"
        + "404 NOT FOUND \n"
        + "</body>\n"
        + "</html>\n";

        out.println(outputLine);
        out.close(); 
    }

    /*
     * Envía la imagen al cliente a través del socket proporcionado.
     * @param clienteSocket Socket del cliente al que se enviará la imagen.
     * @param path Ruta de la imagen que se desea enviar.
     * @throws IOException Si hay algún error de entrada/salida durante la ejecución.
     */
    private void enviarImagen(Socket clienteSocket, String path) throws IOException {
        byte[] imagenBytes = obtenerImagenBytes(path);

        OutputStream out = clienteSocket.getOutputStream();
    
        escribirCabeceras(out, imagenBytes.length);

        out.write(imagenBytes);
        out.flush();
    }
    
    /*
     * Escribe las cabeceras de la respuesta HTTP para la imagen en el OutputStream proporcionado.
     * @param out OutputStream en el que se escribirán las cabeceras de la imagen.
     * @param longitudImagen Longitud de la imagen que se enviará.
     * @throws IOException Si hay algún error de entrada/salida durante la ejecución.
     */
    private void escribirCabeceras(OutputStream out, int longitudImagen) throws IOException {
        out.write("HTTP/1.1 200 OK\r\n".getBytes());
        out.write("Content-Length: ".getBytes());
        out.write(String.valueOf(longitudImagen).getBytes());
        out.write("\r\n".getBytes());
        out.write("Content-Type: image/jpg\r\n".getBytes());
        out.write("\r\n".getBytes());
    }

    /*
     * Obtiene los bytes de la imagen en la ruta proporcionada.
     * @param path Ruta de la imagen de la que se desean obtener los bytes.
     * @return Los bytes de la imagen en la ruta proporcionada.
     */
    private byte[] obtenerImagenBytes(String path) throws IOException {

        FileInputStream fileInputStream = new FileInputStream("target/classes/public/" + path.split("/")[1]);

        long tamañoArchivo = fileInputStream.available();

        byte[] imagenBytes = new byte[(int) tamañoArchivo];

        fileInputStream.read(imagenBytes);
        fileInputStream.close();

        return imagenBytes;
    }
    
        
}
