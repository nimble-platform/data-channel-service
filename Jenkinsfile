#!/usr/bin/env groovy

node('nimble-jenkins-slave') {

    stage('Clone and Update') {
        git(url: 'https://github.com/nimble-platform/data-channel-service.git', branch: env.BRANCH_NAME)
        sh 'git submodule init'
        sh 'git submodule update'
    }

    stage('Build Dependencies') {
        sh 'rm -rf common'
        sh 'git clone https://github.com/nimble-platform/common'
        dir('common') {
            sh 'git checkout ' + env.BRANCH_NAME
            sh 'mvn clean install'
        }
    }

    stage('Build Java') {
        sh 'mvn clean install -DskipTests'
    }

    if (env.BRANCH_NAME == 'staging') {
        stage('Build Docker') {
            sh 'mvn -f data-channel-service/pom.xml docker:build -DdockerImageTag=staging'
        }

        stage('Push Docker') {
            sh 'docker push nimbleplatform/data-channel-service:staging'
        }

        stage('Deploy') {
            sh 'ssh staging "cd /srv/nimble-staging/ && ./run-staging.sh restart-single data-channel-service"'
        }
    } else {
        stage('Build Docker') {
            sh 'mvn -f data-channel-service/pom.xml docker:build'
        }
    }

    if (env.BRANCH_NAME == 'master') {

        stage('Push Docker') {
            sh 'mvn -f data-channel-service/pom.xml docker:push'
            sh 'mvn -f data-channel-service/pom.xml docker:push -DdockerImageTag=latest'
        }

        stage('Deploy MVP') {
            sh 'ssh nimble "cd /data/deployment_setup/prod/ && sudo ./run-prod.sh restart-single data-channel-service"'
        }

        stage('Deploy FMP') {
            sh 'ssh fmp-prod "cd /srv/nimble-fmp/ && ./run-fmp-prod.sh restart-single data-channel-service"'
        }
    }
}
