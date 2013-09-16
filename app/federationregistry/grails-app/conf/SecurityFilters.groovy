import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import org.springframework.beans.factory.InitializingBean

public class SecurityFilters implements InitializingBean {

  def grailsApplication

  String VALID_REFERER
  String DATE_FORMAT

  void afterPropertiesSet() {
    VALID_REFERER = "^${grailsApplication.config.grails.serverURL}".replace("http", "https?")
    DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss z"
  }

  def filters = {

    checkReferer(controller: '*', action: '*') {
      before = {
        if (request.method.toUpperCase() != "GET" && request.method.toUpperCase() != "HEAD") {
          def referer = request.getHeader('Referer')

          if(!(referer && referer =~ VALID_REFERER)) {
            log.error("secfilter: DENIED - ${request.remoteAddr}|$params.controller/$params.action - Referer: $referer was not valid, should have been a match for $VALID_REFERER")
            response.sendError(403)
            return false
          }
        }
      }
    }

    secureResponse(controller: '*', action: '*') {
      after = {
        Date responseDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String responseDateHeader = sdf.format(responseDate);

        // We do everything over SSL so prevent caching
        header("Expires", "Tue, 03 Jul 2001 06:00:00 GMT");
        header("Last-Modified", responseDateHeader);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        //OWASP - prevent click jacking
        response.addHeader("X-FRAME-OPTIONS", "DENY")
      }
    }

    welcome(controller: "welcome") {
      after = {
        log.info("secfilter: PUBLIC - unauthenticated|${request.remoteAddr}|$params.controller")
      }
    }

    dashboard(controller: "dashboard") {
      before = {
        accessControl { true }
      }
      after = {
        log.info("secfilter: ALLOWED - [$subject.id]$subject.principal|${request.remoteAddr}|$params.controller/$params.action")
      }
    }

    bootstrap(controller: "initialBootstrap") {
      before = {
        if( !grailsApplication.config.aaf.fr.bootstrap ) {
          log.info("secfilter: DENIED - [$subject?.id]$subject?.principal|${request.remoteAddr}|$params.controller")
          response.sendError(404) // Deliberately not 403.
          return
        }
        redirect(controller:'console', action: 'index')
        return false
      }
    }
  }

}
