package aaf.fr.identity

import grails.plugin.spock.*

class SubjectSpec extends IntegrationSpec {

  def 'Ensure valid CN returns given and surname'() {
    when:
    def subject = new Subject(cn:'Test User')

    then:
    subject.givenName == 'Test'
    subject.surname == 'User'
  }

  def 'Ensure invalid CN returns given name and invalid surname'() {
    when:
    def subject = new Subject(cn:'Test')

    then:
    subject.givenName == 'Test'
    subject.surname == 'INVALID_CN_FORMAT'
  }

  def 'Ensure missing CN returns invalid'() {
    when:
    def subject = new Subject()

    then:
    subject.givenName == 'INVALID_CN'
    subject.surname == 'INVALID_CN'
  }

}
