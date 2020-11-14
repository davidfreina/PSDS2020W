package homework4;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LambdaFibonacci implements RequestHandler<Map<String, Object>, Map<String,Object>> {
    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        // read the input array from the key-value pair
        ArrayList<Integer> inputArray = (ArrayList<Integer>) input.get("input");

        // put your code here (call fib())
        List<BigInteger> result = inputArray.stream().map(Fibonacci::fib).collect(Collectors.toList());

        // Prepare the output array into a key-value pair
        Map<String, Object> output = new HashMap<>();
        output.put("output", result);
        return output;
    }
}
