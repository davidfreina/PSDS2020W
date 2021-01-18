# Exercises

## Exercise 1

### 1b.)

Due to Redis being very memory and CPU bound we think it would be best to use a memory-optimized instance ([1](https://redislabs.com/blog/5-tips-for-running-redis-over-aws/), [2](https://levelup.gitconnected.com/what-a-redis-migration-taught-us-about-burstable-ec2-instances-508990b002b3)). For this we think the R5B instance type from EC2 would be the right fit. Especially the r5b.xlarge because it should provide enough memory and CPU power for Redis. But due to our accounts being very restricted we can only use instances from the following types:

t2.small, t2.micro, t2.nanao, m4.large, c4.large, c5.large, m5.large, t2.medium, m4.xlarge, c4.xlarge, c5.xlarge, t2.2xlarge, m5.2xlarge, t2.large, t2.xlarge, m5.xlarge

From those selected few instance types we initially thought that m5.2xlarge should be the best instance because it provides enough memory and CPU power.

Another remark is that we specifically chose a HVM-virtualized image because that should provide way better performance ([3](https://redislabs.com/blog/benchmarking-the-new-aws-m3-instances-with-redis/)).

## Exercise 2

### 2a.)

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

### 2b.)

We launched ten different instances to compare their performances.

### 2c.)

Our initial thoughts checked out and we were able to get much better performance by using m5.2xlarge. But after playing around a bit we found the c5.large to be even faster because it is a compute-optimized instance type. Strangely enough the c5.xlarge performed worse than the c5.large. We cannot explain that behaviour. We saw that also with the m5 instance types because the m5.xlarge, our overall best instance, was better than the m5.2xlarge.

see [MEASUREMENTS.md](MEASUREMENTS.md#Exercise2)

## Exercise 3

* 100.000
* Our names were not included. ````Adelheide Widdoes: 30.62````
* ````set "David Freina" 69.69````, ````set "Mathias Thoeni" 69.69````
* ```KEYS David*```. See [MEASUREMENTS.md](MEASUREMENTS.md#Exercise3) for output
* No, it is not easy because the database is built like a hash map where you can only easily access the keys but not the values. You would have to get every entry and then filter them by value