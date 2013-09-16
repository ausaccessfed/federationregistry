package aaf.fr.foundation

import grails.plugin.spock.*

class AttributeAuthorityDescriptorSpec extends IntegrationSpec {

	def "Ensure functioning operates as expected with collaborator"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:true)
		def idp = new IDPSSODescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, collaborator:idp, active:true, approved:true)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)

		then:
		idp.functioning()
		aa.functioning()
	}

  def "Ensure functioning operates as expected with collaborator but no functioning attributeServices"() {
    when:
    def org = new Organization(active:true, approved:true)
    def ed = new EntityDescriptor(organization:org, active:true, approved:true)
    def idp = new IDPSSODescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)
    def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, collaborator:idp, active:true, approved:true)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:false, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)

    then:
    idp.functioning()
    !aa.functioning()
  }
	
	def "Ensure functioning operates as expected without collaborator"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)

		then:
		aa.functioning()
	}

  def "Ensure functioning operates as expected without collaborator but no functioning attributeServices"() {
    when:
    def org = new Organization(active:true, approved:true)
    def ed = new EntityDescriptor(organization:org, active:true, approved:true)
    def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:false, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)

    then:
    !aa.functioning()
  }
	
	def "Ensure functioning fails when not active as expected with collaborator"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:true)
		def idp = new IDPSSODescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, collaborator:idp, active:false, approved:true)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)
		
		then:
		idp.functioning()
		!aa.functioning()
	}
	
	def "Ensure functioning fails as expected with collaborator not active"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new IDPSSODescriptor(organization:org, active:true, approved:true)
		def idp = new IDPSSODescriptor(organization:org, entityDescriptor:ed, active:false, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, collaborator:idp, active:true, approved:true)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)
		
		then:
		!idp.functioning()
		!aa.functioning()
	}
	
	def "Ensure functioning fails when not active as expected without collaborator"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:false, approved:true)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)
		
		then:
		!aa.functioning()
	}
	
	def "Ensure functioning fails when not approved as expected with collaborator"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:true)
		def idp = new IDPSSODescriptor(organization:org, entityDescriptor:ed, active:true, approved:false)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, collaborator:idp, active:false, approved:true)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)
		
		then:
		!idp.functioning()
		!aa.functioning()
	}
	
	def "Ensure functioning fails as expected with collaborator not approved"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new IDPSSODescriptor(organization:org, active:true, approved:true)
		def idp = new IDPSSODescriptor(organization:org, entityDescriptor:ed, active:true, approved:false)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, collaborator:idp, active:true, approved:true)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)
		
		then:
		!idp.functioning()
		!aa.functioning()
	}
	
	def "Ensure functioning fails when not approved as expected without collaborator"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:true, approved:false)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)
		
		then:
		!aa.functioning()
	}
	
	def "Ensure functioning fails when ED not functioning as expected with collaborator"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:false)
		def idp = new IDPSSODescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, collaborator:idp, active:false, approved:true)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)
		
		then:
		!idp.functioning()
		!aa.functioning()
	}
	
	def "Ensure functioning fails when ED not functioning as expected without collaborator"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:false)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:false, approved:true)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)
		
		then:
		!aa.functioning()
	}
	
	def "Ensure functioning fails when Org not functioning as expected with collaborator"() {
		when:
		def org = new Organization(active:false, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:false)
		def idp = new IDPSSODescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, collaborator:idp, active:false, approved:true)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)
		
		then:
		!idp.functioning()
		!aa.functioning()
	}
	
	def "Ensure functioning fails when Org not functioning as expected without collaborator"() {
		when:
		def org = new Organization(active:false, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:false, approved:true)

    def soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
    def attrService = new AttributeService(active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(attrService)
		
		then:
		!aa.functioning()
	}

}
