package homework4;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import jFaaS.Gateway;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class LambdaFibonacciInvoker {

    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {
        Gateway gateway = new Gateway();

        try {
            JsonObject resultLambda = gateway.invokeFunction("arn:aws:lambda:us-east-1:778033607199:function:LambdaFibonacci128MB", readInputFromFile("input.json"));
            logger.info(resultLambda.get("output").toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> readInputFromFile(String path) {

        Gson gson = new Gson();
        JsonObject input = null;
        Map<String, Object> inputMap = new HashMap<>();
        try (FileReader reader = new FileReader(path)) {
            //Read JSON file
            input = gson.fromJson(reader, JsonObject.class);
            inputMap.put("input", input.get("input"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        }

        return inputMap;
    }
}
