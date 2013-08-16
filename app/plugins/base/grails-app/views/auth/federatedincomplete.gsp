<html>
  <head>
    <meta name="layout" content="public" />
  </head>
  
  <body>
    <h2>Requested Attributes Incomplete</h2>
    <div class="alert alert-block alert-error">
    <p>The attributes passed to this service did not meet our minimum requirements for operation. The specific attributes your IDP has not provided are highlighted below. Please ammend your Attribute Filter policy for this service to provide the missing attributes and try again.</p>
    
      <ul>
        <g:each in="${errors}">
          <li><g:message encodeAs="HTML" code="${it}" /></li>
        </g:each>
      </ul>
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
