package botApplication.botApi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import core.Engine;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;

public class BotRequestApi {

    private final String consMsgDef = "[Request API]";

    private Engine engine;


    private HttpServer server;

    public BotRequestApi(Engine engine) {
        this.engine = engine;
    }

    public void boot(boolean overridePort) {
        engine.getUtilityBase().printOutput(consMsgDef + " !reading environment variable api port!", true);
        String sysEnvStr = System.getenv("YUKI_PORT");
        if (sysEnvStr != null || overridePort || engine.getProperties().apiPort != 0) {

            if (overridePort) {
                engine.getUtilityBase().printOutput(consMsgDef + " !Override port is active -> using port 5003 for bot api!", false);
                try {
                    server = HttpServer.create(new InetSocketAddress(5003), 0);
                } catch (IOException e) {
                    if (engine.getProperties().debug)
                        e.printStackTrace();
                    engine.getUtilityBase().printOutput(consMsgDef + " !!!API Start error -> abort!!!", false);
                    return;
                }
            } else if(engine.getProperties().apiPort != 0) {
                engine.getUtilityBase().printOutput(consMsgDef + " !Override port is active -> using port 5003 for bot api!", false);
                try {
                    server = HttpServer.create(new InetSocketAddress(engine.getProperties().apiPort), 0);
                } catch (IOException e) {
                    if (engine.getProperties().debug)
                        e.printStackTrace();
                    engine.getUtilityBase().printOutput(consMsgDef + " !!!API Start error -> abort!!!", false);
                    return;
                }
            } else {
                if (sysEnvStr.equals("")) {
                    engine.getUtilityBase().printOutput(consMsgDef + "!!!Failed to read environment variable -> Failed to start API!!!", false);
                    return;
                }
                engine.getUtilityBase().printOutput(consMsgDef + " !Starting API on port: " + sysEnvStr + "!", false);
                try {
                    server = HttpServer.create(new InetSocketAddress(Integer.valueOf(sysEnvStr)), 0);
                } catch (IOException e) {
                    if (engine.getProperties().debug)
                        e.printStackTrace();
                    engine.getUtilityBase().printOutput(consMsgDef + " !!!API Start error -> abort!!!", false);
                    return;
                }
            }
            server.createContext("/api", new ApiHandler());
            server.setExecutor(null);
            server.start();
            engine.getUtilityBase().printOutput(consMsgDef + " !API Started running on: " + server.getAddress().getHostString() + " port: " + server.getAddress().getPort() + "!", false);
        } else {
            engine.getUtilityBase().printOutput(consMsgDef + "!!!Failed to read environment variable -> Failed to start API!!!", false);
        }
    }

    public String handleRec(String instructions) {
        engine.getUtilityBase().printOutput(consMsgDef + " !Received Api instructions!", true);
        JSONObject msg;
        try {
            msg = engine.getFileUtils().convertStringToJson(instructions);
        } catch (Exception e) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Api instructions was invalid (can't convert to JSON) -> abort!!!", true);
            return null;
        }
        return engine.getFileUtils().convertJsonToString(engine.getBotRequestHandler().handle(msg));
    }

    private class ApiHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String req;
            try {
                req = readRequestBody(httpExchange);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            engine.getUtilityBase().printOutput(consMsgDef + " !Received: (" + req + ") from: (" + httpExchange.getRemoteAddress().getHostString() + ")!", true);
            String rec;
            engine.getUtilityBase().printOutput("---Start from Api command---", true);
            try {
                rec = handleRec(req);
            } catch (Exception e) {
                if (engine.getProperties().debug)
                    e.printStackTrace();
                sendResponse(httpExchange, "{ \"status\" : \"400\", \"response\" : \"Error with json file\"}", 400);
                engine.getUtilityBase().printOutput(consMsgDef + "!!!Error in request -> Respond: " + "400" + "!!!", true);
                return;
            }
            if (rec == null) {
                sendResponse(httpExchange, "{ \"status\" : \"400\", \"response\" : \"Error with json file\"}", 400);
                engine.getUtilityBase().printOutput(consMsgDef + " !Respond: " + "400" + "!", true);
                return;
            }
            if (!rec.equals("")) {
                sendResponse(httpExchange, rec, 200);
                engine.getUtilityBase().printOutput(consMsgDef + " !Respond: " + rec + "!", true);
            } else {
                sendResponse(httpExchange, "{ \"status\" : \"400\", \"response\" : \"Error with json file\"}", 400);
                engine.getUtilityBase().printOutput(consMsgDef + " !Respond: " + "400" + "!", true);
            }
            engine.getUtilityBase().printOutput("---End from Api command---", false);
        }
    }

    private void sendResponse(HttpExchange httpExchange, String s, int httpCode) {
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "content-type");
        OutputStream os = httpExchange.getResponseBody();
        try {
            httpExchange.sendResponseHeaders(200, s.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.write(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String readRequestBody(HttpExchange he) throws IOException {
        InputStream input = he.getRequestBody();
        StringBuilder stringBuilder = new StringBuilder();

        new BufferedReader(new InputStreamReader(input))
                .lines()
                .forEach( (String s) -> stringBuilder.append(s + "\n") );
        return stringBuilder.toString();
    }
}
