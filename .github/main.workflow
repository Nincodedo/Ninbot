workflow "Maven build" {
  on = "push"
  resolves = ["Build"]
}

action "Build" {
  uses = "LucaFeger/action-maven-cli@9d8f23af091bd6f5f0c05c942630939b6e53ce44"
  args = "clean package -Dsonar.login=$SONAR_TOKEN -Dsonar.host.url=https://sonarqube.com -Dsonar.organization=nincraft -Dsonar.branch.name=$GITHUB_REF"
  secrets = ["SONAR_TOKEN"]
}

workflow "PR Build" {
  on = "pull_request"
  resolves = ["PR Maven build"]
}

action "PR Maven build" {
  uses = "LucaFeger/action-maven-cli@9d8f23af091bd6f5f0c05c942630939b6e53ce44"
  secrets = ["SONAR_TOKEN"]
  args = "clean package -Dsonar.login=$SONAR_TOKEN -Dsonar.host.url=https://sonarqube.com -Dsonar.organization=nincraft -Dsonar.branch.name=$GITHUB_REF"
}
