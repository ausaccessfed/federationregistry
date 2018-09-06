
<html>
  <head>
    <meta name="layout" content="members" />
  </head>
  <body>
    <h2><g:message encodeAs="HTML" code="views.fr.foundation.contacts.list.heading" /></h2>

    <table class="table table-sortable borderless">
      <thead>
        <tr>
          <th><g:message encodeAs="HTML" code='label.givenname' /></th>
          <th><g:message encodeAs="HTML" code='label.surname' /></th>
          <th><g:message encodeAs="HTML" code='label.email' /></th>
          <th><g:message encodeAs="HTML" code='label.organization' /></th>
          <th><g:message encodeAs="HTML" code='label.active' /></th>
          <th/>
        </tr>
      </thead>
      <tbody>
      <g:each in="${contactList}" status="i" var="contact">
        <tr>
          <td>${fieldValue(bean: contact, field: "givenName")}</td>
          <td>${fieldValue(bean: contact, field: "surname")}</td>
          <td><a href="mailto:${fieldValue(bean: contact, field: "email")}">${fieldValue(bean: contact, field: "email")}</a></td>
          <td>${fieldValue(bean: contact, field: "organization.displayName")}</td>
          <td>${fieldValue(bean: contact, field: "active")}</td>
          <td><a href="${createLink(action:'show', id:contact.id)}" class="btn"><g:message encodeAs="HTML" code="label.view" default="View"/></a></td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </body>
</html>
