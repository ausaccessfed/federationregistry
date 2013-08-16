<%@ page contentType="text/html"%>

<g:applyLayout name="email">
  <html>
    <head></head>
    <body>

      Unfortunately the Identity Provider you've registered has not been accepted to join the federation at this time.
      <br><br>
      The details of the Identity Provider that was rejected are shown below.
      <br><br>
      <table>
        <tr>
          <td>
            <strong><g:message encodeAs="HTML"  code="label.internalid" /></strong>: 
          </td>
          <td>
            ${fieldValue(bean: identityProvider, field: "id")}
          </td>
        </tr>
        <tr>
          <td>
            <strong><g:message encodeAs="HTML"  code="label.displayname" /></strong>: 
          </td>
          <td>
            ${fieldValue(bean: identityProvider, field: "displayName")}
          </td>
        </tr>
        <tr>
          <td>
            <strong><g:message encodeAs="HTML"  code="label.description" /></strong>: 
          </td>
          <td>
            ${fieldValue(bean: identityProvider, field: "description")}
          </td>
        </tr>
        <tr>
          <td>
            <strong><g:message encodeAs="HTML"  code="label.entitydescriptor" /></strong>: 
          </td>
          <td>
            ${fieldValue(bean: identityProvider, field: "entityDescriptor.entityID")}
          </td>
        </tr>
        <tr>
          <td>
            <strong><g:message encodeAs="HTML"  code="label.organization" /></strong>: 
          </td>
          <td>
            ${fieldValue(bean: identityProvider, field: "organization.displayName")}
          </td>
        </tr>
      </table>
    
      <br><br>
      If you have any questions about why this Identity Provider was not accepted please contact the <g:message code="branding.fr.federationinfo.support.link.long" />.
    
    </body>
  </html>
</g:applyLayout>
