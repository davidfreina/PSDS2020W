# Measurements

## Exercise 2
### t2.micro

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

### t2.2xlarge

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

### m4.xlarge

command | requests/s
--- | ---
PING_INLINE | 76219.52
PING_BULK | 74338.39
SET | 73486.19
GET | 72056.49
INCR | 68936.99
LPUSH | 75041.27
RPUSH | 69089.40
LPOP | 78271.76
RPOP | 70651.41
SADD | 70711.35
HSET | 77651.80
SPOP | 78603.99
ZADD | 76242.76
ZPOPMIN | 75323.89
LPUSH (needed to benchmark LRANGE) | 71808.13
LRANGE_100 (first 100 elements) | 74338.39
LRANGE_300 (first 300 elements) | 73088.73
LRANGE_500 (first 450 elements) | 76557.95
LRANGE_600 (first 600 elements) | 76675.36
MSET (10 keys) | 72982.05


### m5.large

command | requests/s
--- | ---
PING_INLINE | 90826.52
PING_BULK | 90236.42
SET | 90661.84
GET | 91174.32
INCR | 90876.05
LPUSH | 91474.57
RPUSH | 91390.97
LPOP | 91290.85
RPOP | 91141.09
SADD | 90826.52
HSET | 90744.10
SPOP | 90530.52
ZADD | 91424.39
ZPOPMIN | 90760.58
LPUSH (needed to benchmark LRANGE) | 90383.23
LRANGE_100 (first 100 elements) | 91058.09
LRANGE_300 (first 300 elements) | 91374.27
LRANGE_500 (first 450 elements) | 91274.19
LRANGE_600 (first 600 elements) | 91190.96
MSET (10 keys) | 96487.84
### m5.xlarge

command | requests/s
--- | ---
PING_INLINE | 94571.59
PING_BULK | 98775.20
SET | 98483.35
GET | 102459.02
INCR | 100040.02
LPUSH | 100542.93
RPUSH | 98833.76
LPOP | 105064.09
RPOP | 104210.09
SADD | 101153.15
HSET | 105440.74
SPOP | 101030.52
ZADD | 105820.11
ZPOPMIN | 102103.33
LPUSH (needed to benchmark LRANGE) | 101296.60
LRANGE_100 (first 100 elements) | 102417.04
LRANGE_300 (first 300 elements) | 90350.56
LRANGE_500 (first 450 elements) | 93405.56
LRANGE_600 (first 600 elements) | 101071.36
MSET (10 keys) | 100140.20


### m5.2xlarge

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

### c4.large

command | requests/s
--- | ---
PING_INLINE | 68324.68
PING_BULK | 67851.81
SET | 67677.31
GET | 67312.87
INCR | 67778.23
LPUSH | 67096.08
RPUSH | 67213.34
LPOP | 66943.37
RPOP | 67168.19
SADD | 67087.08
HSET | 67458.17
SPOP | 67531.06
ZADD | 67723.15
ZPOPMIN | 67331.00
LPUSH (needed to benchmark LRANGE) | 67585.84
LRANGE_100 (first 100 elements) | 68222.13
LRANGE_300 (first 300 elements) | 67686.48
LRANGE_500 (first 450 elements) | 67870.23
LRANGE_600 (first 600 elements) | 67567.57
MSET (10 keys) | 71073.20

### c4.xlarge

command | requests/s
--- | ---
PING_INLINE | 93615.43
PING_BULK | 89863.41
SET | 94732.86
GET | 90859.53
INCR | 80932.34
LPUSH | 87183.95
RPUSH | 93720.71
LPOP | 86385.62
RPOP | 93005.95
SADD | 87750.09
HSET | 88090.20
SPOP | 84104.29
ZADD | 93370.68
ZPOPMIN | 92609.74
LPUSH (needed to benchmark LRANGE) | 88059.18
LRANGE_100 (first 100 elements) | 92319.05
LRANGE_300 (first 300 elements) | 89094.80
LRANGE_500 (first 450 elements) | 92558.31
LRANGE_600 (first 600 elements) | 90448.62
MSET (10 keys) | 86266.39

### c5.large

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

### c5.xlarge

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

## Exercise 3

### ```KEYS David*```

1. "Davidde Sherard"
2. "Davidjohn Busic"
3. "Davidallen Mata"
4. "David-christian Furey"
5. "David-lee Mailander"
6. "David-andrew Kornbluth"
7. "David-richard Molone"
8. "David-madalin Jangula"
9. "Davidjames Depuy"
10. "Davidjeremiah Hughbanks"
11. "Davids Desmet"
12. "David-jordan Kijak"
13. "Davidallen Davine"
14. "David-nelson Gunderman"
15. "David Stencil"
16. "Davidsonjr Migliaccio"
17. "David-jordan Swille"
18. "Davidjeremiah Sanderford"
19. "David Freina"
20. "Davidjoe Rajaniemi"
21. "Davidjoseph Ruffel"
22. "Davidjunior Jurs"
23. "Davidas Lietz"
24. "Davidjr Musquiz"
25. "Davidjames Elk"
