# Homework 10

## Team

David Freina, 11829022
Mathias Thöni, 11835136
## a) Which packet types does Redis use to exchange information between nodes?

- Redis nodes are connected using a TCP bus and the *Redis Cluster Bus* binary protocol
- All nodes in a Redis cluster are interconnected using the aforementioned protocol
- Different messages
  - gossip protocol to discover new nodes
  - ping packets to make sure all the other nodes are working properly and to send cluster messages needed to signal specific conditions
  - Pub/Sub messages to orchestrate manual failovers when requested by users
  - -MOVED and -ASK are redirection errors which redirect clients

## b) Describe how Redis handles failure detection

- Failure detection procedure
  - Redis Cluster failure detection is used to recognize when a master or slave node is no longer reachable by the majority of nodes
  - The following response is to promote a slave to the role of master
  - If this action is not possible the cluster is put in an error state to stop receiving queries from clients
- Failure detection flags
  - *PFAIL flag*: indicates possible failure, one node is flagged with *PFAIL* if it is not reachable for more than ````NODE_TIMEOUT```` time, *PFAIL* is insufficient to trigger a slave promotion
  - *FAIL flag*: *PFAIL* is escalated *FAIL* if one node should be considered down, the following conditions have to be true for a node to be flagged *FAIL*:
    - Some node, that we'll call A, has another node B flagged as *PFAIL*
    - Node A collected, via gossip sections, information about the state of B from the point of view of the majority of masters in the cluster
    - The majority of masters signaled the *PFAIL* or *FAIL* condition within ````NODE_TIMEOUT```` * ````FAIL_REPORT_VALIDITY_MULT```` time. (The validity factor is set to 2 in the current implementation, so this is just two times the ````NODE_TIMEOUT```` time)
    - If all conditons are true the node is marked as *FAIL* and a *FAIL* message is sent to all reachable nodes which will force the receiving nodes to flag the node *FAIL*

## c) In the context of Redis, what does “epoch” mean and what is it used for?

- There are two different "epoch" in Redis Cluster
  - Cluster current epoch:
    - used to give incremental versioning to events
    - 64bit unsigned number
    - set to 0 at node create in all slave and master nodes
    - if sender epoch of packet is bigger than local epoch it is set to sender epoch
    - basically logical clock
  - Configuration epoch
    - advertised by master nodes as ````configEpoch```` in ping and pong packets
    - when a new node is created it is set to 0 in master nodes
    - new ````configEpoch```` is created during slave election
    - helps resolve conflicts when different nodes claim divergent configurations

## What happens if a master node is not reachable? Describe how a Redis cluster recovers from a failing master node

- Nodes will start flagging the master *PFAIL* and eventually all conditions to flag *FAIL* are met
- With this first condition met there are two more conditions to trigger slave election and promotion
  - The master was serving a non-zero number of slots
  - The slaves replication link was not disconnected for longer than a given amount of time (configurable)
- If all three conditions are met by a slave it starts the slave election and promotion process
  - Increment ````currentEpoch```` by one and request votes from master nodes by broadcasting ````FAILOVER_AUTH_REQUEST```` and wait for a maximum time of 2 * ````NODE_TIMEOUT````
  - If a master has already voted it can not vote again in this time period
  - All votes with an epoch smaller than ````currentEpoch```` are discarded so no old vote gets counted
  - If one node gets the majority of ACKs it wins the election otherwise a new try will be started after a waiting period of ````NODE_TIMEOUT```` * 4
