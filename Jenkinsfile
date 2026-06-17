// Jenkinsfile - VendorService (REST service)
// Pipeline: Gitea checkout -> Maven build/test -> SonarQube -> Quality Gate -> deploy WAR to Tomcat
pipeline {
    agent any

    // Names must match Jenkins > Manage Jenkins > Tools
    tools {
        maven 'Maven3'
        jdk   'JDK17'
    }

    environment {
        APP_NAME     = 'vendor'                    // matches <finalName> in pom.xml -> vendor.war
        CONTEXT_PATH = '/vendor'                   // deployed context on Tomcat
        WAR_FILE     = 'target/vendor.war'

        // Tomcat lives on 8090 (8080 is taken by Jenkins). Change host to your VM.
        TOMCAT_URL   = 'http://YOUR_VM_HOST:8090'
        // 'Username with password' credential holding a Tomcat manager-script user.
        TOMCAT_CREDS = credentials('tomcat-manager')

        SONAR_ENV    = 'SonarQube'                 // name of the SonarQube server in Jenkins
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
                // URL is quoted so cmd.exe does not treat '&' as a command separator.
                // %TOMCAT_CREDS% is used (not Groovy ${...}) so the password stays masked.
                bat 'curl --fail --silent --show-error --upload-file "%WAR_FILE%" -u "%TOMCAT_CREDS%" "%TOMCAT_URL%/manager/text/deploy?path=%CONTEXT_PATH%&update=true"'
            }
        }

        stage('Smoke Test') {
            steps {
                // returnStatus tolerates a non-200 (e.g. REST root has no mapping).
                bat returnStatus: true, script: 'curl --fail --silent --show-error "%TOMCAT_URL%%CONTEXT_PATH%/" -o NUL'
            }
        }
    }

    post {
        success { echo "Deployed ${APP_NAME} -> ${TOMCAT_URL}${CONTEXT_PATH}" }
        failure { echo "Pipeline FAILED for ${APP_NAME}" }
        always  { cleanWs() }
    }
}
