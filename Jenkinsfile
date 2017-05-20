node {
   stage 'Checkout'
   git branch: env.BRANCH_NAME, url: 'https://github.com/Nincodedo/Ninbot.git'

   stage 'Build'
   bat 'mvnw.cmd clean package'

   stage 'Archive'
   archive includes: 'target/*.jar'
}