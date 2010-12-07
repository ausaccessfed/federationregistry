
<html>
	<head>
		<meta name="layout" content="members" />
		<title><g:message code="fedreg.view.members.serviceprovider.edit.title" /></title>
		
		<script type="text/javascript">
			$(function() {			
				$('form').validate({
						ignore: ":disabled",
						success: function(label) {
							if($(label).next())
								$(label).next().remove()	// fix annoying bug where success labels are left laying about if duplicate validations
							label.removeClass("error").addClass("icon icon_accept").html("&nbsp;");
						},
						keyup: false
				});
				$('form').formwizard({ 
				 	formPluginEnabled: false,
				 	validationEnabled: true,
				 	focusFirstInput : true
				});
			});
		</script>
	</head>
	<body>

		<section>
			<h2><g:message code="fedreg.view.members.serviceprovider.edit.heading" args="[serviceProvider.displayName]"/></h2>
			
			<g:form action="update" id="${serviceProvider.id}" method="PUT">
				<div class="step" id="basic">
					<h3><g:message code="fedreg.view.members.serviceprovider.edit.basicinformation.heading" /></h3>
					<p>
						<g:message code="fedreg.view.members.serviceprovider.edit.basicinformation.details" />
					</p>
					<table>
						<tr>
							<th>
								<label for="sp.displayName"><g:message code="label.displayname" /></label>
							</th>
							<td>
								<g:textField name="sp.displayName"  value="${serviceProvider.displayName}" size="50" class="required" minlength="4" maxlength="255" />
							</td>
						</tr>
						<tr>
							<th>
								<label for="sp.description"><g:message code="label.description" /></label>
							</th>
							<td>
								<g:textArea name="sp.description"  value="${serviceProvider.description}" class="required" minlength="4" rows="8" cols="36" maxlength="2000"/>
							</td>
						</tr>
						<tr>
							<th>
								<label for="sp.status"><g:message code="label.status" /></label>
							</th>
							<td>
								<g:radioGroup name="sp.status" values="['true', 'false']" labels="['label.active', 'label.inactive']" value="${serviceProvider.active}" >
									 ${it.radio} <g:message code="${it.label}" /> <br>
								</g:radioGroup>
							</td>
						</tr>
						<tr>
							<th>
								<label for="sp.servicedescription.connecturl"><g:message code="label.serviceurl" /></label>
							</th>
							<td>
								<g:textField name="sp.servicedescription.connecturl" value="${serviceProvider.serviceDescription.connectURL}" size="50" class="required url" maxlength="255"/>
							</td>
						</tr>
						<tr>
							<th>
								<label for="sp.servicedescription.logourl"><g:message code="label.servicelogourl" /></label>
							</th>
							<td>
								<g:textField name="sp.servicedescription.logourl" value="${serviceProvider.serviceDescription.logoURL}" size="50" class="url" maxlength="255"/>
							</td>
						</tr>
					</table>
				</div>

				<div class="step" id="servicedescription">
					<h3><g:message code="fedreg.view.members.serviceprovider.edit.servicedescription.heading" /></h3>
					<p>
						<g:message code="fedreg.view.members.serviceprovider.edit.servicedescription.details" />
					</p>
					<table>
						<tr>
							<th>
								<label for="sp.servicedescription.furtherInfo"><g:message code="label.furtherinfo" /></label>
							</th>
							<td>
								<g:textArea name="sp.servicedescription.furtherinfo" value="${serviceProvider.serviceDescription.furtherInfo}" rows="8" cols="48" maxlength="2000"/>
							</td>
						</tr>
						<tr>
							<th>
								<label for="sp.servicedescription.provides"><g:message code="label.provides" /></label>
							</th>
							<td>
								<g:textArea name="sp.servicedescription.provides" value="${serviceProvider.serviceDescription.provides}" rows="8" cols="48" maxlength="2000"/>
							</td>
						</tr>
						<tr>
							<th>
								<label for="sp.servicedescription.benefits"><g:message code="label.benefits" /></label>
							</th>
							<td>
								<g:textArea name="sp.servicedescription.benefits" value="${serviceProvider.serviceDescription.benefits}" rows="8" cols="48" maxlength="2000"/>
							</td>
						</tr>
						<tr>
							<th>
								<label for="sp.servicedescription.audience"><g:message code="label.audience" /></label>
							</th>
							<td>
								<g:textArea name="sp.servicedescription.audience" value="${serviceProvider.serviceDescription.audience}" rows="8" cols="48" maxlength="2000"/>
							</td>
						</tr>
						<tr>
							<th>
								<label for="sp.servicedescription.restrictions"><g:message code="label.restrictions" /></label>
							</th>
							<td>
								<g:textArea name="sp.servicedescription.restrictions" value="${serviceProvider.serviceDescription.restrictions}" rows="8" cols="48" maxlength="2000"/>
							</td>
						</tr>
						<tr>
							<th>
								<label for="sp.servicedescription.accessing"><g:message code="label.accessing" /></label>
							</th>
							<td>
								<g:textArea name="sp.servicedescription.accessing" value="${serviceProvider.serviceDescription.accessing}" rows="8" cols="48" maxlength="2000"/>
							</td>
						</tr>
						<tr>
							<th>
								<label for="sp.servicedescription.support"><g:message code="label.support" /></label>
							</th>
							<td>
								<g:textArea name="sp.servicedescription.support" value="${serviceProvider.serviceDescription.support}" rows="8" cols="48" maxlength="2000"/>
							</td>
						</tr>
						<tr>
							<th>
								<label for="sp.servicedescription.maintenance"><g:message code="label.maintenance" /></label>
							</th>
							<td>
								<g:textArea name="sp.servicedescription.maintenance" value="${serviceProvider.serviceDescription.maintenance}" rows="8" cols="48" maxlength="2000"/>
							</td>
						</tr>
					</table>
				</div>
				
				<div class="step submit_step" id="creationsummary">
					<h3><g:message code="fedreg.view.members.serviceprovider.edit.summary.heading" /></h3>
					<p>
						<g:message code="fedreg.view.members.serviceprovider.edit.summary.details" />
					</p>
				</div>

				<nav> 							
					<input class="navigation_button" id="back" value="Back" type="reset" />
					<input class="navigation_button" id="next" value="Next" type="submit" />
				</nav>
			</g:form>
		</section>

	</body>
</html>