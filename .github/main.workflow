workflow "Maven build" {
  on = "push"
  resolves = ["Sonar"]
}

action "Build" {
  uses = "LucaFeger/action-maven-cli@9d8f23af091bd6f5f0c05c942630939b6e53ce44"
  args = "clean package"
}

action "Sonar" {
  uses = "LucaFeger/action-maven-cli@9d8f23af091bd6f5f0c05c942630939b6e53ce44"
  args = "sonar:sonar -Dsonar.login=$SONAR_TOKEN -Dsonar.host.url=https://sonarqube.com -Dsonar.organization=nincraft"
  needs = ["Build"]
  secrets = ["SONAR_TOKEN"]
}
