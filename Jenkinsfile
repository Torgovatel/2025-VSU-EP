pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven'
        JAVA_HOME = tool 'JDK21'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                bat 'mvn clean compile test-compile -Dmaven.compiler.release=17'
            }
        }

        stage('Run Tests (feature/* only)') {
            when {
                expression {
                    return env.BRANCH_NAME && env.BRANCH_NAME.startsWith("feature/")
                }
            }
            steps {
                bat 'mvn test'
            }
        }

        stage('Static Analysis (develop only)') {
            when {
                branch 'develop'
            }
            steps {
                bat 'mvn checkstyle:check'
            }
        }

        stage('Run Coverage') {
            steps {
                bat 'mvn jacoco:prepare-agent test jacoco:report'
            }
        }

        stage('Check Coverage Threshold') {
            steps {
                script {
                    def jacocoReportPath = 'aggregator/target/site/jacoco/jacoco.xml'
                    if (!fileExists(jacocoReportPath)) {
                        echo "Coverage report not found: ${jacocoReportPath}. Skipping coverage check."
                        return
                    }

                    def coverageFile = readFile(jacocoReportPath)
                    def missed = coverageFile =~ /<counter type="LINE" missed="(\d+)"/
                    def covered = coverageFile =~ /covered="(\d+)"/

                    if (!missed || !covered) {
                        error "Cannot read coverage data"
                    }

                    def missedLines = missed[0][1] as int
                    def coveredLines = covered[0][1] as int
                    def total = missedLines + coveredLines
                    def percent = (coveredLines * 100) / total

                    echo "Coverage: ${percent}%"
                    if (percent < 80) {
                        error "Code coverage ${percent}% is below threshold"
                    }
                }
            }
        }

        stage('Install Artifacts') {
            steps {
                bat 'mvn install'
            }
        }

        stage('Publish Artifacts') {
            steps {
                bat 'if not exist C:\\tmp\\artifacts mkdir C:\\tmp\\artifacts'
                bat 'copy aggregator\\target\\*.jar C:\\tmp\\artifacts\\'
                echo 'Artifacts copied to C:\\tmp\\artifacts\\'
            }
        }

        stage('Run App') {
            steps {
                echo 'Starting Spring Boot application...'
                bat '''
                    start /B java -jar aggregator\\target\\aggregator-0.0.1-SNAPSHOT.jar
                    exit 0
                '''
            }
        }
    }

    post {
        always {
            script {
                def reportsPath = 'aggregator/target/surefire-reports'
                if (fileExists(reportsPath)) {
                    junit 'aggregator/target/surefire-reports/*.xml'
                } else {
                    echo 'No test reports found to publish.'
                }
            }
        }
        failure {
            echo 'Pipeline failed.'
        }
        success {
            echo 'Pipeline succeeded.'
        }
    }
}