pipeline {
    agent any

    // Tool configuration: Configured in Jenkins (Manage Jenkins -> Tools)
    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    environment {
        // Change to your Docker Hub username or leave as is if pushing is skipped
        DOCKERHUB_USER = 'rishitha2006'
        IMAGE_NAME     = "${env.DOCKERHUB_USER}/student-placement"
        K8S_NAMESPACE  = 'placement'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code from GitHub...'
                checkout scm
            }
        }

        stage('Compile & Test') {
            steps {
                echo 'Running automated JUnit tests with Maven...'
                script {
                    if (isUnix()) {
                        sh 'mvn -B test'
                    } else {
                        bat 'mvn -B test'
                    }
                }
            }
            post {
                always {
                    // Publishes JUnit test results to Jenkins UI
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Package Application') {
            steps {
                echo 'Packaging application into executable Spring Boot JAR...'
                script {
                    if (isUnix()) {
                        sh 'mvn -B package -DskipTests'
                    } else {
                        bat 'mvn -B package -DskipTests'
                    }
                }
                // Archive the JAR artifact in Jenkins
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: true
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image: ${IMAGE_NAME}:${BUILD_NUMBER}..."
                script {
                    if (isUnix()) {
                        sh "docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} -t ${IMAGE_NAME}:latest ."
                    } else {
                        bat "docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} -t ${IMAGE_NAME}:latest ."
                    }
                }
            }
        }

        stage('Push to Docker Hub') {
            when {
                // Only run push if dockerhub-creds credential exists in Jenkins
                expression { return env.PUSH_DOCKER_IMAGE == 'true' }
            }
            steps {
                echo 'Pushing Docker image to Docker Hub...'
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    script {
                        if (isUnix()) {
                            sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
                            sh "docker push ${IMAGE_NAME}:${BUILD_NUMBER}"
                            sh "docker push ${IMAGE_NAME}:latest"
                            sh 'docker logout'
                        } else {
                            bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"
                            bat "docker push ${IMAGE_NAME}:${BUILD_NUMBER}"
                            bat "docker push ${IMAGE_NAME}:latest"
                            bat "docker logout"
                        }
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            when {
                expression { return env.DEPLOY_TO_K8S == 'true' }
            }
            steps {
                echo "Deploying to Kubernetes namespace: ${K8S_NAMESPACE}..."
                script {
                    if (isUnix()) {
                        sh "sed \"s|IMAGE_PLACEHOLDER|${IMAGE_NAME}:${BUILD_NUMBER}|g\" k8s/app-deployment.yaml | kubectl apply -n ${K8S_NAMESPACE} -f -"
                        sh "kubectl apply -n ${K8S_NAMESPACE} -f k8s/app-service.yaml"
                        sh "kubectl rollout status deployment/student-placement -n ${K8S_NAMESPACE} --timeout=180s"
                    } else {
                        powershell """
                        (Get-Content k8s/app-deployment.yaml) -replace 'IMAGE_PLACEHOLDER', '${IMAGE_NAME}:${BUILD_NUMBER}' | kubectl apply -n ${K8S_NAMESPACE} -f -
                        kubectl apply -n ${K8S_NAMESPACE} -f k8s/app-service.yaml
                        kubectl rollout status deployment/student-placement -n ${K8S_NAMESPACE} --timeout=180s
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo '==================================================='
            echo ' CI/CD Pipeline executed successfully!              '
            echo '==================================================='
        }
        failure {
            echo '==================================================='
            echo ' Pipeline failed. Check console output for details. '
            echo '==================================================='
        }
    }
}
