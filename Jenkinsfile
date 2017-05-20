node {
   stage 'Checkout'
   git branch: env.BRANCH_NAME, url: 'https://github.com/Nincodedo/Ninbot.git'

   stage 'Build'
   bat 'mvnw.cmd clean package sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.login=${SONARQUBE_TOKEN}'

   stage 'Archive'
   archive includes: 'target/*.jar'
}
