pipeline {
  agent none
  options {
    copyArtifactPermission('*');
    skipDefaultCheckout true
  }
  environment { plugin = "EventEngine" }
  triggers {
    upstream '/Runsafe/WorldGuardBridge/master'
    pollSCM '@monthly'
  }
  stages {
    stage('Ant Build') {
      agent { label 'ant' }
      tools {
        ant 'Default'
        jdk 'Default'
      }
      steps {
        dir('EventEngine') {
          sh 'cp -a ../lua .'
	}
        buildPluginWithAnt env.plugin, 'WorldGuardBridge', 'build/jar/*.jar,EventEngine'
      }
    }
    stage('Deploy to test server') {
      agent { label 'server4' }
      steps {
        installPlugin "${env.plugin}.tar"
        buildReport env.plugin, 'Deployed to test server'
      }
    }
    stage('Ask for promotion') { steps { askForPromotion() } }
  }
  post { failure { buildReport env.plugin, 'Build failed' } }
}