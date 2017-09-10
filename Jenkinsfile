pipeline {
    agent any
    stages {
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
        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                build job: 'UpdateNinbot', wait: false
            }
        }
    }
}
