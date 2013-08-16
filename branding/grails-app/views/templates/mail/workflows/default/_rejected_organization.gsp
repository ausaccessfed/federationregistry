<%@ page contentType="text/html"%>

<g:applyLayout name="email">
  <html>
    <head></head>
    <body>

      Unfortunately the Organisation you've registered has not been accepted to join the federation at this time.
      <br><br>
      The details of the Organisation that was rejected are shown below.
      <br><br>
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
      If you have any questions about why this Organisation was not accepted please contact the <g:message code="branding.fr.federationinfo.support.link.long" />.
    
    </body>
  </html>
</g:applyLayout>
