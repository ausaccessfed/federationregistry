<fr:isLoggedIn>
  <g:render template='/templates/frtopnavigation'/>

  <ul class="level2">
    <li class="${controllerName == 'adminDashboard' ? 'directactive':''}">
      <g:link controller="adminDashboard" action="index"><g:message encodeAs="HTML"  code="fr.branding.nav.admin.dashboard" default="Dashboard"/></g:link>
    </li>
    <li class="${controllerName == 'subject' ? 'active':''}">
      <g:link controller="subject" action="list"><g:message encodeAs="HTML"  code="fr.branding.nav.admin.subjects" default="Subjects"/></g:link>
    </li>
    <li class="${controllerName == 'role' ? 'active':''}">
      <g:link controller="role" action="list"><g:message encodeAs="HTML"  code="fr.branding.nav.admin.roles" default="Roles"/></g:link>
    </li>
    <li class="${['attributeBase', 'attributeCategory', 'CAKeyInfo', 'contactType', 'monitorType', 'organizationType', 'samlURI'].contains(controllerName) ? 'directactive':''}">
      <g:link controller="attributeBase" action="list"><g:message encodeAs="HTML"  code="fr.branding.nav.admin.datamanagement" default="Data Management"/></g:link>
    </li>
    <li class="${controllerName == 'adminConsole' ? 'directactive':''}">
      <g:link controller="console" action="index" target="_blank"><g:message encodeAs="HTML"  code="fr.branding.nav.admin.console" default="Console"/></g:link>
    </li>
  </ul>

  <g:if test="${controllerName == 'subject'}">
    <ul class="level3">
      <li class="${actionName == 'list' ? 'directactive':''}">
        <g:link controller="subject" action="list"><g:message encodeAs="HTML"  code="fr.branding.nav.subject.subjects.list" default="List"/></g:link>
      </li>
      <g:if test="${actionName == 'show'}">
        <li class="directactive">
          <g:message encodeAs="HTML"  code="fr.branding.nav.admin.subjects.view" default="View"/>
        </li>
      </g:if>
    </ul>
  </g:if>

  <g:if test="${controllerName == 'role'}">
    <ul class="level3">
      <li class="${actionName == 'list' ? 'directactive':''}">
        <g:link controller="role" action="list"><g:message encodeAs="HTML"  code="fr.branding.nav.admin.roles.list" default="List"/></g:link>
      </li>
      <li class="${actionName == 'create' ? 'directactive':''}">
        <g:link controller="role" action="create"><g:message encodeAs="HTML"  code="fr.branding.nav.admin.roles.create" default="Create"/></g:link>
      </li>
      <g:if test="${actionName == 'show'}">
        <li class="directactive">
          <g:message encodeAs="HTML"  code="fr.branding.nav.admin.roles.view" default="View"/>
        </li>
      </g:if>
    </ul>
  </g:if>
</fr:isLoggedIn>
