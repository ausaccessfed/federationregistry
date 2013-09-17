package aaf.fr.metadata

import java.text.SimpleDateFormat
import org.springframework.transaction.annotation.*
import aaf.fr.foundation.*

/**
 * Provides methods to generate Metadata for the entire federation or specific components.
 *
 * @author Bradley Beddoes
 */
class MetadataGenerationService {
  
  def populateSchema() {
    ["xmlns":"urn:oasis:names:tc:SAML:2.0:metadata", "xmlns:xsi":"http://www.w3.org/2001/XMLSchema-instance", 'xmlns:saml':'urn:oasis:names:tc:SAML:2.0:assertion', 'xmlns:shibmd':'urn:mace:shibboleth:metadata:1.0',
      'xmlns:ds':'http://www.w3.org/2000/09/xmldsig#', "xsi:schemaLocation":"urn:oasis:names:tc:SAML:2.0:metadata saml-schema-metadata-2.0.xsd urn:mace:shibboleth:metadata:1.0 shibboleth-metadata-1.0.xsd http://www.w3.org/2000/09/xmldsig# xmldsig-core-schema.xsd"]
  }
  
  def localizedName(builder, type, lang, content) {
    builder."$type"('xml:lang':lang, content)
  }

  def localizedUri(builder, type, lang, content) {
    builder."$type"('xml:lang':lang, content)
  }
  
  def organization(builder, organization) {
    builder.Organization() {
      localizedName(builder, "OrganizationName", organization.lang, organization.name)
      localizedName(builder, "OrganizationDisplayName", organization.lang, organization.displayName)
      localizedUri(builder, "OrganizationURL", organization.lang, organization.url)
    }
  }
  
  def contactPerson(builder, contactPerson) {
    // Ignores non SAML standard contact types and contacts marked as 'other'
    def contactType
    if(["technical", "support", "administrative", "billing"].contains(contactPerson.type.name)) {
      
      builder.ContactPerson(contactType:contactPerson.type.name) {
        if(contactPerson.contact.organization)
          Company(contactPerson.contact.organization.displayName)
        GivenName(contactPerson.contact.givenName)
        SurName(contactPerson.contact.surname)
        EmailAddress("mailto:${contactPerson.contact.email}")
        if(contactPerson.contact.workPhone)
          TelephoneNumber(contactPerson.contact.workPhone)
        if(contactPerson.contact.homePhone)
          TelephoneNumber(contactPerson.contact.homePhone)
        if(contactPerson.contact.mobilePhone)
          TelephoneNumber(contactPerson.contact.mobilePhone)
      }
    }
  }

  private String processCertificateData(String certData) {
    def data = certData.trim().normalize()
    if(data.startsWith('-----BEGIN CERTIFICATE-----'))
      data = data.replace('-----BEGIN CERTIFICATE-----', '')
    if(data.endsWith('-----END CERTIFICATE-----'))
      data = data.replace('-----END CERTIFICATE-----', '')

    // Ensure any hanging new lines are gone and make XML neat with
    // leading and trailing newlines.
    "\n${data.trim()}\n"  
  }
  
  def caKeyInfo(builder, keyInfo) {
    builder.'ds:KeyInfo'('xmlns:ds':'http://www.w3.org/2000/09/xmldsig#') {
      if(keyInfo.keyName)
        'ds:KeyName'(keyInfo.keyName)
      'ds:X509Data'() {
        def data = processCertificateData(keyInfo.certificate.data)
        'ds:X509Certificate'(data)
      }
    }
  }
  
  def keyInfo(builder, keyInfo) {
    builder.'ds:KeyInfo'('xmlns:ds':'http://www.w3.org/2000/09/xmldsig#') {
      if(keyInfo.keyName)
        'ds:KeyName'(keyInfo.keyName)
      'ds:X509Data'() {
        def data = processCertificateData(keyInfo.certificate.data)
        'ds:X509Certificate'(data)
      }
    }
  }
  
  def keyDescriptor(builder, keyDescriptor) {
    if(!keyDescriptor.disabled) {
      builder.KeyDescriptor(use: keyDescriptor.keyType) {
        keyInfo(builder, keyDescriptor.keyInfo)
        if(keyDescriptor.encryptionMethod) {
          EncryptionMethod(Algorithm:keyDescriptor.encryptionMethod.algorithm) {
            if(keyDescriptor.encryptionMethod.keySize)
              'xenc:KeySize'('xmlns:xenc':'http://www.w3.org/2001/04/xmlenc#', keyDescriptor.encryptionMethod.keySize)
            if(keyDescriptor.encryptionMethod.oaeParams)
            'xenc:OAEPparams'('xmlns:xenc':'http://www.w3.org/2001/04/xmlenc#', keyDescriptor.encryptionMethod.oaeParams)
          }
        }
      }
    }
  }
  
  @Transactional(readOnly = true)
  def entitiesDescriptor(builder, all, minimal, roleExtensions, entitiesDescriptor, validUntil, certificateAuthorities) {
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))

    SimpleDateFormat idf = new SimpleDateFormat("_yyyyMMdd'T'HHmmss'Z'")
    idf.setTimeZone(TimeZone.getTimeZone("UTC"))

    def currently = new Date()
    
    def params = [:]
    params.putAll(populateSchema())
    params.ID =  idf.format(currently)
    params.validUntil = sdf.format(validUntil)
    params.Name = entitiesDescriptor.name
      
    builder.EntitiesDescriptor(params) {
      if(certificateAuthorities && certificateAuthorities.size() != 0) {
        builder.Extensions() {
          builder."shibmd:KeyAuthority"("xmlns:shibmd":"urn:mace:shibboleth:metadata:1.0", VerifyDepth: 5) {
            certificateAuthorities?.sort{it.id}.each { ca ->
              caKeyInfo(builder, ca)
            }
          }
        }
      }
      entitiesDescriptor.entitiesDescriptors?.sort{it.id}?.each { eds ->
        this.entitiesDescriptor(builder, all, minimal, roleExtensions, eds)
      }
      entitiesDescriptor.entityDescriptors?.sort{it.entityID}.each { ed ->
        entityDescriptor(builder, all, minimal, roleExtensions, ed, false)
      }
    }
  }
  
  @Transactional(readOnly = true)
  def entitiesDescriptor(builder, all, minimal, roleExtensions, entitiesDescriptor) {
    builder.EntitiesDescriptor() {
      entitiesDescriptor.entitiesDescriptors?.sort{it.id}?.each { eds ->
        this.entitiesDescriptor(builder, all, minimal, roleExtensions, eds)
      }
      entitiesDescriptor.entityDescriptors?.sort{it.entityID}.each { ed ->
        entityDescriptor(builder, all, minimal, roleExtensions, ed, false)
      }
    }
  }
  
  @Transactional(readOnly = true)
  def entityDescriptor(builder, all, minimal, roleExtensions, entityDescriptor, schema) {
    if(all || entityDescriptor.functioning() ) {
      if(!all && entityDescriptor.empty()) {
        log.warn "Not rendering $entityDescriptor no children nodes detected"
      }
      else {
        def params = [:]
        params.entityID = entityDescriptor.entityID
        if(schema) params.putAll(populateSchema()) 
          
        builder.EntityDescriptor(params) {
          entityDescriptor.idpDescriptors?.sort{it.id}?.each { idp -> idpSSODescriptor(builder, all, minimal, roleExtensions, idp) }
          entityDescriptor.spDescriptors?.sort{it.id}?.each { sp -> spSSODescriptor(builder, all, minimal, roleExtensions, sp) }
          entityDescriptor.attributeAuthorityDescriptors?.sort{it.id}?.each { aa -> attributeAuthorityDescriptor(builder, all, minimal, roleExtensions, aa)}

          organization(builder, entityDescriptor.organization)
        }
      }
    }
  }
    
  def endpoint(builder, all, minimal, type, endpoint) {
    if(all || endpoint.functioning() ) {
      if(!endpoint.responseLocation)
        builder."$type"(Binding: endpoint.binding.uri, Location:endpoint.location)
      else
        builder."$type"(Binding: endpoint.binding.uri, Location:endpoint.location, ResponseLocation: endpoint.responseLocation)
    }
  }
  
  def indexedEndpoint(builder, all, minimal, type, endpoint) { 
    if(all || endpoint.functioning() ) {
      if(!endpoint.responseLocation)
        builder."$type"(Binding: endpoint.binding.uri, Location:endpoint.location, index:endpoint.index, isDefault:endpoint.isDefault)
      else
        builder."$type"(Binding: endpoint.binding.uri, Location:endpoint.location, ResponseLocation: endpoint.responseLocation, index:endpoint.index, isDefault:endpoint.isDefault)
    }
  }
  
  def samlURI(builder, type, uri) {
    builder."$type"(uri.uri)
  }
  
  def attribute(builder, attr) {
    if(attr.base.nameFormat?.uri) {
      builder.'saml:Attribute'(NameFormat:attr.base.nameFormat?.uri, Name: "urn:oid:${attr.base.oid}", FriendlyName:attr.base.name) {
        attr.values?.sort{it?.value}.each {
          'saml:AttributeValue'(it.value)
        }
      }
    }
    else {
      builder.'saml:Attribute'(NameFormat: 'urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified', Name: "urn:oid:${attr.base.oid}", FriendlyName:attr.base.name) {
        attr.values?.sort{it?.value}.each {
          'saml:AttributeValue'(it.value)
        }
      }
    }
  }
  
  def requestedAttribute(builder, all, minimal, attr) {
    if(all || attr.approved) {
      if(!attr.base.specificationRequired || (attr.base.specificationRequired && attr.values?.size() > 0)) {
        if(attr.base.nameFormat?.uri) {
          builder.RequestedAttribute(NameFormat:attr.base.nameFormat?.uri, Name: "urn:oid:${attr.base.oid}", FriendlyName:attr.base.name, isRequired:attr.isRequired) {
            attr.values?.sort{it?.value}.each {
              'saml:AttributeValue'(it.value)
            }
          }
        } else {
          builder.RequestedAttribute(NameFormat: 'urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified', Name: "urn:oid:${attr.base.oid}", FriendlyName:attr.base.name, isRequired:attr.isRequired) {
            attr.values?.sort{it?.value}.each {
              'saml:AttributeValue'(it.value)
            }
          }
        }
      } else {
        log.error "Not rendering $attr representing ${attr.base} because no values have been specified"
      }
    }
  }
  
  def attributeConsumingService(builder, all, minimal, acs, index) {
    if(acs.serviceNames?.size() == 0) {
      log.warn "Attribute Consuming Service with no serviceName can't be populated to metadata"
      return
    }
    if(acs.requestedAttributes?.size() == 0) {
      log.warn "Attribute Consuming Service with no requested attributes can't be populated to metadata"
      return
    }
    if(acs.requestedAttributes.findAll{ it.approved }.size() < 1 ) {
      log.warn "Attribute Consuming Service with no approved requested attributes can't be populated to metadata"
      return
    }
    
    builder.AttributeConsumingService(index:index, isDefault:acs.isDefault) {
      acs.serviceNames?.sort{it}.each {
        localizedName(builder, "ServiceName", acs.lang, it)
      }
      acs.requestedAttributes?.sort{it.base.name}.each{ attr -> requestedAttribute(builder, all, minimal, attr)}
    }
  }
  
  def roleDescriptor(builder, all, minimal, roleExtensions, roleDescriptor) {
    if(roleExtensions)
      "${roleDescriptor.class.name.split('\\.').last()}Extensions"(builder, all, roleDescriptor)
    roleDescriptor.keyDescriptors?.sort{it.id}.each{keyDescriptor(builder, it)}   
    if(!minimal) {
      roleDescriptor.contacts?.sort{it.id}.each{cp -> contactPerson(builder, cp)}
    }
  }
  
  def ssoDescriptor(builder, all, minimal, ssoDescriptor) {
    ssoDescriptor.artifactResolutionServices?.sort{it.id}.each{ars -> indexedEndpoint(builder, all, minimal, "ArtifactResolutionService", ars)}
    ssoDescriptor.singleLogoutServices?.sort{it.id}.each{sls -> endpoint(builder, all, minimal, "SingleLogoutService", sls)}
    ssoDescriptor.manageNameIDServices?.sort{it.id}.each{mnids -> endpoint(builder, all, minimal, "ManageNameIDService", mnids)}
    ssoDescriptor.nameIDFormats?.sort{it.id}.each{nidf -> samlURI(builder, "NameIDFormat", nidf)}
  }
  
  def idpSSODescriptor(builder, all, minimal, roleExtensions, idpSSODescriptor) {
    if(!idpSSODescriptor.attributeAuthorityOnly) {
      if(all || idpSSODescriptor.functioning() ) {
        builder.IDPSSODescriptor(protocolSupportEnumeration: idpSSODescriptor.protocolSupportEnumerations.sort{it.uri}.collect({it.uri}).join(' ')) {
        roleDescriptor(builder, all, minimal, roleExtensions, idpSSODescriptor)
        ssoDescriptor(builder, all, minimal, idpSSODescriptor)

        idpSSODescriptor.singleSignOnServices?.sort{it.id}.each{ sso -> endpoint(builder, all, minimal, "SingleSignOnService", sso) }
        idpSSODescriptor.nameIDMappingServices?.sort{it.id}.each{ nidms -> endpoint(builder, all, minimal, "NameIDMappingService", nidms) }
        idpSSODescriptor.assertionIDRequestServices?.sort{it.id}.each{ aidrs -> endpoint(builder, all, minimal, "AssertionIDRequestService", aidrs) }
        idpSSODescriptor.attributeProfiles?.sort{it.id}.each{ ap -> samlURI(builder, "AttributeProfile", ap) }
        if(!minimal)
          idpSSODescriptor.attributes?.sort{it.base.name}.each{ attr -> attribute(builder, attr)}
        }
      }
    }
  }
  
  def spSSODescriptor(builder, all, minimal, roleExtensions, spSSODescriptor) {
    if(all || spSSODescriptor.functioning() ) {
      builder.SPSSODescriptor(protocolSupportEnumeration: spSSODescriptor.protocolSupportEnumerations.sort{it.uri}.collect({it.uri}).join(' ')) {
        roleDescriptor(builder, all, minimal, roleExtensions, spSSODescriptor)
        ssoDescriptor(builder, all, minimal, spSSODescriptor)
      
        spSSODescriptor.assertionConsumerServices?.sort{it.id}.each{ ars -> indexedEndpoint(builder, all, minimal, "AssertionConsumerService", ars) }
        spSSODescriptor.attributeConsumingServices?.sort{it.id}.eachWithIndex{ acs, i -> 
          if(acs.serviceNames?.size() > 0 && acs.requestedAttributes?.size() > 0 )
            attributeConsumingService(builder, all, minimal, acs, i+1)
          else
            log.warn "$acs for $spSSODescriptor will not be rendered because it either has no names defined or no attributes being requested"
        }
      }
    }
  }
  
  def attributeAuthorityDescriptor(builder, all, minimal, roleExtensions, aaDescriptor) {
    if(all || aaDescriptor.functioning() ) {
      if(aaDescriptor.collaborator) {
        if(all || aaDescriptor.collaborator.functioning() ) {
          builder.AttributeAuthorityDescriptor(protocolSupportEnumeration: aaDescriptor.protocolSupportEnumerations.sort{it.uri}.collect({it.uri}).join(' ')) {
            // We don't currently provide direct AA manipulation to reduce general end user complexity.
            // So where a collaborative relationship exists we use all common data from the IDP to render the AA
            roleDescriptor(builder, all, minimal, roleExtensions, aaDescriptor.collaborator)  
            aaDescriptor.attributeServices?.sort{it.id}.each{ attrserv -> endpoint(builder, all, minimal, "AttributeService", attrserv) }
            aaDescriptor.collaborator.assertionIDRequestServices?.sort{it.id}.each{ aidrs -> endpoint(builder, all, minimal, "AssertionIDRequestService", aidrs) }
            aaDescriptor.collaborator.nameIDFormats?.sort{it.id}.each{ nidf -> samlURI(builder, "NameIDFormat", nidf) }
            aaDescriptor.collaborator.attributeProfiles?.sort{it.id}.each{ ap -> samlURI(builder, "AttributeProfile", ap) }
            if(!minimal)
              aaDescriptor.collaborator.attributes?.sort{it.base.name}.each{ attr -> attribute(builder, attr) }
          }
        }
      }
      else {
        builder.AttributeAuthorityDescriptor(protocolSupportEnumeration: aaDescriptor.protocolSupportEnumerations.sort{it.uri}.collect({it.uri}).join(' ')) {
          roleDescriptor(builder, all, minimal, roleExtensions, aaDescriptor) 
          aaDescriptor.attributeServices?.sort{it.id}.each{ attrserv -> endpoint(builder, all, minimal, "AttributeService", attrserv) }
          aaDescriptor.assertionIDRequestServices?.sort{it.id}.each{ aidrs -> endpoint(builder, all, minimal, "AssertionIDRequestService", aidrs) }
          aaDescriptor.nameIDFormats?.sort{it.id}.each{ nidf -> samlURI(builder, "NameIDFormat", nidf) }
          aaDescriptor.attributeProfiles?.sort{it.id}.each{ ap -> samlURI(builder, "AttributeProfile", ap) }
          aaDescriptor.attributes?.sort{it.base.name}.each{ attr -> attribute(builder, attr) }
        }
      }
    }
  }
  
  def IDPSSODescriptorExtensions(builder, all, roleDescriptor) {
    boolean isRegex = roleDescriptor.scope.startsWith('^') && roleDescriptor.scope.endsWith('$')
    builder.Extensions() {
      builder."shibmd:Scope" (regexp:isRegex, roleDescriptor.scope)
    }
  }
  
  def SPSSODescriptorExtensions(builder, all, spSSODescriptor) {
    def funcDiscoveryResponseServices = spSSODescriptor.discoveryResponseServices?.findAll{it.functioning()}
    if(all || funcDiscoveryResponseServices) {
      builder.Extensions() {
        spSSODescriptor.discoveryResponseServices?.sort{it.id}.each { endpoint ->
          if(all || endpoint.functioning() ) {
            builder."dsr:DiscoveryResponse"("xmlns:dsr":"urn:oasis:names:tc:SAML:profiles:SSO:idp-discovery-protocol", Binding: endpoint.binding.uri, Location:endpoint.location, index:endpoint.index, isDefault:endpoint.isDefault)
          }
        }
      }
    }
  }
  
  def AttributeAuthorityDescriptorExtensions(builder, all, roleDescriptor) {
    builder.Extensions() {
      builder."shibmd:Scope" (regexp:false, roleDescriptor.scope)
    } 
  }
  
}
