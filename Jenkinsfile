// Jenkinsfile - VendorService (REST service)
// Pipeline: Gitea checkout -> Maven build/test -> SonarQube -> Quality Gate -> deploy WAR to Tomcat

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

        TOMCAT_URL   = 'http://YOUR_VM_HOST:8090'
        TOMCAT_CREDS = credentials('tomcat-manager')

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
                    bat 'mvn -B sonar:sonar -Dsonar.projectKey=%SONAR_KEY% -Dsonar.projectName=%SONAR_KEY%'
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
                bat 'curl --fail --silent --show-error --upload-file "%WAR_FILE%" -u "%TOMCAT_CREDS%" "%TOMCAT_URL%/manager/text/deploy?path=%CONTEXT_PATH%&update=true"'
            }
        }

        stage('Smoke Test') {
            steps {
                bat returnStatus: true, script: 'curl --fail --silent --show-error "%TOMCAT_URL%%CONTEXT_PATH%/" -o NUL'
            }
        }

    }

    post {
        success {
            echo "Deployed ${APP_NAME} -> ${TOMCAT_URL}${CONTEXT_PATH}"
        }

        failure {
            echo "Pipeline FAILED for ${APP_NAME}"
        }

        always {
            cleanWs()
        }
    }
}