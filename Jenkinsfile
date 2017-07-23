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
                bat 'mvnw.cmd clean package'
            }
        }
        stage('Archive') {
            steps {
                archive includes: 'target/*.jar'
            }
        }
    }
    post {
        success {
            build job: 'UpdateNinbot', wait: false
        }
    }
}