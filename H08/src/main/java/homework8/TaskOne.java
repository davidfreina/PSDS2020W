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
 * Fill in your code into the main method of this class.
 *
 * @author David Freina
 * @author Mathias Thoeni
 *
 */
public class TaskOne {
	static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	static final String SECURITY_GROUP_NAME = "ThoeniFreinaAWSAllowSSH";
	static final String KEY_NAME = "thoeni-freina-key-task8";
	static final String KEY_PATH = KEY_NAME + ".pem";
	static final String REDIS_PASSWORD = "6<v2*%S+!Jr{Wn6ZEqEwJCp,:UZk&!";
	static final String[] COMMANDS = {"ls", "sudo yum -y update", "sudo yum install -y gcc make tcl", "sudo yum groupinstall -y \"Development Tools\"", "cd /usr/local/src && sudo wget https://download.redis.io/releases/redis-6.0.9.tar.gz", "sudo tar xzf redis-6.0.9.tar.gz", "cd redis-6.0.9",
	"sudo make", "sudo cp src/redis-server /usr/local/bin/", "sudo cp src/redis-cli /usr/local/bin/", "echo \"port 6379\nrequirepass " + REDIS_PASSWORD + "\" >> /home/ec2-user/redis-server.cfg",
	"sudo chown ec2-user:ec2-user /home/ec2-user/redis-server.cfg", "sudo chmod +x /home/ec2-user/redis-server.cfg", "/usr/local/bin/redis-server /home/ec2-user/redis-server.cfg",};


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

		RunInstancesResult result = AWSUtils.runInstances(amazonEC2Client, 1, 1, KEY_NAME, SECURITY_GROUP_NAME);

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

		installRedis(instanceIp, COMMANDS);

//		logger.info("Waiting for instances to change state to terminated...");
//		AWSUtils.terminateInstances(amazonEC2Client, instanceIds);
	}

	private static long installRedis(String ipAddress, String[] commands) {

		long start = System.currentTimeMillis();

		for(String command : commands) {
			try {
				logger.info("Sending command " + command + " to instance with IP: " + ipAddress);
				AWSUtils.sendSSHCommandToInstance(ipAddress, KEY_PATH, command, 10);
			} catch (Exception e) {
				logger.severe("Could not send command to instance with IP: " + ipAddress);
				logger.severe(e.toString());
				System.exit(-1);
			}
		}

		long end = System.currentTimeMillis();

		return end-start;
	}

}
