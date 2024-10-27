pipeline {
	agent any
	options {
		timestamps()
	}
	stages {
		stage('Checkout') {
			steps {
				checkout scm
			}
		}
		stage('Build') {
			agent {
				dockerfile {
					dir 'cicd/docker/androidsdk'
					additionalBuildArgs "--user root:root"
					reuseNode true
				}
			}
			steps {
				sh './gradlew assembleDebug'
			}
		}
		stage('Test') {
			agent {
				dockerfile {
					dir 'cicd/docker/androidsdk'
					additionalBuildArgs "--user root:root"
					reuseNode true
				}
			}
			steps {
				sh './gradlew test'
			}
		}
	}
}
