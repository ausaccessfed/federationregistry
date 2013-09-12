package aaf.fr.foundation

import grails.plugin.spock.*
import aaf.fr.workflow.*
import aaf.fr.identity.*

class EntityDescriptorServiceSpec extends IntegrationSpec {
	def cryptoService, entityDescriptorService, params
	
	def setup () {
    params = [:]
		def user = Subject.build()
		SpecHelpers.setupShiroEnv(user)
	}
	
	def cleanup() {
	}
	
	def "Creating valid entity descriptor succeeds (without initial contact)"() {
		setup:
		def organization = Organization.build().save()
		
		params.organization = [id: organization.id]
		params.active = true
		params.entity = [identifier:"http://identityProvider.test.com"]

    entityDescriptorService.roleService = new aaf.fr.identity.RoleService()
    entityDescriptorService.permissionService = new aaf.fr.identity.PermissionService()
		
		when:
		def(created, entityDescriptor_) = entityDescriptorService.create(params)
		
		then:
		created
		entityDescriptor_ != null
		entityDescriptor_.organization == organization
	}
	
	def "Creating entity descriptor with invalid data fails"() {
		setup:
		def organization = Organization.build().save()
		
		params.organization = [id: organization.id]
		params.active = true
		params.entity = [identifier:""]
		
		when:
		def(created, entityDescriptor_) = entityDescriptorService.create(params)
		
		then:
		!created
		entityDescriptor_ != null
		entityDescriptor_.organization == organization
	}
	
	def "Updating entity descriptor with valid data succeeds"() {
		setup:
		def organization = Organization.build().save()
		def entityDescriptor = EntityDescriptor.build(organization:organization).save()
		
		params.id = entityDescriptor.id
		params.entity = [identifier : "http://newed.test.com"]
		
		when:
		def(updated, entityDescriptor_) = entityDescriptorService.update(params)
		
		then:
		updated
		entityDescriptor_ != null
		entityDescriptor_.organization == organization
		entityDescriptor.entityID == "http://newed.test.com"
	}
	
	def "Updating entity descriptor with invalid data fails"() {
		setup:
		def organization = Organization.build().save()
		def entityDescriptor = EntityDescriptor.build(organization:organization).save()
		
		params.id = entityDescriptor.id
		params.entity = [identifier : ""]
		
		when:
		def(updated, entityDescriptor_) = entityDescriptorService.update(params)
		
		then:
		!updated
		entityDescriptor_.hasErrors()
		entityDescriptor_ != null
		entityDescriptor_.organization == organization
		entityDescriptor.entityID == ""
	}
	
	def "Updating entity descriptor with duplicate entityID fails"() {
		setup:
		def organization = Organization.build().save()
		def entityDescriptor = EntityDescriptor.build(organization:organization).save()
		def entityDescriptor2 = EntityDescriptor.build(entityID: "http://my.entityid.com").save()
		
		params.id = entityDescriptor.id
		params.entity = [identifier : "http://my.entityid.com"]
		
		when:
		def(updated, entityDescriptor_) = entityDescriptorService.update(params)
		
		then:
		!updated
		entityDescriptor_.hasErrors()
		entityDescriptor_.errors.getFieldError('entityID').code == 'unique'
		entityDescriptor_ != null
		entityDescriptor_.organization == organization
		entityDescriptor.entityID == "http://my.entityid.com"
	}
}
