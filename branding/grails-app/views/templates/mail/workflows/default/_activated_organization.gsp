<%@ page contentType="text/html"%>

<g:applyLayout name="email">
  <html>
    <head></head>
    <body>
      The Organisation which you've registered with the <g:message code="branding.fr.federationinfo.federationname.long" /> has been <strong>accepted</strong>. You can now complete final setup tasks.
      <br><br>
      Please keep this email for future reference.
      <br><br>
      Your <em>unique code for administrative access</em> (required in <strong>step 2 below</strong>)
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
    
      <br></br>

      <h4 class="h4">Next Steps</h4>
      1. Gain access to Federation Registry
         <br>
         Before you can access Federation Registry you need an account that works with the federation. There are two ways to do this:
         <br><br>
          i.) Register an Identity Provider for your organisation using the publicly accessible <g:link controller='bootstrap' action='idp' absolute='true'>Identity Provider creation page</g:link>. Once this Identity Provider has completed the setup process you'll be able to login to Federation Registry using your institution provided account.
          <br><br>
          ii.) If your Organisation <strong>is not going to setup an Identity Provider</strong> you will have access to create accounts in the AAF Virtual Home Organisation (VHO). If you have not yet been provided details about creating accounts to use with federated services in the VHO please contact the <g:message code="branding.fr.federationinfo.support.link.long" /> for more details.

      <br><br>
      
      2. Claim your administrative access
          <br>
          Once you've undertaken one of the above options navigate to your <g:link controller='organization' action='show' id="${organization.id}" absolute='true'>Organisation's management page</g:link> and access the administrator tab. Enter the unique code provided below in the box labelled <strong>CODE</strong>. This can only be used once. Once applied you can then provide access to other administrators on your team.
          <br><br>
          <strong>Your unique code for administrative access in Federation Registry:</strong>
          <div id="admincode" style="color:#E36C0A; font-size: 22px; padding: 6px; border: 1px solid #CCCCCC; text-align: center;">
            ${fieldValue(bean: invitation, field: "inviteCode")}
          </div>
      
        <br><br>
      
      3. Double check all information associated with your Organisation that is stored in Federation Registry making changes if you note any inaccuracies.
      
        <br><br>
      
      4. You're all done! Welcome to the <g:message code="branding.fr.federationinfo.federationname.long" />.
    
    </body>
  </html>
</g:applyLayout>
