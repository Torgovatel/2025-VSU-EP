pipeline {
    agent any

    // Определяем переменные окружения: пути к Maven и JDK
    environment {
        MAVEN_HOME = tool 'Maven'           // Указывает Jenkins-у использовать установленный Maven
        JAVA_HOME = tool 'JDK17'            // Указывает Jenkins-у использовать JDK 17
        PATH = "${JAVA_HOME}/bin:${env.PATH}" // Добавляем JDK в PATH
    }

    stages {
        // Этап 1: Получение исходного кода из репозитория
        stage('Checkout') {
            steps {
                checkout scm // Стандартная команда получения кода из Git, подключенного к Jenkins job
            }
        }

        // Этап 2: Компиляция исходного кода и тестов
        stage('Compile') {
            steps {
                sh 'mvn clean compile test-compile' // Очищаем проект, компилируем основной и тестовый код
            }
        }

        // Этап 3: Запуск юнит-тестов (только для веток вида feature/...)
        stage('Run Tests (feature/* only)') {
            when {
                expression { return env.BRANCH_NAME.startsWith("feature/") } // Условие: только ветки feature/...
            }
            steps {
                sh 'mvn test' // Запуск тестов
            }
        }

        // Этап 4: Статический анализ кода (только для ветки develop)
        stage('Static Analysis (develop only)') {
            when {
                branch 'develop' // Условие: только ветка develop
            }
            steps {
                sh 'mvn checkstyle:check' // Запуск Checkstyle (можно заменить на pmd:check или spotbugs:check)
            }
        }

        // Этап 5: Генерация отчёта покрытия тестами (Jacoco)
        stage('Run Coverage') {
            steps {
                sh 'mvn jacoco:prepare-agent test jacoco:report' // Подключаем Jacoco, запускаем тесты и формируем отчёт
            }
        }

        // Этап 6: Проверка соответствия тестового покрытия заданному порогу (например, 80%)
        stage('Check Coverage Threshold') {
            steps {
                script {
                    def coverageFile = readFile('target/site/jacoco/jacoco.xml') // Чтение отчёта Jacoco
                    def missed = coverageFile =~ /<counter type="LINE" missed="(\d+)"/ // Извлечение пропущенных строк
                    def covered = coverageFile =~ /covered="(\d+)"/ // Извлечение покрытых строк

                    if (!missed || !covered) {
                        error "Cannot read coverage data" // Ошибка, если отчёт некорректен
                    }

                    def missedLines = missed[0][1] as int
                    def coveredLines = covered[0][1] as int
                    def total = missedLines + coveredLines
                    def percent = (coveredLines * 100) / total

                    echo "Coverage: ${percent}%" // Печать процента покрытия
                    if (percent < 80) {
                        error "Code coverage ${percent}% is below threshold" // Ошибка, если покрытие ниже 80%
                    }
                }
            }
        }

        // Этап 7: Установка артефактов (например, JAR-файла) в локальный репозиторий Maven
        stage('Install Artifacts') {
            steps {
                sh 'mvn install' // Устанавливает артефакты в локальный репозиторий (~/.m2)
            }
        }

        // Этап 8: Публикация артефактов (копирование JAR-файлов в целевую директорию)
        stage('Publish Artifacts') {
            steps {
                sh 'mkdir -p /tmp/artifacts && cp target/*.jar /tmp/artifacts/' // Копируем скомпилированные JAR-файлы
                echo 'Artifacts copied to /tmp/artifacts/'
            }
        }
    }

    // Пост-действия после завершения пайплайна
    post {
        always {
            // Загружаем результаты тестов в Jenkins (для отображения в UI)
            junit '**/target/surefire-reports/*.xml'
        }
        failure {
            echo 'Pipeline failed.' // Пишем в консоль, если пайплайн упал
        }
        success {
            echo 'Pipeline succeeded.' // Пишем в консоль, если пайплайн прошёл успешно
        }
    }
}
