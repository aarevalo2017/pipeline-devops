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
                         if(params.compileTool == 'maven') {
                            //compilar maven
                            //def executor = load "maven.groovy"
                            //executor.call()
                            maven.call(params.stages);
                        } else {
                            //compilar gradle
                            //def executor = load "gradle.groovy"
                            //executor.call()
                            gradle.call(params.stages)
                        }                    }
                }
            }
        }
    }
}

return this;