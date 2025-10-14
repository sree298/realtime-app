pipeline {
    agent any
    tools {
        maven "Maven3"
    }

    environment {
        IMAGE_NAME = "srinu298/realtime-app"
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

        stage('Maven Build WAR') {
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
                    // Create a dynamic image tag (using Jenkins build number)
                    def TAG = "${env.BUILD_NUMBER}"
                    def IMAGE = "${IMAGE_NAME}:${TAG}"

                    withDockerRegistry(credentialsId: 'docker', toolName: 'docker') {

                        // Build new image
                        sh "docker build -t ${IMAGE} ."

                        // Push versioned image
                        sh "docker push ${IMAGE}"

                        // Tag and push 'latest'
                        sh "docker tag ${IMAGE} ${IMAGE_NAME}:latest"
                        sh "docker push ${IMAGE_NAME}:latest"
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Final cleanup: removing dangling Docker layers..."
            sh "docker image prune -f || true"
        }
    }
}

