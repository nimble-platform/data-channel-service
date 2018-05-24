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
//        stage('Build Docker') {
//            sh 'mvn -f identity-service/pom.xml docker:build'
//        }
    }

    if (env.BRANCH_NAME == 'master') {
//        stage('Deploy') {
//            sh 'ssh nimble "cd /data/deployment_setup/prod/ && sudo ./run-prod.sh restart-single identity-service"'
//        }
    }

//    if (env.BRANCH_NAME == 'master') {
//        stage('Push Docker') {
//            withDockerRegistry([credentialsId: 'NimbleDocker']) {
//                sh 'docker push nimbleplatform/identity-service:latest'
//            }
//        }

//
//        stage('Apply to Cluster') {
//            sh 'ssh nimble "cd /data/nimble_setup/ && sudo ./run-prod.sh restart-single identity-service"'
////            sh 'kubectl apply -f kubernetes/deploy.yml -n prod --validate=false'
//        }
//    }
}
