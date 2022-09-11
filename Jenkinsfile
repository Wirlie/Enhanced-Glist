def statusBadge = addEmbeddableBadgeConfiguration(id: "glistStatusBadge", subject: "build", status: "running", color: "orange", animatedOverlayColor: "orange")

pipeline {

    agent any

    stages {
        stage('[ci skip] check') {
            steps {
                script {
                    env.CI_SKIP = 'false'
                    
                    if (sh(script: "git log -1 --pretty=%B | fgrep -ie '[skip ci]' -e '[ci skip]'", returnStatus: true) == 0) {
                        statusBadge.setStatus('skipped [ci skip]')
                        statusBadge.setColor('orange')
                        statusBadge.setAnimatedOverlayColor(null)

                        withCredentials([string(credentialsId: 'discord-webhook-github-log', variable: 'NEXUS_DISCORD_WEBHOOK')]) {
                            discordSend(
                                description: "Build **#" + env.BUILD_NUMBER + "** skipped `[ci skip]`.", 
                                footer: "Jenkins - Build Pipeline", 
                                link: env.BUILD_URL,
                                result: 'ABORTED', 
                                title: JOB_NAME, 
                                webhookURL: NEXUS_DISCORD_WEBHOOK,
                                showChangeset: true
                            )
                        }

                        currentBuild.result = 'NOT_BUILT'
                        env.CI_SKIP = 'true'
                    }
                }
            }
        }
        stage("Setup") {
            when { 
                environment name: 'CI_SKIP', value: 'false' 
            }
            steps {
                script {
                    withCredentials([string(credentialsId: 'discord-webhook-github-log', variable: 'NEXUS_DISCORD_WEBHOOK')]) {
                        discordSend(
                            description: "Build **#" + env.BUILD_NUMBER + "** started.", 
                            footer: "Jenkins - Build Pipeline", 
                            link: env.BUILD_URL,
                            result: 'ABORTED', 
                            title: JOB_NAME, 
                            webhookURL: NEXUS_DISCORD_WEBHOOK,
                            showChangeset: true
                        )
                    }

                    configFileProvider([configFile(fileId: '91da0cee-34a7-41a4-b605-2998257e24ff', variable: 'configFile')]) {
                        script {
                            sh 'mv $configFile ./local.properties' // move file to root
                            println('local.properties file moved to root directory')
                        }
                    }

                    sh 'chmod +x ./gradlew' // give execution permission to gradlew file

                    env.PUBLISH_PR_ID = 'none'
                    
                    println("Branch: " + env.GIT_BRANCH)
                    switch(env.GIT_BRANCH) {
                        case 'master':
                            env.PUBLISH_TO_NEXUS = 'true'
                            env.PUBLISH_SNAPSHOT = 'false'
                            println("Environment variables set for master branch")
                            break
                        case "develop":
                            env.PUBLISH_TO_NEXUS = 'true'
                            env.PUBLISH_SNAPSHOT = 'true'
                            env.ARTIFACT_PUBLISH_SNAPSHOT = 'true'
                            println("Environment variables set for develop branch")
                            break
                        default:
                            if (env.BRANCH_NAME.startsWith('PR-')) {
                                // Pull request, get version from build.gradle file
                                def baseVersion = sh(script: './gradlew properties | grep ^version: | awk \'{print  $2 }\' | tr -d \'\n\'', returnStdout: true)
                                // Publish to nexus and snapshot
                                env.PUBLISH_TO_NEXUS = 'true'
                                env.PUBLISH_SNAPSHOT = 'true'
                                env.PUBLISH_PR_ID = env.BRANCH_NAME.replace('PR-', '')
                                env.ARTIFACT_PUBLISH_SNAPSHOT = 'true'
                                // Version: "X.X.X-PR-X" example "1.1.10-PR-5"
                                env.ARTIFACT_VERSION = baseVersion + "-" + env.BRANCH_NAME
                                println("Pull request branch: " + env.GIT_BRANCH)
                            } else {
                                env.PUBLISH_TO_NEXUS = 'false'
                                env.PUBLISH_SNAPSHOT = 'false'
                                println("Branch " + env.GIT_BRANCH + " found, action: only build.")
                            }
                            break
                    }
                }
            }
        }
        stage('Project Compile') {
            when { 
                environment name: 'CI_SKIP', value: 'false' 
            }
            steps {
                script {
                    sh './gradlew clean build'
                }
            }
        }
    }
    
    post {
        success {
            script {
                if(env.CI_SKIP == 'false') {
                    statusBadge.setStatus('success')
                    statusBadge.setColor('brightgreen')
                    statusBadge.setAnimatedOverlayColor(null)
                    
                    withCredentials([string(credentialsId: 'discord-webhook-github-log', variable: 'NEXUS_DISCORD_WEBHOOK')]) {
                        discordSend(
                            description: "Build **#" + env.BUILD_NUMBER + "** completed.", 
                            footer: "Jenkins - Build Pipeline", 
                            link: env.BUILD_URL,
                            result: currentBuild.currentResult, 
                            title: JOB_NAME, 
                            webhookURL: NEXUS_DISCORD_WEBHOOK
                        )
                    }
                }
            }
        }
        failure {
            script {
                statusBadge.setStatus('failed')
                statusBadge.setColor('red')
                statusBadge.setAnimatedOverlayColor(null)
                
                withCredentials([string(credentialsId: 'discord-webhook-github-log', variable: 'NEXUS_DISCORD_WEBHOOK')]) {
                    discordSend(
                        description: "Build **#" + env.BUILD_NUMBER + "** failed.", 
                        footer: "Jenkins - Build Pipeline", 
                        link: env.BUILD_URL,
                        result: currentBuild.currentResult, 
                        title: JOB_NAME, 
                        webhookURL: NEXUS_DISCORD_WEBHOOK
                    )
                }
            }
        }
        aborted {
            script {
                statusBadge.setStatus('aborted')
                statusBadge.setColor('red')
                withCredentials([string(credentialsId: 'discord-webhook-github-log', variable: 'NEXUS_DISCORD_WEBHOOK')]) {
                    discordSend(
                        description: "Build **#" + env.BUILD_NUMBER + "** aborted.", 
                        footer: "Jenkins - Build Pipeline", 
                        link: env.BUILD_URL,
                        result: currentBuild.currentResult, 
                        title: JOB_NAME, 
                        webhookURL: NEXUS_DISCORD_WEBHOOK
                    )
                }
            }
        }
    }
}

def nexusFetch(snapshot) {
    withCredentials([string(credentialsId: 'nexus-jenkins-user-name', variable: 'NEXUS_FETCH_USERNAME'), string(credentialsId: 'nexus-jenkins-user-pass', variable: 'NEXUS_FETCH_USERPASS')]) {
        script {
            if(snapshot == 'true') {
                fetchRepository = "public-snapshots"
            } else {
                fetchRepository = "public-releases"
            }

            fetchMavenGroupId = sh(script: './gradlew properties | grep ^group: | awk \'{print  $2 }\' | tr -d \'\n\'', returnStdout: true)
            fetchMavenArtifactId = sh(script: './gradlew properties | grep ^name: | awk \'{print  $2 }\' | tr -d \'\n\'', returnStdout: true)
            fetchMavenBaseVersion = sh(script: './gradlew properties | grep ^version: | awk \'{print  $2 }\' | tr -d \'\n\'', returnStdout: true)

            println("Artifact to fetch -> " + fetchMavenGroupId + ":" + fetchMavenArtifactId + ":" + fetchMavenBaseVersion)

            def items = fetchPartialData(fetchRepository, fetchMavenGroupId, fetchMavenArtifactId, fetchMavenBaseVersion, "", null)
            
            if(items.size() == 0) {
                return null   
            }
            
            return items[items.size() - 1]
        }
    }
}

def fetchPartialData(repository, group, artifact, version, continuationToken, items) {
    if(continuationToken == "") {
        continuationTokenPart = ""
    } else {
        continuationTokenPart = "&continuationToken=" + continuationToken
    }
    
    def data = sh(script: "curl -u \$NEXUS_FETCH_USERNAME:\$NEXUS_FETCH_USERPASS -X GET \"https://nexus.wirlie.net/service/rest/v1/search/assets?repository=${repository}&maven.groupId=${group}&maven.artifactId=${artifact}&maven.extension=jar&maven.baseVersion=${version}&sort=name${continuationTokenPart}\"", returnStdout: true)
    def jsonObj = readJSON text: data
    def newItems = jsonObj['items']
    
    println("Items found: " + newItems.size())
    
    if(items != null) {
        newItems.each {
            items.add(it)
        }
        newItems = items
    }
    
    if(jsonObj.containsKey('continuationToken') && !(jsonObj['continuationToken'] instanceof net.sf.json.JSONNull)) {
        println("Continuation token found.")
        println jsonObj['continuationToken'].getClass()
        return fetchPartialData(repository, group, artifact, version, jsonObj['continuationToken'], newItems)
    } else {
        return newItems
    }
}

def resolveCommitMessage() {
    if(env.PUBLISH_PR_ID == 'none') {
        return sh(script: 'git log -1 --pretty=%B ${GIT_COMMIT}', returnStdout: true).trim()
    } else {
        return sh(script: 'git log -1 --skip 1 --pretty=%B ${GIT_COMMIT}', returnStdout: true).trim()
    }
}

def resolveCommitAuthor() {
    if(env.PUBLISH_PR_ID == 'none') {
        return sh(script: 'git log -1 --pretty=format:\'%an\' ${GIT_COMMIT} | tr -d \'\n\'', returnStdout: true)    
    } else {
        return sh(script: 'git log -1 --skip 1 --pretty=format:\'%an\' ${GIT_COMMIT} | tr -d \'\n\'', returnStdout: true).trim()
    }
}

def resolveCommitHash() {
    if(env.PUBLISH_PR_ID == 'none') {
        return sh(script: 'git log -1 --pretty=format:\'%h\' ${GIT_COMMIT} | tr -d \'\n\'', returnStdout: true)    
    } else {
        return sh(script: 'git log -1 --skip 1 --pretty=format:\'%h\' ${GIT_COMMIT} | tr -d \'\n\'', returnStdout: true).trim()
    }
}
