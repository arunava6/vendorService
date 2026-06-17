pipeline {

    agent any

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