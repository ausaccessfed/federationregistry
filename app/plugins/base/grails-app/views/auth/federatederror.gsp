<html>
  <head>
    <meta name="layout" content="public" />
  </head>
  
  <body>
    <h2>Session establishment error</h2>

    <div class="alert alert-block alert-error">
      <p>Unfortunately an error occured while attempting to establish your session with this service. Additional details have been provided Federation Registry administrators.</p>
    </div>

    <p> If this problem persists please contact the <g:message code="branding.fr.federationinfo.support.link.long" /> for help in resolving this fault</strong>.</p>

    <div class="row-spacer">
      <g:message code="branding.fr.login.error.hint" />
    </div>
    
    <div class="row-spacer">
      <h2>Complete Request Details</h2>
      <div class="row">
        <div class="span12">
          <g:include controller="auth" action="echo" />
        </div>
      </div>
    </div>
  </body>
</html>
