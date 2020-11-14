package homework4;

import com.google.gson.*;
import jFaaS.Gateway;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class LambdaFibonacciInvoker {

    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String LAMBDA_FUNCTION_128MB = "arn:aws:lambda:us-east-1:722672042190:function:LambdaFibonacci128MB";
    private static final String LAMBDA_FUNCTION_2GB = "arn:aws:lambda:us-east-1:722672042190:function:LambdaFibonacci2GB";
    private static final String INPUT_FILE = "input.json";
    private static final int ITERATIONS = 5;

    public static void main(String[] args) {

        Map<String, Object> inputValues = readInputFromFile(INPUT_FILE);

        /* Start one lambda function for all values with 128MB */
        long start_time = System.currentTimeMillis();
        runLamdaFibonacci(LAMBDA_FUNCTION_128MB, inputValues, ITERATIONS);
        logger.info("Sequential with 128MB execution took: " + (System.currentTimeMillis() - start_time));


        /* Start one lambda function for all values with 2GB */
        start_time = System.currentTimeMillis();
        runLamdaFibonacci(LAMBDA_FUNCTION_2GB, inputValues, ITERATIONS);
        logger.info("Sequential with 2GB execution took: " + (System.currentTimeMillis() - start_time));



        /* Start lambda function for each value */
        start_time = System.currentTimeMillis();
        ((JsonArray)inputValues.get("input")).forEach( value -> {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put("input", new Integer[]{value.getAsInt()});
            runLamdaFibonacci(LAMBDA_FUNCTION_128MB, tmp);
        });
        logger.info("Execution for each value took: " + (System.currentTimeMillis() - start_time));


        /* Start lambda function for each value with threading */
        List<Thread> threadList = new ArrayList<>();
        start_time = System.currentTimeMillis();
        ((JsonArray)inputValues.get("input")).forEach( value -> {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put("input", new Integer[]{value.getAsInt()});
            Thread thread = new Thread(() -> runLamdaFibonacci(LAMBDA_FUNCTION_128MB, tmp));
            threadList.add(thread);
        });

        threadList.forEach(Thread::start);
        threadList.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        logger.info("Execution for each value with threading took: " + (System.currentTimeMillis() - start_time));

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

    private static void runLamdaFibonacci(String functionName, Map<String, Object> input, int iterations) {
        Gateway gateway = new Gateway();
        for (int i = 1; i <= iterations; i++) {

            try {
                long start_time = System.currentTimeMillis();
                JsonObject resultLambda = gateway.invokeFunction(functionName, input);
                logger.info(resultLambda.toString());
                logger.info("Iteration " + i + " took: " + (System.currentTimeMillis() - start_time));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void runLamdaFibonacci(String functionName, Map<String, Object> input) {
        runLamdaFibonacci(functionName, input, 1);
    }
}
