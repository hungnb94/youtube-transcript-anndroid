# CICD
## Preparation
Before running docker-compose, make sure to do the following
- Start the jenkins container by running docker-compose
```shell
docker pull enkins/jenkins:lts
docker run --name jenkins \
	-u root \
	-v /var/run/docker.sock:/var/run/docker.sock \
	-v jenkins_home:/var/jenkins_home \
	-p 8080:8080 -p 50000:50000 \
	--restart=on-failure \
	jenkins/jenkins:lts
```

## Configuration

- Open Jenkins in the browser http://localhost:8080
- Continue the installation using the admin password. You can get the admin password by running
```
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

- When prompted, enter the external url of the Jenkins installation 
  - Running on a development machine, use ngrok `ngrok http 80`
  - Use the external ngrok url as the base url for Jenkins
- Install the default plugins. This will take several minutes
- Install additional plugins needed
  - Install Docker Pipeline plugin

## Create a new Pipeline Item
