pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven'
        JAVA_HOME = tool 'JDK21'
        PATH = "${JAVA_HOME}\\bin;${MAVEN_HOME}\\bin;${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Detect Branch') {
            steps {
                script {
                    if (!env.BRANCH_NAME) {
                        def branch = bat(
                            script: 'git name-rev --name-only HEAD',
                            returnStdout: true
                        ).trim()
                        env.BRANCH_NAME = branch
                    }
                    echo "Detected branch: ${env.BRANCH_NAME}"
                }
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
                    return env.BRANCH_NAME?.startsWith("feature/")
                }
            }
            steps {
                bat 'mvn test'
            }
        }

        stage('Static Analysis (develop only)') {
            when {
                expression {
                    return env.BRANCH_NAME == 'develop'
                }
            }
            steps {
                bat 'mvn checkstyle:check'
            }
        }

        stage('Run Coverage') {
            steps {
                bat 'mvn jacoco:prepare-agent test jacoco:report'
            }
            post {
                always {
                    jacoco(execPattern: 'core/target/jacoco.exec')
                }
                failure {
                    echo 'Сборка провалена!'
                }
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
                    def missed = coverageFile =~ /<counter type="LINE" missed="(\\d+)"/
                    def covered = coverageFile =~ /covered="(\\d+)"/

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
