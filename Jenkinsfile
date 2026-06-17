pipeline {
    agent any

    tools {
        maven 'Apache Maven 3.9.3'
        jdk 'OpenJDK-21'
    }

    environment {
        APP_NAME     = 'vendor'
        CONTEXT_PATH = '/vendor'
        WAR_FILE     = 'target/vendor.war'

        // 🔴 UPDATE THIS if Tomcat is on another machine
        TOMCAT_URL   = 'http://localhost:8081'

        SONAR_ENV    = 'SonarQube'
        SONAR_KEY    = 'ims-vendor'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Unit Test') {
            steps {
                bat 'mvn -B clean verify'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONAR_ENV}") {
                    bat """
                    mvn sonar:sonar ^
                    -Dsonar.projectKey=%SONAR_KEY% ^
                    -Dsonar.projectName=%SONAR_KEY%
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'tomcat-cred',
                    usernameVariable: 'TUSER',
                    passwordVariable: 'TPASS'
                )]) {

                    bat """
                    curl --fail --silent --show-error ^
                    -u %TUSER%:%TPASS% ^
                    --upload-file %WAR_FILE% ^
                    "%TOMCAT_URL%/manager/text/deploy?path=%CONTEXT_PATH%^&update=true"
                    """
                }
            }
        }

        stage('Smoke Test') {
            steps {
                bat """
                curl --fail --silent --show-error "%TOMCAT_URL%%CONTEXT_PATH%/"
                """
            }
        }
    }

    post {
        success {
            echo "SUCCESS: Deployed ${APP_NAME} -> ${TOMCAT_URL}${CONTEXT_PATH}"
        }

        failure {
            echo "FAILED: Pipeline for ${APP_NAME}"
        }

        always {
            cleanWs()
        }
    }
}