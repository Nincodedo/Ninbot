pipeline {
    agent none

    stages {
      stage('Maven Build') {
        steps {
            sh "mvn clean verify org.pitest:pitest-maven:mutationCoverage -P git-commit"
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
                recordCoverage ignoreParsingErrors: true, skipPublishingChecks: true, tools: [[parser: 'JACOCO', pattern: 'ninbot-test-coverage/**/jacoco.xml'], [parser: 'PIT']]
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
