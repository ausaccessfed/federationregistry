<div id="overview-serviceprovider">
  <g:render template="/templates/serviceprovider/overview_editable" plugin="foundation" model="[descriptor:serviceProvider]" />
  <fr:hasPermission target="federation:management:descriptor:${serviceProvider.id}:update">
    <a class="show-edit-serviceprovider btn btn-small btn-info"><g:message code="label.edit"/></a>
  </fr:hasPermission>
</div>
<g:render template="/templates/serviceprovider/edit" plugin="foundation" model="[descriptor:serviceProvider]" />

<hr>

<div id="internalstate-serviceprovider">
  <h4><g:message code="templates.fr.serviceprovider.overview.internalstate" /></h4>
  <table class="table borderless fixed">
    <tbody>
      <tr>
        <th><g:message code="label.organization"/></th>
        <td><g:link controller="organization" action="show" id="${serviceProvider.organization.id}">${fieldValue(bean: serviceProvider, field: "organization.displayName")}</g:link></td>
      </tr>
      <fr:hasPermission target="federation:management:saml:advanced">
        <tr>
          <th><g:message code="label.entitydescriptor"/></th>
          <td><g:link controller="entityDescriptor" action="show" id="${serviceProvider.entityDescriptor.id}">${fieldValue(bean: serviceProvider, field: "entityDescriptor.entityID")}</g:link></td>
        </tr>
      </fr:hasPermission>
      <fr:lacksPermission target="federation:management:saml:advanced">
        <tr>
          <th><g:message code="label.entitydescriptor"/></th>
          <td>${fieldValue(bean: serviceProvider, field: "entityDescriptor.entityID")}</td>
        </tr>
      </fr:lacksPermission>
      <tr>
        <th><g:message code="label.protocolsupport"/></th>
        <td>
          <g:each in="${serviceProvider.protocolSupportEnumerations}" status="i" var="pse">
            ${fieldValue(bean: pse, field: "uri")}<br>
          </g:each>
        </td>
      <tr>
        <th><g:message code="label.archived"/></th>
        <td>
          <g:if test="${serviceProvider.archived}"> 
            <span class="label label-warning"><g:message code="label.warningmetadataarchived" /></span>
          </g:if>
          <g:else>
            <g:message code="label.no" /> 
          </g:else>
        </td>
      </tr>
      <tr>
        <th><g:message code="label.approved"/></th>
        <td>
          <g:if test="${serviceProvider.approved}">
            <g:message code="label.yes" />
          </g:if>
          <g:else>
            <span class="label label-important"><g:message code="label.undergoingapproval" /></span>
          </g:else>
        </td>
      </tr>
      <tr>
        <th><g:message code="label.datecreated" /></th>
        <td>${fieldValue(bean: serviceProvider, field: "dateCreated")}</td>
      </tr>
      <tr>
        <th><g:message code="label.lastupdated" /></th>
        <td>${fieldValue(bean: serviceProvider, field: "lastUpdated")}</td>
      </tr> 
    </tbody>
  </table>
</div>