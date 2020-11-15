# Homework 4

Team:
 - Mathias Thoeni
 - David Freina, 11829022

## Task 2

### Task 2.1

Implementation can be found in src/main/java/homework4/LambdaFibonacci

### Task 2.2

 - First we created the jar using 'gradle jar'
 - Then we used the generated 'LambdaFibonacci-1.0-SNAPSHOT' and created both AWS Lambda functions
 - After creating the functions we tried both of them with the given input of 1-35 and received the expected output
 
### Task 2.3

We were not 100% sure whether the parameters should be given to the main function or to the invocation function.
We implemented it by giving our custom 'runLamdaFibonacci' the required three parameters.
There was a bit of a struggle with the jFaaS-Tool at first, but after some modifications (reading the AWS-Credentials directly in the empty Gateway constructor call) we made it work and used the provided library
Our LambdaFibonacci128MB()-function ran in 19.4s

### Task 2.4

#### 128MB

We can observe that the first execution takes about 3-3.5s longer than the following warm starts.
We therefore can say that there is probably some kind of cache being used.

| run # | time  | mem_used  | BilledDurationInGBSeconds |
| ---   | ---   | ---       | ---                       |
| 1     | 19.4s | 78MB      | 2.1875                    |
| 2     | 15.9s | 78MB      | 1.9875                    |
| 3     | 15.9s | 78MB      | 1.975                     |
| 4     | 16.2s | 78MB      | 2.0125                    |
| 5     | 16.2s | 78MB      | 2.0125                    |

#### 2GB

We can observe the same effects like we did with the 128MB version but the 2GB version is way faster overall than the 128MB version.

| run # | time  | mem_used  | BilledDurationInGBSeconds |
| ---   | ---   | ---       | ---                       |
| 1     | 2.9s  | 95MB      | 2.2                       |
| 2     | 1.0s  | 95MB      | 2.0                       |
| 3     | 1.0s  | 95MB      | 1.8                       |
| 4     | 1.0s  | 95MB      | 1.8                       |
| 5     | 1.0s  | 95MB      | 1.8                       |


Even though the 2GB function is much faster it is only using 17MB more RAM which is pretty efficient given the time improvement.

## Task 3

### Task 3.2

| run # | time  | mem_used          | BilledDurationInGBSeconds |
| ---   | ---   | ---               | ---                       |
| 1     | 10.4s | ~80MB/thread      | 2.5375                    |
| 2     | 7.5s  | ~80MB/thread      | 2.5375                    |
| 3     | 7.3s  | ~80MB/thread      | 2.55                      |
| 4     | 7.3s  | ~80MB/thread      | 2.7625                    |
| 5     | 7.5s  | ~80MB/thread      | 2.75                      |

We can observe that the single "fat" LambdaFibonacci2GB() is by far the best approach because it is cheaper than the "thin" LambdaFibonacci128MB() and only slightly more expensive than the "fat" LambdaFibonacci128MB() but way faster than both of those implementations.