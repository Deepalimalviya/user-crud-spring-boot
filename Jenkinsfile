pipeline {
    agent any
    tools {
        jdk 'jdk21'
        maven 'maven3'
    }
    environment {
        IMAGE_NAME = 'deepali/user-crud'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }
    stages {
        stage('Checkout') { steps { checkout scm } }
        stage('Build & Test') {
            steps { sh 'mvn --batch-mode clean verify' }
            post { always { junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml' } }
        }
        stage('Package') { steps { sh 'mvn --batch-mode package -DskipTests' } }
        stage('Docker Build') {
            steps { sh 'docker build -t $IMAGE_NAME:$IMAGE_TAG -t $IMAGE_NAME:latest .' }
        }
        // Configure registry credentials in Jenkins before enabling this stage.
        stage('Publish Image') {
            when { branch 'main' }
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
                    sh 'docker push $IMAGE_NAME:$IMAGE_TAG && docker push $IMAGE_NAME:latest'
                    sh 'docker logout'
                }
            }
        }
    }
    post { success { echo 'Build, tests and Docker image build completed.' } failure { echo 'Pipeline failed. Review test/build logs.' } }
}