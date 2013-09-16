package aaf.fr.foundation

import grails.plugin.spock.*

class AttributeServiceSpec extends IntegrationSpec {
  def soap

  def setup() {
    soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
  }

	def "Ensure functioning operates"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)
    def ep = new AttributeService(descriptor:aa, active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(ep)

		then:
		ep.functioning()
	}

	def "Ensure functioning fails when not active"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)
		def ep = new AttributeService(descriptor:aa, active:false, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)

		then:
		!ep.functioning()
	}

	def "Ensure functioning fails when unapproved"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)
		def ep = new AttributeService(descriptor:aa, active:true, approved:false, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)

		then:
		!ep.functioning()
	}

	def "Ensure functioning fails when not active and unapproved"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)
		def ep = new AttributeService(descriptor:aa, active:false, approved:false, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)

		then:
		!ep.functioning()
	}

	def "Ensure functioning fails when locally fine but parent descriptor not functioning"() {
		when:
		def org = new Organization(active:true, approved:true)
		def ed = new EntityDescriptor(organization:org, active:true, approved:true)
		def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:false, approved:true)
		def ep = new AttributeService(descriptor:aa, active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)

		then:
		!ep.functioning()
	}

  def "Ensure selffunctioning operates"() {
    when:
    def org = new Organization(active:true, approved:true)
    def ed = new EntityDescriptor(organization:org, active:true, approved:true)
    def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)
    def ep = new AttributeService(descriptor:aa, active:true, approved:true, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)

    then:
    ep.selfFunctioning()
  }

  def "Ensure selffunctioning fails when unapproved"() {
    when:
    def org = new Organization(active:true, approved:true)
    def ed = new EntityDescriptor(organization:org, active:true, approved:true)
    def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)
    def ep = new AttributeService(descriptor:aa, active:true, approved:false, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)

    then:
    !ep.selfFunctioning()
  }

  def "Ensure selffunctioning fails when not active and unapproved"() {
    when:
    def org = new Organization(active:true, approved:true)
    def ed = new EntityDescriptor(organization:org, active:true, approved:true)
    def aa = new AttributeAuthorityDescriptor(organization:org, entityDescriptor:ed, active:true, approved:true)
    def ep = new AttributeService(descriptor:aa, active:false, approved:false, location:'https://idp.example.org:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)

    then:
    !ep.selfFunctioning()
  }

}
