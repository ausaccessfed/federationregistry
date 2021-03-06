<html>
  <head>  
    <meta name="layout" content="members" />
  </head>
  <body>
    <h2><g:message encodeAs="HTML" code="views.fr.foundation.contacts.show.heading" args="[contact.givenName, contact.surname]"/></h2>

    <g:render template="/templates/flash" plugin="foundation"/>
  
    <table class="table borderless">
      <tbody> 
        <g:if test="${contact.organization}">
          <tr>
            <th><g:message encodeAs="HTML" code="label.organization" /></th>
            <td><g:link controller="organization" action="show" id="${contact.organization.id}">${fieldValue(bean: contact, field: "organization.displayName")}</g:link></td>
          </tr> 
        </g:if>
        <tr>
          <th><g:message encodeAs="HTML" code="label.givenname" /></th>
          <td>${fieldValue(bean: contact, field: "givenName")}</td>
        </tr>
        <tr>
          <tr>
            <th><g:message encodeAs="HTML" code="label.surname" /></th>
            <td>${fieldValue(bean: contact, field: "surname")}</td>
          </tr>
        </tr>
        <tr>
          <tr>
            <th><g:message encodeAs="HTML" code="label.email" /></th>
            <td><a href="mailto:${fieldValue(bean: contact, field: "email")}">${fieldValue(bean: contact, field: "email")}</a></td>
          </tr>
        </tr>
        <tr>
          <th><g:message encodeAs="HTML" code="label.workphone" /></th>
          <td>${fieldValue(bean: contact, field: "workPhone")}</td>
        </tr>
        <tr>
          <th><g:message encodeAs="HTML" code="label.mobilephone" /></th>
          <td>${fieldValue(bean: contact, field: "mobilePhone")}</td>
        </tr>
        <tr>
          <th><g:message encodeAs="HTML" code="label.active" /></th>
          <td>${fieldValue(bean: contact, field: "active")}</td>
        </tr>
      </tbody>
    </table>

    <fr:hasAnyPermission in='["federation:management:contacts", "contact:${contact.id}:update"]'>
      <g:link action="edit" id="${contact.id}" class="btn btn-info btn-small"><g:message encodeAs="HTML" code="label.edit" default="Edit"/></g:link>
    </fr:hasAnyPermission>

  </body>
</html>
