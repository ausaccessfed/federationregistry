<%@ page contentType="text/html"%>

<g:applyLayout name="email">
  <html>
    <head></head>
    <body>
      This email confirms that the Identity Provider which you've registered with the <g:message code="branding.fr.federationinfo.federationname.long" /> has been <strong>received but is not yet ready for use</strong>. 
      <br/><br/>
      Your Identity Provider will now move through a workflow process that requires approvals by <g:message code="branding.fr.federationinfo.federationname" /> staff and potentially your organisation.
      <br/><br/>
      Please allow <strong>up to 48 hours for this to occur</strong>. Upon completion of the process you will receive another email confirming activation of your Identity Provider and further instructions for completing your deployment.
      <br/><br/>
      <h4 class="h4">Important Details</h4>
      <table>
        <tr>
          <td>
            <strong>Internal ID</strong>: 
          </td>
          <td>
            ${fieldValue(bean: identityProvider, field: "id")}
          </td>
        </tr>
        <tr>
          <td>
            <strong>Entity ID</strong>: 
          </td>
          <td>
            ${fieldValue(bean: identityProvider, field: "entityDescriptor.entityID")}
          </td>
        </tr>
        <tr>
          <td>
            <strong>Display Name</strong>: 
          </td>
          <td>
            ${fieldValue(bean: identityProvider, field: "displayName")}
          </td>
        </tr>
        <tr>
          <td>
            <strong>Description</strong>: 
          </td>
          <td>
            ${fieldValue(bean: identityProvider, field: "description")}
          </td>
        </tr>
        <tr>
          <td>
            <strong>Owner</strong>: 
          </td>
          <td>
            ${fieldValue(bean: identityProvider, field: "organization.displayName")}
          </td>
        </tr>
      </table>

      <br><br>
      Please contact the <g:message code="branding.fr.federationinfo.support.link.long" /> if the details are incorrect or if you have not received a response within 48 hours.
    
    </body>
  </html>
</g:applyLayout>
