package aaf.fr.foundation

/**
 * @author Bradley Beddoes
 */
class RequestedAttribute extends Attribute {
	static auditable = true
	
	String reasoning
	boolean isRequired
	boolean approved = false
	
	static constraints = {
		reasoning(nullable:false, blank:false)
	}

	static belongsTo = [attributeConsumingService: AttributeConsumingService]
	
	public String toString() {	"requestedattribute:[id:$id, isRequired: $isRequired]" }

  public boolean functioning() {
    // Ensure the requested attribute is approved and if requiring specification has 1 or more values present
    approved && (base.specificationRequired ? values?.findAll{ it.approved }?.size() > 0 : true)
  }

}
