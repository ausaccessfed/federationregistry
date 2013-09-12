eventCompileStart = { 
  projectCompiler.srcDirectories << "$basedir/test/common".toString()
} 
eventAllTestsStart = { 
   classLoader.addURL(new File("$basedir/test/common".toString()).toURL())
}
