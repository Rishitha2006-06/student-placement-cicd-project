# CampusConnect — Student Placement Management System

A Spring Boot + MySQL web app demonstrating Git/GitHub, Maven, Jenkins, Docker, and Kubernetes.

## Features
- View job openings and eligibility details
- Add job openings
- Apply with student name and email
- View applications and update status

> Educational starter: authentication/authorization is not implemented. Do not expose publicly or use real student data until you add security and privacy controls. The included credentials are local-demo values only.

## Prerequisites
Java 17+, Git, Docker Desktop with Docker Compose. For Jenkins/Kubernetes stages, also configure Jenkins, Maven/JDK tools, Docker access, Docker Hub credentials, kubectl, and a reachable Kubernetes cluster.

## Run locally
Recommended easiest route from the project root:
```bash
docker compose up --build
```
Open http://localhost:8080. Stop with Ctrl+C, then `docker compose down`. `docker compose down -v` also deletes local database data.

## GitHub
Create an empty GitHub repository, then:
```bash
git init
git add .
git commit -m "Initial placement portal"
git branch -M main
git remote add origin https://github.com/YOUR_GITHUB_USERNAME/student-placement-cicd.git
git push -u origin main
```

## Jenkins
1. Install Pipeline, Git, JUnit, and Credentials plugins.
2. Configure JDK tool named `jdk17` and Maven tool named `maven3`.
3. Replace `YOUR_DOCKERHUB_USERNAME` in Jenkinsfile.
4. Add a username/password credential with ID `dockerhub-creds`; use a Docker Hub access token as the password.
5. Ensure the Jenkins agent can use Docker and has kubectl plus Kubernetes credentials.
6. Create a Pipeline job connected to your GitHub repository.
7. For a first run, disable the Kubernetes Deploy stage until your cluster and namespace are configured.

## Kubernetes demo
Ensure the cluster has a default StorageClass. Edit the example secret with demo-only values, then:
```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/mysql-secret.example.yaml
kubectl apply -f k8s/mysql.yaml
kubectl rollout status deployment/mysql -n placement
```
Build/push an image and replace `IMAGE_PLACEHOLDER` in `k8s/app-deployment.yaml` with your image:
```bash
docker build -t YOUR_DOCKERHUB_USERNAME/student-placement:latest .
docker push YOUR_DOCKERHUB_USERNAME/student-placement:latest
kubectl apply -f k8s/app-deployment.yaml
kubectl get pods,svc -n placement
```
Open http://localhost:30080 for many local clusters; networking varies by Kubernetes setup.

## CI/CD flow
Developer → GitHub → Jenkins checkout → Maven build → JUnit tests → Docker image build → Docker Hub push → Kubernetes deployment rollout.

## Report outline
Title page; abstract; problem statement; objectives; requirements; architecture; technology stack; modules/database; Jenkins pipeline; Docker; Kubernetes; testing/results; limitations/future work; conclusion; references.

## Screenshots to capture yourself
Home page, add-job form, application status page, GitHub repository, Jenkins build/test report, Docker image, `kubectl get pods,svc -n placement`, and app running via Kubernetes.
