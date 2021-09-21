pipeline {
  agent any
  stages {
    stage("build") {
      steps {
        bat ‘echo building'
        }
       }
    stage("test") {
      steps {
        bat ‘gradle test’
            }
       }   
    stage("deploy") {
       steps {
        bat “echo deploying"     }
       }
       }
       }
