def call(stages){
  def stagesList = stages.split(";")
  def listStagesOrder = [
      'git_diff': 'stageGitDiff',
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
  stageGitDiff();
  stageDownloadNexus();
  stageRunJar();
  stageCurlJar();
}

def stageGitDiff(){
    env.DESCRIPTION_STAGE = "Paso 0: Git Diff"
    stage("${env.DESCRIPTION_STAGE}"){
      env.STAGE = "git_diff - ${env.DESCRIPTION_STAGE}"
      sh "echo  ${env.STAGE}"
      sh "git diff origin/main"
      }

}

def stageDownloadNexus(){
    env.DESCRIPTION_STAGE = "Paso 1: Descargar Nexus"
    stage("${env.DESCRIPTION_STAGE}"){
      env.STAGE = "download_nexus - ${env.DESCRIPTION_STAGE}"
      sh "echo  ${env.STAGE}"
      sh "curl -X GET -u $NEXUS_USER:$NEXUS_PASSWORD 'http://nexus:8081/repository/devops-laboratorio/com/devopsusach2020/DevOpsUsach2020/${env.POM_VERSION}/DevOpsUsach2020-${env.POM_VERSION}.jar' -O"
    }
}

def stageRunJar(){
    env.DESCRIPTION_STAGE = "Paso 2: Correr Jar"
    stage("${env.DESCRIPTION_STAGE}"){
      env.STAGE = "run_jar - ${env.DESCRIPTION_STAGE}"
      sh "echo  ${env.STAGE}"
      sh "nohup java -jar DevOpsUsach2020-${env.POM_VERSION}.jar & >/dev/null"
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