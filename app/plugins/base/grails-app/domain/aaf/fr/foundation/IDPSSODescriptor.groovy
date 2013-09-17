package aaf.fr.foundation

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * @author Bradley Beddoes
 */
class IDPSSODescriptor extends SSODescriptor  {
	static auditable = true
	
	String scope
	
	AttributeAuthorityDescriptor collaborator
		
	boolean wantAuthnRequestsSigned = false
	boolean autoAcceptServices = false
  boolean attributeAuthorityOnly = false

	List singleSignOnServices

	static belongsTo = [entityDescriptor:EntityDescriptor]

	static hasMany = [
		singleSignOnServices: SingleSignOnService,
		nameIDMappingServices: NameIDMappingService,
		assertionIDRequestServices: AssertionIDRequestService,
		attributeProfiles: SamlURI,
		attributes: Attribute
	]

 	static constraints = {
		scope(nullable: false)
		collaborator(nullable: true)
		singleSignOnServices(minSize: 1)
		nameIDMappingServices(nullable: true)
		assertionIDRequestServices(nullable: true)
		attributeProfiles(nullable: true)
		attributes(nullable: true)
	}
	
	public String toString() {	"idpssodescriptor:[id:$id, displayName: $displayName]" }
	
	public boolean functioning() {
		( !archived && active && approved && entityDescriptor.functioning() )
	}
	
	public List sortedAttributes() {
		def c = Attribute.createCriteria()
		def attributeList = c.list {
			eq("idpSSODescriptor", this)
			createAlias("base","_base")
			order("_base.category", "asc")
			order("_base.name", "asc")
		}
	}
	
	public boolean equals(Object obj) {
		if( this.is(obj) ) return true
		if ( obj == null ) return false
		if ( !obj.instanceOf(IDPSSODescriptor) ) return false
		
		IDPSSODescriptor rhs = (IDPSSODescriptor) obj
		return new EqualsBuilder()
			.append(this.id, rhs.id)
			.append(this.scope, rhs.scope)
			.append(this.displayName, rhs.displayName)
			.append(this.organization, rhs.organization)
			.isEquals()
	}

	public int hashCode() {
		// hard-coded, randomly chosen, non-zero, odd number different for each class
		return new HashCodeBuilder(21, 123).
		toHashCode();
	}
}
