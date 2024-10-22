pipeline {
	agent any
	stages {
		stage('Checkout') {
			steps {
				checkout scm
			}
		}
		stage('Build') {
			steps {
				sh './gradlew assembleDebug'
			}
		}
		stage('Test') {
			steps {
				sh './gradlew test'
			}
		}
	}
}
