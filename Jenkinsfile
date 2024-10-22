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
					additionalBuildArgs "--build-arg JDK_VERSION=$JDK_VERSION \
						--build-arg PLATFORM_VERSION=$PLATFORM_VERSION \
						--build-arg BUILD_TOOLS_VERSION=$BUILD_TOOLS_VERSION"
					args "--network host -v $HOME/.gradle:/root/.gradle"
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
					additionalBuildArgs "--build-arg JDK_VERSION=$JDK_VERSION \
						--build-arg PLATFORM_VERSION=$PLATFORM_VERSION \
						--build-arg BUILD_TOOLS_VERSION=$BUILD_TOOLS_VERSION"
					args "--network host -v $HOME/.gradle:/root/.gradle"
					reuseNode true
				}
			}
			steps {
				sh './gradlew test'
			}
		}
	}
}
