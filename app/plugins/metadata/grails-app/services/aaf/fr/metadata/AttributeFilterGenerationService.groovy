package aaf.fr.metadata

import java.text.SimpleDateFormat

import aaf.fr.foundation.*

/**
 * Provides methods to generate AttributeFilters for IDPSSODescriptor instances.
 *
 * @author Bradley Beddoes
 */
class AttributeFilterGenerationService {

	def generate(builder, groupID, idp) {
		builder.mkp.xmlDeclaration(version:'1.0')
		attributeFilterPolicyGroup(builder, groupID, idp)
	}
	
	def attributeFilterPolicyGroup(builder, groupID, idp) {
		SimpleDateFormat idf = new SimpleDateFormat("_yyyyMMdd'T'HHmmss'Z'")
		def currently = new Date()
		
		// id is lower case in these polices per AF 2.0 schema and is of type String not ID.
		
		builder.AttributeFilterPolicyGroup(id: idf.format(currently),
			"xmlns":"urn:mace:shibboleth:2.0:afp",
			"xmlns:basic":"urn:mace:shibboleth:2.0:afp:mf:basic",
			"xmlns:saml":"urn:mace:shibboleth:2.0:afp:mf:saml",
			"xmlns:xsi":"http://www.w3.org/2001/XMLSchema-instance",
			"xsi:schemaLocation":"urn:mace:shibboleth:2.0:afp classpath:/schema/shibboleth-2.0-afp.xsd urn:mace:shibboleth:2.0:afp:mf:basic classpath:/schema/shibboleth-2.0-afp-mf-basic.xsd urn:mace:shibboleth:2.0:afp:mf:saml classpath:/schema/shibboleth-2.0-afp-mf-saml.xsd") 
			{	
				// Process all our services
				def identityProvider = IDPSSODescriptor.get(idp)
				if(!identityProvider)
					throw new ErronousStateException("Identity Provider specified ($idp) for Attribute Filter creation does not exist.")
					
				builder.mkp.yield "\n"
				comment builder, "Custom generated attribute filter for IDP: ${identityProvider.displayName}, EntityID: ${identityProvider.entityDescriptor.entityID}"
				comment builder, "This version exported from Federation Registry at ${new Date()}"
				
				defaultReleasePolicy(builder, groupID)
				def serviceProviders = SPSSODescriptor.list().findAll { sp -> sp.functioning() }
				serviceProviders?.each { serviceProvider ->
					servicePolicy(builder, identityProvider, serviceProvider, groupID)
				}
				builder.mkp.yield "\n"
			}
	}

	def defaultReleasePolicy(builder, groupID) {
		builder.mkp.yield "\n"
		builder.AttributeFilterPolicy(id:"afp_default_for:$groupID") {
			PolicyRequirementRule('xsi:type':'saml:AttributeRequesterInEntityGroup', 'groupID':groupID)

			AttributeRule(attributeID:"persistentID") {
				PermitValueRule("xsi:type" : "basic:ANY")
			}

			AttributeRule("attributeID" : "transientId") {
				PermitValueRule("xsi:type" : "basic:ANY")
			}
		}
	}
	
	def servicePolicy(builder, identityProvider, serviceProvider, groupID) {
		builder.mkp.yield "\n"
		comment builder, "Attribute release filter for SP ${serviceProvider.displayName} in entity ${serviceProvider.entityDescriptor.entityID}"
		builder.AttributeFilterPolicy(id:"afp_for:${serviceProvider.entityDescriptor.entityID}") {
			PolicyRequirementRule("xsi:type" : "basic:AND") {
				"basic:Rule"("xsi:type":"basic:AttributeRequesterString", value:"${serviceProvider.entityDescriptor.entityID}")
				"basic:Rule"("xsi:type":"saml:AttributeRequesterInEntityGroup", groupID:groupID)
			}

      if(serviceProvider.forceAttributesInFilter)
        comment builder, "Federation pillar service. Thus all attributes are requested regardless of indicated IdP attribute support FR."

			serviceProvider.attributeConsumingServices.each { acs ->
				acs.requestedAttributes?.sort{it.base.name}.each { ra ->
					if(ra.functioning()) {
						if(identityProvider.attributes.findAll{it.base == ra.base}.size() == 1 || serviceProvider.forceAttributesInFilter) {
							if(ra.base.specificationRequired) {
								if(ra.values?.size() > 0) {
									AttributeRule(attributeID:ra.base.name){
										PermitValueRule("xsi:type":"basic:OR") {
											if(ra.values.size() == 1) {
												def v = ra.values.toList().get(0)	// OR requires min 2 rules. We add this here to be schema compliant even when only a single value is requested
												"basic:Rule" ("xsi:type":"basic:AttributeValueString", value:v.value, ignoreCase:"true")
												"basic:Rule" ("xsi:type":"basic:AttributeValueString", value:v.value, ignoreCase:"true")
											}
											else {
												ra.values.sort{it.value}.each { v ->
													"basic:Rule" ("xsi:type":"basic:AttributeValueString", value:v.value, ignoreCase:"true")
												}
											}
										}
									}
								}
							} else {
								AttributeRule(attributeID:ra.base.name){
									PermitValueRule("xsi:type":"basic:ANY")
								}
							}
						}
						else{
							comment builder, "Attribute ${ra.base.name} is not supported by this Identity Provider. Please configure your Attribute Resolver accordingly and indicate your support using Federation Registry"
							if(ra.isRequired)
								comment builder, "Additionally the attribute ${ra.base.name} is required by this service provider. User access to this service will fail."
						}
					}
				}
			}
		}
	}
	
	def comment(builder, comment) {
		builder.mkp.yieldUnescaped "\n<!-- $comment -->"
	}

}
