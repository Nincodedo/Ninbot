pipeline {
    agent any
    stages {
        stage('Checkout') {
            checkout scm
        }
        stage('Build') {
           bat 'mvnw.cmd clean package'
        }
        stage('Archive') {
            archive includes: 'target/*.jar'
        }
    }
}