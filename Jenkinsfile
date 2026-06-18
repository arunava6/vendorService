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

        TOMCAT_URL   = 'http://10.1.0.27:8081'

        SONAR_ENV    = 'SonarQube'
        SONAR_KEY    = 'ims-vendor'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 45, unit: 'MINUTES')
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvn -B clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
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

        stage('Quality Gate (FAST MODE)') {
            steps {
                script {
                    timeout(time: 2, unit: 'MINUTES') {
                        def qg = waitForQualityGate()
                        echo "Quality Gate: ${qg.status}"

                        if (qg.status != 'OK') {
                            error "Quality Gate FAILED"
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'tomcat-cred',
                    usernameVariable: 'TUSER',
                    passwordVariable: 'TPASS'
                )]) {

                    bat """
                    curl --fail -u %TUSER%:%TPASS% ^
                    --upload-file %WAR_FILE% ^
                    "%TOMCAT_URL%/manager/text/deploy?path=%CONTEXT_PATH%^&update=true"
                    """
                }
            }
        }

        stage('Smoke Test') {
            steps {
                bat "curl --fail %TOMCAT_URL%%CONTEXT_PATH%/"
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}