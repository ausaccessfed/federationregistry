package fedreg.core

class DescriptorAttributeController {

	static allowedMethods = [remove: "POST"]
	
	def remove = {
		if(!params.id) {
			log.warn "Descriptor ID was not present"
			render message(code: 'fedreg.controllers.namevalue.missing')
			response.setStatus(500)
			return
		}
		
		if(!params.attributeID) {
			log.warn "Attribute ID was not present"
			render message(code: 'fedreg.controllers.namevalue.missing')
			response.setStatus(500)
			return
		}
		
		def descriptor = RoleDescriptor.get(params.id)
		if(!descriptor) {
			log.warn "RoleDescriptor identified by id $params.id was not located"
			render message(code: 'fedreg.attribute.nonexistant', args: [params.id])
			response.setStatus(500)
			return
		}
		
		def attribute = Attribute.get(params.attributeID)
		if(!attribute) {
			log.warn "Attribute identified by id ${params.attributeID} was not located"
			render message(code: 'fedreg.attribute.nonexistant', args: [params.attributeID])
			response.setStatus(500)
			return
		}
		
		if(!descriptor.attributes.contains(attribute)) {
			log.warn "${attribute} isn't supported by descriptor ${params.id}"
			response.setStatus(500)
			render message(code: 'fedreg.attribute.remove.notsupported', args:[attribute.base.friendlyName])
			return
		}
		
		log.info "Removing ${attribute} from descriptor ${params.id}"
		descriptor.removeFromAttributes(attribute)
		descriptor.save()
		if(descriptor.hasErrors()) {
			log.warn "Removing ${attribute} from descriptor ${params.id} failed"
			descriptor.errors.each {
				log.debug it
			}
			render message(code: 'fedreg.attribute.remove.failed', args:[attribute.base.friendlyName])
			response.setStatus(500)
			return
		}else {
			render message(code: 'fedreg.attribute.remove.success', args:[attribute.base.friendlyName])
		}
	}
	
	def list = {
		if(!params.id) {
			log.warn "Descriptor ID was not present"
			render message(code: 'fedreg.controllers.namevalue.missing')
			response.setStatus(500)
			return
		}
		
		if(!params.containerID) {
			log.warn "Container ID was not present"
			render message(code: 'fedreg.controllers.namevalue.missing')
			response.setStatus(500)
			return
		}
		
		def descriptor = RoleDescriptor.get(params.id)
		if(!descriptor) {
			log.warn "RoleDescriptor identified by id ${params.id} was not located"
			render message(code: 'fedreg.roledescriptor.nonexistant', args: [params.id])
			response.setStatus(500)
			return
		}
		
		render template: "/templates/attributes/list", contextPath: pluginContextPath, model:[attrs:descriptor.attributes, containerID:params.containerID]
	}
	
	def add = {
		if(!params.id) {
			log.warn "Descriptor ID was not present"
			render message(code: 'fedreg.controllers.namevalue.missing')
			response.setStatus(500)
			return
		}
		
		if(!params.attributeID) {
			log.warn "Attribute ID was not present"
			render message(code: 'fedreg.controllers.namevalue.missing')
			response.setStatus(500)
			return
		}
		
		def descriptor = RoleDescriptor.get(params.id)
		if(!descriptor) {
			log.warn "RoleDescriptor identified by id ${params.id} was not located"
			render message(code: 'fedreg.roledescriptor.nonexistant', args: [params.id])
			response.setStatus(500)
			return
		}
		
		def base = AttributeBase.get(params.attributeID)
		if(!base) {
			log.warn "Attribute Base identified by id ${params.attributeID} was not located"
			render message(code: 'fedreg.nameidformat.nonexistant', args: [params.attributeID])
			response.setStatus(500)
			return
		}
		
		for( a in descriptor.attributes) {
			if(a.base == base) {
				log.warn "${base} is already supported by descriptor ${params.id}"
				response.setStatus(500)
				render message(code: 'fedreg.attribute.add.alreadysupported', args:[base.friendlyName])
				return
			}
		}
		
		descriptor.addToAttributes(new Attribute(base:base))
		descriptor.save()
		if(descriptor.hasErrors()) {
			log.warn "Adding ${attribute} to descriptor ${params.id} failed"
			descriptor.errors.each {
				log.debug it
			}
			render message(code: 'fedreg.attribute.add.failed', args:[base.friendlyName])
			response.setStatus(500)
			return
		}else {
			render message(code: 'fedreg.attribute.add.success', args:[base.friendlyName])
		}
	}

}
