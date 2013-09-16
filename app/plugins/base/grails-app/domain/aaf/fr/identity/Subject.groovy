package aaf.fr.identity

import grails.plugins.federatedgrails.SubjectBase
import aaf.fr.foundation.Contact

class Subject extends SubjectBase {

  String cn
  String email
  String sharedToken

  Contact contact

  static constraints = {
    email email:true
    cn nullable: false, blank: false

    contact nullable:true
    sharedToken nullable:true   // This two may be left null for internal system accounts only.
  }

  public String toString() {
      "aaf.fr.identity.Subject [id: $id, principal: $principal, cn:$cn]"
  }

  // Crude but necessary
  public String getGivenName() {
    if(cn) {
      if(cn.contains(' '))
        cn?.split(' ')[0]
      else
        cn
    } else
      'INVALID_CN'
  }

  public String getSurname() {

    if(cn) {
      if(cn.contains(' '))
        cn.split(' ')[1]
      else
        'INVALID_CN_FORMAT'
    } else
      'INVALID_CN'
  }
}
