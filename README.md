# Setup Instructions
1. Checkout the repository to your local folder: `git clone https://github.com/haiqingwang1005/JobRecommendation.git`
2. Open Eclipse IDE for Enterprise Java Developers, you can download it from Eclipse website: https://www.eclipse.org/downloads/packages/
3. After opening Eclipse, choose "File" -> "Import..." -> "Maven" -> "Existing Maven Projects", and choose your project folder.
4. Right click the "pom.xml" file, choose "Run As" -> "maven install"

# Local Deploy
## Set up Tomcat server
1. Download tomcat from http://tomcat.apache.org/download-90.cgi. You can choose the Core ones. Unzip the binary to a folder that you can remeber, and we will use it later.
2. Make sure the "Servers" window is available. If not, open  "Window"->"Show View"->"Servers" and then "Servers" would show up.
3. In the "Servers" window, click "No servers are available"
4. Choose "Apache"->"Tomcat v9.0 Server" and click Next.
5. Click "Browse" and choose the "apache-tomcat-9.0.xxx" that you have just downloaded and unzipped in Step 1, click ‘Open’.
6.  Click "Finish" and then you will find "Tomcat v9.0 Server at localhost ..." in "Servers" window.
7. Update Server configuration. Double Click "Tomcat v9.0 Server at localhost" in the Server window. In "Server Locations", click "Use Tomcat installation ..."
8. Right click "Tomcat v9.0 Server at localhost", choose "Properties". Click "Switch Location" to change the location to "/Servers/Tomcat v9.0 Server at localhost.server". 
9. To start the Tomcat Server, right click on "Tomcat v9.0 Server at localhost" and click "Start".

## Deploy the JobRecommendation application
1. Right click "Tomcat v9.0 Server at localhost", choose "Add and Remove..."
2. Move "Job" from left to right and click finish.
3. Restart the server, you can play it on http://localhost:8080/job now.

# EC2 Deploy
## Launch an EC2 instance
1. go to http://aws.amazon.com, sign into your account and then open the EC2 dash board. Under the EC2 page, click Launch Instance.
2. Under choose AMI page, choose the “Ubuntu Server 18.04 LTS (HVM), SSD Volume Type” image.
3. Under choose instance type page, choose t2.micro which is free tier eligible. Click next instead of launch.
4. Keep clicking next to configure security group page. Click the Add Rule button, then put 80 under the Port Range column and 0.0.0.0/0 under the Source column.
5. Click Launch, you will be asked to create a new key pair and download the private key. You can name it as mykey.pem
6. After instance state is running, find the public IP of your instance. You’ll need it in the next step.

## Connect to the instance
1. Open your terminal and run the following command. You need to use your own path of mykey.pem and IP address of your instance.
   * `chmod 600 YOUR_PRIVATE_KEY_LOCATION`
   * `ssh -i YOUR_PRIVATE_KEY_LOCATION ubuntu@YOUR_INSTANCE_IP`
2. If asked “Are you sure you want to continue connecting (yes/no)? ”, type “yes”, enter.
3. Verify that you’ve successfully SSH’ed to your instance.

## Run Docker container on EC2 VM

### Install Docker
1. Go back to the ssh window of your EC2 instance. Use the following command to install Docker CE. For more background about Docker installation, you can refer to https://docs.docker.com/install/linux/docker-ce/ubuntu.
   * `sudo apt-get update`
   * 
     ```bash
     sudo apt-get install \
     apt-transport-https \
     ca-certificates \
     curl \
     gnupg-agent \
     software-properties-common
     ```   
   * `curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -`
   * `sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"`
   * `sudo apt-get update`
   * `sudo apt-get install docker-ce docker-ce-cli containerd.io`
2. Use the following command to verify Docker is installed correctly: `sudo docker run hello-world`

### Build Job Recommendation app Archive and Upload to EC2
1. In Eclipse, select File -> Export -> Web, and choose WAR file under the export window.
2. Select the "job" project and choose an export destination that you can remember.
3. Click Finish and make sure you can see find the .war file on the destination on the local machine. It should be like "job.war"
4. Open a new terminal and execute the following command, use your own location and instance ip address in the command. Close the terminal once upload is down.
`scp -i YOUR_PRIVATE_KEY_LOCATION  YOUR_APP_WAR_LOCATION ubuntu@YOUR_INSTANCE_IP:~/`

### Build a Docker Image
1. In the ssh window of your EC2 instance, create a new file called Dockerfile:  `vim Dockerfile`
2. Copy the following content into Dockerfile file. Replace the maintainer to your own email address.
    ```
    FROM tomcat:9.0.31-jdk13-openjdk-oracle
    MAINTAINER haiqingwang1005@gmail.com

    ADD ./job.war /usr/local/tomcat/webapps
    EXPOSE 8080
    CMD ["catalina.sh", "run"]
    ```

3. Save and quit vim editor, and use the 'ls' to make sure both Dockerfile and job.war are in the current directory.
4. Run the following command to build an image named "job".
   ```bash
   #sudo docker build -t <image_name>
   sudo docker build -t job
   ```
5. (optional) you can use the following command to check existing images.
`sudo docker images`

### Run a Docker container
1. Use the following command to run your image locally.
`sudo docker run -d -p 80:8080 job`
2. (optional) you can use the following command to check running containers.
`sudo docker ps`
3. Open a new tab in your browser and use http://YOUR_INSTANCE_IP/job to test your service. Remember to use your own IP address.
4. (Optional) To check the log of a container, use the following command. You can find the container_id in step 2.
`sudo docker logs <container_id>`
5. Optional) To stop your Docker container, use the following command. You can find the comtainer_id in step 2.
`sudo docker stop <container_id>`
6. (optional) You can also use the following command to remove an existing container or image.
`sudo docker rm <container_id>`
`sudo docker rmi <image_name>`








