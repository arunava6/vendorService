pipeline {

    agent any

    tools {
    jdk 'OpenJDK-21'
    maven 'Apache Maven 3.9.3'
    }

    stages {

        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }

    }

    post {
        success {
            echo 'Build Successful'
        }

        failure {
            echo 'Build Failed'
        }
    }
}