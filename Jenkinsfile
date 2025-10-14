pipeline {
    agent none

    stages {
      stage('Maven Build') {
        steps {
            sh "mvn clean verify -P git-commit"
        }
        agent any
        tools {
          jdk 'jdk25'
          maven 'M3'
        }
        post {
            success {
                archiveArtifacts 'ninbot-app/target/*.jar'
            }
            always {
                junit allowEmptyResults: true, stdioRetention: 'ALL', testResults: '**/target/surefire-reports/TEST-*.xml'
            }
        }
      }
    }
    post {
        regression {
            emailext attachLog: true, body: "${env.BUILD_URL}", compressLog: true, recipientProviders: [buildUser(), developers()], subject: "${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - ${currentBuild.currentResult}!"
        }
    }
}
