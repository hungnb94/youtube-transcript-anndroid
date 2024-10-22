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
					dir 'cicd/androidsdk'
					args "--network host -v \"$HOME/.gradle\":/root/.gradle"
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
					dir 'cicd/androidsdk'
					args "--network host -v \"$HOME/.gradle\":/root/.gradle"
					reuseNode true
				}
			}
			steps {
				sh './gradlew test'
			}
		}
	}
}
