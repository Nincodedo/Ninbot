pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                bat "mvnw.cmd clean package sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.login=${env.SONARQUBE_TOKEN} -Dsonar.organization=${env.NINCRAFT_SONAR_ORG_KEY}"
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
