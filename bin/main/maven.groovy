def call(stages){
  def stagesList = stages.split(";")
  def listStagesOrder = [
       'compile': 'stageCompile',
        'test': 'stageTest',
        'build': 'stageBuild',
        'sonar': 'stageSonar',
        'upload_nexus': 'stageUploadNexus',
      'download_nexus': 'stageDownloadNexus',
      'run_jar': 'stageRunJar',
      'curl_jar': 'stageCurlJar',
  ]

  if (stages==""){
    executeAllStages()
  }
  else {
    echo 'Stages a ejecutar :' + stages
    listStagesOrder.each { stageName, stageFunction ->
      stagesList.each{ stageToExecute ->//variable as param
        if(stageName.equals(stageToExecute)){
          echo 'Ejecutando ' + stageFunction
          "${stageFunction}"()
        }
      }
    }
  } 
}
return this;

def executeAllStages(){
  echo "Ejecutando todos los stages..."
    stageCompile()
    stageTest()
    stageBuild()
    stageSonar()
    stageUploadNexus()
  stageDownloadNexus()
    stageRunJar()
  stageCurlJar()
}
def stageCompile() {
    env.DESCRIPTION_STAGE = "Paso 1: Compilar"
    stage("${env.DESCRIPTION_STAGE}"){
        env.STAGE = "compile - ${env.DESCRIPTION_STAGE}"
        sh "echo  ${env.STAGE}"
        sh "mvn clean compile -e"
    }
}

def stageTest() {
    env.DESCRIPTION_STAGE = "Paso 2: Testear"
    stage("${env.DESCRIPTION_STAGE}"){
        env.STAGE = "test - ${env.DESCRIPTION_STAGE}"
        sh "echo  ${env.STAGE}"
        sh "mvn clean test -e"
    }
}

def stageBuild() {
    env.DESCRIPTION_STAGE = "Paso 3: Build jar"
    stage("${env.DESCRIPTION_STAGE}"){
        env.STAGE = "build - ${env.DESCRIPTION_STAGE}"
        sh "echo  ${env.STAGE}"
        sh "mvn clean package -e"
    }
}

def stageSonar() {
    env.DESCRIPTION_STAGE = "Paso 4: Análisis SonarQube"
    stage("${env.DESCRIPTION_STAGE}"){
        env.STAGE = "sonar - ${env.DESCRIPTION_STAGE}"
        repoName = env.GIT_URL.tokenize('/')[3].split("\\.")[0]
        println(repoName)
        withSonarQubeEnv('sonarqube') {
            sh "echo  ${env.STAGE}"
            def sonarName = "${repoName}-${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
            println(sonarName)
            sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=github-sonar -Dsonar.projectName=' + sonarName 
        }
    }
}

def stageUploadNexus() {
  env.DESCRIPTION_STAGE = "Paso 5: Subir Nexus"
  stage("${env.DESCRIPTION_STAGE}"){
        env.STAGE = "upload_nexus - ${env.DESCRIPTION_STAGE}"
        sh "echo  ${env.STAGE}"
        // nexusPublisher nexusInstanceId: 'nexus',
        // nexusRepositoryId: 'devops-usach-nexus',
        // packages: [
        //     [$class: 'MavenPackage',
        //         mavenAssetList: [
        //             [classifier: '',
        //             extension: 'jar',
        //             filePath: "build/DevOpsUsach2020-0.1.2.jar"
        //         ]
        //     ],
        //         mavenCoordinate: [
        //             artifactId: 'DevOpsUsach2020',
        //             groupId: 'com.devopsusach2020',
        //             packaging: 'jar',
        //             version: "0.1.2"
        //         ]
        //     ]
        // ]
    }
}
def stageDownloadNexus(){
    env.DESCRIPTION_STAGE = "Paso 1: Descargar Nexus"
    stage("${env.DESCRIPTION_STAGE}"){
      env.STAGE = "download_nexus - ${env.DESCRIPTION_STAGE}"
      sh "echo  ${env.STAGE}"
      sh "curl -X GET -u $NEXUS_USERNAME:$NEXUS_PASSWORD 'http://nexus:8081/repository/devops-laboratorio/com/devopsusach2020/DevOpsUsach2020/0.1.2/DevOpsUsach2020-0.1.2.jar' -O"
    }
}

def stageRunJar(){
    env.DESCRIPTION_STAGE = "Paso 2: Correr Jar"
    stage("${env.DESCRIPTION_STAGE}"){
      env.STAGE = "run_jar - ${env.DESCRIPTION_STAGE}"
      sh "echo  ${env.STAGE}"
      sh "nohup java -jar DevOpsUsach2020-0.1.2.jar & >/dev/null"
  }
}

def stageCurlJar(){
    env.DESCRIPTION_STAGE = "Paso 3: Testear Artefacto - Dormir 20sg"
    stage("${env.DESCRIPTION_STAGE}"){
      env.STAGE = "curl_jar - ${env.DESCRIPTION_STAGE}"
      sh "echo  ${env.STAGE}"
      sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
  }
}