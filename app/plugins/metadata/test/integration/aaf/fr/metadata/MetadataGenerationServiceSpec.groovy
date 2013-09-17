package aaf.fr.metadata

import org.custommonkey.xmlunit.*;

import grails.plugin.spock.*
import groovy.xml.MarkupBuilder

import aaf.fr.foundation.*

class MetadataGenerationServiceSpec extends IntegrationSpec {
	def metadataGenerationService
	def cryptoService
	
	def writer
	def builder
	
	def soap
	def httpRedirect
	def httpPost
	def httpArtifact
  def disc

	def attrUri, coreCategory, optionalCategory
	
	def saml2Prot
	def saml1Prot
	def protocolSupportEnumerations
	
	def cleanup() {
	}
	
	def setup () {
		writer = new StringWriter()
		builder = new MarkupBuilder(writer)

		XMLUnit.ignoreComments = true
		XMLUnit.ignoreWhitespace = true
	}

	def similarExcludingID(def diff) {
		def result = true
		def ddiff = new DetailedDiff(diff)
		ddiff.allDifferences.each {
			// id is time based and subject to change
	    if(!it.controlNodeDetail.xpathLocation.equals("/EntitiesDescriptor[1]/@ID")) {
	    	result = false
	    }
    }
    result
	}
	
	def setupBindings() {
		soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'')
		httpRedirect = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect', description:'')
		httpPost = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST', description:'')
		httpArtifact = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact', description:'')
    disc = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:profiles:SSO:idp-discovery-protocol', description:'')

		attrUri = new SamlURI(type:SamlURIType.AttributeNameFormat, uri:'urn:oasis:names:tc:SAML:2.0:attrname-format:uri', description:'').save()
		coreCategory = new AttributeCategory(name:'Core').save()
		optionalCategory = new AttributeCategory(name:'Optional').save()

		saml2Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:2.0:protocol')
		saml1Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:1.1:protocol urn:mace:shibboleth:1.0')
		protocolSupportEnumerations = [saml1Prot, saml2Prot]
	}
	
	def loadExpected(file) {
		new File("./test/data/metadatagenerationservicespec/${file}.xml").text
	}
	
	def String loadPK() {
		new File('./test/data/selfsigned.pem').text
	}

    def String loadPK2() {
        new File('./test/data/selfsigned2.pem').text
    }

	def "Test valid endpoint generation"() {
		setup:
		setupBindings()
		
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")		
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
		def idp = IDPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)
		
		def location = "https://test.example.com/SSO"
		def sso = SingleSignOnService.build(descriptor: idp, active:true, approved:true, binding:httpPost, location:location)
		def expected = loadExpected('testvalidendpointgeneration')
		
		when:
		metadataGenerationService.endpoint(builder, false, false, "SingleSignOnService", sso)
		
		then:
		def xml = writer.toString()
		xml == expected
	}

	def "Test valid endpoint generation with response"() {
		setup:
		setupBindings()
		
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")		
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
		def idp = IDPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)
		
		def location = "https://test.example.com/SSO"
		def responseLocation = "https://test.example.com/response"
		def sso = SingleSignOnService.build(descriptor:idp, active:true, approved:true, binding:httpPost, location:location, responseLocation:responseLocation)
		def expected = loadExpected('testvalidendpointgenerationresponse')
		
		when:
		metadataGenerationService.endpoint(builder, false, false, "SingleSignOnService", sso)
		
		then:
		def xml = writer.toString()
		xml == expected
	}
	
	def "Test endpoint generation when endpoint inactive"() {
		setup:
		setupBindings()
		
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")		
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
		def idp = IDPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)
		
		def location = "https://test.example.com/SSO"
		def sso = SingleSignOnService.build(descriptor:idp, active:false, approved:true, binding:httpPost, location:location)
		
		when:
		metadataGenerationService.endpoint(builder, false, false, "SingleSignOnService", sso)
		
		then:
		def xml = writer.toString()
		xml == ""
	}
	
	def "Test endpoint generation when endpoint not approved"() {
		setup:
		setupBindings()
		
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")		
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
		def idp = IDPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)
		
		def location = "https://test.example.com/SSO"
		def sso = SingleSignOnService.build(descriptor:idp, active:true, approved:false, binding:httpPost, location:location)
		
		when:
		metadataGenerationService.endpoint(builder, false, false, "SingleSignOnService", sso)
		
		then:
		def xml = writer.toString()
		xml == ""
	}
	
	def "Test valid indexed endpoint generation"() {
		setup:
		setupBindings()
		
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")		
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
		def idp = IDPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)
		
		def location = "https://test.example.com/artifact"
		def ars = ArtifactResolutionService.build(descriptor: idp, active:true, approved:true, binding:httpPost, location:location, index:100)
		def expected = loadExpected('testvalidindexedendpointgeneration')
		
		when:
		metadataGenerationService.indexedEndpoint(builder, false, false, "ArtifactResolutionService", ars)
		
		then:
		def xml = writer.toString()
		xml == expected
	}
	
	def "Test valid indexed endpoint generation with response"() {
		setup:
		setupBindings()
		
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")		
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
		def idp = IDPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)
		
		def location = "https://test.example.com/artifact"
		def responseLocation = "https://test.example.com/response"
		def ars = ArtifactResolutionService.build(descriptor:idp, active:true, approved:true, binding:httpPost, location:location, responseLocation:responseLocation, index:100)
		def expected = loadExpected('testvalidindexedendpointgenerationresponse')
		
		when:
		metadataGenerationService.indexedEndpoint(builder, false, false, "ArtifactResolutionService", ars)
		
		then:
		def xml = writer.toString()
		xml == expected
	}
	
	def "Test indexed endpoint generation when not active"() {
		setup:
		setupBindings()
		
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")		
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
		def idp = IDPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)
		
		def location = "https://test.example.com/artifact"
		def ars = ArtifactResolutionService.build(descriptor:idp, active:false, approved:true, binding:httpPost, location:location, index:100)
		
		when:
		metadataGenerationService.indexedEndpoint(builder, false, false, "ArtifactResolutionService", ars)
		
		then:
		def xml = writer.toString()
		xml == ""
	}
	
	def "Test indexed endpoint generation when not approved"() {
		setup:
		setupBindings()
		
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")		
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
		def idp = IDPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)
		
		def location = "https://test.example.com/artifact"
		def ars = ArtifactResolutionService.build(descriptor:idp, active:true, approved:false, binding:httpPost, location:location, index:100)
		
		when:
		metadataGenerationService.indexedEndpoint(builder, false, false, "ArtifactResolutionService", ars)
		
		then:
		def xml = writer.toString()
		xml == ""
	}
	
	def "Test valid organization generation"() {
		setup:
		def organization = Organization.build(name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def expected = loadExpected('testvalidorganization')
		
		when:
		metadataGenerationService.organization(builder, organization)
		
		then:
		def xml = writer.toString()
		xml == expected
	}
	
	def "Test valid contact person generation"() {
		setup:
		def email = "test@example.com"
		def home = "(07) 1111 1111"
		def work = "(567) 222 22222"
		def mobile = "0413 867 208"
		def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
		def ct = ContactType.build(name:"administrative")
		def contactPerson = ContactPerson.build(contact:contact, type:ct)
		def expected = loadExpected('testvalidcontact')
		
		when:
		metadataGenerationService.contactPerson(builder, contactPerson)
		
		then:
		def xml = writer.toString()
		xml == expected
	}

  def "Test valid contact person generation with non SAML type"() {
      setup:
      def email = "test@example.com"
      def home = "(07) 1111 1111"
      def work = "(567) 222 22222"
      def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work)
      def ct = ContactType.build(name:"businessowner")
      def contactPerson = ContactPerson.build(contact:contact, type:ct)
      
      when:
      metadataGenerationService.contactPerson(builder, contactPerson)
      
      then:
      def xml = writer.toString()
      xml.length() == 0
  }
	
	def "Test valid contact person generation with only mobile phone"() {
		setup:
		def email = "test@example.com"
		def mobile = "0413 867 208"
		def contact = Contact.build(givenName:"Test", surname:"User", email:email, mobilePhone:mobile)
		def ct = ContactType.build(name:"administrative")
		def contactPerson = ContactPerson.build(contact:contact, type:ct)
		def expected = loadExpected('testvalidcontactonlymobile')
		
		when:
		metadataGenerationService.contactPerson(builder, contactPerson)
		
		then:
		def xml = writer.toString()
		xml == expected
	}
	
	def "Test valid EntitiesDescriptor generation"() {
		setup:
    setupBindings()

		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def email = "test@example.com"
		def home = "(07) 1111 1111"
		def work = "(567) 222 22222"
		def mobile = "0413 867 208"
		def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
		def admin = ContactType.build(name:"administrative")
		def contactPerson = ContactPerson.build(contact:contact, type:admin)

		def entitiesDescriptor = new EntitiesDescriptor(name:"some.test.name")
		(1..2).each { i ->
			def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID$i", active:true, approved:true)
			entityDescriptor.addToContacts(contactPerson)

			entityDescriptor.addToIdpDescriptors(IDPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true))
			entityDescriptor.addToSpDescriptors(SPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true))

			entitiesDescriptor.addToEntityDescriptors(entityDescriptor)
      entityDescriptor.validate()
		}

		def keyInfo = new CAKeyInfo(certificate:new CACertificate(data:loadPK()))
		def keyInfo2 = new CAKeyInfo(certificate:new CACertificate(data:loadPK2()))
		def certificateAuthorities = []
		certificateAuthorities.add(keyInfo)
		certificateAuthorities.add(keyInfo2)

		def validUntil = new GregorianCalendar(2009, Calendar.JULY, 22)

		def expected = loadExpected('testvalidentitiesdescriptor')

		when:
		metadataGenerationService.entitiesDescriptor(builder, false, false, true, entitiesDescriptor, validUntil.getTime(), certificateAuthorities)
		def xml = writer.toString()
		def diff = new Diff(expected, xml)

		then:
		similarExcludingID(diff)
	}
	
	def "Test valid EntitiesDescriptor generation with embedded entitiesdescriptors and no CA"() {
		setup:
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def email = "test@example.com"
		def home = "(07) 1111 1111"
		def work = "(567) 222 22222"
		def mobile = "0413 867 208"
		def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
		def admin = ContactType.build(name:"administrative")
		def contactPerson = ContactPerson.build(contact:contact, type:admin)
		
		def entitiesDescriptor = new EntitiesDescriptor(name:"some.test.name")
		def entitiesDescriptor1 = new EntitiesDescriptor()
		entitiesDescriptor.addToEntitiesDescriptors(entitiesDescriptor1)
		(1..2).each { i ->
			def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID$i", active:true, approved:true)
			entityDescriptor.addToContacts(contactPerson)
			entityDescriptor.addToIdpDescriptors(IDPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true))
			entityDescriptor.addToSpDescriptors(SPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true))

			entitiesDescriptor1.addToEntityDescriptors(entityDescriptor)
		}
		
		def certificateAuthorities = []
		
		def validUntil = new GregorianCalendar(2009, Calendar.JULY, 22)
			
		def expected = loadExpected('testvalidentitiesdescriptorembedded')
		
		
		when:
		metadataGenerationService.entitiesDescriptor(builder, false, true, true, entitiesDescriptor, validUntil.getTime(), certificateAuthorities)
		def xml = writer.toString()
		def diff = new Diff(expected, xml)

		then:
		similarExcludingID(diff)
	}
	
	def "Test inactive EntityDescriptor generation"() {
		setup:
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def ed = EntityDescriptor.build(organization:organization, active:false, approved:true)
		
		when:
		metadataGenerationService.entityDescriptor(builder, false, false, true, ed, false)
		
		then:
		def xml = writer.toString()
		xml == ""
	}
	
	def "Test not approved EntityDescriptor generation"() {
		setup:
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def ed = EntityDescriptor.build(organization:organization, active:true, approved:false)
		
		when:
		metadataGenerationService.entityDescriptor(builder, false, false, true, ed, false)
		
		then:
		def xml = writer.toString()
		xml == ""
	}
	
	def "Test empty EntityDescriptor generation"() {
		setup:
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def ed = EntityDescriptor.build(organization:organization, active:true, approved:true)
		
		when:
		metadataGenerationService.entityDescriptor(builder, false, false, true, ed, false)
		
		then:
		def xml = writer.toString()
		xml == ""
	}

	def "Test valid EntityDescriptor generation with schema population"() {
		setup:
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def email = "test@example.com"
		def home = "(07) 1111 1111"
		def work = "(567) 222 22222"
		def mobile = "0413 867 208"
		def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
		def admin = ContactType.build(name:"administrative")
		def contactPerson = ContactPerson.build(contact:contact, type:admin)
		
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
		entityDescriptor.addToContacts(contactPerson)
		entityDescriptor.addToIdpDescriptors(IDPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true))
		entityDescriptor.addToIdpDescriptors(IDPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true))
		entityDescriptor.addToSpDescriptors(SPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true))
		entityDescriptor.addToSpDescriptors(SPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true))
		entityDescriptor.addToSpDescriptors(SPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true))

		def expected = loadExpected('testvalidentitydescriptorschema')
		
		when:
		metadataGenerationService.entityDescriptor(builder, false, false, true, entityDescriptor, true)
		def xml = writer.toString()
		def diff = new Diff(expected, xml)

		then:
		diff.similar()
	}
	
	def "Test inactive IDPSSODescriptor generation"() {
		setup:
		def idp = IDPSSODescriptor.build(active:false)
		
		when:
		metadataGenerationService.idpSSODescriptor(builder, false, false, true, idp)
		
		then:
		def xml = writer.toString()
		xml == ""
	}
	
	def "Test not approved IDPSSODescriptor generation"() {
		setup:
		def idp = IDPSSODescriptor.build(approved:false)
		
		when:
		metadataGenerationService.idpSSODescriptor(builder, false, false, true, idp)
		
		then:
		def xml = writer.toString()
		xml == ""
	}

	def "Test valid IDPSSODescriptor generation"() {
		setup:
		setupBindings()
		
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
		
		def email = "test@example.com"
		def home = "(07) 1111 1111"
		def work = "(567) 222 22222"
		def mobile = "0413 867 208"
		def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
		def admin = ContactType.build(name:"administrative")
		def contactPerson = ContactPerson.build(contact:contact, type:admin)
		
		def idp = IDPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)
		        
        def certificate = new Certificate(data:loadPK())
        def keyInfo = new KeyInfo(keyName:"key1", certificate:certificate)
        def encryptionMethod = new EncryptionMethod(algorithm:"http://www.w3.org/2001/04/xmlenc#tripledes-cbc")
        def keyDescriptor = new KeyDescriptor(roleDescriptor:idp, keyType:KeyTypes.encryption, keyInfo:keyInfo, encryptionMethod:encryptionMethod)

        def certificate2 = new Certificate(data:loadPK2())
        def keyInfo2 = new KeyInfo(certificate:certificate2)
        def keyDescriptor2 = new KeyDescriptor(roleDescriptor:idp, keyType:KeyTypes.signing, keyInfo:keyInfo2)

        def certificate3 = new Certificate(data:loadPK())
        def keyInfo3 = new KeyInfo(certificate:certificate3)
        def keyDescriptor3 = new KeyDescriptor(roleDescriptor:idp, keyType:KeyTypes.signing, keyInfo:keyInfo3, disabled:true)

        keyDescriptor.id = 1
        keyDescriptor2.id = 2
        keyDescriptor3.id = 3

		def ars = new ArtifactResolutionService(descriptor:idp, index:100, active:true, approved:true, isDefault:true, binding:soap, location:"https://test.example.com/ars/artifact")
		def ars2 = new ArtifactResolutionService(descriptor:idp, index:101, active:true, approved:true, isDefault:false, binding:soap, location:"https://test.example.com/ars/artifact2")

        ars.id = 1
        ars2.id = 2
		
		def slo = new SingleLogoutService(descriptor:idp, active:true, approved:true, binding:httpPost, location:"https://test.example.com/slo/POST")
		def mnid = new ManageNameIDService(descriptor:idp, active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/mnid/REDIRECT")
		def nidf = new SamlURI(uri:"supported:nameid:format:urn")
		
		def sso = new SingleSignOnService(descriptor:idp,active:true, approved:true, binding:httpPost, location:"https://test.example.com/sso/POST")
		def nidms = new NameIDMappingService(descriptor:idp,active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/nameidmappingserivce/REDIRECT")
		def aidrs = new AssertionIDRequestService(descriptor:idp,active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/assertionidrequestservice/REDIRECT")
		
		def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
		def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
		def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
		def ba4 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()
		
		def attr1 = new Attribute(idpSSODescriptor:idp, base:ba1)
		def attr2 = new Attribute(idpSSODescriptor:idp, base:ba2)
		def attr3 = new Attribute(idpSSODescriptor:idp, base:ba3)
		def attr4 = new Attribute(idpSSODescriptor:idp, base:ba4)
		attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:1'))
		attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:2'))
		attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:3'))
		
		idp.addToKeyDescriptors(keyDescriptor)
		idp.addToKeyDescriptors(keyDescriptor2)
    idp.addToKeyDescriptors(keyDescriptor3)
		idp.addToContacts(contactPerson)
		
		idp.addToArtifactResolutionServices(ars)
		idp.addToArtifactResolutionServices(ars2)
		idp.addToSingleLogoutServices(slo)
		idp.addToManageNameIDServices(mnid)
		idp.addToNameIDMappingServices(nidms)
		idp.addToAssertionIDRequestServices(aidrs)
		idp.addToNameIDFormats(nidf)		
		idp.addToSingleSignOnServices(sso)
        
		idp.addToAttributes(attr1)
		idp.addToAttributes(attr2)
		idp.addToAttributes(attr3)
		idp.addToAttributes(attr4)
		
		def expected = loadExpected('testvalididpssodescriptor')
		
		when:
		metadataGenerationService.idpSSODescriptor(builder, false, false, true, idp)
		def xml = writer.toString()
		def strippedXML = xml.replace("shibmd:", "").replace("saml:", "")	// dodgy as hell but easiest option presently
        def diff = new Diff(expected, strippedXML)

		then:
		xml.contains('saml:Attribute')
		xml.contains('shibmd:Scope')
		diff.similar()
	}

  def "Test valid IDPSSODescriptor generation with regex scope"() {
    setup:
    setupBindings()
    
    def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
    def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
    
    def email = "test@example.com"
    def home = "(07) 1111 1111"
    def work = "(567) 222 22222"
    def mobile = "0413 867 208"
    def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
    def admin = ContactType.build(name:"administrative")
    def contactPerson = ContactPerson.build(contact:contact, type:admin)
    
    def idp = IDPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true, scope:'^([a-zA-Z0-9-]{1,63}\\.){0,2}test.com$')
            
        def certificate = new Certificate(data:loadPK())
        def keyInfo = new KeyInfo(keyName:"key1", certificate:certificate)
        def encryptionMethod = new EncryptionMethod(algorithm:"http://www.w3.org/2001/04/xmlenc#tripledes-cbc")
        def keyDescriptor = new KeyDescriptor(roleDescriptor:idp, keyType:KeyTypes.encryption, keyInfo:keyInfo, encryptionMethod:encryptionMethod)

        def certificate2 = new Certificate(data:loadPK2())
        def keyInfo2 = new KeyInfo(certificate:certificate2)
        def keyDescriptor2 = new KeyDescriptor(roleDescriptor:idp, keyType:KeyTypes.signing, keyInfo:keyInfo2)

        def certificate3 = new Certificate(data:loadPK())
        def keyInfo3 = new KeyInfo(certificate:certificate3)
        def keyDescriptor3 = new KeyDescriptor(roleDescriptor:idp, keyType:KeyTypes.signing, keyInfo:keyInfo3, disabled:true)

        keyDescriptor.id = 1
        keyDescriptor2.id = 2
        keyDescriptor3.id = 3

    def ars = new ArtifactResolutionService(descriptor:idp, index:100, active:true, approved:true, isDefault:true, binding:soap, location:"https://test.example.com/ars/artifact")
    def ars2 = new ArtifactResolutionService(descriptor:idp, index:101, active:true, approved:true, isDefault:false, binding:soap, location:"https://test.example.com/ars/artifact2")

        ars.id = 1
        ars2.id = 2
    
    def slo = new SingleLogoutService(descriptor:idp, active:true, approved:true, binding:httpPost, location:"https://test.example.com/slo/POST")
    def mnid = new ManageNameIDService(descriptor:idp, active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/mnid/REDIRECT")
    def nidf = new SamlURI(uri:"supported:nameid:format:urn")
    
    def sso = new SingleSignOnService(descriptor:idp,active:true, approved:true, binding:httpPost, location:"https://test.example.com/sso/POST")
    def nidms = new NameIDMappingService(descriptor:idp,active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/nameidmappingserivce/REDIRECT")
    def aidrs = new AssertionIDRequestService(descriptor:idp,active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/assertionidrequestservice/REDIRECT")
    
    def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
    def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
    def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
    def ba4 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()
    
    def attr1 = new Attribute(idpSSODescriptor:idp, base:ba1)
    def attr2 = new Attribute(idpSSODescriptor:idp, base:ba2)
    def attr3 = new Attribute(idpSSODescriptor:idp, base:ba3)
    def attr4 = new Attribute(idpSSODescriptor:idp, base:ba4)
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:1'))
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:2'))
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:3'))
    
    idp.addToKeyDescriptors(keyDescriptor)
    idp.addToKeyDescriptors(keyDescriptor2)
        idp.addToKeyDescriptors(keyDescriptor3)
    idp.addToContacts(contactPerson)
    
    idp.addToArtifactResolutionServices(ars)
    idp.addToArtifactResolutionServices(ars2)
    idp.addToSingleLogoutServices(slo)
    idp.addToManageNameIDServices(mnid)
    idp.addToNameIDMappingServices(nidms)
    idp.addToAssertionIDRequestServices(aidrs)
    idp.addToNameIDFormats(nidf)    
    idp.addToSingleSignOnServices(sso)
        
    idp.addToAttributes(attr1)
    idp.addToAttributes(attr2)
    idp.addToAttributes(attr3)
    idp.addToAttributes(attr4)
    
    def expected = loadExpected('testvalididpssodescriptorwithregexscope')
    
    when:
    metadataGenerationService.idpSSODescriptor(builder, false, false, true, idp)
    def xml = writer.toString()
    def strippedXML = xml.replace("shibmd:", "").replace("saml:", "") // dodgy as hell but easiest option presently
    def diff = new Diff(expected, strippedXML)

    then:
    xml.contains('saml:Attribute')
    xml.contains('shibmd:Scope')
    diff.similar()
  }

	def "Test inactive SPSSODescriptor generation"() {
		setup:
		def sp = SPSSODescriptor.build(active:false)
		
		when:
		metadataGenerationService.spSSODescriptor(builder, false, false, true, sp)
		
		then:
		def xml = writer.toString()
		xml == ""
	}
	
	def "Test not approved SPSSODescriptor generation"() {
		setup:
		def sp = SPSSODescriptor.build(approved:false)
		
		when:
		metadataGenerationService.spSSODescriptor(builder, false, false, true, sp)
		
		then:
		def xml = writer.toString()
		xml == ""
	}
	
	def "Test valid SPSSODescriptor generation"() {
		setup:
		setupBindings()
		def saml2Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:2.0:protocol')
		def saml1Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:1.1:protocol urn:mace:shibboleth:1.0')
		def protocolSupportEnumerations = [saml1Prot, saml2Prot]
		
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
		
		def email = "test@example.com"
		def home = "(07) 1111 1111"
		def work = "(567) 222 22222"
		def mobile = "0413 867 208"
		def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
		def admin = ContactType.build(name:"administrative")
		def contactPerson = ContactPerson.build(contact:contact, type:admin)

    def sp = SPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)

    def certificate = new Certificate(data:loadPK())
    def keyInfo = new KeyInfo(keyName:"key1", certificate:certificate)
    def encryptionMethod = new EncryptionMethod(algorithm:"http://www.w3.org/2001/04/xmlenc#tripledes-cbc")
    def keyDescriptor = new KeyDescriptor(roleDescriptor:sp, keyType:KeyTypes.encryption, keyInfo:keyInfo, encryptionMethod:encryptionMethod)

    def certificate2 = new Certificate(data:loadPK2())
    def keyInfo2 = new KeyInfo(keyName:"key2", certificate:certificate2)
    def keyDescriptor2 = new KeyDescriptor(roleDescriptor:sp, keyType:KeyTypes.signing, keyInfo:keyInfo2)

    keyDescriptor.id = 1
    keyDescriptor2.id = 2
		
		def ars = new ArtifactResolutionService(index:200, active:true, approved:true, isDefault:true, binding:soap, location:"https://test.example.com/ars/artifact")
		def ars2 = new ArtifactResolutionService(index:201, active:true, approved:true, isDefault:false, binding:soap, location:"https://test.example.com/ars/artifact2")
    ars.id = 1
    ars2.id = 2
		
		def slo = new SingleLogoutService(active:true, approved:true, binding:httpPost, location:"https://test.example.com/slo/POST")
		def mnid = new ManageNameIDService(active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/mnid/REDIRECT")
		def nidf = SamlURI.build(uri:"supported:nameid:format:urn")
    def ds = new DiscoveryResponseService(active:true, approved:true, index:1, isDefault:true, binding:disc, location:'https://test.example.com/Shibboleth.sso/DS')
		
		def acs = new AssertionConsumerService(index:300, active:true, approved:true, binding:httpArtifact, location:"https://test.example.com/acs/ART")
		
		def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
		def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
		def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
		def ba4 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()
    def ba5 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement2', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()
		
		def attrService = AttributeConsumingService.build(lang:'en')
		attrService.addToServiceNames("Test Name 1")
		attrService.addToServiceNames("Test Name 2")
		attrService.addToServiceDescriptions("This is a great description")

		def attr1 = new RequestedAttribute(attributeConsumingService: attrService, base:ba1, isRequired:true, approved:true)
		def attr2 = new RequestedAttribute(attributeConsumingService: attrService, base:ba2, approved:true)
		def attr3 = new RequestedAttribute(attributeConsumingService: attrService, base:ba3, approved:true)
		def attr4 = new RequestedAttribute(attributeConsumingService: attrService, base:ba4, approved:true)
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:1'))
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:2'))
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:3'))

    def attr5 = new RequestedAttribute(attributeConsumingService: attrService, base:ba5, approved:true)
		attrService.addToRequestedAttributes(attr1)
		attrService.addToRequestedAttributes(attr2)
		attrService.addToRequestedAttributes(attr3)
		attrService.addToRequestedAttributes(attr4)
    attrService.addToRequestedAttributes(attr5) // should not appear - spec required but no values
		
		sp.addToKeyDescriptors(keyDescriptor)
		sp.addToKeyDescriptors(keyDescriptor2)
		sp.addToContacts(contactPerson)
		
		sp.addToArtifactResolutionServices(ars)
		sp.addToArtifactResolutionServices(ars2)
		sp.addToSingleLogoutServices(slo)
		sp.addToManageNameIDServices(mnid)
    sp.addToDiscoveryResponseServices(ds)
		sp.addToNameIDFormats(nidf)
		
		sp.addToAssertionConsumerServices(acs)
		sp.addToAttributeConsumingServices(attrService)
		
		def expected = loadExpected('testvalidspssodescriptor')
		
		when:
		metadataGenerationService.spSSODescriptor(builder, false, false, true, sp)
		def xml = writer.toString()
		def strippedXML = xml.replace("saml:", "")	// dodgy as hell but easiest option presently as namespaces causes problems in validation

		then:
		xml.contains('saml:Attribute')
		def diff = new Diff(expected, strippedXML)
		diff.similar()
	}

  def "Test valid SPSSODescriptor generation with all discovery services disabled"() {
    setup:
    setupBindings()
    def saml2Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:2.0:protocol')
    def saml1Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:1.1:protocol urn:mace:shibboleth:1.0')
    def protocolSupportEnumerations = [saml1Prot, saml2Prot]

    def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
    def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)

    def email = "test@example.com"
    def home = "(07) 1111 1111"
    def work = "(567) 222 22222"
    def mobile = "0413 867 208"
    def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
    def admin = ContactType.build(name:"administrative")
    def contactPerson = ContactPerson.build(contact:contact, type:admin)

    def sp = SPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)

    def certificate = new Certificate(data:loadPK())
    def keyInfo = new KeyInfo(keyName:"key1", certificate:certificate)
    def encryptionMethod = new EncryptionMethod(algorithm:"http://www.w3.org/2001/04/xmlenc#tripledes-cbc")
    def keyDescriptor = new KeyDescriptor(roleDescriptor:sp, keyType:KeyTypes.encryption, keyInfo:keyInfo, encryptionMethod:encryptionMethod)

    def certificate2 = new Certificate(data:loadPK2())
    def keyInfo2 = new KeyInfo(keyName:"key2", certificate:certificate2)
    def keyDescriptor2 = new KeyDescriptor(roleDescriptor:sp, keyType:KeyTypes.signing, keyInfo:keyInfo2)

    keyDescriptor.id = 1
    keyDescriptor2.id = 2

    def ars = new ArtifactResolutionService(index:200, active:true, approved:true, isDefault:true, binding:soap, location:"https://test.example.com/ars/artifact")
    def ars2 = new ArtifactResolutionService(index:201, active:true, approved:true, isDefault:false, binding:soap, location:"https://test.example.com/ars/artifact2")
    ars.id = 1
    ars2.id = 2

    def slo = new SingleLogoutService(active:true, approved:true, binding:httpPost, location:"https://test.example.com/slo/POST")
    def mnid = new ManageNameIDService(active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/mnid/REDIRECT")
    def nidf = SamlURI.build(uri:"supported:nameid:format:urn")
    def ds = new DiscoveryResponseService(active:false, approved:true, index:1, isDefault:true, binding:disc, location:'https://test.example.com/Shibboleth.sso/DS')
    def acs = new AssertionConsumerService(index:300, active:true, approved:true, binding:httpArtifact, location:"https://test.example.com/acs/ART")

    def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
    def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
    def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
    def ba4 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()
    def ba5 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement2', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()

    def attrService = AttributeConsumingService.build(lang:'en')
    attrService.addToServiceNames("Test Name 1")
    attrService.addToServiceNames("Test Name 2")
    attrService.addToServiceDescriptions("This is a great description")

    def attr1 = new RequestedAttribute(attributeConsumingService: attrService, base:ba1, isRequired:true, approved:true)
    def attr2 = new RequestedAttribute(attributeConsumingService: attrService, base:ba2, approved:true)
    def attr3 = new RequestedAttribute(attributeConsumingService: attrService, base:ba3, approved:true)
    def attr4 = new RequestedAttribute(attributeConsumingService: attrService, base:ba4, approved:true)
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:1'))
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:2'))
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:3'))

    def attr5 = new RequestedAttribute(attributeConsumingService: attrService, base:ba5, approved:true)
    attrService.addToRequestedAttributes(attr1)
    attrService.addToRequestedAttributes(attr2)
    attrService.addToRequestedAttributes(attr3)
    attrService.addToRequestedAttributes(attr4)
        attrService.addToRequestedAttributes(attr5) // should not appear - spec required but no values

    sp.addToKeyDescriptors(keyDescriptor)
    sp.addToKeyDescriptors(keyDescriptor2)
    sp.addToContacts(contactPerson)

    sp.addToArtifactResolutionServices(ars)
    sp.addToArtifactResolutionServices(ars2)
    sp.addToSingleLogoutServices(slo)
    sp.addToManageNameIDServices(mnid)
    sp.addToDiscoveryResponseServices(ds)
    sp.addToNameIDFormats(nidf)

    sp.addToAssertionConsumerServices(acs)
    sp.addToAttributeConsumingServices(attrService)

    def expected = loadExpected('testvalidspssodescriptor-nodisc')

    when:
    metadataGenerationService.spSSODescriptor(builder, false, false, true, sp)
    def xml = writer.toString()
    def strippedXML = xml.replace("saml:", "")  // dodgy as hell but easiest option presently as namespaces causes problems in validation

    then:
    xml.contains('saml:Attribute')
    def diff = new Diff(expected, strippedXML)
    diff.similar()
  }

	def "Test valid SPSSODescriptor generation with no approved attributes"() {
		setup:
		setupBindings()
		def saml2Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:2.0:protocol')
		def saml1Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:1.1:protocol urn:mace:shibboleth:1.0')
		def protocolSupportEnumerations = [saml1Prot, saml2Prot]
		
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
		
		def email = "test@example.com"
		def home = "(07) 1111 1111"
		def work = "(567) 222 22222"
		def mobile = "0413 867 208"
		def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
		def admin = ContactType.build(name:"administrative")
		def contactPerson = ContactPerson.build(contact:contact, type:admin)
		
        def sp = SPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)

        def certificate = new Certificate(data:loadPK())
        def keyInfo = new KeyInfo(keyName:"key1", certificate:certificate)
        def encryptionMethod = new EncryptionMethod(algorithm:"http://www.w3.org/2001/04/xmlenc#tripledes-cbc")
        def keyDescriptor = new KeyDescriptor(roleDescriptor:sp, keyType:KeyTypes.encryption, keyInfo:keyInfo, encryptionMethod:encryptionMethod)

        def certificate2 = new Certificate(data:loadPK2())
        def keyInfo2 = new KeyInfo(keyName:"key2", certificate:certificate2)
        def keyDescriptor2 = new KeyDescriptor(roleDescriptor:sp, keyType:KeyTypes.signing, keyInfo:keyInfo2)

        keyDescriptor.id = 1
        keyDescriptor2.id = 2
        
        def ars = new ArtifactResolutionService(index:200, active:true, approved:true, isDefault:true, binding:soap, location:"https://test.example.com/ars/artifact")
        def ars2 = new ArtifactResolutionService(index:201, active:true, approved:true, isDefault:false, binding:soap, location:"https://test.example.com/ars/artifact2")
        ars.id = 1
        ars2.id = 2
		
		def slo = new SingleLogoutService(active:true, approved:true, binding:httpPost, location:"https://test.example.com/slo/POST")
		def mnid = new ManageNameIDService(active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/mnid/REDIRECT")
		def nidf = SamlURI.build(uri:"supported:nameid:format:urn")
		
		def acs = AssertionConsumerService.build(index:300, active:true, approved:true, binding:httpArtifact, location:"https://test.example.com/acs/ART")
		
		def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
		def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
		def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
		def ba4 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()
		
		def attrService = AttributeConsumingService.build(lang:'en')
		attrService.addToServiceNames("Test Name 1")
		attrService.addToServiceNames("Test Name 2")
		attrService.addToServiceDescriptions("This is a great description")

		def attr1 = new RequestedAttribute(attributeConsumingService: attrService, base:ba1, isRequired:true)
		def attr2 = new RequestedAttribute(attributeConsumingService: attrService, base:ba2)
		def attr3 = new RequestedAttribute(attributeConsumingService: attrService, base:ba3)
		def attr4 = new RequestedAttribute(attributeConsumingService: attrService, base:ba4)
		attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:1'))
		attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:2'))
		attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:3'))

		attrService.addToRequestedAttributes(attr1)
		attrService.addToRequestedAttributes(attr2)
		attrService.addToRequestedAttributes(attr3)
		attrService.addToRequestedAttributes(attr4)		
		
		sp.addToKeyDescriptors(keyDescriptor)
		sp.addToKeyDescriptors(keyDescriptor2)
		sp.addToContacts(contactPerson)
		
		sp.addToArtifactResolutionServices(ars)
		sp.addToArtifactResolutionServices(ars2)
		sp.addToSingleLogoutServices(slo)
		sp.addToManageNameIDServices(mnid)
		sp.addToNameIDFormats(nidf)
		
		sp.addToAssertionConsumerServices(acs)
		sp.addToAttributeConsumingServices(attrService)
		
		def expected = loadExpected('testvalidspssodescriptornoapprovals')
		
		when:
		metadataGenerationService.spSSODescriptor(builder, false, false, true, sp)
		def xml = writer.toString()
		def strippedXML = xml.replace("saml:", "")	// dodgy as hell but easiest option presently as namespaces causes problems in validation

		then:
		!xml.contains('saml:Attribute')
		def diff = new Diff(expected, strippedXML)
		diff.similar()
	}

    def "Test valid SPSSODescriptor generation with no acs name"() {
        setup:
        setupBindings()
        def saml2Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:2.0:protocol')
        def saml1Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:1.1:protocol urn:mace:shibboleth:1.0')
        def protocolSupportEnumerations = [saml1Prot, saml2Prot]
        
        def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
        def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
        
        def email = "test@example.com"
        def home = "(07) 1111 1111"
        def work = "(567) 222 22222"
        def mobile = "0413 867 208"
        def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
        def admin = ContactType.build(name:"administrative")
        def contactPerson = ContactPerson.build(contact:contact, type:admin)
        
        def sp = SPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)

        def certificate = new Certificate(data:loadPK())
        def keyInfo = new KeyInfo(keyName:"key1", certificate:certificate)
        def encryptionMethod = new EncryptionMethod(algorithm:"http://www.w3.org/2001/04/xmlenc#tripledes-cbc")
        def keyDescriptor = new KeyDescriptor(roleDescriptor:sp, keyType:KeyTypes.encryption, keyInfo:keyInfo, encryptionMethod:encryptionMethod)

        def certificate2 = new Certificate(data:loadPK2())
        def keyInfo2 = new KeyInfo(keyName:"key2", certificate:certificate2)
        def keyDescriptor2 = new KeyDescriptor(roleDescriptor:sp, keyType:KeyTypes.signing, keyInfo:keyInfo2)

        keyDescriptor.id = 1
        keyDescriptor2.id = 2
        
        def ars = new ArtifactResolutionService(index:200, active:true, approved:true, isDefault:true, binding:soap, location:"https://test.example.com/ars/artifact")
        def ars2 = new ArtifactResolutionService(index:201, active:true, approved:true, isDefault:false, binding:soap, location:"https://test.example.com/ars/artifact2")
        ars.id = 1
        ars2.id = 2
        
        def slo = new SingleLogoutService(active:true, approved:true, binding:httpPost, location:"https://test.example.com/slo/POST")
        def mnid = new ManageNameIDService(active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/mnid/REDIRECT")
        def nidf = SamlURI.build(uri:"supported:nameid:format:urn")
        
        def acs = new AssertionConsumerService(index:300, active:true, approved:true, binding:httpArtifact, location:"https://test.example.com/acs/ART")
        
        def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
        def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
        def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
        def ba4 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()
        
        def attrService = AttributeConsumingService.build(lang:'en')
        attrService.addToServiceDescriptions("This is a great description")

        def attr1 = new RequestedAttribute(attributeConsumingService: attrService, base:ba1, isRequired:true)
        def attr2 = new RequestedAttribute(attributeConsumingService: attrService, base:ba2)
        def attr3 = new RequestedAttribute(attributeConsumingService: attrService, base:ba3)
        def attr4 = new RequestedAttribute(attributeConsumingService: attrService, base:ba4)
        attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:1'))
        attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:2'))
        attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:3'))

        attrService.addToRequestedAttributes(attr1)
        attrService.addToRequestedAttributes(attr2)
        attrService.addToRequestedAttributes(attr3)
        attrService.addToRequestedAttributes(attr4)  
        
        sp.addToKeyDescriptors(keyDescriptor)
        sp.addToKeyDescriptors(keyDescriptor2)
        sp.addToContacts(contactPerson)
        
        sp.addToArtifactResolutionServices(ars)
        sp.addToArtifactResolutionServices(ars2)
        sp.addToSingleLogoutServices(slo)
        sp.addToManageNameIDServices(mnid)
        sp.addToNameIDFormats(nidf)
        
        sp.addToAssertionConsumerServices(acs)
        sp.addToAttributeConsumingServices(attrService)
        
        def expected = loadExpected('testvalidspssodescriptornoservicename')
        
        when:
        metadataGenerationService.spSSODescriptor(builder, false, false, true, sp)
        def xml = writer.toString()
        def strippedXML = xml.replace("saml:", "")  // dodgy as hell but easiest option presently as namespaces causes problems in validation

        then:
        !xml.contains('saml:Attribute')
        def diff = new Diff(expected, strippedXML)
        diff.similar()
    }

    def "Test valid SPSSODescriptor generation with no requested attributes"() {
        setup:
        setupBindings()
        def saml2Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:2.0:protocol')
        def saml1Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:1.1:protocol urn:mace:shibboleth:1.0')
        def protocolSupportEnumerations = [saml1Prot, saml2Prot]
        
        def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
        def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)
        
        def email = "test@example.com"
        def home = "(07) 1111 1111"
        def work = "(567) 222 22222"
        def mobile = "0413 867 208"
        def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
        def admin = ContactType.build(name:"administrative")
        def contactPerson = ContactPerson.build(contact:contact, type:admin)
        
        def sp = SPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true)

        def certificate = new Certificate(data:loadPK())
        def keyInfo = new KeyInfo(keyName:"key1", certificate:certificate)
        def encryptionMethod = new EncryptionMethod(algorithm:"http://www.w3.org/2001/04/xmlenc#tripledes-cbc")
        def keyDescriptor = new KeyDescriptor(roleDescriptor:sp, keyType:KeyTypes.encryption, keyInfo:keyInfo, encryptionMethod:encryptionMethod)

        def certificate2 = new Certificate(data:loadPK2())
        def keyInfo2 = new KeyInfo(keyName:"key2", certificate:certificate2)
        def keyDescriptor2 = new KeyDescriptor(roleDescriptor:sp, keyType:KeyTypes.signing, keyInfo:keyInfo2)

        keyDescriptor.id = 1
        keyDescriptor2.id = 2
        
        def ars = new ArtifactResolutionService(index:200, active:true, approved:true, isDefault:true, binding:soap, location:"https://test.example.com/ars/artifact")
        def ars2 = new ArtifactResolutionService(index:201, active:true, approved:true, isDefault:false, binding:soap, location:"https://test.example.com/ars/artifact2")
        ars.id = 1
        ars2.id = 2
        
        def slo = new SingleLogoutService(active:true, approved:true, binding:httpPost, location:"https://test.example.com/slo/POST")
        def mnid = new ManageNameIDService(active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/mnid/REDIRECT")
        def nidf = SamlURI.build(uri:"supported:nameid:format:urn")
        
        def acs = AssertionConsumerService.build(index:300, active:true, approved:true, binding:httpArtifact, location:"https://test.example.com/acs/ART")
        
        def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
        def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
        def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
        def ba4 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()
        
        def attrService = AttributeConsumingService.build(lang:'en')
        attrService.addToServiceNames("Test Name 1")
        attrService.addToServiceNames("Test Name 2")
        attrService.addToServiceDescriptions("This is a great description")
        

        sp.addToKeyDescriptors(keyDescriptor)
        sp.addToKeyDescriptors(keyDescriptor2)
        sp.addToContacts(contactPerson)
        
        sp.addToArtifactResolutionServices(ars)
        sp.addToArtifactResolutionServices(ars2)
        sp.addToSingleLogoutServices(slo)
        sp.addToManageNameIDServices(mnid)
        sp.addToNameIDFormats(nidf)
        
        sp.addToAssertionConsumerServices(acs)
        sp.addToAttributeConsumingServices(attrService)
        
        def expected = loadExpected('testvalidspssodescriptornora')
        
        when:
        metadataGenerationService.spSSODescriptor(builder, false, false, true, sp)
        def xml = writer.toString()
        def strippedXML = xml.replace("saml:", "")  // dodgy as hell but easiest option presently as namespaces causes problems in validation

        then:
        !xml.contains('saml:Attribute')
        def diff = new Diff(expected, strippedXML)
        diff.similar()
    }
	
	def "Test inactive AttributeAuthorityDescriptor generation"() {
		setup:
		def aa = AttributeAuthorityDescriptor.build(active:false)
		
		when:
		metadataGenerationService.attributeAuthorityDescriptor(builder, false, false, true, aa)
		
		then:
		def xml = writer.toString()
		xml == ""
	}
	
	def "Test not approved AttributeAuthorityDescriptor generation"() {
		setup:
		def aa = AttributeAuthorityDescriptor.build(approved:false)
		
		when:
		metadataGenerationService.attributeAuthorityDescriptor(builder, false, false, true, aa)
		
		then:
		def xml = writer.toString()
		xml == ""
	}
	
	def "Test valid AttributeAuthorityDescriptor creation when collaborating with IDP"() {
		setup:
		setupBindings()
		def saml2Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:2.0:protocol')
		def saml1Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:1.1:protocol urn:mace:shibboleth:1.0')
		def protocolSupportEnumerations = [saml1Prot, saml2Prot]

		def organization = new Organization(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def entityDescriptor = new EntityDescriptor(organization:organization, entityID:"https://test.com", active:true, approved:true)
		
		def email = "test@example.com"
		def home = "(07) 1111 1111"
		def work = "(567) 222 22222"
		def mobile = "0413 867 208"
		def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
		def admin = ContactType.build(name:"administrative")
		def contactPerson = ContactPerson.build(contact:contact, type:admin)

    def idp = new IDPSSODescriptor(protocolSupportEnumerations:protocolSupportEnumerations, entityDescriptor:entityDescriptor, organization:organization, approved:true, active:true, scope:"test.com")

    def certificate = new Certificate(data:loadPK())
    def keyInfo = new KeyInfo(keyName:"key1", certificate:certificate)
    def encryptionMethod = new EncryptionMethod(algorithm:"http://www.w3.org/2001/04/xmlenc#tripledes-cbc")
    def keyDescriptor = new KeyDescriptor(roleDescriptor:idp, keyType:KeyTypes.encryption, keyInfo:keyInfo, encryptionMethod:encryptionMethod)

    def certificate2 = new Certificate(data:loadPK())
    def keyInfo2 = new KeyInfo(keyName:"key2", certificate:certificate2)
    def keyDescriptor2 = new KeyDescriptor(roleDescriptor:idp, keyType:KeyTypes.signing, keyInfo:keyInfo2)

    keyDescriptor.id = 1
    keyDescriptor2.id = 2
		
		def nidf = new SamlURI(uri:"supported:nameid:format:urn")
		def aidrs = new AssertionIDRequestService(active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/assertionidrequestservice/REDIRECT")
		
		def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
		def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
		def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
		def ba4 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()
		
		def attr1 = new Attribute(base:ba1)
		def attr2 = new Attribute(base:ba2)
		def attr3 = new Attribute(base:ba3)
		def attr4 = new Attribute(base:ba4)
		attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:1'))
		attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:2'))
		attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:3'))
		
		idp.addToKeyDescriptors(keyDescriptor)
		idp.addToKeyDescriptors(keyDescriptor2)
		idp.addToContacts(contactPerson)
		
		idp.addToNameIDFormats(nidf)
		idp.addToAssertionIDRequestServices(aidrs)
		
		idp.addToAttributes(attr1)
		idp.addToAttributes(attr2)
		idp.addToAttributes(attr3)
		idp.addToAttributes(attr4)

		def aa = new AttributeAuthorityDescriptor(protocolSupportEnumerations:protocolSupportEnumerations, entityDescriptor:entityDescriptor, organization:organization, approved:true, active:true)
		aa.collaborator = idp
    def ep = new AttributeService(descriptor:aa, active:true, approved:true, location:'https://test.example.com:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(ep)

		idp.collaborator = aa

		def expected = loadExpected('testvalidaadescriptor')

		when:
		metadataGenerationService.attributeAuthorityDescriptor(builder, false, false, true, aa)
		def xml = writer.toString()
		def strippedXML = xml.replace("shibmd:", "").replace("saml:", "")	// dodgy as hell but easiest option presently as namespaces causes problems in validation

		then:
		xml.contains('saml:Attribute')
		xml.contains('shibmd:Scope')
		def diff = new Diff(expected, strippedXML)
		diff.similar()
	}
	
	def "Test valid AttributeAuthorityDescriptor creation when not collaborating with IDP"() {
		setup:
		setupBindings()
		def saml2Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:2.0:protocol')
		def saml1Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:1.1:protocol urn:mace:shibboleth:1.0')
		def protocolSupportEnumerations = [saml1Prot, saml2Prot]

		def organization = new Organization(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def entityDescriptor = new EntityDescriptor(organization:organization, entityID:"https://test.com", active:true, approved:true)

		def email = "test@example.com"
		def home = "(07) 1111 1111"
		def work = "(567) 222 22222"
		def mobile = "0413 867 208"
		def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
		def admin = ContactType.build(name:"administrative")
		def contactPerson = ContactPerson.build(contact:contact, type:admin)

		def aa = new AttributeAuthorityDescriptor(protocolSupportEnumerations:protocolSupportEnumerations, entityDescriptor:entityDescriptor, organization:organization, approved:true, active:true, scope:"test.com")
    def ep = new AttributeService(descriptor:aa, active:true, approved:true, location:'https://test.example.com:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(ep)

    def certificate = new Certificate(data:loadPK())
    def keyInfo = new KeyInfo(keyName:"key1", certificate:certificate)
    def encryptionMethod = new EncryptionMethod(algorithm:"http://www.w3.org/2001/04/xmlenc#tripledes-cbc")
    def keyDescriptor = new KeyDescriptor(roleDescriptor:aa, keyType:KeyTypes.encryption, keyInfo:keyInfo, encryptionMethod:encryptionMethod)

    def certificate2 = new Certificate(data:loadPK())
    def keyInfo2 = new KeyInfo(keyName:"key2", certificate:certificate2)
    def keyDescriptor2 = new KeyDescriptor(roleDescriptor:aa, keyType:KeyTypes.signing, keyInfo:keyInfo2)

    keyDescriptor.id = 1
    keyDescriptor2.id = 2

		def nidf = new SamlURI(uri:"supported:nameid:format:urn")
		def aidrs = new AssertionIDRequestService(active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/assertionidrequestservice/REDIRECT")

		def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
		def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
		def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
		def ba4 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()

		def attr1 = new Attribute(base:ba1)
		def attr2 = new Attribute(base:ba2)
		def attr3 = new Attribute(base:ba3)
		def attr4 = new Attribute(base:ba4)
		attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:1'))
		attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:2'))
		attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:3'))

		aa.addToKeyDescriptors(keyDescriptor)
		aa.addToKeyDescriptors(keyDescriptor2)
		aa.addToContacts(contactPerson)

		aa.addToNameIDFormats(nidf)
		aa.addToAssertionIDRequestServices(aidrs)

		aa.addToAttributes(attr1)
		aa.addToAttributes(attr2)
		aa.addToAttributes(attr3)
		aa.addToAttributes(attr4)

		def expected = loadExpected('testvalidaadescriptor')	// deliberate both paths should give same outcome
		
		when:
		metadataGenerationService.attributeAuthorityDescriptor(builder, false, false, true, aa)
		def xml = writer.toString()
		def strippedXML = xml.replace("shibmd:", "").replace("saml:", "")	// dodgy as hell but easiest option presently as namespaces causes problems in validation

		then:
		xml.contains('saml:Attribute')
		xml.contains('shibmd:Scope')
		def diff = new Diff(expected, strippedXML)
		diff.similar()
	}

  def "Test invalid AttributeAuthorityDescriptor due to non functioning AttributeService"() {
    setup:
    setupBindings()
    def saml2Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:2.0:protocol')
    def saml1Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:1.1:protocol urn:mace:shibboleth:1.0')
    def protocolSupportEnumerations = [saml1Prot, saml2Prot]

    def organization = new Organization(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
    def entityDescriptor = new EntityDescriptor(organization:organization, entityID:"https://test.com", active:true, approved:true)

    def email = "test@example.com"
    def home = "(07) 1111 1111"
    def work = "(567) 222 22222"
    def mobile = "0413 867 208"
    def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
    def admin = ContactType.build(name:"administrative")
    def contactPerson = ContactPerson.build(contact:contact, type:admin)

    def aa = new AttributeAuthorityDescriptor(protocolSupportEnumerations:protocolSupportEnumerations, entityDescriptor:entityDescriptor, organization:organization, approved:true, active:true, scope:"test.com")
    def ep = new AttributeService(descriptor:aa, active:false, approved:true, location:'https://test.example.com:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(ep)

    def certificate = new Certificate(data:loadPK())
    def keyInfo = new KeyInfo(keyName:"key1", certificate:certificate)
    def encryptionMethod = new EncryptionMethod(algorithm:"http://www.w3.org/2001/04/xmlenc#tripledes-cbc")
    def keyDescriptor = new KeyDescriptor(roleDescriptor:aa, keyType:KeyTypes.encryption, keyInfo:keyInfo, encryptionMethod:encryptionMethod)

    def certificate2 = new Certificate(data:loadPK())
    def keyInfo2 = new KeyInfo(keyName:"key2", certificate:certificate2)
    def keyDescriptor2 = new KeyDescriptor(roleDescriptor:aa, keyType:KeyTypes.signing, keyInfo:keyInfo2)

    keyDescriptor.id = 1
    keyDescriptor2.id = 2

    def nidf = new SamlURI(uri:"supported:nameid:format:urn")
    def aidrs = new AssertionIDRequestService(active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/assertionidrequestservice/REDIRECT")

    def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
    def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
    def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
    def ba4 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()

    def attr1 = new Attribute(base:ba1)
    def attr2 = new Attribute(base:ba2)
    def attr3 = new Attribute(base:ba3)
    def attr4 = new Attribute(base:ba4)
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:1'))
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:2'))
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:3'))

    aa.addToKeyDescriptors(keyDescriptor)
    aa.addToKeyDescriptors(keyDescriptor2)
    aa.addToContacts(contactPerson)

    aa.addToNameIDFormats(nidf)
    aa.addToAssertionIDRequestServices(aidrs)

    aa.addToAttributes(attr1)
    aa.addToAttributes(attr2)
    aa.addToAttributes(attr3)
    aa.addToAttributes(attr4)

    when:
    metadataGenerationService.attributeAuthorityDescriptor(builder, false, false, true, aa)
    def xml = writer.toString()

    then:
    xml == ""
  }

  def "Test valid AttributeAuthorityDescriptor creation with on AttributeService not functioning"() {
    setup:
    setupBindings()
    def saml2Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:2.0:protocol')
    def saml1Prot = SamlURI.build(uri:'urn:oasis:names:tc:SAML:1.1:protocol urn:mace:shibboleth:1.0')
    def protocolSupportEnumerations = [saml1Prot, saml2Prot]

    def organization = new Organization(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
    def entityDescriptor = new EntityDescriptor(organization:organization, entityID:"https://test.com", active:true, approved:true)

    def email = "test@example.com"
    def home = "(07) 1111 1111"
    def work = "(567) 222 22222"
    def mobile = "0413 867 208"
    def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
    def admin = ContactType.build(name:"administrative")
    def contactPerson = ContactPerson.build(contact:contact, type:admin)

    def aa = new AttributeAuthorityDescriptor(protocolSupportEnumerations:protocolSupportEnumerations, entityDescriptor:entityDescriptor, organization:organization, approved:true, active:true, scope:"test.com")
    def ep = new AttributeService(descriptor:aa, active:true, approved:true, location:'https://test.example.com:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(ep)
    def ep2 = new AttributeService(descriptor:aa, active:false, approved:true, location:'https://test2.example.com:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(ep2)

    def certificate = new Certificate(data:loadPK())
    def keyInfo = new KeyInfo(keyName:"key1", certificate:certificate)
    def encryptionMethod = new EncryptionMethod(algorithm:"http://www.w3.org/2001/04/xmlenc#tripledes-cbc")
    def keyDescriptor = new KeyDescriptor(roleDescriptor:aa, keyType:KeyTypes.encryption, keyInfo:keyInfo, encryptionMethod:encryptionMethod)

    def certificate2 = new Certificate(data:loadPK())
    def keyInfo2 = new KeyInfo(keyName:"key2", certificate:certificate2)
    def keyDescriptor2 = new KeyDescriptor(roleDescriptor:aa, keyType:KeyTypes.signing, keyInfo:keyInfo2)

    keyDescriptor.id = 1
    keyDescriptor2.id = 2

    def nidf = new SamlURI(uri:"supported:nameid:format:urn")
    def aidrs = new AssertionIDRequestService(active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/assertionidrequestservice/REDIRECT")

    def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
    def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
    def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
    def ba4 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()

    def attr1 = new Attribute(base:ba1)
    def attr2 = new Attribute(base:ba2)
    def attr3 = new Attribute(base:ba3)
    def attr4 = new Attribute(base:ba4)
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:1'))
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:2'))
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:3'))

    aa.addToKeyDescriptors(keyDescriptor)
    aa.addToKeyDescriptors(keyDescriptor2)
    aa.addToContacts(contactPerson)

    aa.addToNameIDFormats(nidf)
    aa.addToAssertionIDRequestServices(aidrs)

    aa.addToAttributes(attr1)
    aa.addToAttributes(attr2)
    aa.addToAttributes(attr3)
    aa.addToAttributes(attr4)

    def expected = loadExpected('testvalidaadescriptor')  // deliberate both paths should give same outcome

    when:
    metadataGenerationService.attributeAuthorityDescriptor(builder, false, false, true, aa)
    def xml = writer.toString()
    def strippedXML = xml.replace("shibmd:", "").replace("saml:", "") // dodgy as hell but easiest option presently as namespaces causes problems in validation

    then:
    xml.contains('saml:Attribute')
    xml.contains('shibmd:Scope')
    def diff = new Diff(expected, strippedXML)
    diff.similar()
  }

  def "Test valid EntityDescriptor generation where IDPSSODescriptor indicates AttributeAuthorityOnly"() {
    setup:
    setupBindings()

    def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
    def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID", active:true, approved:true)

    def email = "test@example.com"
    def home = "(07) 1111 1111"
    def work = "(567) 222 22222"
    def mobile = "0413 867 208"
    def contact = Contact.build(givenName:"Test", surname:"User", email:email, homePhone:home, workPhone:work, mobilePhone:mobile)
    def admin = ContactType.build(name:"administrative")
    def contactPerson = ContactPerson.build(contact:contact, type:admin)

    def idp = IDPSSODescriptor.build(protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, entityDescriptor:entityDescriptor, approved:true, active:true, attributeAuthorityOnly:aaonly)

    def certificate = new Certificate(data:loadPK())
    def keyInfo = new KeyInfo(keyName:"key1", certificate:certificate)
    def encryptionMethod = new EncryptionMethod(algorithm:"http://www.w3.org/2001/04/xmlenc#tripledes-cbc")
    def keyDescriptor = new KeyDescriptor(roleDescriptor:idp, keyType:KeyTypes.encryption, keyInfo:keyInfo, encryptionMethod:encryptionMethod)

    def certificate2 = new Certificate(data:loadPK2())
    def keyInfo2 = new KeyInfo(certificate:certificate2)
    def keyDescriptor2 = new KeyDescriptor(roleDescriptor:idp, keyType:KeyTypes.signing, keyInfo:keyInfo2)

    def certificate3 = new Certificate(data:loadPK())
    def keyInfo3 = new KeyInfo(certificate:certificate3)
    def keyDescriptor3 = new KeyDescriptor(roleDescriptor:idp, keyType:KeyTypes.signing, keyInfo:keyInfo3, disabled:true)

    keyDescriptor.id = 1
    keyDescriptor2.id = 2
    keyDescriptor3.id = 3

    def ars = new ArtifactResolutionService(descriptor:idp, index:100, active:true, approved:true, isDefault:true, binding:soap, location:"https://test.example.com/ars/artifact")
    def ars2 = new ArtifactResolutionService(descriptor:idp, index:101, active:true, approved:true, isDefault:false, binding:soap, location:"https://test.example.com/ars/artifact2")

    ars.id = 1
    ars2.id = 2

    def slo = new SingleLogoutService(descriptor:idp, active:true, approved:true, binding:httpPost, location:"https://test.example.com/slo/POST")
    def mnid = new ManageNameIDService(descriptor:idp, active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/mnid/REDIRECT")
    def nidf = new SamlURI(uri:"supported:nameid:format:urn")

    def sso = new SingleSignOnService(descriptor:idp,active:true, approved:true, binding:httpPost, location:"https://test.example.com/sso/POST")
    def nidms = new NameIDMappingService(descriptor:idp,active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/nameidmappingserivce/REDIRECT")
    def aidrs = new AssertionIDRequestService(descriptor:idp,active:true, approved:true, binding:httpRedirect, location:"https://test.example.com/assertionidrequestservice/REDIRECT")

    def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
    def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
    def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
    def ba4 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()

    def attr1 = new Attribute(idpSSODescriptor:idp, base:ba1)
    def attr2 = new Attribute(idpSSODescriptor:idp, base:ba2)
    def attr3 = new Attribute(idpSSODescriptor:idp, base:ba3)
    def attr4 = new Attribute(idpSSODescriptor:idp, base:ba4)
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:1'))
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:2'))
    attr4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:3'))

    idp.addToKeyDescriptors(keyDescriptor)
    idp.addToKeyDescriptors(keyDescriptor2)
    idp.addToKeyDescriptors(keyDescriptor3)
    idp.addToContacts(contactPerson)

    idp.addToArtifactResolutionServices(ars)
    idp.addToArtifactResolutionServices(ars2)
    idp.addToSingleLogoutServices(slo)
    idp.addToManageNameIDServices(mnid)
    idp.addToNameIDMappingServices(nidms)
    idp.addToAssertionIDRequestServices(aidrs)
    idp.addToNameIDFormats(nidf)
    idp.addToSingleSignOnServices(sso)

    idp.addToAttributes(attr1)
    idp.addToAttributes(attr2)
    idp.addToAttributes(attr3)
    idp.addToAttributes(attr4)

    def aa = new AttributeAuthorityDescriptor(protocolSupportEnumerations:protocolSupportEnumerations, entityDescriptor:entityDescriptor, organization:organization, approved:true, active:true, scope:"test.com")
    def ep = new AttributeService(descriptor:aa, active:true, approved:true, location:'https://test.example.com:8443/idp/profile/SAML2/SOAP/AttributeQuery', binding:soap)
    aa.addToAttributeServices(ep)

    idp.collaborator = aa
    aa.collaborator = idp

    entityDescriptor.addToIdpDescriptors(idp)
    entityDescriptor.addToAttributeAuthorityDescriptors(aa)

    def expected = loadExpected(expect)

    when:
    metadataGenerationService.entityDescriptor(builder, false, false, true, entityDescriptor, false)

    def xml = writer.toString()
    def strippedXML = xml.replace("shibmd:", "").replace("saml:", "") // dodgy as hell but easiest option presently
    def diff = new Diff(expected, strippedXML)

    then:
    xml.contains('saml:Attribute')
    xml.contains('shibmd:Scope')
    similarExcludingID(diff)

    where:
    aaonly << [true, false]
    expect << ['testentitydescriptorwhereidpindicatesonlyaa', 'testentitydescriptorwhereidpdoesnotindicateonlyaa']
  }

}
