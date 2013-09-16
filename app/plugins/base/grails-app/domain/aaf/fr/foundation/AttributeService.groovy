package aaf.fr.foundation

class AttributeService extends Endpoint  {
  static auditable = true

  static belongsTo = [descriptor: AttributeAuthorityDescriptor]
  
  public String toString() {  "attributeservice:[id:$id, location: $location]" }
  
  public boolean functioning() {
    ( active && approved && descriptor.functioning() )
  }

  public boolean selfFunctioning() {
    ( active && approved )
  }
}
