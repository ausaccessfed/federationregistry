<div id="editor-identityprovider" class="revealable">
  <g:form action="update" id="${identityProvider.id}" method="PUT" class="form-horizontal validating">

    <fieldset>
      <div class="control-group">
        <label class="control-label" for="idp.status"><g:message encodeAs="HTML" code="label.status" /></label>
        <div class="controls">
          <input type="radio" name="idp.status" value="true" ${identityProvider.active ? "checked" : ''} />
          <g:message encodeAs="HTML" code="label.active"/>
        
          <input type="radio" name="idp.status" value="false" ${identityProvider.active ? '' : "checked"}/>
          <g:message encodeAs="HTML" code="label.inactive"/>

          <fr:tooltip code='help.fr.identityprovider.status' />
        </div>
      </div>
      
      <div class="control-group">
        <label class="control-label" for="idp.displayName"><g:message encodeAs="HTML" code="label.displayname" /></label>
        <div class="controls">
          <g:textField name="idp.displayName" value="${identityProvider.displayName}" class="required span4" minlength="3" maxlength="255" />
          <fr:tooltip code='help.fr.identityprovider.displayname' />
        </div>
      </div>

      <div class="control-group">
        <label class="control-label" for="idp.description"><g:message encodeAs="HTML" code="label.description" /></label>
        <div class="controls">
          <g:textArea name="idp.description"  value="${identityProvider.description}" class="required span4" minlength="4" rows="8" cols="36" maxlength="2000"/>
          <fr:tooltip code='help.fr.identityprovider.description' />
        </div>
      </div>

      <div class="control-group">
        <label class="control-label" for="idp.autoAcceptServices"><g:message encodeAs="HTML" code="label.usesaa" /></label>
        <div class="controls">

          <input type="radio" name="idp.autoacceptservices" value="true" ${identityProvider.autoAcceptServices ? "checked" : ''} />
          <g:message encodeAs="HTML" code="label.yes"/>
        
          <input type="radio" name="idp.autoacceptservices" value="false" ${identityProvider.autoAcceptServices ? '' : "checked"}/>
          <g:message encodeAs="HTML" code="label.no"/>

          <fr:tooltip code='help.fr.identityprovider.usesaa' />
        </div>
      </div>

      <div class="control-group">
        <label class="control-label" for="idp.attributeAuthorityOnly"><g:message encodeAs="HTML" code="label.attributeauthorityonly" /></label>
        <div class="controls">

          <input type="radio" name="idp.attributeauthorityonly" value="true" ${identityProvider.attributeAuthorityOnly ? "checked" : ''} />
          <g:message encodeAs="HTML" code="label.yes"/>

          <input type="radio" name="idp.attributeauthorityonly" value="false" ${identityProvider.attributeAuthorityOnly ? '' : "checked"}/>
          <g:message encodeAs="HTML" code="label.no"/>

          <fr:tooltip code='help.fr.identityprovider.attributeauthorityonly' />
        </div>
      </div>

      <div class="control-group">
        <label class="control-label" for="idp.scope"><g:message encodeAs="HTML" code="label.scope" /></label>
        <div class="controls">
          <g:textField name="idp.scope"  value="${identityProvider.scope}" class="required span4" size="50" class="required" minlength="4" maxlength="255"/>
          <fr:tooltip code='help.fr.identityprovider.scope' />
        </div>
      </div>


      <div class="form-actions">
        <button type="submit" class="edit-identityprovider btn btn-success"><g:message encodeAs="HTML" code="label.save"/></button>
        <a class="cancel-edit-identityprovider btn"><g:message encodeAs="HTML" code="label.cancel"/></a>
      </div>

    </fieldset>
  </g:form>
</div>
