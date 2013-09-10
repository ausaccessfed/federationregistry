package aaf.fr.api.v1

import aaf.fr.foundation.*
import grails.converters.JSON
import groovy.xml.MarkupBuilder

class AttributeFiltersAPIv1Controller {
	def attributeFilterGenerationService
	
	def list = {	
		def results = []
		
		// Distribute non functioning IdP filters (but ignore archived) incase they come online and request AA before subsequent update
		def identityProviders = IDPSSODescriptor.list(archived:false).sort{it.id}		
		identityProviders.each { idp ->
			def filter = [:]
			filter.identityprovider = [:]
			filter.identityprovider.id = idp.id
			filter.identityprovider.name = idp.displayName
			filter.format = "xml"
			filter.link = g.createLink(controller: 'attributeFiltersAPIv1', id: idp.id, absolute: true)
			
			results.add(filter)
		}
		
		def response = [attributefilters:results]
		render response as JSON
	}
	
	def show = {		
		if(!params.id) {
			log.warn "IDPSSODescriptor ID was not present"
			render message(code: 'controllers.fr.generic.namevalue.missing')
			response.sendError(400)
			return
		}
		
		def identityProvider = IDPSSODescriptor.get(params.id)
		if (!identityProvider) {
			log.warn "IDPSSODescriptor does not exist for ${params.id}"
			render message(code: 'controllers.fr.generic.namevalue.missing')
			response.sendError(400)
			return
		}
		
		def writer = new StringWriter()
		def builder = new MarkupBuilder(writer)
		builder.doubleQuotes = true
		
		def federation = grailsApplication.config.aaf.fr.metadata.federation
		
		attributeFilterGenerationService.generate(builder, federation, identityProvider.id)
		def xml = writer.toString()
		
		render(text:"${xml}\n\n<!-- Exported automatically from Federation Registry at ${new Date()} -->\n\n", contentType:"text/xml", encoding:"UTF-8")		
	}
	
}
