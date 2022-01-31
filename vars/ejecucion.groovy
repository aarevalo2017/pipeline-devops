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
        }
        stages {
            stage("Pipeline"){
                steps {
                    script{
                        def ejecucion = load "${params.compileTool}.groovy"
                        ejecucion.call()
                    }
                }
            }
        }
    }
}

return this;