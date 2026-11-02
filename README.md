# End-to-End CI/CD Pipeline for Student Placement Management System (CampusConnect)

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9.16-blue.svg)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED.svg)](https://www.docker.com/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-v1.30+-326CE5.svg)](https://kubernetes.io/)
[![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-D33833.svg)](https://www.jenkins.io/)

---

## 1. Project Overview

**CampusConnect** is a web-based Student Placement Management System engineered as a complete DevOps case-study demonstration. The application connects college students with recruitment drives, enabling students to explore company openings, check eligibility, apply directly, and allows placement coordinators to manage job postings and update candidate application statuses.

### Key Objectives
- Modern cloud-native Spring Boot application with Spring Data JPA and Thymeleaf UI.
- Containerization using multi-stage Docker build for minimal image size and non-root security.
- Fully automated Jenkins CI/CD pipeline performing checkout, unit testing, packaging, and Docker image builds.
- Production-grade Kubernetes orchestration with Deployments, Services (NodePort), PVCs, and Secrets.
- Centralized relational data management with MySQL 8.4 and in-memory H2 for testing.

---

## 2. Technology Stack

| Domain | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Java | 21 LTS | Core backend programming language |
| **Framework** | Spring Boot | 3.4.5 | Web MVC, JPA, Thymeleaf, Actuator |
| **Build & Dependency Tool** | Apache Maven | 3.9.16 | Compilation, dependency resolution, packaging |
| **Testing** | JUnit 5 & MockMvc | 5.11 | Automated unit, repository, and controller testing |
| **Database** | MySQL / H2 | 8.4 / 2.3 | Persistent relational storage & isolated test DB |
| **Version Control** | Git & GitHub | Latest | Source code management & webhook triggers |
| **CI/CD Automation** | Jenkins | 2.x | Automated multi-stage pipeline execution |
| **Containerization** | Docker & Compose | Latest | Standardized container packaging and local multi-service orchestration |
| **Container Orchestration**| Kubernetes | v1.30+ | Resilient deployment, scaling, service discovery |

---

## 3. Architecture & CI/CD Pipeline Workflow

```
+-----------------------------------------------------------------------------------+
|                              DEVELOPER WORKSTATION                                |
|  Write Code (Java 21 / Spring Boot) -> Local Test -> Git Commit & Push to GitHub   |
+------------------------------------------+----------------------------------------+
                                           |
                                           v
+-----------------------------------------------------------------------------------+
|                               GITHUB REPOSITORY                                   |
|               https://github.com/Rishitha2006-06/student-placement-cicd-project    |
+------------------------------------------+----------------------------------------+
                                           | Webhook / Polling
                                           v
+-----------------------------------------------------------------------------------+
|                            JENKINS CI/CD PIPELINE                                 |
|                                                                                   |
|  [Stage 1: Checkout]     -> Clones source code from GitHub                        |
|  [Stage 2: Test]         -> Executes JUnit 5 tests via `mvn test`                 |
|                             Publishes test reports (`target/surefire-reports`)    |
|  [Stage 3: Package]      -> Packages executable JAR (`mvn package -DskipTests`)   |
|                             Archives build artifact                               |
|  [Stage 4: Docker Build] -> Builds Docker image with tags `:BUILD_NUMBER` & :latest|
|  [Stage 5: Docker Push]  -> (Optional) Pushes image to Docker Hub                  |
|  [Stage 6: K8s Deploy]   -> (Optional) Rolls out Deployment to Kubernetes Cluster  |
+------------------------------------------+----------------------------------------+
                                           |
                                           v
+-----------------------------------------------------------------------------------+
|                           DEPLOYMENT ENVIRONMENTS                                 |
|                                                                                   |
|  Local Docker Compose:                 Kubernetes Cluster (Minikube / Docker K8s):|
|  - MySQL 8.4 (Port 3306)               - Namespace: `placement`                   |
|  - Spring Boot App (Port 8000)         - MySQL Deployment + PVC + Secret          |
|                                        - App Deployment (2 Replicas)              |
|                                        - NodePort Service (Port 30080)            |
+-----------------------------------------------------------------------------------+
```

---

## 4. Local Development & Running the Application

### Option A: Complete Stack via Docker Compose (Recommended)
Make sure Docker Desktop is running on Windows, then run from the project root:

```powershell
# Start both MySQL and the Spring Boot application
docker compose up --build
```
- Open browser: `http://localhost:8000`
- To stop the containers: `Ctrl + C` followed by:
```powershell
docker compose down
```
*(To also wipe database volume: `docker compose down -v`)*

---

### Option B: Running Spring Boot Locally with Dockerized MySQL
If you want to run or debug Spring Boot in IntelliJ, VS Code, or command line:

1. **Start only the MySQL database container:**
```powershell
docker compose up -d mysql
```
*(MySQL is accessible on `localhost:3306` with user `placement`, password `placement123`, database `placementdb`).*

2. **Run the Spring Boot application:**
```powershell
# In PowerShell:
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12"
.\mvnw.cmd spring-boot:run
# Or using installed Maven:
& "C:\Program Files\Maven\Apache\apache-maven-3.9.16\bin\mvn.cmd" spring-boot:run
```

3. **Access the portal:**
Navigate to `http://localhost:8000` in your web browser. Sample jobs from Google, Microsoft, and Amazon are automatically seeded on first launch!

---

## 5. Automated Testing (JUnit 5)

The project includes 12 automated unit and integration tests covering:
- **`PlacementApplicationTests`**: Verifies Spring context initialization.
- **`JobEntityAndRepositoryTest`**: Verifies entity creation, fields, JPA queries, and relations.
- **`WebControllerTest`**: Uses MockMvc to test GET/POST endpoints, form validation, and status transitions.

Run the test suite:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12"
& "C:\Program Files\Maven\Apache\apache-maven-3.9.16\bin\mvn.cmd" test
```

Test reports are generated in XML and HTML under:
```
target/surefire-reports/
  ├── TEST-com.example.placement.PlacementApplicationTests.xml
  ├── TEST-com.example.placement.JobEntityAndRepositoryTest.xml
  └── TEST-com.example.placement.WebControllerTest.xml
```

---

## 6. Docker Containerization

### Dockerfile Highlights
- **Multi-stage build**:
  - Build stage: `maven:3.9-eclipse-temurin-21`
  - Runtime stage: `eclipse-temurin:21-jre`
- **Security**: Runs under an unprivileged user (`appuser`, UID 10001) rather than root.
- **Optimized JVM memory**: `-XX:MaxRAMPercentage=75.0` for container memory limits.

### Build and Run Docker Image Manually
```powershell
# 1. Build the image
docker build -t student-placement:latest .

# 2. Run container (requires MySQL running)
docker run -d --name student-placement-app `
  -p 8000:8000 `
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/placementdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" `
  -e SPRING_DATASOURCE_USERNAME="placement" `
  -e SPRING_DATASOURCE_PASSWORD="placement123" `
  student-placement:latest
```

---

## 7. Jenkins CI/CD Setup

Jenkins is configured to run at `http://localhost:9090`.

### Step 1: Start Jenkins
If running Jenkins WAR file:
```powershell
java -jar jenkins.war --httpPort=9090
```
Or start the Jenkins Windows service from `services.msc`.

### Step 2: Configure Global Tools in Jenkins
1. Go to **Manage Jenkins** → **Tools**.
2. **JDK installations**:
   - Name: `jdk21`
   - JAVA_HOME: `C:\Program Files\Java\jdk-21.0.12`
3. **Maven installations**:
   - Name: `maven3`
   - MAVEN_HOME: `C:\Program Files\Maven\Apache\apache-maven-3.9.16`
4. Click **Save**.

### Step 3: Create Jenkins Pipeline Job
1. Click **New Item** → Enter name: `student-placement-pipeline` → Select **Pipeline** → Click **OK**.
2. Scroll to the **Pipeline** section:
   - Definition: **Pipeline script from SCM**
   - SCM: **Git**
   - Repository URL: `https://github.com/Rishitha2006-06/student-placement-cicd-project.git`
   - Branch Specifier: `*/main`
   - Script Path: `Jenkinsfile`
3. Click **Save**.
4. Click **Build Now** to trigger the pipeline.
5. Inspect the **Stage View** and verify all stages pass.

---

## 8. Kubernetes Deployment

The application includes complete production manifests under the [`k8s/`](./k8s) directory.

### Manifests Included
- `k8s/namespace.yaml`: Creates the dedicated `placement` namespace.
- `k8s/mysql-secret.example.yaml`: Database secret credentials.
- `k8s/mysql.yaml`: PersistentVolumeClaim (1Gi), MySQL Deployment, and ClusterIP Service.
- `k8s/app-deployment.yaml`: Spring Boot application deployment (2 replicas, liveness & readiness health probes).
- `k8s/app-service.yaml`: NodePort Service mapping port `8000` to NodePort `30080`.

### Local Deployment Steps (Minikube or Docker Desktop Kubernetes)
1. **Enable Kubernetes** in Docker Desktop settings: **Settings → Kubernetes → Enable Kubernetes → Apply & Restart**.
2. **Deploy MySQL and Secrets:**
```powershell
# Create namespace
kubectl apply -f k8s/namespace.yaml

# Create Secret (copy from example)
kubectl apply -f k8s/mysql-secret.example.yaml

# Deploy MySQL
kubectl apply -f k8s/mysql.yaml

# Wait until MySQL is ready
kubectl rollout status deployment/mysql -n placement --timeout=120s
```

3. **Deploy the Student Placement Application:**
```powershell
# If using a local image, tag it:
docker tag student-placement:latest rishitha2006/student-placement:latest

# Apply Deployment (replace placeholder if needed)
(Get-Content k8s/app-deployment.yaml) -replace 'IMAGE_PLACEHOLDER', 'student-placement:latest' | kubectl apply -n placement -f -

# Apply Service
kubectl apply -f k8s/app-service.yaml

# Wait for application rollout
kubectl rollout status deployment/student-placement -n placement --timeout=180s
```

4. **Verify Running Resources:**
```powershell
kubectl get pods,svc,pvc -n placement
```

5. **Access Application:**
- In browser, open: `http://localhost:30080` (or `minikube service student-placement -n placement` if using Minikube).

---

## 9. Screenshots Checklist for College Case Study Report

Capture and include the following screenshots in your project documentation:

| # | Screenshot Title | Where / How to Capture | Key Elements to Highlight |
| :-: | :--- | :--- | :--- |
| **1** | **Application Home Page** | Browser at `http://localhost:8000` | CampusConnect header, hero section, job listings (Google, Microsoft) |
| **2** | **Add Job Posting Form** | Browser at `http://localhost:8000/jobs/new` | Job title, company, location, eligibility fields |
| **3** | **Student Application Submission** | Browser at `http://localhost:8000` | Filling in Student Name and Email under a job card |
| **4** | **Applications Dashboard** | Browser at `http://localhost:8000/applications` | Table displaying applicant name, role, company, and status dropdown |
| **5** | **GitHub Repository** | GitHub web interface | Commits history, repository structure, `Jenkinsfile` and `k8s/` |
| **6** | **Maven Build & Test Success** | Terminal / PowerShell | `mvn test` output showing `12/12 Tests Run: 0 Failures: BUILD SUCCESS` |
| **7** | **Jenkins Stage View** | Jenkins UI `http://localhost:9090/job/student-placement-pipeline/` | Green pipeline stages (Checkout, Compile & Test, Package, Docker Build) |
| **8** | **Jenkins Test Result Trend** | Jenkins build page → **Test Result** | 12 tests passed graph and Surefire XML report summary |
| **9** | **Docker Desktop / CLI** | Docker Desktop GUI or `docker images` in terminal | Tagged `student-placement` image and running container containers |
| **10**| **Kubernetes Pods & Services** | PowerShell: `kubectl get pods,svc -n placement` | 2 app pods Running (1/1), 1 mysql pod Running (1/1), NodePort `30080` |
| **11**| **App Running on Kubernetes** | Browser at `http://localhost:30080` | Placement portal served via Kubernetes NodePort |

---

## 10. Troubleshooting Guide

| Issue | Cause | Solution |
| :--- | :--- | :--- |
| `java.lang.UnsupportedClassVersionError` | JDK mismatch (e.g. running Java 21 code on Java 17 runtime) | Ensure `$env:JAVA_HOME` points to `C:\Program Files\Java\jdk-21.0.12` and run `java -version`. |
| `Failed to connect to MySQL on localhost:3306` | MySQL container is not started or port conflict | Run `docker compose up -d mysql` and verify with `docker ps`. |
| `failed to connect to docker API` | Docker Desktop is not running on Windows | Launch **Docker Desktop** from the Start menu and wait for the whale icon to turn steady green. |
| `Jenkins tool 'jdk21' not found` | Tool name in Jenkins does not match `Jenkinsfile` | Go to **Manage Jenkins → Tools** and name the JDK `jdk21` or comment out the `tools` section in `Jenkinsfile` if using system PATH. |
| `k8s pods stuck in Pending` | No StorageClass or insufficient resources | In Docker Desktop, ensure Kubernetes is enabled and cluster has started. For PVC, check `kubectl get sc`. |
| `EPERM: operation not permitted / clean failed` | Windows OneDrive file locking on `target/` | Run `Remove-Item -Recurse -Force target` in PowerShell or run `mvn package` without `clean`. |
