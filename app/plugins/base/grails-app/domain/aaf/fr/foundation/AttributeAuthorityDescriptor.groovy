package aaf.fr.foundation

/**
 * @author Bradley Beddoes
 */
class AttributeAuthorityDescriptor extends RoleDescriptor {
	static auditable = true
	
	IDPSSODescriptor collaborator    // This links the AA with an IDP that collaborates with it to provide authentication assertion services
	String scope
	
	static belongsTo = [entityDescriptor:EntityDescriptor]

	static hasMany = [
		  attributeServices: AttributeService,
		  assertionIDRequestServices: AssertionIDRequestService,
		  nameIDFormats: SamlURI,
		  attributeProfiles: String,
		  attributes: Attribute
	]

	static constraints = {
		scope(nullable:false)
		collaborator(nullable:true)
		assertionIDRequestServices(nullable: true)
		nameIDFormats(nullable: true)
		attributeProfiles(nullable: true)
		attributes(nullable: true)
	}

	public String toString() {	"attributeauthoritydescriptor:[id:$id]" }
	
	public boolean functioning() {
		if(collaborator)
			( attributeServices?.findAll{it.selfFunctioning()}?.size() > 0 && !archived && active && approved && collaborator.functioning() && entityDescriptor.functioning() )
		else
			( attributeServices?.findAll{it.selfFunctioning()}?.size() > 0 && !archived && active && approved && entityDescriptor.functioning() )
	}
}
