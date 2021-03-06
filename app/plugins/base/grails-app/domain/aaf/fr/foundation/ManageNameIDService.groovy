package aaf.fr.foundation

/**
 * @author Bradley Beddoes
 */
class ManageNameIDService extends Endpoint  {
	static auditable = true

  	static belongsTo = [descriptor: SSODescriptor]

	public String toString() { "managenameidservice:[id:$id, location: $location]" }
	
	public boolean functioning() {
		( active && approved && descriptor.functioning() )
	}

}
