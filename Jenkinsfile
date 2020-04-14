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
def master_tag
def build_hash
def container_id_build
def container_id_stage

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
                    sh '''
                        old_container=`docker ps -a | grep '''+env.BRANCH_NAME+''' | awk '{print $1}'`
                        if [ ! -z "$old_container" ] ; then docker rm -f $old_container ; fi
                    '''
                }
            }
        }
        stage('Descendant of master?') {
            when{
                branch 'ready/*'
            }
            steps {
                script {
                    sh '''
                        git --version
                        git rev-parse --is-inside-work-tree
                        git config remote.origin.url http://devopser:f2qskL4Zd6@192.168.11.10/gitlab/devopser/devopscalculator.git
                        git fetch --progress --prune http://192.168.11.10/gitlab/devopser/devopscalculator +refs/heads/*:refs/remotes/origin/*
                        git checkout -f origin/master
                        git checkout -B master origin/master
                        git branch -a -v --no-abbrev
                    '''
                    master_hash = sh(script: 'git rev-parse origin/master', returnStdout: true).trim()
                    master_tag = sh(script: 'git describe --tags --always $(git rev-list --tags --max-count=1)', returnStdout: true).trim()
                    sh '''
                        echo '''+master_tag+'''
                        git merge --squash --ff-only origin/'''+env.BRANCH_NAME+'''
                        git add -A
                        git commit --no-edit
                    '''
                    build_hash = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
                }
            }
        }
        stage('Commit stage') {
            when{
                branch 'ready/*'
            }
            steps {
                echo 'Building...'
                script {
                    label = env.BRANCH_NAME + "/env-build"
                    sh '''
                        cp /ssh/id_rsa ./environments/dev/
                        cp /ssh/id_rsa.pub ./environments/dev/
                        chmod 700 ./environments/dev/id_rsa
                        docker build -t '''+label+''' ./environments/dev
                    '''
                    container_id_build = sh(script: 'docker run -d -v data:/temp/ -p 15000-15999:8080 '+label, returnStdout: true).trim()
                    container_ip = sh(script: 'docker inspect -f "{{ .NetworkSettings.IPAddress }}" '+container_id_build, returnStdout: true).trim()
                    container_port = sh(script: '''docker inspect -f '{{ (index (index .NetworkSettings.Ports "8080/tcp") 0).HostPort }}' '''+container_id_build, returnStdout: true).trim()
                    echo container_id_build
                    echo container_ip
                    echo container_port
                    
                    sh '''
                        export ANSIBLE_HOST_KEY_CHECKING=False
                        ansible-playbook --private-key ./environments/dev/id_rsa -u root -i '''+container_ip+''', ./environments/dev/playbook.yml
                        
                        docker cp ./mvnw '''+container_id_build+''':tmp
                        docker cp ./.mvn/ '''+container_id_build+''':tmp
                        docker cp ./pom.xml '''+container_id_build+''':tmp
                        docker cp ./src/ '''+container_id_build+''':tmp
                        docker exec '''+container_id_build+''' bash -c 'cd /tmp/ ; ./mvnw clean package ; cp target/calculator-web-*.war ROOT.war'
                        docker container cp '''+container_id_build+''':/tmp/ROOT.war ./ROOT.war
                    '''
                }
            }
        }
        stage('Acceptance testing') {
            when{
                branch 'ready/*'
            }
            steps {
                echo 'Testing...'
                script {
                    sh '''
                        docker exec '''+container_id_build+''' bash -c 'cd /tmp/ ; ./mvnw failsafe:integration-test -DskipTests=false'
                    '''
                }
            }
        }
        stage('Deploy to stage') {
            when{
                branch 'ready/*'
            }
            steps {
                echo 'Deploying to stage...'
                script {
                    label = env.BRANCH_NAME + "/env-stage"
                    sh '''
                        cp /ssh/id_rsa ./environments/prod/
                        cp /ssh/id_rsa.pub ./environments/prod/
                        chmod 700 ./environments/prod/id_rsa
                        
                        docker build -t '''+label+''' ./environments/prod
                    '''
                    container_id_stage = sh(script: 'docker run -d -v data:/temp/ -p 15000-15999:8080 '+label, returnStdout: true).trim()
                    container_ip = sh(script: 'docker inspect -f "{{ .NetworkSettings.IPAddress }}" '+container_id_stage, returnStdout: true).trim()
                    container_port = sh(script: '''docker inspect -f '{{ (index (index .NetworkSettings.Ports "8080/tcp") 0).HostPort }}' '''+container_id_stage, returnStdout: true).trim()
                    echo container_id_stage
                    echo container_ip
                    echo container_port
                    
                    sh '''
                        export ANSIBLE_HOST_KEY_CHECKING=False
                        ansible-playbook --private-key ./environments/prod/id_rsa -u root -i '''+container_ip+''', ./environments/prod/playbook.yml
                        docker cp ./ROOT.war '''+container_id_stage+''':/var/lib/tomcat8/webapps/ROOT.war
                    '''
                }
            }
        }
        stage('Push to master') {
            when{
                branch 'ready/*'
            }
            steps {
                input "Do manual testing if needed.\nhttp://localhost:"+container_port+"\nRelease and push to master?"
                sh '''#!/bin/bash
                    git fetch --no-tags --progress http://192.168.11.10/gitlab/devopser/devopscalculator +refs/heads/master:refs/remotes/origin/master 
                    master_hash_new=`git rev-parse origin/master`
                    if [ '''+master_hash+''' != $master_hash_new ] ; then echo "Someone merged to master. Pull master changes and try again" && exit 128 ; fi
                    
                    # add tags
                    VERSION='''+master_tag+'''
                    echo "Old tag $VERSION"
                    if [[ $VERSION != v*.*.* ]] ; then VERSION=v0.0.0 ; fi
                    echo "Old tag? $VERSION"
                    VERSION="${VERSION#v}"; MAJOR="${VERSION%%.*}";
                    VERSION="${VERSION#*.}"; MINOR="${VERSION%%.*}"
                    VERSION="${VERSION#*.}"; PATCH="${VERSION%%.*}";
                    PATCH=$((PATCH+1))
                    NEW_TAG="v$MAJOR.$MINOR.$PATCH"
                    echo "New tag $NEW_TAG"
                    git tag -a -m "Merged from '''+env.BRANCH_NAME+''' feature branch" $NEW_TAG
                    
                    echo "Pushing .war to Artifactory"
                    curl -u admin:sHMHY6iZjh -X PUT "http://localhost:8022/artifactory/generic-local/$NEW_TAG/ROOT.war" -T ./ROOT.war
                    
                    git push --follow-tags origin master
                    git push origin --delete '''+env.BRANCH_NAME+'''
                    git reset --hard
                    git clean -fdx
                '''
            }
        }
        stage('Deploy to prod') {
            when{
                branch 'master'
            }
            steps {
                echo 'Deploying to prod...'
                script {
                    label = "master/env-prod"
                    sh '''
                        git fetch --tags
                        tag=`git describe --abbrev=0`
                        echo Tag = $tag
                        curl -u admin:sHMHY6iZjh -X GET "http://localhost:8022/artifactory/generic-local/$tag/ROOT.war" -o ./ROOT.war
                        
                        cp /ssh/id_rsa ./environments/prod/
                        cp /ssh/id_rsa.pub ./environments/prod/
                        chmod 700 ./environments/prod/id_rsa
                        docker build -t '''+label+''' ./environments/prod
                    '''
                    
                    container_id_prod = sh(script: 'docker run -d -v data:/temp/ -p 15900:8080 '+label, returnStdout: true).trim()
                    container_ip = sh(script: 'docker inspect -f "{{ .NetworkSettings.IPAddress }}" '+container_id_prod, returnStdout: true).trim()
                    echo container_id_prod
                    echo container_ip
                    
                    sh '''
                        export ANSIBLE_HOST_KEY_CHECKING=False
                        ansible-playbook --private-key ./environments/prod/id_rsa -u root -i '''+container_ip+''', ./environments/prod/playbook.yml
                        docker cp ./ROOT.war '''+container_id_prod+''':/var/lib/tomcat8/webapps/ROOT.war
                    '''
                    echo "Prod server: http://localhost:15900"
                }
            }
        }
    }

    post {
        always {
            sh '''
                running_containers=`docker ps -a | grep ready/ | awk '{print $1}'`
                if [ ! -z "$running_containers" ] ; then docker rm -f $running_containers ; fi
            '''
        }
    }
}
