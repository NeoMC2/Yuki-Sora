package botApplication.discApplication.utils;

import core.Engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NetworkManager {

    private final Engine engine;

    public NetworkManager(Engine engine) {
        this.engine = engine;
    }

    public String post(String path, String json, String apiToken) {
        return req(path, json, apiToken, "POST");
    }

    public String patch(String path, String json, String apiToken) {
        return req(path, json, apiToken, "PATCH");
    }

    public String delete(String path, String json, String apiToken) {
        return req(path, json, apiToken, "DELETE");
    }

    private String req(String path, String json, String apiToken, String methode) {
        if (engine.getProperties().debug)
            if(engine.getProperties().networkDebug)
            System.out.println("REQ : " + path + " Methode: " + methode + " req: " + json);
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) makeConnection(path);
        } catch (Exception e) {
            if (engine.getProperties().debug) {
                e.printStackTrace();
            }
            return null;
        }
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        if (apiToken != null) {
            addApiToken(apiToken, connection);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
        }
        try {
            if (methode.equals("PATCH")) {
                connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
                //connection.setRequestProperty("X-HTTP-Method", "PATCH");
                connection.setRequestMethod("POST");
            } else
                connection.setRequestMethod(methode);
        } catch (ProtocolException e) {
            if (engine.getProperties().debug) {
                e.printStackTrace();
            }
            return null;
        }
        connection.setDoInput(true);
        connection.setDoOutput(true);
        try {
            OutputStreamWriter os = new OutputStreamWriter(connection.getOutputStream());
            char[] ar = json.toCharArray();
            os.write(json);
            os.flush();
            os.close();
        } catch (ConnectException e) {
            if(engine.getProperties().networkDebug)
            engine.getUtilityBase().printOutput("[Network Manager] Requested server is not available | Timeout", true);
        } catch (IOException e) {
            if (engine.getProperties().debug) {
                e.printStackTrace();
            }
        }

        return readResponse(connection);
    }

    public String get(String path, String apiToken) {
        if (engine.getProperties().debug)
            if(engine.getProperties().networkDebug)
            System.out.println("REQ : " + path + " Methode: " + "GET");
        try {
            HttpURLConnection c = (HttpURLConnection) makeConnection(path);
            if (apiToken != null) {
                addApiToken(apiToken, c);
                c.setRequestProperty("Accept", "application/json");
            }
            return readResponse(c);
        } catch (Exception e) {
            if (engine.getProperties().debug) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private String readResponse(HttpURLConnection connection) {
        String responseString = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        } catch (ConnectException e) {
            if(engine.getProperties().networkDebug)
            engine.getUtilityBase().printOutput("[Network Manager] Requested server is not available | Timeout", true);
        } catch (IOException e) {
            if (engine.getProperties().debug) {
                e.printStackTrace();
            }
        }

        try {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            responseString = response.toString();
        } catch (Exception e) {

        }
        if (engine.getProperties().debug)
            if(engine.getProperties().networkDebug)
            System.out.println("Res: " + responseString);
        return responseString;
    }

    private Object makeConnection(String path) throws Exception {
        Object c;
        if (path.startsWith("https"))
            c = new URL(path).openConnection();
        else
            c = new URL(path).openConnection();
        return c;
    }

    private void addApiToken(String token, HttpURLConnection c) {
        c.setRequestProperty("api-token", token);
    }
}
