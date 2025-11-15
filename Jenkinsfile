pipeline {
    agent any
    
    tools {
        jdk 'JDK-17'
        gradle 'Gradle-8.5'
    }
    
    environment {
        DOCKER_REGISTRY = 'your-registry'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }
        
        stage('Test') {
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }
        
        stage('Code Quality') {
            steps {
                sh './gradlew check'
            }
        }
        
        stage('Build Docker Images') {
            steps {
                script {
                    def services = ['eureka-server', 'config-server', 'api-gateway', 
                                   'user-service', 'product-service', 'order-service', 
                                   'inventory-service', 'payment-service']
                    
                    services.each { service ->
                        sh """
                            docker build -t ${DOCKER_REGISTRY}/${service}:${IMAGE_TAG} \
                                -f ${service}/Dockerfile .
                        """
                    }
                }
            }
        }
        
        stage('Push Docker Images') {
            steps {
                script {
                    def services = ['eureka-server', 'config-server', 'api-gateway', 
                                   'user-service', 'product-service', 'order-service', 
                                   'inventory-service', 'payment-service']
                    
                    services.each { service ->
                        sh """
                            docker push ${DOCKER_REGISTRY}/${service}:${IMAGE_TAG}
                            docker tag ${DOCKER_REGISTRY}/${service}:${IMAGE_TAG} \
                                ${DOCKER_REGISTRY}/${service}:latest
                            docker push ${DOCKER_REGISTRY}/${service}:latest
                        """
                    }
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            when {
                branch 'main'
            }
            steps {
                script {
                    sh """
                        kubectl set image deployment/user-service user-service=\
                            ${DOCKER_REGISTRY}/user-service:${IMAGE_TAG} -n ecommerce
                        kubectl set image deployment/product-service product-service=\
                            ${DOCKER_REGISTRY}/product-service:${IMAGE_TAG} -n ecommerce
                        kubectl set image deployment/order-service order-service=\
                            ${DOCKER_REGISTRY}/order-service:${IMAGE_TAG} -n ecommerce
                        kubectl set image deployment/inventory-service inventory-service=\
                            ${DOCKER_REGISTRY}/inventory-service:${IMAGE_TAG} -n ecommerce
                        kubectl set image deployment/payment-service payment-service=\
                            ${DOCKER_REGISTRY}/payment-service:${IMAGE_TAG} -n ecommerce
                    """
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}

