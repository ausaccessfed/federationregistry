
<html>
  <head>    
    <meta name="layout" content="members" />
    <title><g:message encodeAs="HTML" code="views.fr.foundation.identityprovider.show.title" /></title>
  </head>
  <body>
    
    <h2><g:message encodeAs="HTML" code="views.fr.foundation.identityprovider.show.heading" args="[identityProvider.displayName]"/></h2>

    <g:if test="${!identityProvider.functioning()}">
      <p class="alert alert-message alert-danger">
        <g:message encodeAs="HTML" code="views.fr.foundation.identityprovider.show.notfunctioning"/>
      </p>
    </g:if>

    <g:render template="/templates/flash" plugin="foundation"/>
      
    <ul class="nav nav-tabs">
      <li class="active"><a href="#tab-overview" data-toggle="tab"><g:message encodeAs="HTML" code="label.overview" /></a></li>
      <li><a href="#tab-contacts" data-toggle="tab"><g:message encodeAs="HTML" code="label.contacts" /></a></li>
      <li class="level"><a href="#tab-saml" data-toggle="tab"><g:message encodeAs="HTML" code="label.saml" /></a></li>
      <fr:hasAnyPermission in='["federation:management:descriptor:${identityProvider.id}:reporting" , "federation:management:reporting"]'>
        <li><a href="#tab-reports" data-toggle="tab"><g:message encodeAs="HTML" code="label.reporting" /></a></li>
      </fr:hasAnyPermission>
      <li><a href="#tab-monitors" data-toggle="tab"><g:message encodeAs="HTML" code="label.monitoring" /></a></li>
      <g:if test="${identityProvider.approved}">
        <li><a href="#tab-admins" data-toggle="tab"><g:message encodeAs="HTML" code="label.administrators" /></a></li>
      </g:if>
    </ul>

    <div class="tab-content">
      <div id="tab-overview" class="tab-pane active">
        <g:render template="/templates/identityprovider/overview" plugin="foundation" model="[identityProvider:identityProvider]" />
      </div>

      <div id="tab-contacts" class="tab-pane">
        <g:render template="/templates/contacts/list" plugin="foundation" model="[host:identityProvider, hostType:'descriptor']" />
        <g:render template="/templates/contacts/create" plugin="foundation" model="[host:identityProvider, hostType:'descriptor', contactTypes:contactTypes]"/>
      </div>

      <div id="tab-saml" class="tab-pane">
        <div class="tabbable">
          <ul class="nav nav-tabs">
            <li class="active"><a href="#tab-crypto" data-toggle="tab"><g:message encodeAs="HTML" code="label.crypto" /></a></li>
            <li><a href="#tab-endpoints" data-toggle="tab"><g:message encodeAs="HTML" code="label.endpoints" /></a></li>
            <li><a href="#tab-attributes" data-toggle="tab"><g:message encodeAs="HTML" code="label.supportedattributes" /></a></li>
            <li><a href="#tab-nameidformats" data-toggle="tab"><g:message encodeAs="HTML" code="label.supportednameidformats" /></a></li>

            <fr:hasPermission target="federation:management:descriptor:${identityProvider.id}:manage">
              <li><a href="#tab-metadata" data-toggle="tab"><g:message encodeAs="HTML" code="label.metadata" /></a></li>
              <li><a href="#tab-attrfilpol" data-toggle="tab"><g:message encodeAs="HTML" code="label.attributefilter" /></a></li>
            </fr:hasPermission>
          </ul>

          <div class="tab-content">
            
            <div id="tab-crypto" class="tab-pane active">
              <g:render template="/templates/certificates/list" plugin="foundation" model="[descriptor:identityProvider]" />
              <g:render template="/templates/certificates/create" plugin="foundation" model="[descriptor:identityProvider]"/>
            </div>

            <div id="tab-endpoints" class="tab-pane">
              <div class="tabbable tabs-left">
                <ul class="nav nav-tabs">
                  <li class="active"><a href="#tab-sso" data-toggle="tab"><g:message encodeAs="HTML" code="label.ssoservices" /></a></li>
                  <li><a href="#tab-ars" data-toggle="tab"><g:message encodeAs="HTML" code="label.artifactresolutionservices" /></a></li>
                  <g:if test="${identityProvider.collaborator}">
                    <li><a href="#tab-attrs" data-toggle="tab"><g:message encodeAs="HTML" code="label.attributeservices" /></a></li>
                  </g:if>
                  <li><a href="#tab-slo" data-toggle="tab"><g:message encodeAs="HTML" code="label.sloservices" /></a></li>
                </ul>
                
                <div class="tab-content span8">
                  <div id="tab-sso" class="tab-pane active">
                    <g:render template="/templates/endpoints/list" plugin="foundation" model="[endpoints:identityProvider.singleSignOnServices, endpointType:'singleSignOnServices', minEndpoints:1]" />
                    
                    <g:render template="/templates/endpoints/create" plugin="foundation" model="[descriptor:identityProvider, endpointType:'singleSignOnServices']" />      
                  </div>

                  <div id="tab-ars" class="tab-pane">
                    <g:render template="/templates/endpoints/list" plugin="foundation" model="[endpoints:identityProvider.artifactResolutionServices, endpointType:'artifactResolutionServices']" />
                    
                    <g:render template="/templates/endpoints/create" plugin="foundation" model="[descriptor:identityProvider, endpointType:'artifactResolutionServices', indexed:true]" />
                  </div>

                  <div id="tab-slo" class="tab-pane">
                    <g:render template="/templates/endpoints/list" plugin="foundation" model="[endpoints:identityProvider.singleLogoutServices, endpointType:'singleLogoutServices']" />
                    
                    <g:render template="/templates/endpoints/create" plugin="foundation" model="[descriptor:identityProvider, endpointType:'singleLogoutServices']" />
                  </div>

                  <g:if test="${identityProvider.collaborator}">
                    <div id="tab-attrs" class="tab-pane">
                      <g:render template="/templates/endpoints/list" plugin="foundation" model="[endpoints:identityProvider.collaborator.attributeServices, endpointType:'attributeServices']" />
                      
                      <g:render template="/templates/endpoints/create" plugin="foundation" model="[descriptor:identityProvider.collaborator, endpointType:'attributeServices']" />
                    </div>
                  </g:if>
                  <g:render template="/templates/endpoints/modals" plugin="foundation" />
                </div>
              </div>
            </div>

            <div id="tab-attributes" class="tab-pane">
              <g:render template="/templates/attributes/list" plugin="foundation" model="[descriptor:identityProvider, attrs:identityProvider.sortedAttributes()]" />
              
              <g:render template="/templates/attributes/add" plugin="foundation" model="[descriptor:identityProvider]"/>
            </div>

            <div id="tab-nameidformats" class="tab-pane">
              <g:render template="/templates/nameidformats/list" plugin="foundation" model="[descriptor:identityProvider, nameIDFormats:identityProvider.nameIDFormats, containerID:'nameidformats']" />
              
              <g:render template="/templates/nameidformats/add" plugin="foundation" model="[descriptor:identityProvider, containerID:'nameidformats']"/>
            </div>

            <fr:hasPermission target="federation:management:descriptor:${identityProvider.id}:manage">
              <div id="tab-metadata" class="tab-pane">
                <g:if test="${identityProvider.functioning()}">
                  <div class="span8">
                    <p><g:message encodeAs="HTML" code="views.fr.foundation.identityprovider.show.metadata.details" /></p>
                  </div>
                  <div class="span1 offset2">
                    <a class="load-descriptor-metadata btn btn-info"><g:message encodeAs="HTML" code="label.load" /></a>
                  </div>
                  <div class="span11">
                    <pre id="descriptormetadata" class="metadata hidden"></pre>
                  </div>
                </g:if>
                <g:else>
                  <div class="alert alert-message">
                    <g:message encodeAs="HTML" code="views.fr.foundation.identityprovider.show.metadata.unavailable.details" />
                  </div>
                </g:else>
              </div>
              
              <div id="tab-attrfilpol" class="tab-pane">                        
                <div class="span8">
                  <p>
                    <g:message encodeAs="HTML" code="views.fr.foundation.identityprovider.show.attributefilter.description" />
                  </p>
                  <p>
                    <g:message code="branding.fr.federationinfo.distribute.${grailsApplication.config.federation.environment}.attributefilter" args="${[identityProvider.id as String]}" />
                  </p>
                  <p>
                    <g:message code="views.fr.foundation.identityprovider.show.attributefilter.configurationurl" />
                  </p>
                </div>
                <div class="span1 offset2">
                  <a class="load-descriptor-attrfilter btn btn-info">
                    <g:message encodeAs="HTML" code="label.load"/>
                  </a>
                </div>
                <div class="span11">
                  <pre id="descriptorattributefilter" class="metadata hidden"></pre>
                </div>
              </div>
            </fr:hasPermission>
          </div>
        </div>
      </div>

      <fr:hasAnyPermission in='["federation:management:descriptor:${identityProvider.id}:reporting" , "federation:management:reporting"]'>
        <div id="tab-reports" class="tab-pane">
          <g:render template="/templates/identityprovider/reporting" model="[idpID:identityProvider.id]" />
        </div>
      </fr:hasAnyPermission>

      <div id="tab-monitors" class="tab-pane">
        <g:render template="/templates/monitor/list" plugin="foundation" model="[roleDescriptor:identityProvider]" />
        <fr:hasPermission target="federation:management:descriptor:${identityProvider.id}:manage:monitors">
          <g:render template="/templates/monitor/create" plugin="foundation" model="[descriptor:identityProvider]" />
        </fr:hasPermission>
      </div>

      <g:if test="${identityProvider.approved}">
        <div id="tab-admins" class="tab-pane">
          <g:render template="/templates/descriptor/listreportadministration" plugin="foundation" model="[descriptor:identityProvider]" />
          <hr>
          <g:render template="/templates/descriptor/listfulladministration" plugin="foundation" model="[descriptor:identityProvider]" />
        </div>
      </g:if>

    </div>
           
    <r:script>
      var updateIdentityProviderEndpoint = "${createLink(controller:'identityProvider', action:'update', id:identityProvider.id )}";

      var contactCreateEndpoint = "${createLink(controller:'descriptorContact', action:'create', id:identityProvider.id )}";
      var contactDeleteEndpoint = "${createLink(controller:'descriptorContact', action:'delete' )}";
      var contactListEndpoint = "${createLink(controller:'descriptorContact', action:'list', id:identityProvider.id ) }";
      var contactSearchEndpoint = "${createLink(controller:'descriptorContact', action:'search')}";
      
      var certificateListEndpoint = "${createLink(controller:'roleDescriptorCrypto', action:'list', id:identityProvider.id )}";
      var certificateCreationEndpoint = "${createLink(controller:'roleDescriptorCrypto', action:'create', id:identityProvider.id)}";
      var certificateDeleteEndpoint = "${createLink(controller:'roleDescriptorCrypto', action:'delete')}";
      var certificateValidationEndpoint = "${createLink(controller:'coreUtilities', action:'validateCertificate')}";
      
      var endpointDeleteEndpoint = "${createLink(controller:'descriptorEndpoint', action:'delete')}";
      var endpointListEndpoint = "${createLink(controller:'descriptorEndpoint', action:'list', id:identityProvider.id)}";
      var endpointCreationEndpoint = "${createLink(controller:'descriptorEndpoint', action:'create', id:identityProvider.id)}";
      var endpointToggleStateEndpoint = "${createLink(controller:'descriptorEndpoint', action:'toggle')}";
      var endpointMakeDefaultEndpoint = "${createLink(controller:'descriptorEndpoint', action:'makeDefault')}";
      var endpointEditEndpoint = "${createLink(controller:'descriptorEndpoint', action:'edit')}";
      var endpointUpdateEndpoint = "${createLink(controller:'descriptorEndpoint', action:'update')}";
      
      var nameIDFormatRemoveEndpoint = "${createLink(controller:'descriptorNameIDFormat', action:'remove', id:identityProvider.id )}";
      var nameIDFormatListEndpoint = "${createLink(controller:'descriptorNameIDFormat', action:'list', id:identityProvider.id )}";
      var nameIDFormatAddEndpoint = "${createLink(controller:'descriptorNameIDFormat', action:'add', id:identityProvider.id )}";
      
      var attributeRemoveEndpoint = "${createLink(controller:'descriptorAttribute', action:'remove', id:identityProvider.id )}";
      var attributeListEndpoint = "${createLink(controller:'descriptorAttribute', action:'list', id:identityProvider.id )}";
      var attributeAddEndpoint = "${createLink(controller:'descriptorAttribute', action:'add', id:identityProvider.id )}";
      
      var searchNewAdministratorsEndpoint = "${createLink(controller:'descriptorAdministration', action:'searchNewAdministrators', id:identityProvider.id)}";
      var searchNewReportViewersEndpoint = "${createLink(controller:'descriptorAdministration', action:'searchNewReportViewers', id:identityProvider.id)}";
      var descriptorFullAdministratorGrantEndpoint = "${createLink(controller:'descriptorAdministration', action:'grantFullAdministration', id:identityProvider.id)}";
      var descriptorFullAdministratorRevokeEndpoint = "${createLink(controller:'descriptorAdministration', action:'revokeFullAdministration', id:identityProvider.id)}";
      var descriptorFullAdministratorListEndpoint = "${createLink(controller:'descriptorAdministration', action:'listFullAdministration', id:identityProvider.id)}";
      var descriptorFullAdministratorSearchEndpoint = "${createLink(controller:'descriptorAdministration', action:'searchFullAdministration', id:identityProvider.id)}";
      
      var monitorDeleteEndpoint = "${createLink(controller:'roleDescriptorMonitor', action:'delete')}";
      var monitorListEndpoint = "${createLink(controller:'roleDescriptorMonitor', action:'list', id:identityProvider.id )}";
      var monitorCreateEndpoint = "${createLink(controller:'roleDescriptorMonitor', action:'create', id:identityProvider.id )}";
      
      var attributeFilterEndpoint = "${createLink(controller:'attributeFilter', action:'generate', id:identityProvider.id )}";
      
      var descriptorMetadataEndpoint = "${createLink(controller:'metadata', action:'entity', id:identityProvider.entityDescriptor.id )}";
      
      var idpReportsConnectivityEndpoint = "${createLink(controller:'identityProviderReports', action:'connectivityjson', id:identityProvider.id)}"
      var idpReportsSessionsEndpoint = "${createLink(controller:'identityProviderReports', action:'sessionsjson', id:identityProvider.id)}"
      var idpReportsTotalsEndpoint = "${createLink(controller:'identityProviderReports', action:'totalsjson', id:identityProvider.id)}"
      var idpReportsLoginsEndpoint = "${createLink(controller:'identityProviderReports', action:'loginsjson', id:identityProvider.id)}"
    </r:script>  
  </body>
</html>
