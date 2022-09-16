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
                    sh './gradlew cleanCompiledArtifactsFolder'

                    env.ARTIFACT_PUBLISH_SNAPSHOT = 'false'
                    env.PUBLISH_PR_ID = 'none'
                    env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_START = ''
                    env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_MIDDLE = ''
                    env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_END = ''
                    env.GENERATED_SNAPSHOT_ARTIFACTS_MESSAGE_PORTION_START = ''
                    env.GENERATED_SNAPSHOT_ARTIFACTS_MESSAGE_PORTION_MIDDLE = ''
                    env.GENERATED_SNAPSHOT_ARTIFACTS_MESSAGE_PORTION_END = ''
                    env.GENERATED_PR_ARTIFACTS_MESSAGE_PORTION_START = ''
                    env.GENERATED_PR_ARTIFACTS_MESSAGE_PORTION_MIDDLE = ''
                    env.GENERATED_PR_ARTIFACTS_MESSAGE_PORTION_END = ''
                    
                    println("Branch: " + env.GIT_BRANCH)
                    switch(env.GIT_BRANCH) {
                        case 'master':
                            env.PUBLISH_TO_NEXUS = 'true'
                            env.PUBLISH_SNAPSHOT = 'false'
                            println("Environment variables set for master branch")
                            break
                        case "develop":
                        case "2.0.0":
                            env.PUBLISH_TO_NEXUS = 'true'
                            env.PUBLISH_SNAPSHOT = 'true'
                            env.ARTIFACT_PUBLISH_SNAPSHOT = 'true'
                            println("Environment variables set for develop branch")
                            // Set build description
                            currentBuild.description = "<hr>✅ Contribute to this project by testing experimental builds.<br/>⚠️ This build can be unstable and may contain bugs or perfomance problems! Be careful, always take a backup before installing an unstable build.<br/>🐛 Please make a <a href=\"https://github.com/Wirlie/Enhanced-Glist/issues\" target=\"_blank\">bug report</a> if you have any issue.<br/><hr>"
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
                    
                    // Target release version
                    env.BUILD_TARGET_RELEASE = 'none'
                    
                    def branchName = env.CHANGE_BRANCH
                    
                    if(branchName == null) {
                        // Not in PR, use BRANCH_NAME instead
                        branchName = env.BRANCH_NAME
                    }
                    
                    switch(branchName) {
                        case "develop":
                        case "2.0.0":
                            env.BUILD_TARGET_RELEASE = '2.0.0'
                            break
                        case "master":
                            break
                        default:
                            println("Branch does not have a target relase: " + branchName)
                    }
                    
                    if(env.BRANCH_NAME != 'master' && env.BUILD_TARGET_RELEASE == 'none') {
                        error("Only master branch is allowed to not have a target release, edit Jenkinsfile to fix this.")   
                    }
                    
                    println("Target release for branch " + env.CHANGE_BRANCH + " is " + env.BUILD_TARGET_RELEASE)
                }
            }
        }
        stage('BungeeCord Build') {
            when { 
                environment name: 'CI_SKIP', value: 'false' 
            }
            steps {
                script {
                    sh './gradlew :EnhancedGlist-BungeeCord:test :EnhancedGlist-BungeeCord:shadowJar'
                    archiveArtifacts artifacts: 'compiled/*.jar', fingerprint: true
                }
            }
        }
        stage('BungeeCord Deploy') {
            when { 
                environment name: 'CI_SKIP', value: 'false'
                environment name: 'PUBLISH_TO_NEXUS', value: 'true'
            }
            steps {
                script {
                    nexusPublish("EnhancedGlist-BungeeCord-API")
                }
            }
        }
        stage('Velocity Build') {
            when { 
                environment name: 'CI_SKIP', value: 'false' 
            }
            steps {
                script {
                    sh './gradlew :EnhancedGlist-Velocity:test :EnhancedGlist-Velocity:shadowJar'
                    archiveArtifacts artifacts: 'compiled/*.jar', fingerprint: true
                }
            }
        }
        stage('Velocity Deploy') {
            when { 
                environment name: 'CI_SKIP', value: 'false'
                environment name: 'PUBLISH_TO_NEXUS', value: 'true'
            }
            steps {
                script {
                    nexusPublish("EnhancedGlist-Velocity-API")
                }
            }
        }
        stage('Spigot Build') {
            when { 
                environment name: 'CI_SKIP', value: 'false' 
            }
            steps {
                script {
                    sh './gradlew :EnhancedGlist-Spigot-Bridge:test :EnhancedGlist-Spigot-Bridge:shadowJar'
                    archiveArtifacts artifacts: 'compiled/*.jar', fingerprint: true
                }
            }
        }
        stage('Spigot Deploy') {
            when { 
                environment name: 'CI_SKIP', value: 'false'
                environment name: 'PUBLISH_TO_NEXUS', value: 'true'
            }
            steps {
                script {
                    //nexusPublish("EnhancedGlist-Spigot-Bridge-API")
                    println("Skipped for now")
                }
            }
        }
    }
    
    post {
        always {
            script {
                if(env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_START != '') {
                    withCredentials([string(credentialsId: 'discord-webhook-maven-releases', variable: 'NEXUS_DISCORD_WEBHOOK')]) {
                        discordSend(
                            description: env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_START + env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_MIDDLE + env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_END, 
                            footer: "Jenkins - Build Pipeline", 
                            link: env.BUILD_URL,
                            result: "SUCCESS", 
                            title: JOB_NAME, 
                            webhookURL: NEXUS_DISCORD_WEBHOOK
                        )
                    }
                }
                if(env.GENERATED_SNAPSHOT_ARTIFACTS_MESSAGE_PORTION_START != '') {
                    withCredentials([string(credentialsId: 'discord-webhook-maven-snapshots', variable: 'NEXUS_DISCORD_WEBHOOK')]) {
                        discordSend(
                            description: env.GENERATED_SNAPSHOT_ARTIFACTS_MESSAGE_PORTION_START + env.GENERATED_SNAPSHOT_ARTIFACTS_MESSAGE_PORTION_MIDDLE + env.GENERATED_SNAPSHOT_ARTIFACTS_MESSAGE_PORTION_END, 
                            footer: "Jenkins - Build Pipeline", 
                            link: env.BUILD_URL,
                            result: "SUCCESS", 
                            title: JOB_NAME, 
                            webhookURL: NEXUS_DISCORD_WEBHOOK
                        )
                    }
                }
                if(env.GENERATED_PR_ARTIFACTS_MESSAGE_PORTION_START != '') {
                    withCredentials([string(credentialsId: 'discord-webhook-github-pr', variable: 'NEXUS_DISCORD_WEBHOOK')]) {
                        discordSend(
                            description: env.GENERATED_PR_ARTIFACTS_MESSAGE_PORTION_START + env.GENERATED_PR_ARTIFACTS_MESSAGE_PORTION_MIDDLE + env.GENERATED_PR_ARTIFACTS_MESSAGE_PORTION_END, 
                            footer: "Jenkins - Build Pipeline", 
                            link: env.BUILD_URL,
                            result: "SUCCESS", 
                            title: JOB_NAME, 
                            webhookURL: NEXUS_DISCORD_WEBHOOK
                        )
                    }
                }
            }
        }
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

def nexusFetch(snapshot, project) {
    withCredentials([string(credentialsId: 'nexus-jenkins-user-name', variable: 'NEXUS_FETCH_USERNAME'), string(credentialsId: 'nexus-jenkins-user-pass', variable: 'NEXUS_FETCH_USERPASS')]) {
        script {
            if(snapshot == 'true') {
                fetchRepository = "public-snapshots"
            } else {
                fetchRepository = "public-releases"
            }

            fetchMavenGroupId = sh(script: "./gradlew :${project}:properties | grep ^group: | awk '{print  \$2 }' | tr -d '\n'", returnStdout: true)
            fetchMavenArtifactId = sh(script: "./gradlew :${project}:properties | grep ^name: | awk '{print  \$2 }' | tr -d '\n'", returnStdout: true)
            fetchMavenBaseVersion = sh(script: "./gradlew :${project}:properties | grep ^version: | awk '{print  \$2 }' | tr -d '\n'", returnStdout: true)

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

def nexusPublish(project) {
    if(env.PUBLISH_SNAPSHOT == 'false') {
        println("Project to publish: " + project)
        def item = nexusFetch('false', project)

        if(item == null) {
            sh(script: "./gradlew :${project}:publishMavenPublicationToNexusRepository") //execute gradle

            // Fetch from Nexus
            item = nexusFetch('false', project)

            if(item == null) {
                println("Release no encontrado en Nexus. Inesperado pero no se fallará el build debido a esta inconsistencia.")
            } else {
                def maven2 = item['maven2']
                
                if(env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_START == '') {
                    env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_START = "Nexus Release\n\n**Repository:**\n`https://nexus.wirlie.net/repository/" + item['repository'] + "/`\n"
                    env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_END = ""
                }
                
                env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_MIDDLE = env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_MIDDLE + "\n**Group:** `" + maven2['groupId'] + "`\n**Name:** `" + maven2['artifactId'] + "`\n**Version:** `" + maven2['version'] + "`\n"
            }
        } else {
            // Error, artifact already exists in Nexus
            def maven2 = item['maven2']

            if(env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_START == '') {
                env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_START = "Nexus Release\n\n**Repository:**\n`https://nexus.wirlie.net/repository/" + item['repository'] + "/`\n"
                env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_END = ""
            }
                
            env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_MIDDLE = env.GENERATED_RELEASE_ARTIFACTS_MESSAGE_PORTION_MIDDLE + "\n⚠️ __Not Published (duplicated)__\n**Group:** ~~`" + maven2['groupId'] + "`~~\n**Name:** ~~`" + maven2['artifactId'] + "`~~\n**Version:** ~~`" + maven2['version'] + "`~~\n"
        }
    } else {
        println("Project to publish: " + project)
        sh (script: "./gradlew :${project}:publishMavenPublicationToNexusRepository") //execute gradle

        // Fetch from Nexus
        def item = nexusFetch('true', project)

        if(item == null) {
            println("Snapshot not found, not expected but we will not fail build.")
        } else {
            def maven2 = item['maven2']

            if(env.PUBLISH_PR_ID == 'none') {
                // Not in pull request
                if(env.GENERATED_SNAPSHOT_ARTIFACTS_MESSAGE_PORTION_START == '') {
                    env.GENERATED_SNAPSHOT_ARTIFACTS_MESSAGE_PORTION_START = "Nexus Snapshot\n\n**Repository:**\n`https://nexus.wirlie.net/repository/" + item['repository'] + "/`\n"
                    env.GENERATED_SNAPSHOT_ARTIFACTS_MESSAGE_PORTION_END = ""
                }
                
                env.GENERATED_SNAPSHOT_ARTIFACTS_MESSAGE_PORTION_MIDDLE = env.GENERATED_SNAPSHOT_ARTIFACTS_MESSAGE_PORTION_MIDDLE + "\n**Group:** `" + maven2['groupId'] + "`\n**Name:** `" + maven2['artifactId'] + "`\n**Version:** `" + maven2['version'] + "`\n"
            } else {
                // Pull request
                if(env.GENERATED_PR_ARTIFACTS_MESSAGE_PORTION_START == '') {
                    env.GENERATED_PR_ARTIFACTS_MESSAGE_PORTION_START = "Nexus Snapshot for Pull Request **#" + env.PUBLISH_PR_ID + "**\n\n**Repository:**\n`https://nexus.wirlie.net/repository/" + item['repository'] + "/`\n"
                    env.GENERATED_PR_ARTIFACTS_MESSAGE_PORTION_END = ""
                }
                
                env.GENERATED_PR_ARTIFACTS_MESSAGE_PORTION_MIDDLE = env.GENERATED_PR_ARTIFACTS_MESSAGE_PORTION_MIDDLE + "\n**Group:** `" + maven2['groupId'] + "`\n**Name:** `" + maven2['artifactId'] + "`\n**Version:** `" + maven2['version'] + "`\n"
            }
        }
    }   
}
