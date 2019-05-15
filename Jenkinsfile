pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                bat "mvnw.cmd clean package"
            }
        }
        stage('Sonar') {
            steps {
                withCredentials([usernamePassword(credentialsId: '4800875c-8956-4d0a-8fe0-30f231d20723', passwordVariable: 'token', usernameVariable: 'username')]) {
                    bat "mvnw.cmd sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.login=${token} -Dsonar.organization=${username} -Dsonar.branch.name=${env.BRANCH_NAME}"
                }
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
