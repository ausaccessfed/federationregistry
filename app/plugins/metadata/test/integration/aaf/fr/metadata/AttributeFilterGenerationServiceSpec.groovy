package aaf.fr.metadata

import org.custommonkey.xmlunit.*;

import grails.plugin.spock.*
import groovy.xml.*

import aaf.fr.foundation.*

class AttributeFilterGenerationServiceSpec extends IntegrationSpec {
	def attributeFilterGenerationService
	def cryptoService

	def writer
	def builder

	def soap
	def httpRedirect
	def httpPost
	def httpArtifact
	def attrUri, coreCategory, optionalCategory
	def saml2Prot, saml1Prot

	def cleanup() {
	}

	def setup () {
		writer = new StringWriter()
		builder = new MarkupBuilder(writer)
		builder.doubleQuotes = true

		XMLUnit.ignoreComments = true
		XMLUnit.ignoreWhitespace = true
	}

	def setupSAML() {
		soap = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:SOAP', description:'').save()
		httpRedirect = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect', description:'').save()
		httpPost = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST', description:'').save()
		httpArtifact = new SamlURI(type:SamlURIType.ProtocolBinding, uri:'urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact', description:'').save()
		
		saml2Prot = new SamlURI(type:SamlURIType.ProtocolSupport, uri:'urn:oasis:names:tc:SAML:2.0:protocol').save()
		saml1Prot = new SamlURI(type:SamlURIType.ProtocolSupport, uri:'urn:oasis:names:tc:SAML:1.1:protocol urn:mace:shibboleth:1.0').save()
		
		attrUri = new SamlURI(type:SamlURIType.AttributeNameFormat, uri:'urn:oasis:names:tc:SAML:2.0:attrname-format:uri', description:'').save()
		coreCategory = new AttributeCategory(name:'Core').save()
		optionalCategory = new AttributeCategory(name:'Optional').save()
	}
	
	def loadResult(file) {
		new File("./test/data/attributefiltergenerationspec/${file}.xml").text
	}
	
	def String loadPK() {
		new File('./test/data/selfsigned.pem').text
	}

	def similarExcludingID(def diff) {
		def result = true
		def ddiff = new DetailedDiff(diff)
		ddiff.allDifferences.each {
			// id is time based and subject to change
	    if(!it.controlNodeDetail.xpathLocation.equals("/AttributeFilterPolicyGroup[1]/@id")) {
	    	result = false
	    	println it
	    }
    }
    result
	}

	def 'Test generation with no Service Providers'() {
		setup:
		def expected = loadResult("testnoserviceproviders")
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID1", active:true, approved:true)
		def idp = IDPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true).save()

		if(!idp) {
			throw new Exception("IDP save failed")
		}

		when:
		attributeFilterGenerationService.generate(builder, "test.aaf.edu.au", idp.id)
		def xml = writer.toString()
		def diff = new Diff(expected, xml)
		
		then:
		similarExcludingID(diff)
	}
	
	def 'Test generation with no active Service Providers'() {
		setup:
		setupSAML()
		def expected = loadResult("testnoserviceproviders")
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID1", active:true, approved:true)
		def idp = IDPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true).save()

		if(!idp)
			throw new Exception("IDP save failed")
		
		(1..5).each { i->
			def sp = baseSP("$i")
			sp.active = false
			def sps = sp.save()
			
			if(!sps) {
				sp.errors.each {println it}
				throw new Exception("SP save failed")
			}
		}

		when:
		attributeFilterGenerationService.generate(builder, "test.aaf.edu.au", idp.id)
		def xml = writer.toString()
		def diff = new Diff(expected, xml)
		
		then:
		similarExcludingID(diff)
	}
	
	def 'Test generation with no approved Service Providers'() {
		setup:
		setupSAML()
		def expected = loadResult("testnoserviceproviders")
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID1", active:true, approved:true)
		def idp = IDPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true).save()

		if(!idp)
			throw new Exception("IDP save failed")
		
		(1..5).each { i->
			def sp = baseSP("$i")
			sp.approved = false
			def sps = sp.save()
			
			if(!sps) {
				sp.errors.each {println it}
				throw new Exception("SP save failed")
			}
		}

		when:
		attributeFilterGenerationService.generate(builder, "test.aaf.edu.au", idp.id)
		def xml = writer.toString()
		def diff = new Diff(expected, xml)
		
		then:
		similarExcludingID(diff)
	}
	
	def 'Test generation with single SP requesting attributes that IDP fully supports'() {
		setup:
		setupSAML()
		def expected = loadResult("testsinglesp")
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID1", active:true, approved:true)
		def idp = IDPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true).save()

		if(!idp)
			throw new Exception("IDP save failed")
		
		def sp = baseSP("1").save()
		if(!sp) {
			sp.errors.each {println it}
			throw new Exception("SP save failed")
		}
		
		def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
		def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
		def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
		
		
		def a1 = new Attribute(base:ba1).save()
		def a2 = new Attribute(base:ba2).save()
		def a3 = new Attribute(base:ba3).save()
		idp.addToAttributes(a1).save()
		idp.addToAttributes(a2).save()
		idp.addToAttributes(a3).save()
		
		def ra1 = new RequestedAttribute(base:ba1, isRequired:true, approved:true, reasoning:"valid test case")
		def ra2 = new RequestedAttribute(base:ba2, isRequired:false, approved:true, reasoning:"valid test case")
		def ra3 = new RequestedAttribute(base:ba3, isRequired:false, approved:true, reasoning:"valid test case")
		def ra4 = new RequestedAttribute(base:ba3, isRequired:false, approved:false, reasoning:"valid test case")
		def attrService = sp.attributeConsumingServices.toList().get(0)
		attrService.addToRequestedAttributes(ra1)
		attrService.addToRequestedAttributes(ra2)
		attrService.addToRequestedAttributes(ra3)
		attrService.addToRequestedAttributes(ra4)
		
		when:
		attributeFilterGenerationService.generate(builder, "test.aaf.edu.au", idp.id)
		def xml = writer.toString()
		def diff = new Diff(expected, xml)
		
		then:
		similarExcludingID(diff)
	}
	
	def 'Test generation with multiple SP requesting attributes that IDP fully supports'() {
		setup:
		setupSAML()
		def expected = loadResult("testmultiplesp")
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID1", active:true, approved:true)
		def idp = IDPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true).save()

		if(!idp)
			throw new Exception("IDP save failed")
		
		def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
		def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
		def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
		
		
		def a1 = new Attribute(base:ba1).save()
		def a2 = new Attribute(base:ba2).save()
		def a3 = new Attribute(base:ba3).save()
		idp.addToAttributes(a1).save()
		idp.addToAttributes(a2).save()
		idp.addToAttributes(a3).save()
		
		(1..5).each { i ->
			def sp = baseSP("$i").save()
			if(!sp) {
				sp.errors.each {println it}
				throw new Exception("SP save failed")
			}
			
			def ra1 = new RequestedAttribute(base:ba1, isRequired:true, approved:true, reasoning:"valid test case")
			def ra2 = new RequestedAttribute(base:ba2, isRequired:true, approved:true, reasoning:"valid test case")
			def ra3 = new RequestedAttribute(base:ba3, isRequired:true, approved:true, reasoning:"valid test case")
			def ra4 = new RequestedAttribute(base:ba3, isRequired:false, approved:false, reasoning:"valid test case")
			def attrService = sp.attributeConsumingServices.toList().get(0)
			attrService.addToRequestedAttributes(ra1)
			if(i == 1 || i == 2 || i == 4)
				attrService.addToRequestedAttributes(ra2)
			if(i == 1 || i == 3 || i == 5)
				attrService.addToRequestedAttributes(ra3)
			attrService.addToRequestedAttributes(ra4)
		}
		
		when:
		attributeFilterGenerationService.generate(builder, "test.aaf.edu.au", idp.id)
		def xml = writer.toString()
		def diff = new Diff(expected, xml)
		
		then:
		similarExcludingID(diff)
	}
	
	def 'Test generation with single SP requesting attributes that IDP only supports 1 of (givenName)'() {
		setup:
		setupSAML()
		def expected = loadResult("testsinglespmissing")
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID1", active:true, approved:true)
		def idp = IDPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true).save()

		if(!idp)
			throw new Exception("IDP save failed")
		
		def sp = baseSP("1").save()
		if(!sp) {
			sp.errors.each {println it}
			throw new Exception("SP save failed")
		}
		
		def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
		def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
		def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
		
		
		def a1 = new Attribute(base:ba3).save()
		idp.addToAttributes(a1).save()
		
		def ra1 = new RequestedAttribute(base:ba1, isRequired:true, approved:true, reasoning:"valid test case")
		def ra2 = new RequestedAttribute(base:ba2, isRequired:false, approved:true, reasoning:"valid test case")
		def ra3 = new RequestedAttribute(base:ba3, isRequired:false, approved:true, reasoning:"valid test case")
		def attrService = sp.attributeConsumingServices.toList().get(0)
		attrService.addToRequestedAttributes(ra1)
		attrService.addToRequestedAttributes(ra2)
		attrService.addToRequestedAttributes(ra3)
		
		when:
		attributeFilterGenerationService.generate(builder, "test.aaf.edu.au", idp.id)
		def xml = writer.toString()
		def diff = new Diff(expected, xml)
		
		then:
		similarExcludingID(diff)
	}
	
	def 'Test generation with multiple SP requesting attributes that IDP only supports 1 of (givenName)'() {
		setup:
		setupSAML()
		def expected = loadResult("testmultiplespmissing")
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID1", active:true, approved:true)
		def idp = IDPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true).save()

		if(!idp)
			throw new Exception("IDP save failed")
		
		def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
		def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
		def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
		
		
		def a1 = new Attribute(base:ba3).save()
		idp.addToAttributes(a1).save()
		
		(1..5).each { i ->
			def sp = baseSP("$i").save()
			if(!sp) {
				sp.errors.each {println it}
				throw new Exception("SP save failed")
			}
			
			def ra1 = new RequestedAttribute(base:ba1, isRequired:true, approved:true, reasoning:"valid test case")
			def ra2 = new RequestedAttribute(base:ba2, isRequired:false, approved:true, reasoning:"valid test case")
			def ra3 = new RequestedAttribute(base:ba3, isRequired:false, approved:true, reasoning:"valid test case")
			def attrService = sp.attributeConsumingServices.toList().get(0)
			attrService.addToRequestedAttributes(ra1)
			if(i == 1 || i == 2 || i == 4)
				attrService.addToRequestedAttributes(ra2)
			if(i == 1 || i == 3 || i == 5)
			attrService.addToRequestedAttributes(ra3)
		}
		
		when:
		attributeFilterGenerationService.generate(builder, "test.aaf.edu.au", idp.id)
		def xml = writer.toString()
		def diff = new Diff(expected, xml)
		
		then:
		similarExcludingID(diff)
	}
	
	def 'Test generation with single SP requesting attributes that IDP fully supports, additionally SP requests eduPersonEntitlement which requires specification'() {
		setup:
		setupSAML()
		def expected = loadResult("testsinglespusingentitlement")
		def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID1", active:true, approved:true)
		def idp = IDPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true).save()

		if(!idp)
			throw new Exception("IDP save failed")
		
		def sp = baseSP("1").save()
		if(!sp) {
			sp.errors.each {println it}
			throw new Exception("SP save failed")
		}
		
		def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
		def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
		def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
		def ba4 =  new AttributeBase(oid:'1.3.6.1.4.1.5923.1.1.1.7', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:eduPersonEntitlement', name:'eduPersonEntitlement', description:'Member of: URI (either URL or URN) that indicates a set of rights to specific resources based on an agreement across the releavant community', category:coreCategory, specificationRequired:true).save()
		
		def a1 = new Attribute(base:ba1).save()
		def a2 = new Attribute(base:ba2).save()
		def a3 = new Attribute(base:ba3).save()
		def a4 = new Attribute(base:ba4).save()
		idp.addToAttributes(a1).save()
		idp.addToAttributes(a2).save()
		idp.addToAttributes(a3).save()
		idp.addToAttributes(a4).save()
		
		def ra1 = new RequestedAttribute(base:ba1, isRequired:true, approved:true, reasoning:"valid test case")
		def ra2 = new RequestedAttribute(base:ba2, isRequired:false, approved:true, reasoning:"valid test case")
		def ra3 = new RequestedAttribute(base:ba3, isRequired:false, approved:true, reasoning:"valid test case")
		
		def ra4 = new RequestedAttribute(base:ba4, isRequired:false, approved:true, reasoning:"valid test case")
		ra4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:1'))
		ra4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:2'))
		ra4.addToValues(new AttributeValue(value:'urn:mace:test:attr:value:3'))
		
		def attrService = sp.attributeConsumingServices.toList().get(0)
		attrService.addToRequestedAttributes(ra1)
		attrService.addToRequestedAttributes(ra2)
		attrService.addToRequestedAttributes(ra3)
		attrService.addToRequestedAttributes(ra4)
		
		when:
		attributeFilterGenerationService.generate(builder, "test.aaf.edu.au", idp.id)
		def xml = writer.toString()
		println xml
		def diff = new Diff(expected, xml)
		
		then:
		similarExcludingID(diff)
	}

  def 'Test generation with multiple SP requesting attributes that IDP only supports 1 of (givenName) with one SP having forceAttributesInFilter set true'() {
    setup:
    setupSAML()
    def expected = loadResult("testmultiplespmissingforce")
    def organization = Organization.build(active:true, approved:true, name:"Test Organization", displayName:"Test Organization Display", lang:"en", url: "http://example.com")
    def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://test.example.com/myuniqueID1", active:true, approved:true)
    def idp = IDPSSODescriptor.build(entityDescriptor:entityDescriptor, organization:organization, active:true, approved:true).save()

    if(!idp)
      throw new Exception("IDP save failed")
    
    def ba1 =  new AttributeBase(oid:'2.5.4.3', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:cn', name:'commonName', description:'An individuals common name, typically their full name. This attribute should not be used in transactions where it is desirable to maintain user anonymity.', category:coreCategory, specificationRequired:false).save()
    def ba2 =  new AttributeBase(oid:'2.5.4.4', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:sn', name:'surname', description:'Surname or family name', category:optionalCategory, specificationRequired:false).save()
    def ba3 =  new AttributeBase(oid:'2.5.4.42', nameFormat: attrUri, legacyName:'urn:mace:dir:attribute-def:givenName', name:'givenName', description:'Given name of a person', category:optionalCategory, specificationRequired:false).save()
    
    
    def a1 = new Attribute(base:ba3).save()
    idp.addToAttributes(a1).save()
    
    (1..5).each { i ->
      def sp = baseSP("$i").save()
      if(!sp) {
        sp.errors.each {println it}
        throw new Exception("SP save failed")
      }

      if(i == 1){
        sp.forceAttributesInFilter = true
        sp.save()
      }
      
      def ra1 = new RequestedAttribute(base:ba1, isRequired:true, approved:true, reasoning:"valid test case")
      def ra2 = new RequestedAttribute(base:ba2, isRequired:false, approved:true, reasoning:"valid test case")
      def ra3 = new RequestedAttribute(base:ba3, isRequired:false, approved:true, reasoning:"valid test case")
      def attrService = sp.attributeConsumingServices.toList().get(0)
      attrService.addToRequestedAttributes(ra1)
      if(i == 1 || i == 2 || i == 4)
        attrService.addToRequestedAttributes(ra2)
      if(i == 1 || i == 3 || i == 5)
      attrService.addToRequestedAttributes(ra3)
    }
    
    when:
    attributeFilterGenerationService.generate(builder, "test.aaf.edu.au", idp.id)
    def xml = writer.toString()
    def diff = new Diff(expected, xml)
    
    then:
    similarExcludingID(diff)
  }
	
	def baseSP(unique) {
		def protocolSupportEnumerations = [saml1Prot, saml2Prot]
		
		def organization = Organization.build(name:"Test Organization - $unique", displayName:"Test Organization Display - $unique", lang:"en", url: "http://example.com", active:true, approved:true)
		def entityDescriptor = EntityDescriptor.build(organization:organization, entityID:"https://server${unique}.test.example.com/saml", active:true, approved:true)
		
		def attrService = new AttributeConsumingService(lang:'en')
		attrService.addToServiceNames("Test Name 1 - ${unique}")
		attrService.addToServiceDescriptions("This is a great description - ${unique}")
		
		def sp = new SPSSODescriptor(displayName: "Test SP Display - ${unique}", description:"Test SP Description - ${unique}", protocolSupportEnumerations:protocolSupportEnumerations, organization:organization, approved:true, active:true, entityDescriptor:entityDescriptor)
		
		sp.addToAttributeConsumingServices(attrService)
    def servDesc = new ServiceDescription()
		sp.serviceDescription = servDesc
		
		sp
	}

}
