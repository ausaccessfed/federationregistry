package aaf.fr.foundation

class Contact {
	static auditable = true

	String givenName
	String surname
	String description

	Organization organization

	String email
	String secondaryEmail

	String workPhone
	String homePhone
	String mobilePhone

	boolean active = true

	Date dateCreated
	Date lastUpdated

	static hasMany = [
		contactPersons: ContactPerson
	]

	static constraints = {
		givenName(nullable:false, blank: false)
		surname(nullable:false, blank: false)
		description(nullable:true, blank:true)
		organization(nullable:true)
		email(nullable:false, email:true, unique:true)
		secondaryEmail(nullable:true, email:true)
		workPhone(nullable:true)
		homePhone(nullable:true)
		mobilePhone(nullable:true)
		dateCreated(nullable:true)
		lastUpdated(nullable:true)
    active(nullable: false)
	}

	static mapping = {
		autoImport false
		sort "surname"
		active defaultValue: true
	}

	public String toString() { "contact:[id:$id, givenName: $givenName, surname: $surname, email: $email]" }

	def structureAsJson() {
	  def json = new groovy.json.JsonBuilder()
	  json {
	    id id
	    given_name givenName
	    surname surname
	    description description ?: ''

	    organization {
	    	id organization?.id ?: ''
  			name organization?.displayName ?: ''
  		}

	    email email
	    secondary_email secondaryEmail ?: ''
	    work_phone workPhone ?: ''
	    home_phone homePhone ?: ''
	    mobile_phone mobilePhone ?: ''

	    created_at dateCreated
	    updated_at lastUpdated
	  }
	}

}
