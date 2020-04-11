def master_hash

pipeline {
    agent any
    
    stages {
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
                        git merge --squash --ff-only origin/ready/123
                        git add -A
                        git commit --no-edit
                    '''
                }
            }
        }
        stage('Build') {
            steps {
                echo 'Building..'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
        stage('Push to master') {
            when{
                branch 'ready/*'
            }
            steps {
                input "Do manual testing if needed. Release and push to master?"
                
                sh '''
                    git fetch --no-tags --progress http://192.168.11.10/gitlab/devopser/devopscalculator +refs/heads/master:refs/remotes/origin/master 
                    master_hash_new=`git rev-parse origin/master`
                    if [ '''+master_hash+''' != $master_hash_new ] ; then echo "Someone merged to master. Pull master changes and try again" && exit 128 ; fi
                    git push origin master
                    git push origin --delete ready/123
                    git reset --hard
                    git clean -fdx
                '''
            }
        }
    }
}
