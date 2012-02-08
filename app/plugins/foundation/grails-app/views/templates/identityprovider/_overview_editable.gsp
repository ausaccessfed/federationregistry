<div id="overview-identityprovider-editable">
  <table class="borderless fixed">
    <tbody>
      <tr>
        <th><g:message code="label.status"/></th>
        <td>
          <g:if test="${identityProvider.active}">
            <g:message code="label.active" />
          </g:if>
          <g:else>
            <span class="more-info not-in-federation" rel="twipsy" data-original-title="${g.message(code:'label.warningmetadata')}" data-placement="right"><g:message code="label.inactive" /></span>
          </g:else>
        </td>
      </tr>
      <tr>
        <th><g:message code="label.displayname"/></th>
        <td>${fieldValue(bean: identityProvider, field: "displayName")}</td>
      </tr>
      <tr>
        <th><g:message code="label.description"/></th>
        <td>${fieldValue(bean: identityProvider, field: "description")}</td>
      </tr>
      <tr>
        <th><g:message code="label.usesaa"/></th>
        <td>
          <g:if test="${identityProvider.autoAcceptServices}">
            <g:message code="label.yes" />
          </g:if>
          <g:else>
            <span class="not-in-federation"><g:message code="label.no" /></span>
          </g:else>
        </td>
      </tr>
      <tr>
        <th><g:message code="label.scope"/></th>
        <td>${fieldValue(bean: identityProvider, field: "scope")}</td>
      </tr>
    </tbody> 
  </table>
</div>