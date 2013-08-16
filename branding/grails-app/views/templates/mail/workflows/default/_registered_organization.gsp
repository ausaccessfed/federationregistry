<%@ page contentType="text/html"%>

<g:applyLayout name="email">
  <html>
    <head></head>
    <body>
      This email confirms that the Organisation which you've registered with the <g:message code="branding.fr.federationinfo.federationname.long" /> has been <strong>received but is not yet ready for use</strong>. 
      <br/><br/>
      Your Organisation will now move through a workflow process that requires approvals by <g:message code="branding.fr.federationinfo.federationname" /> staff.
      <br/><br/>
      Please allow <strong>up to 48 hours for this to occur</strong>. Upon completion of the process you will receive another email confirming activation of your Organisation and further instructions for completing your deployment.
      <br/><br/>
      <h4 class="h4">Important Details</h4>
      <table>
        <tr>
          <td>
            <strong>Internal ID</strong>: 
          </td>
          <td>
            ${fieldValue(bean: organization, field: "id")}
          </td>
        </tr>
        <tr>
          <td>
            <strong>Display Name</strong>: 
          </td>
          <td>
            ${fieldValue(bean: organization, field: "displayName")}
          </td>
        </tr>
      </table>

      <br><br>
      Please contact the <g:message code="branding.fr.federationinfo.support.link.long" /> if the details are incorrect or if you have not received a response within 48 hours.
    
    </body>
  </html>
</g:applyLayout>
