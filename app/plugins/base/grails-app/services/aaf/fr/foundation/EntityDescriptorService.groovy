package aaf.fr.foundation

import org.springframework.transaction.interceptor.TransactionAspectSupport

import grails.plugins.federatedgrails.Permission
import aaf.fr.identity.Subject

/**
 * Provides methods for managing EntityDescriptor instances.
 *
 * @author Bradley Beddoes
 */
class EntityDescriptorService {
  static transactional = true

  def grailsApplication
  def workflowProcessService
  aaf.fr.identity.RoleService roleService
  def permissionService
  
  // This is primarily to avoid an issue where cross service calls with a save
  // would not rollback if the calling service laters throws an exception.... weird.
  def createNoSave(def params) {
    // Organization
    def organization = Organization.get(params.organization?.id)
  
    // Entity Descriptor
    def entityDescriptor = new EntityDescriptor(approved:false, active: params.active, entityID: params.entity?.identifier?.trim(), organization: organization)

    if(!entityDescriptor.validate()) {      
      entityDescriptor.errors.each {log.warn it}
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly() 
      return [false, entityDescriptor]
    }
    
    log.info "$subject created $entityDescriptor (not yet persisted)"
    return [true, entityDescriptor]
  }
  
  def create(def params) {
    def (created, entityDescriptor) = createNoSave(params)
    
    if(!created)
      return [false, entityDescriptor]
      
    // We approved directly created entity descriptors as only folks with higher privs should be allowed to do this
    entityDescriptor.approved = true
    entityDescriptor.active = true
  
    def savedEntityDescriptor = entityDescriptor.save()
    if(!savedEntityDescriptor) {
      entityDescriptor.errors.each {log.warn it}
      throw new ErronousStateException("Unable to save when creating ${entityDescriptor}")
    }
    
    def adminRole = roleService.createRole("descriptor-${savedEntityDescriptor.id}-administrators", "Global administrators for the entity ${savedEntityDescriptor.entityID}", false)
    Permission permission = new Permission(target:"descriptor:${entityDescriptor.id}:*")
    permission.managed = false
    permission.type = Permission.defaultPerm
    permissionService.createPermission(permission, adminRole)
    
    roleService.addMember(subject, adminRole)
  
    log.info "$subject created $entityDescriptor"
    return [true, entityDescriptor]
  }
  
  def update(def params) {
    def entityDescriptor = EntityDescriptor.get(params.id)
    if(!entityDescriptor)
      return [false, null]
    
    entityDescriptor.active = params.entity?.active == 'true'
    entityDescriptor.entityID = params.entity?.identifier?.trim()
    entityDescriptor.extensions = params.entity?.extensions
    
    if(!entityDescriptor.validate()) {      
      entityDescriptor.errors.each {log.warn it}
      return [false, entityDescriptor]
    }
    
    if(!entityDescriptor.save()) {      
      entityDescriptor.errors.each {log.warn it}
      throw new ErronousStateException("Unable to save when updating ${entityDescriptor}")
    }
    
    log.info "$subject updated $entityDescriptor"
    return [true, entityDescriptor]
  }
  
  def delete (def id) {    
    def ed = EntityDescriptor.get(id)
    if(!ed)
      throw new ErronousStateException("Unable to find EntityDescriptor with id $id")
    
    log.info "$subject attempting to delete $ed and children"

    // We have to initialize like this as there is a circular dependency
    def identityProviderService = grailsApplication.mainContext.identityProviderService
    def serviceProviderService = grailsApplication.mainContext.serviceProviderService
    def attributeAuthorityDescriptorService = grailsApplication.mainContext.attributeAuthorityDescriptorService

    // We need to do this for GORM stupidity. If you delete an IDP (and hence collaborator) then try to process any remaining AA associted with the ED collaborators are still present
    // in the list (regardless of refresh type calls). So for now at least an AA only ED is not processed - more thought needed.
    if(ed.attributeAuthorityDescriptors?.size() > ed.idpDescriptors?.size() || ed.pdpDescriptors?.size() != 0)
      throw new ErronousStateException("EntityDescriptor $ed holds unique combination of IDP/SP/AA/PDP that is not supported by this delete method, manual intervention will be required")
      
    ed.idpDescriptors.each { identityProviderService.delete(it.id) }
    ed.spDescriptors.each { serviceProviderService.delete(it.id)}
    ed.contacts.each { it.delete() }
    
    ed.delete()
    
    //log.info "$subject deleted $ed"
  }
  
  def archive (def id) {
    def ed = EntityDescriptor.get(id)
    if(!ed)
      throw new ErronousStateException("Unable to find EntityDescriptor with id $id")

    // We have to initialize like this as there is a circular dependency
    def identityProviderService = grailsApplication.mainContext.identityProviderService
    def serviceProviderService = grailsApplication.mainContext.serviceProviderService
    def attributeAuthorityDescriptorService = grailsApplication.mainContext.attributeAuthorityDescriptorService
          
    ed.idpDescriptors?.each { identityProviderService.archive(it.id) }
    ed.spDescriptors?.each { serviceProviderService.archive(it.id) }
    ed.attributeAuthorityDescriptors?.each { attributeAuthorityDescriptorService.archive(it.id) }
    
    ed.archived = true
    ed.active = false
    if(!ed.save()) {
      log.error "Unable to archive $ed"
      ed.errors.each { log.error it }
      throw new ErronousStateException("Unable to archive EntityDescriptor with id $id")
    }
    
    log.info "$subject successfully archived $ed"
  }
  
  def unarchive (def id) {
    def ed = EntityDescriptor.get(id)
    if(!ed)
      throw new ErronousStateException("Unable to find EntityDescriptor with id $id")

    if(ed.organization.archived)
      throw new ErronousStateException("Unable unarchive EntityDescriptor with id $id as parent organization is archived")
    
    // We have to initialize like this as there is a circular dependency
    def identityProviderService = grailsApplication.mainContext.identityProviderService
    def serviceProviderService = grailsApplication.mainContext.serviceProviderService
    def attributeAuthorityDescriptorService = grailsApplication.mainContext.attributeAuthorityDescriptorService
          
    ed.idpDescriptors?.each { identityProviderService.unarchive(it.id) }
    ed.spDescriptors?.each { serviceProviderService.unarchive(it.id) }
    ed.attributeAuthorityDescriptors?.each { attributeAuthorityDescriptorService.unarchive(it.id) }
    
    ed.archived = false
    if(!ed.save()) {
      log.error "Unable to unarchive $ed"
      ed.errors.each { log.error it }
      throw new ErronousStateException("Unable to unarchive EntityDescriptor with id $id")
    }
    
    log.info "$subject successfully unarchived $ed"
  }
  
  def activate (def id) {
    def ed = EntityDescriptor.get(id)
    if(!ed)
      throw new ErronousStateException("Unable to find EntityDescriptor with id $id")

    // We have to initialize like this as there is a circular dependency
    def identityProviderService = grailsApplication.mainContext.identityProviderService
    def serviceProviderService = grailsApplication.mainContext.serviceProviderService
    def attributeAuthorityDescriptorService = grailsApplication.mainContext.attributeAuthorityDescriptorService
      
    ed.idpDescriptors?.each { identityProviderService.activate(it.id) }
    ed.spDescriptors?.each { serviceProviderService.activate(it.id) }
    ed.attributeAuthorityDescriptors?.each { attributeAuthorityDescriptorService.activate(it.id) }
    
    ed.active = true
    if(!ed.save()) {
      log.error "Unable to activate $ed"
      ed.errors.each { log.error it }
      throw new ErronousStateException("Unable to activate EntityDescriptor with id $id")
    }
    
    log.info "$subject successfully activated $ed"
  }

  def migrateOrganization(def id, def orgId) {
    def entity = EntityDescriptor.get(id)
    if(!entity)
      throw new ErronousStateException("Unable to find EntityDescriptor with id $id")

    def organization = Organization.get(orgId)
    if(!organization)
      throw new ErronousStateException("Unable to find organization with id $orgId")

    entity.organization = organization
    entity.idpDescriptors.each {
      it.organization = organization
    }
    entity.idpDescriptors.each {
      it.organization = organization
    }
    entity.spDescriptors.each {
      it.organization = organization
    }
    entity.attributeAuthorityDescriptors.each {
      it.organization = organization
    }
    entity.pdpDescriptors.each {
      it.organization = organization
    }

    entity.save()
    if(entity.hasErrors()) {
      log.error "Unable to modify organization for $entity and children"
      entity.errors.each { log.error it }
      throw new ErronousStateException("Unable to modify organization for $entity and children")
    }
  }

}
