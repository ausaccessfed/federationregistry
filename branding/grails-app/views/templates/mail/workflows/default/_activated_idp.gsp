<%@ page contentType="text/html"%>

<g:applyLayout name="email">
  <html>
    <head></head>
    <body>
      The Identity Provider which you've registered with the <g:message code="branding.fr.federationinfo.federationname.long" /> has been <strong>accepted</strong>. You can now complete final setup tasks.
      <br><br>
      Please keep this email for future reference.
      <br><br>
      Your <em>unique code for administrative access</em> (required in <strong>step 4 below</strong>)
      <div id="admincode" style="color:#E36C0A; font-size: 22px; padding: 6px; border: 1px solid #CCCCCC; text-align: center;">
        ${fieldValue(bean: invitation, field: "inviteCode")}
      </div>
      <br><br>
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
    
      <br></br>

      <h4 class="h4">Next Steps</h4>
      1. Validate EntityID
          <br>
          Your Identity Provider must use a unique EntityID to identify it to the rest of the federation and this value <strong><em>must match both your local configuration and Federation Registry</em></strong>.
          <br><br>
          Please verify your Identity Provider configuration uses the EntityID shown above under 'Important Details'. <strong>If it doesn't, you can't go any further. Contact the <g:message code="branding.fr.federationinfo.support.link.long" /> for help in resolving this fault</strong>.
      
        <br><br>
      2. Ensure Time Sync
          <br>
          The server which runs your Identity Provider must be synchronized to a suitable NTP source and <strong><em>remain in sync at all times</em></strong>.
          <br><br>
          Users from an Identity Provider which is out of time sync will <strong>fail to login to remote services</strong>.
      
        <br><br>
      3. Configure Attribute Release
          <br>
          Configure your Identity Provider to correctly release attributes to the federation as documented in <a href="http://support.aaf.edu.au/entries/22545567-Automating-Attribute-Release">Automating Attribute Release</a>. When asked for the value of <strong>[INTERNALID]</strong> please provide ${fieldValue(bean: identityProvider, field: "id")}.
      
        <br><br><br>
      
          <em>Ensure <strong>at least</strong> an hour has passed since you received this email before continuing. This allows distribution of metadata including details of your new Identity Provider to circulate to the federation.</em>
      
        <br><br><br>
      
      4. Claim your administrative access
          <br>
          Navigate to your <g:link controller='identityProvider' action='show' id="${identityProvider.id}" absolute='true'>Identity Provider's management page</g:link> and access the administrator tab. Enter the unique code provided below in the box labelled <strong>CODE</strong>. This can only be used once. Once applied you can then provide access to other administrators on your team.
          <br><br>
          <strong>Your unique code for administrative access in Federation Registry:</strong>
          <div id="admincode" style="color:#E36C0A; font-size: 22px; padding: 6px; border: 1px solid #CCCCCC; text-align: center;">
            ${fieldValue(bean: invitation, field: "inviteCode")}
          </div>
      
        <br><br>
      
      5. Double check all information associated with your Identity Provider that is stored in Federation Registry making changes if you note any inaccuracies.
      
        <br><br>
      
      6. Ensure you've applied appropriate branding to all of your Identity Providers pages including the login screen and <strong>all error pages</strong>.
          <br><br>
          Branding should be consistent with that used on your organisation's website.
      
        <br><br>
      
      7. Test your new Identity Provider with other services connected to the federation, especially those which are of importance to your organisation.
      
        <br><br>
      
      8. You're all done! Welcome to the <g:message code="branding.fr.federationinfo.federationname.long" />.
    
    </body>
  </html>
</g:applyLayout>
