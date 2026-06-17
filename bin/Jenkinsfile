node {
    def workspace = pwd()    
    stage('Application_Build') {
        checkout scm
        bat './mvnw clean package -DskipTests'
    }    
    stage('Application_Unit_Test') {        
        bat './mvnw compiler:testCompile surefire:test'
        step([$class: 'JUnitResultArchiver', testResults: "**/surefire-reports/*.xml"])
    }    
    stage('Application_Code_Analysis') {        
        withSonarQubeEnv {
            bat './mvnw sonar:sonar -Dsonar.projectKey=VendorService_Static_Code_Analysis -Dsonar.projectName=VendorService_Static_Code_Analysis'
        }
    }
    stage('Application_Deploy') {
        bat "copy ${workspace}\\target\\project.war ${TOMCAT_HOME}\\webapps\\"
    }
}