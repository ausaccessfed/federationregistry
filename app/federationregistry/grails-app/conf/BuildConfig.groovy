grails.servlet.version = "3.0"

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6

// Local Plugins
grails.plugin.location.base = "../plugins/base"
grails.plugin.location.reporting = "../plugins/reporting"
grails.plugin.location.metadata = "../plugins/metadata"
grails.plugin.location.administration = "../plugins/administration"
grails.plugin.location.api = "../plugins/api"

grails.project.dependency.resolution = {
  inherits("global") {
  }

  log "warn"
  checksums true
  
  repositories {
    inherits true

    grailsPlugins()
    grailsHome()
    grailsCentral()

    mavenLocal()
    mavenCentral()

    mavenRepo "http://snapshots.repository.codehaus.org"
    mavenRepo "http://repository.codehaus.org"
    mavenRepo "http://download.java.net/maven/2/"
    mavenRepo "http://repository.jboss.com/maven2/"
  }

  dependencies {
    test 'mysql:mysql-connector-java:5.1.18'
    test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
  }

  plugins {
    /*
    build: Dependencies for the build system only
    compile: Dependencies for the compile step
    runtime: Dependencies needed at runtime but not for compilation (see above)
    test: Dependencies needed for testing but not at runtime (see above)
    provided: Dependencies needed at development time, but not during WAR deployment
    */

    build ":tomcat:$grailsVersion"

    compile ':cache:1.0.0'
    compile ":mail:1.0"
    compile ":build-test-data:2.0.3"
    compile ":hibernate:$grailsVersion"
    
    runtime ":resources:1.1.6"
    runtime ":zipped-resources:1.0"
    runtime ":cached-resources:1.0"
    runtime ":yui-minify-resources:0.1.4"
    runtime ":database-migration:1.1"
    runtime ":jquery:1.7.2"
    runtime ":modernizr:2.5.3"
    runtime (":twitter-bootstrap:2.1.1") { excludes "svn" }
    runtime 'org.grails.plugins:constraintkeys:0.1'
    runtime ":console:1.2"
    runtime ":cache-headers:1.1.5"
    runtime ":audit-logging:0.5.4"

    test(":spock:0.7") {
      exclude "spock-grails-support"
    }

    provided ":greenmail:1.3.4"
  }
}

codenarc.reports = {
    HTMLReport('html') {
        outputFile = 'target/test-reports/codenarc.html' 
        title = 'CodeNarc' 
    } 
}
