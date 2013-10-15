<g:hasErrors>
  <div class="alert alert-message alert-danger">
    <p><strong><g:message encodeAs="HTML" code="templates.fr.service.create.errors" /></strong></p>
    <p>
      <g:message encodeAs="HTML" code="label.identifiederrors"/>
      <g:renderErrors bean="${serviceProvider}" as="list" />
      <g:renderErrors bean="${contact}" as="list"/>
      <g:renderErrors bean="${entityDescriptor}" as="list"/>
    </p>
  </div>
</g:hasErrors>

<g:if test="${g.message(code:'branding.aaf.rapid') != ''}">
  <div id="rapidconnect" class="alert alert-info">
    <g:message code="branding.aaf.rapid" />
  </div>
</g:if>

<g:form action="${saveAction}" method="post" useToken="true" class="form-horizontal validating">
  <g:hiddenField name="active" value="true"/>
  <g:hiddenField name="aa.create" value="true"/>
  <g:hiddenField name="contact.type" value="technical" />
  
  <div id="overview">
    <p><g:message code="templates.fr.serviceprovider.create.overview.details" /></p>
  </div>

  <hr>
  
  <div id="contact" class="step">
    <h3>1. <g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.contact.heading" /></h3>
    <p><g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.contact.details" /></p>
    <fieldset>
      <div class="control-group">
        <label class="control-label" for="contact.givenName"><g:message encodeAs="HTML" code="label.givenname" /></label>
        <div class="controls">
          <g:if test="${contact?.givenName}">
            <g:textField name="contact.givenName"  class="required" value="${contact?.givenName}"/>
          </g:if>
          <g:else>
            <g:textField name="contact.givenName"  class="required" value="${fr?.subject() ? fr.subject().givenName:''}"/>
          </g:else>
        </div>
      </div>

      <div class="control-group">
        <label class="control-label" for="contact.surname"><g:message encodeAs="HTML" code="label.surname" /></label>
        <div class="controls">
          <g:if test="${contact?.surname}">
            <g:textField name="contact.surname"  class="required" value="${contact?.surname}"/>
          </g:if>
          <g:else>
            <g:textField name="contact.surname"  class="required" value="${fr?.subject() ? fr.subject().surname:''}"/>
          </g:else>
        </div>
      </div>

      <div class="control-group">
        <label class="control-label" for="contact.email"><g:message encodeAs="HTML" code="label.email" /></label>
        <div class="controls">
          <g:if test="${contact?.email}">
            <g:textField name="contact.email"  class="required email" value="${contact?.email}"/>
          </g:if>
          <g:else>
            <g:textField name="contact.email"  class="required" value="${fr?.subject() ? fr.subject().email:''}"/>
          </g:else>
        </div>
      </div>
    </fieldset>
  </div>

  <hr>
  
  <div id="basic" class="step">
    <h3>2. <g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.basicinformation.heading" /></h3>
    <p><g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.basicinformation.details" /></p>

    <fieldset>
      <div class="control-group">
        <label class="control-label" for="organization.id"><g:message encodeAs="HTML" code="label.organization" /></label>
        <div class="controls">
          <g:select name="organization.id" from="${organizationList.sort{it.displayName}}" optionKey="id" optionValue="displayName" value="${organization?.id}" class="span4"/>
        </div>
      </div>
      
      <div class="control-group">
        <label class="control-label" for="sp.displayName"><g:message encodeAs="HTML" code="label.displayname" /></label>
        <div class="controls">
          <g:textField name="sp.displayName" class="required span4" value="${serviceProvider?.displayName}"/>
          <fr:tooltip code='help.fr.serviceprovider.displayname' />
        </div>
      </div>
      
      <div class="control-group">
        <label class="control-label" for="sp.description"><g:message encodeAs="HTML" code="label.description" /></label>
        <div class="controls">
          <g:textArea name="sp.description"  class="required span4" rows="8" value="${serviceProvider?.description}"/>
          <fr:tooltip code='help.fr.serviceprovider.description' />
        </div>
      </div>
      
      <div class="control-group">
        <label class="control-label" for="sp.servicedescription.connecturl"><g:message encodeAs="HTML" code="label.serviceurl" /></label>
        <div class="controls">
          <g:textField name="sp.servicedescription.connecturl" class="required url span4" value="${servicedescription?.connecturl}"/>
          <fr:tooltip code='help.fr.serviceprovider.connecturl' />
        </div>
      </div>
      
      <div class="control-group">
        <label class="control-label" for="sp.servicedescription.logourl"><g:message encodeAs="HTML" code="label.servicelogourl" /></label>
        <div class="controls">
          <g:textField name="sp.servicedescription.logourl" class="url span4" value="${servicedescription?.logourl}"/>
          <fr:tooltip code='help.fr.serviceprovider.logourl' />
        </div>
      </div>
    </fieldset>
  </div>

  <hr>

  <div id="saml" class="step">
    <h3>3. <g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.saml.heading" /></h3>
    <p><g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.saml.details" /></p>
    
    <div id="samlbasicmode" class="hero-unit">
      <h3><g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.saml.known.heading" /></h3>
      <p><g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.saml.known.descriptive" /></p>

      <fieldset>
        <div class="control-group">
          <label class="control-label" for="knownimpl"><g:message encodeAs="HTML" code="label.implementation" /></label>
          <div class="controls">
            <div id="knownimpl">
              <strong>&nbsp;&nbsp;&nbsp;&nbsp;<g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.saml.known.shib13" /></strong>&nbsp;&nbsp;<span class="label important"><g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.saml.known.shib13.descriptive" /></span></span><br>
            </div>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label" for="hostname"><g:message encodeAs="HTML" code="label.url" /></label>
          <div class="controls">
            <g:textField name="hostname" id="hostname" class="url span4" value="${hostname}" placeholder="https://sp.example.edu.au"/>
            <fr:tooltip code='help.fr.serviceprovider.hostname' />
          </div>
        </div>
      </fieldset>
    </div>

    <div class="centered">
      <h2>OR</h2>
    </div>
    
    <div id="samladvancedmode" class="hero-unit">
      <h3><g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.saml.advanced.heading" /></h3>
      <p><g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.saml.advanced.descriptive" /></p>

      <fieldset>
        <div class="control-group">
          <label class="control-label" for="entity.identifier"><g:message encodeAs="HTML" code="label.entitydescriptor" /></label>
          <div class="controls">
            <g:textField name="entity.identifier" class="required url span4" value="${entityDescriptor?.entityID}"/>
            <fr:tooltip code='help.fr.serviceprovider.entitydescriptor' />
          </div>
        </div>
      </fieldset>
      <hr>
      <fieldset>
        <div class="control-group">
          <label class="control-label" for="sp.acs.post"><g:message encodeAs="HTML" code="label.acspostendpoint" /></label>
          <div class="controls">
            <g:hiddenField name="sp.acs.post-isdefault" value="true" />
            <g:textField name="sp.acs.post" class="required url span4"  value="${httpPostACS?.location}"/>
            
            <span class="index">Index:</span>
            <g:textField name="sp.acs.post-index" class="required number index span1" value="${httpPostACS?.index}"/>
            <fr:tooltip code='help.fr.serviceprovider.acspost' />
            <br><span class="binding"><strong><g:message encodeAs="HTML" code="label.binding" /></strong>: SAML:2.0:bindings:HTTP-POST</span>
          </div>
        </div>
        
        <div class="control-group">
          <label class="control-label" for="sp.acs.artifact"><g:message encodeAs="HTML" code="label.acsartifactendpoint" /></label>
          <div class="controls">
            <g:hiddenField name="sp.acs.artifact-isdefault" value="false" />
            <g:textField name="sp.acs.artifact" class="required url span4" value="${httpArtifactACS?.location}"/>
            
            <span class="index">Index:</span>
            <g:textField name="sp.acs.artifact-index" class="required number index span1" value="${httpArtifactACS?.index}"/>
            <fr:tooltip code='help.fr.serviceprovider.acsartifcate' />
            <br><span class="binding"><strong><g:message encodeAs="HTML" code="label.binding" /></strong>: SAML:2.0:bindings:HTTP-Artifact</span>
          </div>
        </div>
      </fieldset>
      <hr>
      <fieldset>
        <div class="control-group">
          <label class="control-label" for="sp.slo.artifact"><g:message encodeAs="HTML" code="label.sloartifactendpoint" /></label>
          <div class="controls">
            <g:textField name="sp.slo.artifact" class="samloptional url span4" value="${sloArtifact?.location}"/>
            <fr:tooltip code='help.fr.serviceprovider.sloartifact' />
            <br><span class="binding"><strong><g:message encodeAs="HTML" code="label.binding" /></strong>: SAML:2.0:bindings:HTTP-Artifact</span>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label" for="sp.slo.redirect"><g:message encodeAs="HTML" code="label.sloredirectendpoint" /></label>
          <div class="controls">
            <g:textField name="sp.slo.redirect" class="samloptional url span4" value="${sloRedirect?.location}"/>
            <fr:tooltip code='help.fr.serviceprovider.sloredriect' />
            <br><span class="binding"><strong><g:message encodeAs="HTML" code="label.binding" /></strong>: SAML:2.0:bindings:HTTP-Redirect</span>
          </div>
        </div>
        
        <div class="control-group">
          <label class="control-label" for="sp.slo.soap"><g:message encodeAs="HTML" code="label.slosoapendpoint" /></label>
          <div class="controls">
            <g:textField name="sp.slo.soap" class="samloptional url span4" value="${sloSOAP?.location}"/>
            <fr:tooltip code='help.fr.serviceprovider.slosoap' />
            <br><span class="binding"><strong><g:message encodeAs="HTML" code="label.binding" /></strong>: SAML:2.0:bindings:SOAP</span>
          </div>
        </div>
        
        <div class="control-group">
          <label class="control-label" for="sp.slo.post"><g:message encodeAs="HTML" code="label.slopostendpoint" /></label>
          <div class="controls">
            <g:textField name="sp.slo.post" class="samloptional url span4"  value="${sloPost?.location}"/>
            <fr:tooltip code='help.fr.serviceprovider.slopost' />
            <br><span class="binding"><strong><g:message encodeAs="HTML" code="label.binding" /></strong>: SAML:2.0:bindings:HTTP-POST</span>
          </div>
        </div>
      </fieldset>
      <hr>
      <fieldset>
        <div class="control-group">
          <label class="control-label" for="sp.drs"><g:message encodeAs="HTML" code="label.drsendpoint" /></label>
          <div class="controls">
            <g:textField name="sp.drs" class="samloptional url span4" value="${discoveryResponseService?.location}"/>
            <g:hiddenField name="sp.drs-isdefault" value="true" />
            <fr:tooltip code='help.fr.serviceprovider.disco' />
            <br><span class="binding"><strong><g:message encodeAs="HTML" code="label.binding" /></strong>: SAML:profiles:SSO:idp-discovery-protocol</span>
          </div>
        </div>
      </fieldset>
      <hr>
      <fieldset>
        <div class="control-group">
          <label class="control-label" for="sp.mnid.artifact"><g:message encodeAs="HTML" code="label.mnidartifactendpoint" /></label>
          <div class="controls">
            <g:textField name="sp.mnid.artifact" class="samloptional url span4" value="${mnidArtifact?.location}"/>
            <fr:tooltip code='help.fr.serviceprovider.mnidaritfact' />
            <br><span class="binding"><strong><g:message encodeAs="HTML" code="label.binding" /></strong>: SAML:2.0:bindings:HTTP-Artifact</span>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label" for="sp.mnid.redirect"><g:message encodeAs="HTML" code="label.mnidredirectendpoint" /></label>
          <div class="controls">
            <g:textField name="sp.mnid.redirect" class="samloptional url span4" value="${mnidRedirect?.location}"/>
            <fr:tooltip code='help.fr.serviceprovider.mnidredirect' />
            <br><span class="binding"><strong><g:message encodeAs="HTML" code="label.binding" /></strong>: SAML:2.0:bindings:HTTP-Redirect</span>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label" for="sp.mnid.soap"><g:message encodeAs="HTML" code="label.mnidsoapendpoint" /></label>
          <div class="controls">
            <g:textField name="sp.mnid.soap" class="samloptional url span4" value="${mnidSOAP?.location}"/>
            <fr:tooltip code='help.fr.serviceprovider.mnidsoap' />
            <br><span class="binding"><strong><g:message encodeAs="HTML" code="label.binding" /></strong>: SAML:2.0:bindings:SOAP</span>
          </div>
        </div>

        <div class="control-group">
          <label class="control-label" for="sp.mnid.post"><g:message encodeAs="HTML" code="label.mnidpostendpoint" /></label>
          <div class="controls">
            <g:textField name="sp.mnid.post" class="samloptional url span4" value="${mnidPost?.location}"/>
            <fr:tooltip code='help.fr.serviceprovider.mnidpost' />
            <br><span class="binding"><strong><g:message encodeAs="HTML" code="label.binding" /></strong>: SAML:2.0:bindings:HTTP-POST</span>
          </div>
        </div>
      </fieldset>
    </div>
  </div>

  <hr>
  
  <div id="crypto" class="step">
    <h3>4. <g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.crypto.heading" /></h3>
    <p><g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.crypto.details" /></p>

    <fieldset>
      <div class="control-group">
          <label class="control-label" for="newcertificatedata"><g:message encodeAs="HTML" code="label.certificate" /></label>
        <div class="controls">
          <div id="newcertificatedetails">
          </div>
          <g:hiddenField name="sp.crypto.sig" value="${true}" />
          <g:hiddenField name="sp.crypto.enc" value="${true}" />
          <g:textArea name="cert" id="cert" class="cert required" rows="25" cols="60" value="${certificate}"/>
          <fr:tooltip code='help.fr.serviceprovider.certificate' />
        </div>
      </div>
  </div>

  <hr>
  
  <div id="attributesupport" class="step">
    <h3>5. <g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.attributesupport.heading" /></h3>
    <p><g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.attributesupport.details" /></p>
    <p><strong><g:message encodeAs="HTML" code="help.fr.serviceprovider.attribute.isrequired" /></strong></p>
    <fieldset>
      <table class="table table-striped borderless">
        <tbody>
          <tr>
            <th class="span6"><g:message encodeAs="HTML" code="label.name" /></th>
            <th class="centered"><g:message encodeAs="HTML" code="label.category" /></th>
            <th class="centered"><g:message encodeAs="HTML" code="label.requested" /></th>
            <th class="centered"><g:message encodeAs="HTML" code="label.reasonrequested" /></th>
            <th class="centered"><g:message encodeAs="HTML" code="label.required" /></th>
          </tr>
          <g:each in="${attributeList}" var="attr" status="i">
            <g:if test="${!attr.specificationRequired}">
              <g:set var="ra" value="${supportedAttributes.find {it.base == attr}}" />
              <tr>
                <td>
                  <strong class="highlight">${fieldValue(bean: attr, field: "name")}</strong><br>
                  <code>oid:${fieldValue(bean: attr, field: "oid")}</code>
                  <br><br><em>${fieldValue(bean: attr, field: "description")}</em>
                </td>
                <td class="centered">
                  ${fieldValue(bean: attr, field: "category.name")}
                </td>
                <td class="centered">
                  <g:checkBox name="sp.attributes.${attr.id}.requested" checked="${ra}" class="request-attribute" data-attrid="${attr.id}" />
                </td>
                <td class="centered">
                  <input name="sp.attributes.${attr.id}.reasoning" size="40" value="${ra?.reasoning}" rel="twipsy" data-original-title="${g.message(encodeAs:"HTML", code:'help.fr.serviceprovider.attribute.reason')}" data-placement="right" class="reason-attribute" data-attrid="${attr.id}"/>
                </td>
                <td class="centered">
                  <g:checkBox name="sp.attributes.${attr.id}.required" id="spattributes${attr.id}required" checked="${ra?.isRequired}" rel="twipsy" data-original-title="${g.message(encodeAs:"HTML", code:'help.fr.serviceprovider.attribute.isrequired')}" data-placement="right" class="require-attribute" data-attrid="${attr.id}" />
                </td>
              </tr>
            </g:if>
          </g:each>
        </tbody>
      </table>
    </fieldset>
  </div>
  
  <hr>
  
  <div id="creationsummary" class="step">
    <h3>6. <g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.summary.heading" /></h3>
    <p><g:message encodeAs="HTML" code="templates.fr.serviceprovider.create.summary.details" /></p>

    <div class="form-action">
        <button type="submit" name="submit" value="submit" class="btn btn-success btn-large"><g:message encodeAs="HTML" code="label.submit"/></button>
    </div>
  </div>

</g:form>

<r:script>
  var certificateValidationEndpoint = "${createLink(controller:'coreUtilities', action:'validateCertificate')}";
  var knownSPImplEndpoint = "${createLink(controller:'coreUtilities', action:'knownSPImpl')}";
  
  var newCertificateValid = false;
  var knownSPImpl;
  var currentImpl;
  
  $(function() {
    $.ajax({
      type: "GET",
      cache: false,
      dataType: 'json',
      url: knownSPImplEndpoint,
      success: function(res) {
        knownSPImpl = res;
        
        $.each(knownSPImpl, function(key, value) {
          if(knownSPImpl[key].selected) {
            currentImpl = key
            $('<input type="radio" class="currentimpl" name="knownimpls" checked value='+key+'> <strong>' + knownSPImpl[key].displayName + '</strong><br>').appendTo($("#knownimpl"));
          }
          else
            $('<input type="radio" class="currentimpl" name="knownimpls" value='+key+'> <strong>' + knownSPImpl[key].displayName + '</strong><br>').appendTo($("#knownimpl"));
        });
        
        $('input.currentimpl').change(function() {
          currentImpl = $(this).val();
          fr.configureServiceProviderSAML($('#hostname').val());
        });
        },
        error: function (xhr, ajaxOptions, thrownError) {
        
        }
    });
    
    $('#hostname').alphanumeric({nocaps:true, ichars:';'});

    $('form').validate({
      ignore: ":disabled",
      keyup: false
    });
    jQuery.validator.addMethod("validcert", function(value, element, params) { 
      fr.validateCertificate();
      return valid_certificate; 
    }, jQuery.format("PEM data invalid"));
    
    $('#cert').rules("add", {
         required: true,
         validcert: true
    });
    
    $('#hostname').bind('change',  function() {
      var val = $.trim($(this).val());
      if( val.indexOf('/', val.length - 1) !== -1 && val.length > 9)
        val = val.substring(0, val.length - 1);
      
      if( val.indexOf('/shibboleth', val.length - 11) !== -1 && val.length > 9)
        val = val.substring(0, val.length - 11);

      fr.configureServiceProviderSAML(val);
      $(this).val(val);
    });
  });
</r:script>
