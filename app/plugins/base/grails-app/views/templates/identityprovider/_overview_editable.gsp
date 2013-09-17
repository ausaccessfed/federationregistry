<div id="overview-identityprovider-editable">
  <table class="table borderless fixed">
    <tbody>
      <tr>
        <th><g:message encodeAs="HTML" code="label.status"/></th>
        <td>
          <g:if test="${identityProvider.active}">
            <g:message encodeAs="HTML" code="label.active" />
          </g:if>
          <g:else>
            <span class="label label-important"><g:message encodeAs="HTML" code="label.inactive" /></span><fr:tooltip code='label.warningmetadata'/>
          </g:else>
        </td>
      </tr>
      <tr>
        <th><g:message encodeAs="HTML" code="label.displayname"/></th>
        <td>${fieldValue(bean: identityProvider, field: "displayName")}</td>
      </tr>
      <tr>
        <th><g:message encodeAs="HTML" code="label.description"/></th>
        <td>${fieldValue(bean: identityProvider, field: "description")}</td>
      </tr>
      <tr>
        <th><g:message encodeAs="HTML" code="label.usesaa"/></th>
        <td>
          <g:if test="${identityProvider.autoAcceptServices}">
            <g:message encodeAs="HTML" code="label.yes" />
          </g:if>
          <g:else>
            <span class="not-in-federation"><g:message encodeAs="HTML" code="label.no" /></span>
          </g:else>
        </td>
      </tr>
      <tr>
        <th><g:message encodeAs="HTML" code="label.attributeauthorityonly"/></th>
        <td>
          <g:if test="${identityProvider.attributeAuthorityOnly}">
            <span class="not-in-federation"><g:message encodeAs="HTML" code="label.yes" /></span>
          </g:if>
          <g:else>
            <g:message encodeAs="HTML" code="label.no" />
          </g:else>
        </td>
      </tr>
      <tr>
        <th><g:message encodeAs="HTML" code="label.scope"/></th>
        <td>${fieldValue(bean: identityProvider, field: "scope")}</td>
      </tr>
    </tbody> 
  </table>
</div>
