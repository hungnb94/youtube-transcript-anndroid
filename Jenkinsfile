pipeline {
	agent any
	environment {
		JDK_VERSION = '17'
		PLATFORM_VERSION = 'android-34'
		BUILD_TOOLS_VERSION = '34.0.0'
	}
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
// 					additionalBuildArgs "-u root"
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
// 					additionalBuildArgs "-u root"
					reuseNode true
				}
			}
			steps {
				sh './gradlew test'
			}
		}
	}
}
