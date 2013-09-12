import org.apache.catalina.connector.Connector

eventConfigureTomcat = { tomcat ->
  println 'Adding AJP connector to Tomcat'
  def ajpConnector = new Connector('org.apache.coyote.ajp.AjpProtocol')
  ajpConnector.port = 8009
  ajpConnector.setProperty('redirectPort', '8443')
  ajpConnector.setProperty('protocol', 'AJP/1.3')
  ajpConnector.setProperty('enableLookups', 'false')
  ajpConnector.setProperty('URIEncoding', 'utf-8')
  tomcat.service.addConnector ajpConnector 
}

eventCompileStart = {
  projectCompiler.srcDirectories <<"$basedir/test/common".toString()
} 
eventAllTestsStart = {
   classLoader.addURL(new File("$basedir/test/common".toString()).toURL())
}
