pipeline {
    agent any

    tools {
        maven "Maven3"
    }

    environment {
        // 🔧 General configuration
        IMAGE_NAME = "srinu298/realtime-app"

        // 🔧 SonarQube configuration (no hardcoding)
        SONARQUBE_SERVER = "SonarQube"
        SONAR_PROJECT_KEY = "realtime-app"
        SONAR_PROJECT_NAME = "realtime-app"
        SONAR_AUTH_TOKEN = credentials('Sonar-token')
    }

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout from Git') {
            steps {
                git branch: 'main', url: 'https://github.com/sree298/realtime-app.git'
            }
        }

        stage('Build & SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_SERVER}") {
                    sh '''
                        mvn clean verify sonar:sonar \
                          -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                          -Dsonar.projectName=${SONAR_PROJECT_NAME} \
                          -Dsonar.host.url=$SONAR_HOST_URL \
                          -Dsonar.login=$SONAR_AUTH_TOKEN
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'Sonar-token'
                }
            }
        }

        stage('Package WAR') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Remove Old Docker Images') {
            steps {
                script {
                    echo "Removing old Docker images for ${IMAGE_NAME}..."
                    sh """
                        docker images ${IMAGE_NAME} -q | xargs -r docker rmi -f || true
                        docker image prune -f || true
                    """
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def TAG = "${env.BUILD_NUMBER}"
                    def IMAGE = "${IMAGE_NAME}:${TAG}"

                    withDockerRegistry(credentialsId: 'docker', toolName: 'docker') {

                        sh "docker build -t ${IMAGE} ."
                        sh "docker push ${IMAGE}"

                        sh "docker tag ${IMAGE} ${IMAGE_NAME}:latest"
                        sh "docker push ${IMAGE_NAME}:latest"
                    }
                }
            }
        }

        // 🔒 TRIVY filesystem scan
        stage('TRIVY FS Scan') {
            steps {
                sh "trivy fs . > trivyfs.txt || true"
            }
        }

        // 🔒 TRIVY image scan
        stage('TRIVY Image Scan') {
            steps {
                script {
                    def IMAGE = "${IMAGE_NAME}:latest"
                    sh "trivy image ${IMAGE} > trivyimage.txt || true"
                }
            }
        }

    }

    post {
        always {
            echo "Final cleanup: removing dangling Docker layers..."
            sh "docker image prune -f || true"

            script {
            def buildStatus = currentBuild.currentResult
            def buildUser = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')[0]?.userId ?: 'Github User'
            
            emailext (
                subject: "Pipeline ${buildStatus}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    <p>This is a Jenkins HOTSTAR CICD pipeline status.</p>
                    <p>Project: ${env.JOB_NAME}</p>
                    <p>Build Number: ${env.BUILD_NUMBER}</p>
                    <p>Build Status: ${buildStatus}</p>
                    <p>Started by: ${buildUser}</p>
                    <p>Build URL: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                """,
                to: 'srinivasarao4255@gmail.com',
                from: 'srinivasarao4255@gmail.com',
                replyTo: 'srinivasarao4255@gmail.com',
                mimeType: 'text/html',
                attachmentsPattern: 'trivyfs.txt,trivyimage.txt'
            )
           }

            
        }
    }
}
