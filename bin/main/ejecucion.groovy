/*

	forma de invocación de método call:

	def ejecucion = load 'script.groovy'
	ejecucion.call()

*/

def call(){
    pipeline {
        agent any
        environment {
            NEXUS_USERNAME     = credentials('nexus-username')
            NEXUS_PASSWORD     = credentials('nexus-password')
        }
        parameters {
            choice(
                name:'compileTool',
                choices: ['maven', 'gradle'],
                description: 'Seleccione herramienta de compilacion'
            )
            text description: 'Enviar los stages separados por ";"... Vacío si desea ejecutar todos los stages', name: 'stages'
        }
        stages {
            stage("Pipeline"){
                steps {
                    script{
                        echo "The branch name is ${env.BRANCH_NAME}"
                        echo "The build number is ${env.BUILD_NUMBER}"
                        echo "You can also use \${BUILD_NUMBER} -> ${BUILD_NUMBER}"
                        sh 'echo "I can access $BUILD_NUMBER in shell command as well."'
                        // "${params.compileTool}.call(${params.stages})"
                        if(params.compileTool == 'maven') {
                            maven.call(params.stages)
                        } else {
                            gradle.call(params.stages)
                        }
                    }
                }
            }
        }
    }
}

return this;