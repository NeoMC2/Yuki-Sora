package botApplication.discApplication.utils;

import core.Engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class NetworkManager {

    private Engine engine;

    public NetworkManager(Engine engine) {
        this.engine = engine;
    }

    public String post(String path, String json) {
        HttpURLConnection connection;
        try {
            connection = makeConnection(path);
        } catch (Exception e) {
            if(engine.getProperties().debug){e.printStackTrace();}
            return null;
        }
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            if(engine.getProperties().debug){e.printStackTrace();}
            return null;
        }
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        try {
            OutputStreamWriter os = new OutputStreamWriter(connection.getOutputStream());
            os.write(json);
            os.flush();
        } catch (IOException e) {
            if(engine.getProperties().debug){e.printStackTrace();}
        }

        return readResponse(connection);
    }

    public String get(String path) {
        try {
            return readResponse(makeConnection(path));
        } catch (Exception e) {
            if(engine.getProperties().debug){e.printStackTrace();}
            return null;
        }
    }

    private String readResponse(HttpURLConnection connection) {
        String responseString = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            responseString = response.toString();
        } catch (IOException e) {
            if(engine.getProperties().debug){e.printStackTrace();}
        }
        return responseString;
    }

    private HttpURLConnection makeConnection(String path) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(path).openConnection();
        c.setConnectTimeout(3000);
        return c;
    }
}
