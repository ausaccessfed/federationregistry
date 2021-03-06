package aaf.fr.workflow

import org.apache.shiro.SecurityUtils
import aaf.fr.identity.SubjectService

/**
 * Provides workflow approval views.
 *
 * @author Bradley Beddoes
 */
class WorkflowApprovalController {
	static defaultAction = "list"
	
	def workflowTaskService
	
	def list = {
		def tasks = workflowTaskService.retrieveTasksAwaitingApproval(subject)
		[tasks:tasks]
	}
	
	def administrative = {
		if(SecurityUtils.subject.isPermitted("federation:management:workflow:approval:administrator")) {
			def c = TaskInstance.createCriteria()
			def tasks = c.listDistinct {
				and {
					eq("status", TaskStatus.APPROVALREQUIRED)
					processInstance {
						eq("status", ProcessStatus.INPROGRESS)
					}
				}
			}
			[tasks:tasks]
		}
		else {
			log.warn("Attempt to do administrative taskInstance listing by $subject was denied, not administrative user")
			response.sendError(403)
		}
	}
	
	def approve = {
		if(!params.id) {
			log.warn "Task instance id was not present"
			flash.type = "error"
		    flash.message = message(code: 'controllers.fr.generic.namevalue.missing')
			redirect action:list
			return
		}
		
		def taskInstance = TaskInstance.get(params.id)
		if(!taskInstance) {
			flash.type = "error"
		    flash.message = message(code: 'domains.fr.workflow.taskinstance.nonexistant', args: [params.id])
			redirect action: "list"
			return
		}
		
		if(taskInstance.potentialApprovers.contains(subject) || SecurityUtils.subject.isPermitted("federation:globaladministrator")) {
			log.info "$subject is approving $taskInstance"
			workflowTaskService.approve(taskInstance.id)
			flash.type = "success"
		    flash.message = message(code: 'domains.fr.workflow.taskinstance.successfully.approved')
		
			log.info "$subject approval of $taskInstance completed"
			redirect action: "list"
		}
		else {
			log.warn("Attempt to approve $taskInstance by $subject was denied, no permission to modify this record")
			response.sendError(403)
		}
	}
	
	def reject = {
		if(!params.id) {
			log.warn "Task instance id was not present"
			flash.type = "error"
		    flash.message = message(code: 'controllers.fr.generic.namevalue.missing')
			redirect action: "list"
			return
		}
		
		if(!params.rejection) {
			log.warn "Rejection selection was not present"
			flash.type = "error"
		    flash.message = message(code: 'controllers.fr.generic.namevalue.missing')
			redirect action: "list"
			return
		}
		
		def taskInstance = TaskInstance.get(params.id)
		if(!taskInstance) {
			flash.type = "error"
		    flash.message = message(code: 'domains.fr.workflow.taskinstance.nonexistant', args: [params.id])
			redirect action: "list"
			return
		}
		
		if(!taskInstance.task.rejections.containsKey(params.rejection)) {
			flash.type = "error"
		    flash.message = message(code: 'domains.fr.workflow.taskinstance.no.such.rejection', args: [params.id])
			redirect action: "list"
			return
		}
		
		if(taskInstance.potentialApprovers.contains(subject) || SecurityUtils.subject.isPermitted("federation:globaladministrator")) {
			log.info "$subject is rejecting $taskInstance"
			workflowTaskService.reject(taskInstance.id, params.rejection)
			flash.type = "success"
		    flash.message = message(code: 'domains.fr.workflow.taskinstance.successfully.rejected')
		
			log.info "$subject rejection of $taskInstance completed"
			redirect action: "list"
		}
		else {
			log.warn("Attempt to reject $taskInstance with ${params.rejection} by $subject was denied, no permission to modify this record")
			response.sendError(403)
		}
	}
}