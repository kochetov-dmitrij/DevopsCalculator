@NonCPS
def cancelPreviousBuilds() {
    def jobName = env.JOB_NAME
    def buildNumber = env.BUILD_NUMBER.toInteger()
    def currentJob = Jenkins.instance.getItemByFullName(jobName)

    /* Iterating over the builds for specific job */
    for (def build : currentJob.builds) {
        def exec = build.getExecutor()
        def sameBranch = build.parent.toString().endsWith(currentJob.name + "]")
        /* If there is a build that is currently running and it's not current build */
        if (sameBranch && build.isBuilding() && build.number.toInteger() != buildNumber && exec != null) {
            /* Then stop it */
            exec.interrupt(
                    Result.ABORTED,
                    new CauseOfInterruption.UserInterruption("Aborted by #" + buildNumber)
                )
            println("Aborted previously running build #${build.number}")  
        }
    }
}

def master_hash

pipeline {
    agent any
    
    stages {
        stage('Cancel old builds') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'master' && env.BUILD_NUMBER.toInteger() == 1) {
                        currentBuild.result = "NOT_BUILT"
                        error("First master build, nothing to release")
                    }
                    cancelPreviousBuilds()
                    if (env.BRANCH_NAME != 'master' && sh(script: 'git rev-parse origin/'+env.BRANCH_NAME, returnStdout: true) == sh(script: 'git rev-parse origin/master', returnStdout: true)) {
                        currentBuild.result = "NOT_BUILT"
                        error("The branch matches master")
                    }
                }
            }
        }
        stage('Pull from master') {
            when{
                branch 'ready/*'
            }
            steps {
                script {
                    sh '''
                        git --version
                        git rev-parse --is-inside-work-tree
                        git config remote.origin.url http://devopser:f2qskL4Zd6@192.168.11.10/gitlab/devopser/devopscalculator.git
                        git fetch --no-tags --progress --prune http://192.168.11.10/gitlab/devopser/devopscalculator +refs/heads/*:refs/remotes/origin/*
                        git checkout -f origin/master
                        git checkout -B master origin/master
                        git branch -a -v --no-abbrev
                    '''
                    master_hash = sh(script: 'git rev-parse origin/master', returnStdout: true).trim()
                    sh '''
                        git merge --squash --ff-only origin/'''+env.BRANCH_NAME+'''
                        git add -A
                        git commit --no-edit
                    '''
                }
            }
        }
        stage('Build') {
            steps {
                echo 'Building..'
                script {
                    label = env.BRANCH_NAME + "/env-build"
                    sh '''
                        cp ~jenkins/.ssh/id_rsa.pub ./environments/dev/
                        docker build -t '''+label+''' ./environments/dev
                    '''
                    container_id = sh(script: 'docker run -d -v data:/temp/ -p 0:8080 '+label, returnStdout: true).trim()
                    container_ip = sh(script: 'docker inspect -f "{{ .NetworkSettings.IPAddress }}" '+container_id, returnStdout: true).trim()
                    container_port = sh(script: '''docker inspect -f '{{ (index (index .NetworkSettings.Ports "8080/tcp") 0).HostPort }}' '''+container_id, returnStdout: true).trim()
                    echo container_id
                    echo container_ip
                    echo container_port
                    input "ok?"
                    sh '''
                        ansible-playbook --private-key ~jenkins/.ssh/id_rsa -i '''+container_ip+''', ./environments/dev/playbook.yml
                    '''
                }
                
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy to stage') {
            steps {
                echo 'Deploying....'
            }
        }
        stage('Push to master') {
            when{
                branch 'ready/*'
            }
            steps {
                input "Do manual testing if needed.\nRelease and push to master?"
                
                sh '''
                    git fetch --no-tags --progress http://192.168.11.10/gitlab/devopser/devopscalculator +refs/heads/master:refs/remotes/origin/master 
                    master_hash_new=`git rev-parse origin/master`
                    if [ '''+master_hash+''' != $master_hash_new ] ; then echo "Someone merged to master. Pull master changes and try again" && exit 128 ; fi
                    git push origin master
                    git push origin --delete '''+env.BRANCH_NAME+'''
                    git reset --hard
                    git clean -fdx
                '''
            }
        }
    }
    
    post {
        always {
            sh '''
                running_containers=`docker ps -a | grep '''+env.BRANCH_NAME+'''/* | awk '{print $1}'`
                echo $running_containers
                if [ ! -z "$running_containers" ] ; then
                    docker stop $running_containers
                    docker rm $running_containers
                fi
            '''
        }
    }
}
