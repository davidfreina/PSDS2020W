# Exercises

## Exercise 1b

Due to Redis being very memory and CPU bound we think it would be best to use a memory-optimized instance ([1](https://redislabs.com/blog/5-tips-for-running-redis-over-aws/), [2](https://levelup.gitconnected.com/what-a-redis-migration-taught-us-about-burstable-ec2-instances-508990b002b3)). For this we think the R5B instance type from EC2 would be the right fit. Especially the r5b.xlarge because it should provide enough memory and CPU power for Redis. But due to our accounts being very restricted we can only use instances from the following types:

t2.small, t2.micro, t2.nanao, m4.large, c4.large, c5.large, m5.large, t2.medium, m4.xlarge, c4.xlarge, c5.xlarge, t2.2xlarge, m5.2xlarge, t2.large, t2.xlarge, m5.xlarge

From those selected few instance types we initially thought that m5.2xlarge should be the best instance because it provides enough memory and CPU power. But after playing around a bit we found the c5.large to be even faster because it is a compute-optimized instance type. Strangely enough the c5.xlarge performed worse than the c5.large. We cannot explain that behaviour.

Another remark is that we specifically chose a HVM-virtualized image because that should provide way better performance ([3](https://redislabs.com/blog/benchmarking-the-new-aws-m3-instances-with-redis/)).

## Exercise 2a

The values display should tell us how many of the given operations the Redis server can handle per second.

* ````redis-benchmark````
  * SET: set a ````key```` to hold a ````value````
  * GET: get the ````value```` of a ````key````
  * INCR: increment number stored at ````key```` by one
  * LPUSH: insert specified ````values```` at head of list stored at ````key````
  * RPUSH: insert specified ````values```` at tail of list stored at ````key````
  * LPOP: remove and return first ````count```` elements of list stored at ````key````
  * RPOP: remove and return last ````count```` elements of list stored at ````key````
  * SADD: add specified ````values```` to set stored at ````key````
  * HSET: set ````field```` in hash stored at ````key```` to ````value````
  * SPOP: remove and return ````count```` random members from set at ````key````
  * ZADD: add all specified ````members```` with specified ````scores```` to the sorted set stored at ````key````
  * ZPOPMIN: remove and return up to ````count```` with lowest scores from sorted set stored at ````key````
  * LPUSH: insert specified ````values```` at head of list stored at ````key````
  * LRANGE_100: return first 100 elements stored in list at ````key````
  * LRANGE_300:return first 300 elements stored in list at ````key````
  * LRANGE_500:return first 500 elements stored in list at ````key````
  * LRANGE_600:return first 600 elements stored in list at ````key````
  * MSET: set given ````keys```` to respective ````values````, atomic operation

### Outputs

#### t2.micro

command | requests/s
--- | ---
PING_INLINE | 45562.24
PING_BULK | 46121.21
SET | 45712.20
GET | 45653.76
INCR | 45637.09
LPUSH | 45749.84
RPUSH | 45216.13
LPOP | 45220.22
RPOP | 45787.55
SADD | 45252.96
HSET | 45089.73
SPOP | 45599.63
ZADD | 45134.50
ZPOPMIN | 45791.74
LPUSH (needed to benchmark LRANGE) | 45273.45
LRANGE_100 (first 100 elements) | 45199.78
LRANGE_300 (first 300 elements) | 45446.28
LRANGE_500 (first 450 elements) | 45351.48
LRANGE_600 (first 600 elements) | 45339.14
MSET (10 keys) | 45524.90

#### m5.2xlarge

command | requests/s
--- | ---
PING_INLINE | 91810.50
PING_BULK | 91979.40
SET | 93023.26
GET | 92114.96
INCR | 91759.96
LPUSH | 93492.89
RPUSH | 91945.56
LPOP | 91810.50
RPOP | 91759.96
SADD | 93791.04
HSET | 92919.53
SPOP | 91642.23
ZADD | 91759.96
ZPOPMIN | 93248.79
LPUSH (needed to benchmark LRANGE) | 93248.79
LRANGE_100 (first 100 elements) | 92592.59
LRANGE_300 (first 300 elements) | 92064.07
LRANGE_500 (first 450 elements) | 91124.48
LRANGE_600 (first 600 elements) | 91911.77
MSET (10 keys) | 92850.51

#### c5.large

command | requests/s
--- | ---
PING_INLINE | 100603.62
PING_BULK | 99462.90
SET | 99920.06
GET | 99601.59
INCR | 99880.14
LPUSH | 100040.02
RPUSH | 99920.06
LPOP | 99720.78
RPOP | 99940.04
SADD | 100080.07
HSET | 100140.20
SPOP | 99800.40
ZADD | 100240.58
ZPOPMIN | 99542.11
LPUSH (needed to benchmark LRANGE) | 100100.10
LRANGE_100 (first 100 elements) | 100482.32
LRANGE_300 (first 300 elements) | 100401.61
LRANGE_500 (first 450 elements) | 99980.01
LRANGE_600 (first 600 elements) | 100321.02
MSET (10 keys) | 105663.57

#### c5.xlarge

command | requests/s
--- | ---
PING_INLINE | 96302.01
PING_BULK | 94822.69
SET | 95310.72
GET | 96525.10
INCR | 95328.89
LPUSH | 95238.09
RPUSH | 95474.51
LPOP | 95767.09
RPOP | 97333.07
SADD | 95111.28
HSET | 97106.23
SPOP | 94393.05
ZADD | 94732.86
ZPOPMIN | 97181.73
LPUSH (needed to benchmark LRANGE) | 94768.77
LRANGE_100 (first 100 elements) | 97694.41
LRANGE_300 (first 300 elements) | 95932.47
LRANGE_500 (first 450 elements) | 95075.11
LRANGE_600 (first 600 elements) | 97200.62
MSET (10 keys) | 99601.59

#### t2.2xlarge

command | requests/s
--- | ---
GET | 76045.63
INCR | 76161.46
LPUSH | 75631.52
RPUSH | 76604.88
LPOP | 76429.23
RPOP | 76934.91
SADD | 76219.52
HSET | 76852.13
SPOP | 76745.97
ZADD | 76277.66
ZPOPMIN | 75907.09
LPUSH (needed to benchmark LRANGE) | 77315.60
LRANGE_100 (first 100 elements) | 76557.95
LRANGE_300 (first 300 elements) | 73249.34
LRANGE_500 (first 450 elements) | 77363.45
LRANGE_600 (first 600 elements) | 77232.01
MSET (10 keys) | 77375.43
