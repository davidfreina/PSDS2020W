package aws;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.waiters.WaiterParameters;
import com.amazonaws.waiters.WaiterTimedOutException;
import com.jcraft.jsch.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author David Freina
 * @author Mathias Thoeni
 */

public class AWSUtils {
    static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    //using PosixFilePermission to set file permissions 777
    static final Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>(Collections.singletonList(PosixFilePermission.OWNER_READ));

    public static void createSecurityGroup(String securityGroupName, AmazonEC2Client amazonEC2Client) {
        try {
            CreateSecurityGroupRequest createSecurityGroupRequest = new CreateSecurityGroupRequest();
            createSecurityGroupRequest.withGroupName(securityGroupName).withDescription("My security group");

            amazonEC2Client.createSecurityGroup(createSecurityGroupRequest);
        } catch (AmazonEC2Exception ex) {
            logger.warning("SecurityGroup already exists. Skipping...");
        }

    }

    public static void createIpPermission(String securityGroupName, AmazonEC2Client amazonEC2Client) {
        IpPermission ipPermission = new IpPermission();

        try {
            IpRange ipRange1 = new IpRange().withCidrIp("0.0.0.0/0");

            ipPermission.withIpv4Ranges(Collections.singletonList(ipRange1))
                    .withIpProtocol("tcp")
                    .withFromPort(22)
                    .withToPort(22);

            AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest =
                    new AuthorizeSecurityGroupIngressRequest();

            authorizeSecurityGroupIngressRequest.withGroupName(securityGroupName)
                    .withIpPermissions(ipPermission);

            amazonEC2Client.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
        } catch (AmazonEC2Exception ex) {
            logger.warning("Rule already exists. Skipped...");
        }
    }

    public static void generateKeyPair(String keyName, AmazonEC2Client amazonEC2Client) {
        try {
            CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
            createKeyPairRequest.withKeyName(keyName);
            savePrivateKey(amazonEC2Client.createKeyPair(createKeyPairRequest));
        } catch (AmazonEC2Exception ex) {
            logger.warning("Key pair already exists. Skipped...");
        }
    }

    private static void savePrivateKey(CreateKeyPairResult createKeyPairResult) {
        try {
            File privateKeyFile = new File(createKeyPairResult.getKeyPair().getKeyName() + ".pem");
            if (privateKeyFile.createNewFile()) {
                writeToFile(privateKeyFile, createKeyPairResult.getKeyPair().getKeyMaterial());
                Files.setPosixFilePermissions(privateKeyFile.toPath(), perms);
                logger.info("Private key file created: " + privateKeyFile.getName());
            } else {
                logger.info("File already exists.");
            }
        } catch (IOException e) {
            logger.severe("There was an error while creating the private key file!");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void writeToFile(File fileToWrite, String stringToWrite) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileToWrite);
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(fileOutputStream));
        dataOutputStream.writeUTF(stringToWrite);
        dataOutputStream.close();

        String verification;
        FileInputStream fileInputStream = new FileInputStream(fileToWrite);
        DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        verification = dataInputStream.readUTF();
        dataInputStream.close();
        if(!verification.equals(stringToWrite)) {
            logger.severe("Failed to write key to file!");
            System.exit(-1);
        }
    }

    public static void sendSSHCommandToInstance(String instanceIp, String keyFingerprint, String command,
                                                int counter) throws Exception {

        Session session = null;
        ChannelExec channelExec = null;

        try {
            JSch jsch = new JSch();
            JSch.setConfig("StrictHostKeyChecking", "no");
            jsch.addIdentity(keyFingerprint);
            session = jsch.getSession("ec2-user", instanceIp, 22);
            session.connect();

            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channelExec.setOutputStream(responseStream);
            channelExec.connect();


            while (channelExec.isConnected())
                Thread.sleep(100);

            if (channelExec.isClosed()) {
                logger.info("Channel exit status: " + channelExec.getExitStatus());
                if (channelExec.getExitStatus() != 0 && counter < 10) {
                    sendSSHCommandToInstance(instanceIp, keyFingerprint, command, ++counter);
                } else if (channelExec.getExitStatus() != 0) {
                    throw new SendCommandException("Could not send command to instance after 10 retries");
                }
            } else {
                channelExec.sendSignal("2");
            }
        } finally {
            if (session != null)
                session.disconnect();
            if (channelExec != null)
                channelExec.disconnect();
        }
    }

    public static void sendFileToInstance(String instanceIp, String keyPath, String sourcePath, String destinationPath) {
        Session session = null;
        ChannelSftp channelSftp = null;
        FileInputStream fileInputStream = null;
        File inputFile = null;

        try {
            JSch jsch = new JSch();
            JSch.setConfig("StrictHostKeyChecking", "no");
            jsch.addIdentity(keyPath);
            session = jsch.getSession("ec2-user", instanceIp, 22);
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");

            inputFile = new File(sourcePath);
            fileInputStream = new FileInputStream(inputFile);

            channelSftp.connect();

            channelSftp.put(fileInputStream, destinationPath);

            logger.info("Successfully uploaded file " + sourcePath);

            channelSftp.disconnect();
        } catch (NullPointerException e) {
            logger.severe("Error!");
            e.printStackTrace();
            System.exit(-1);
        } catch (FileNotFoundException e) {
            logger.severe("Couldn't find file!");
            e.printStackTrace();
            System.exit(-1);
        } catch (SftpException e) {
            logger.severe("SFTP upload unsuccessful!");
            e.printStackTrace();
            System.exit(-1);
        } catch (JSchException e) {
            logger.severe("SSH connection failed!");
            e.printStackTrace();
            System.exit(-1);
        } finally {
            if(channelSftp != null)
                channelSftp.disconnect();
        }

    }

    public static void getFileFromInstance(String instanceIp, String keyPath, String path) throws Exception {

        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();
            JSch.setConfig("StrictHostKeyChecking", "no");
            jsch.addIdentity(keyPath);
            session = jsch.getSession("ec2-user", instanceIp, 22);
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");

            logger.info(path);

            channelSftp.connect();

            channelSftp.get(path, path);
        } catch (NullPointerException e) {
            logger.severe("Error!");
            e.printStackTrace();
            System.exit(-1);
        } catch (SftpException e) {
            logger.severe("SFTP download unsuccessful!");
            e.printStackTrace();
            System.exit(-1);
        } catch (JSchException e) {
            logger.severe("SSH connection failed!");
            e.printStackTrace();
            System.exit(-1);
        } finally {
            if(channelSftp != null)
                channelSftp.disconnect();
        }
    }

    public static TerminateInstancesResult terminateInstances(AmazonEC2Client amazonEC2Client, List<String> instanceIds){
        TerminateInstancesResult result = null;
        try {
            TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest(instanceIds);
            result = amazonEC2Client.terminateInstances(terminateInstancesRequest);

            amazonEC2Client.waiters().instanceTerminated().run(new WaiterParameters<>(new DescribeInstancesRequest().withInstanceIds(instanceIds)));
            logger.info("Terminated all instances. Exiting...");
            return result;
        } catch (WaiterTimedOutException waiterTimedOutException) {
            logger.severe("Could not terminate all instances!");
            return result;
        }
    }

    public static RunInstancesResult runInstances(AmazonEC2Client amazonEC2Client, String imageId, InstanceType instanceType, int minCount, int maxCount, String keyName, String securityGroupName){

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
        runInstancesRequest.withImageId(imageId)
                .withInstanceType(instanceType)
                .withMinCount(minCount)
                .withMaxCount(maxCount)
                .withKeyName(keyName)
                .withSecurityGroups(securityGroupName);

        return amazonEC2Client.runInstances(runInstancesRequest);
    }

    public static RunInstancesResult runInstances(AmazonEC2Client amazonEC2Client, int minCount, int maxCount, String keyName, String securityGroupName){

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
        runInstancesRequest.withImageId("ami-0947d2ba12ee1ff75")
                .withInstanceType(InstanceType.T2Micro)
                .withMinCount(minCount)
                .withMaxCount(maxCount)
                .withKeyName(keyName)
                .withSecurityGroups(securityGroupName);

        return amazonEC2Client.runInstances(runInstancesRequest);
    }

    public static List<String> getInstanceIps(AmazonEC2Client amazonEC2Client, List<String> instanceIds){
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withInstanceIds(instanceIds);
        return amazonEC2Client.describeInstances(describeInstancesRequest).getReservations().stream().map(Reservation::getInstances).flatMap(List::stream).map(Instance::getPublicIpAddress).collect(Collectors.toList());
    }

    public static List<String> getInstanceIps(AmazonEC2Client amazonEC2Client){
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
        return amazonEC2Client.describeInstances(describeInstancesRequest).getReservations().stream().map(Reservation::getInstances).flatMap(List::stream).map(Instance::getPublicIpAddress).collect(Collectors.toList());
    }

    public static void waitForInstanceStatusOk(AmazonEC2Client amazonEC2Client, List<String> instanceIds){
        amazonEC2Client.waiters().instanceStatusOk().run(new WaiterParameters<>(new DescribeInstanceStatusRequest().withInstanceIds(instanceIds)));
    }

    public static void waitForInstanceRunning(AmazonEC2Client amazonEC2Client, List<String> instanceIds){
        amazonEC2Client.waiters().instanceRunning().run(new WaiterParameters<>(new DescribeInstancesRequest().withInstanceIds(instanceIds)));
    }

    public static List<String> getPrivateInstanceIp(AmazonEC2Client amazonEC2Client, List<String> instanceIds){
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withInstanceIds(instanceIds);
        return amazonEC2Client.describeInstances(describeInstancesRequest).getReservations().stream().map(Reservation::getInstances).flatMap(List::stream).map(Instance::getPrivateIpAddress).collect(Collectors.toList());
    }

    public static List<String> getPrivateDNS(AmazonEC2Client amazonEC2Client, List<String> instanceIds){
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withInstanceIds(instanceIds);
        return amazonEC2Client.describeInstances(describeInstancesRequest).getReservations().stream().map(Reservation::getInstances).flatMap(List::stream).map(Instance::getPrivateDnsName).collect(Collectors.toList());
    }

    public static List<String> getPublicDNS(AmazonEC2Client amazonEC2Client, List<String> instanceIds){
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withInstanceIds(instanceIds);
        return amazonEC2Client.describeInstances(describeInstancesRequest).getReservations().stream().map(Reservation::getInstances).flatMap(List::stream).map(Instance::getPublicDnsName).collect(Collectors.toList());
    }
}