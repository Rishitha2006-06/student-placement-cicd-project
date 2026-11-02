pipeline {
  agent any
  tools { jdk 'jdk17'; maven 'maven3' }
  environment {
    IMAGE_NAME = 'YOUR_DOCKERHUB_USERNAME/student-placement'
    K8S_NAMESPACE = 'placement'
  }
  stages {
    stage('Checkout') { steps { checkout scm } }
    stage('Build') { steps { sh 'mvn -B clean package -DskipTests' } }
    stage('Test') {
      steps { sh 'mvn -B test' }
      post { always { junit 'target/surefire-reports/*.xml' } }
    }
    stage('Docker Build') { steps { sh 'docker build -t $IMAGE_NAME:$BUILD_NUMBER -t $IMAGE_NAME:latest .' } }
    stage('Docker Push') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin && docker push "$IMAGE_NAME:$BUILD_NUMBER" && docker push "$IMAGE_NAME:latest" && docker logout'
        }
      }
    }
    stage('Kubernetes Deploy') {
      steps {
        sh 'sed "s|IMAGE_PLACEHOLDER|$IMAGE_NAME:$BUILD_NUMBER|g" k8s/app-deployment.yaml | kubectl apply -n "$K8S_NAMESPACE" -f -'
        sh 'kubectl apply -n "$K8S_NAMESPACE" -f k8s/app-service.yaml'
        sh 'kubectl rollout status deployment/student-placement -n "$K8S_NAMESPACE" --timeout=180s'
      }
    }
  }
  post { success { echo 'CI/CD pipeline completed.' } failure { echo 'Pipeline failed. Review stage logs.' } }
}