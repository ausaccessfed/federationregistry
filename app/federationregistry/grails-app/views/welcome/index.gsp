<html>
  <head>      
      <meta name="layout" content="public" />
  </head>

  <body>
    <div class="hero-unit">
      <g:message code="branding.fr.welcome.header" />

      <div class="row-spacer">
        <g:message code="branding.fr.welcome.login.question" />
      </div>

      <div class="row row-spacer">
        <div class="span5">
          <g:message code="branding.fr.welcome.login.yes" />
          <g:link class="btn btn-success btn-large" controller="auth">Welcome - Please Login</g:link>
          <br><br>
          <a class="show-problems-logging-on small" href="#problems">Problems logging on?</a>
        </div>

        <div class="span5">
          <g:message code="branding.fr.welcome.login.no" />
          <g:link class="btn btn-info btn-large" controller="bootstrap" action="organization">Create an Organisation</g:link>
          <br><br>
          <g:link class="btn btn-info btn-large" controller="bootstrap" action="idp">Create an Identity Provider</g:link>
          <br><br>
          <g:link class="btn btn-info btn-large" controller="bootstrap" action="sp">Create a Service Provider</g:link>
        </div>
      </div>
    </div>

    <div class="problems-logging-on hidden">
      <hr>
      <a name="problems"></a>
      <g:message code="branding.fr.welcome.login.problems" />
    </div>
  </body>
</html>
