# Exercises

## Exercise 1b

Due to Redis being very memory and CPU bound we think it would be best to use a memory-optimized instance ([1](https://redislabs.com/blog/5-tips-for-running-redis-over-aws/), [2](https://levelup.gitconnected.com/what-a-redis-migration-taught-us-about-burstable-ec2-instances-508990b002b3)). For this we think the R5B instance type from EC2 would be the right fit. Especially the r5b.xlarge because it should provide enough memory and CPU power for Redis. But due to our accounts being very restricted we can only use general purpose instance from the following types:

t2.small, t2.micro, t2.nanao, m4.large, c4.large, c5.large, m5.large, t2.medium, m4.xlarge, c4.xlarge, c5.xlarge, t2.2xlarge, m5.2xlarge, t2.large, t2.xlarge, m5.xlarge

From those selected few instance types we think that m5.2xlarge should be the best instance because it provides enough memory and CPU power.

Another remark is that we specifically chose a HVM-virtualized instance because that should provide way better performance. [3](https://redislabs.com/blog/benchmarking-the-new-aws-m3-instances-with-redis/)

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

