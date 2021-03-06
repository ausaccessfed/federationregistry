package aaf.fr.app

import java.security.MessageDigest

import grails.plugins.federatedgrails.Role
import aaf.fr.identity.Subject

/**
 * Provides methods for managing invitations.
 *
 * @author Bradley Beddoes
 */
class InvitationService {
  def roleService
  def groupService

  def create(role, controller, action, objID) {
    def invitation = new Invitation(role:role, controller:controller, action:action, objID:objID)
    
    def hash = "$role-$controller-$action-$objID-${System.currentTimeMillis().toString()}"
    
    def messageDigest = MessageDigest.getInstance("SHA1")
    messageDigest.update( hash.getBytes() );
    def sha1Hex = new BigInteger(1, messageDigest.digest()).toString(16).padLeft( 40, '0' ).substring(0,10)

    invitation.inviteCode = sha1Hex
    invitation.save()
    if(invitation.hasErrors()) {
      invitation.errors.each {
        log.error it
      }
      throw new RuntimeException ("Error when attempting to create invitation") 
    }
    
    invitation
  }
  
  def claim(String inviteCode, long objID) {
    def invite = Invitation.findWhere(inviteCode:inviteCode)
    if(invite) {
      if(invite.utilized) {
        log.warn "Attempt by $subject to re-use invitation code $inviteCode"
        return null
      }

      if(invite.objID != objID) {
        log.warn "Attempt by $subject to use invitation code $inviteCode against wrong objID ($invite.objID vs $objID)"
        return null
      } 

      invite.utilized = true
      invite.utilizedBy = subject
      
      if(invite.role) {
        def role = Role.get(invite.role) 
        if(role){
          roleService.addMember(subject, role)
        } else {
          throw new RuntimeException ("Invite $inviteCode references role with id ${invite.role} but this doesn't exist")
        }
      }
      
      invite.save()
      if(invite.hasErrors()) {
        invite.errors.each { log.warn it }
        throw new RuntimeException ("Error when attempting to utilize invitation, unable to save state")
      }
      return invite
    }
    else {
      log.error "Request submitted invite code $inviteCode, no such identifier"
      return null
    }
  }

}