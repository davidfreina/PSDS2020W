package homework8;

import aws.AWSUtils;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.waiters.WaiterTimedOutException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author David Freina
 * @author Mathias Thoeni
 */
public class TaskOne {
    static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    static final String SECURITY_GROUP_NAME = "ThoeniFreinaAWSAllowSSH";
    static final String KEY_NAME = "thoeni-freina-key-task8";
    static final String KEY_PATH = KEY_NAME + ".pem";
    static final String REDIS_PASSWORD = "38U12wtDCXtyduwQ";
    static final String SALARY_FILE_SOURCE = "salary.txt";
    static final String SALARY_FILE_DESTINATION = "salary.txt";
    static final String IMAGE_ID = "ami-0947d2ba12ee1ff75";
    static final InstanceType INSTANCE_TYPE = InstanceType.M52xlarge;
    static final String[] COMMANDS = {
            "sudo yum -y update",
            "sudo yum install -y gcc make tcl",
            "sudo yum groupinstall -y \"Development Tools\"",
            "cd /usr/local/src && sudo wget https://download.redis.io/releases/redis-6.0.9.tar.gz",
            "cd /usr/local/src && sudo tar xzf redis-6.0.9.tar.gz", "cd /usr/local/src/redis-6.0.9 && sudo make",
            "cd /usr/local/src/redis-6.0.9 && sudo cp src/redis-server /usr/local/bin/ && sudo cp src/redis-cli /usr/local/bin/ && sudo cp src/redis-benchmark /usr/local/bin/",
            "echo \"port 6379\nrequirepass " + REDIS_PASSWORD + "\" >> /home/ec2-user/redis-server.cfg",
            "sudo chown ec2-user:ec2-user /home/ec2-user/redis-server.cfg", "sudo chmod +x /home/ec2-user/redis-server.cfg",
            "nohup /usr/local/bin/redis-server /home/ec2-user/redis-server.cfg > /dev/null 2>&1 &"
    };


    public static void main(String[] args) {

        ProfileCredentialsProvider profileCredentialsProvider = new ProfileCredentialsProvider();
        if (profileCredentialsProvider.getCredentials() == null) {
            logger.severe("Could not get credentials!");
            System.exit(-1);
        }

        AmazonEC2Client amazonEC2Client = (AmazonEC2Client) AmazonEC2Client.builder().build();

        AWSUtils.createSecurityGroup(SECURITY_GROUP_NAME, amazonEC2Client);

        AWSUtils.createIpPermission(SECURITY_GROUP_NAME, amazonEC2Client);

        AWSUtils.generateKeyPair(KEY_NAME, amazonEC2Client);

        RunInstancesResult result = AWSUtils.runInstances(amazonEC2Client, IMAGE_ID, INSTANCE_TYPE, 1, 1, KEY_NAME, SECURITY_GROUP_NAME);

        List<Instance> instances = result.getReservation().getInstances();

        List<String> instanceIds = instances.stream()
                .map(Instance::getInstanceId)
                .collect(Collectors.toList());

        logger.info("Waiting for instance status to be running...");
        try {
            AWSUtils.waitForInstanceRunning(amazonEC2Client, instanceIds);
        } catch (WaiterTimedOutException waiterTimedOutException) {
            logger.severe("Could not detect all instances as running!");
        }

        // For some reason we had to wait for the instances to have status ok because otherwise we got a 'Connection refused' when connecting with ssh
        logger.info("Waiting for instance status to be ok...");
        try {
            AWSUtils.waitForInstanceStatusOk(amazonEC2Client, instanceIds);
        } catch (WaiterTimedOutException waiterTimedOutException) {
            logger.severe("Could not detect all instances as ok!");
        }

        logger.info("Getting Instance IPs...");

        String instanceIp = AWSUtils.getInstanceIps(amazonEC2Client, instanceIds).get(0);
        String instancePrivateIp = AWSUtils.getPrivateInstanceIp(amazonEC2Client, instanceIds).get(0);
        String instancePrivateDNS = AWSUtils.getPrivateDNS(amazonEC2Client, instanceIds).get(0);
        String instancePublicDNS = AWSUtils.getPublicDNS(amazonEC2Client, instanceIds).get(0);

        long execTime = installRedis(instanceIp, COMMANDS);

        AWSUtils.sendFileToInstance(instanceIp, KEY_PATH, SALARY_FILE_SOURCE, SALARY_FILE_DESTINATION);

        logger.info("Statistics: \n\tPublic IP: " + instanceIp + "\n\tPrivate IP: " + instancePrivateIp + "\n\tPublic DNS: " + instancePublicDNS + "\n\tPrivate DNS: " + instancePrivateDNS + "\n\tInstance type: " + INSTANCE_TYPE.toString() + "\nExecution Time: " + execTime);


        System.exit(0);
//		logger.info("Waiting for instances to change state to terminated...");
//		AWSUtils.terminateInstances(amazonEC2Client, instanceIds);
    }

    private static long installRedis(String ipAddress, String[] commands) {

        long start = System.currentTimeMillis();

        for (String command : commands) {
            try {
                logger.info("Sending command \"" + command + "\" to instance with IP: " + ipAddress);
                AWSUtils.sendSSHCommandToInstance(ipAddress, KEY_PATH, command, 10);
            } catch (Exception e) {
                logger.severe("Could not send command to instance with IP: " + ipAddress);
                logger.severe(e.toString());
                System.exit(-1);
            }
        }

        long end = System.currentTimeMillis();

        return end - start;
    }

}
