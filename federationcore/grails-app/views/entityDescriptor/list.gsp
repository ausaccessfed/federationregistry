
<html>
	<head>
		
		<meta name="layout" content="members" />
		<title><g:message code="fedreg.view.members.entity.list.title" /></title>
	</head>
	<body>
		<section>
			<h2><g:message code="fedreg.view.members.entity.list.heading" /></h2>

			<table class="enhancedtabledata">
				<thead>
					<tr>
					
						<th>${message(code: 'label.entitydescriptor')}</th>
						<th>${message(code: 'label.active')}</th>
						<th><g:message code="label.approved" /></th>
						<th />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${entityList.sort{it.entityID}}" status="i" var="entity">
					<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
						<td>${fieldValue(bean: entity, field: "entityID")}</td>
						<td>${fieldValue(bean: entity, field: "active")}</td>
						<td>${fieldValue(bean: entity, field: "approved")}</td>
						<td><n:button href="${createLink(controller:'entityDescriptor', action:'show', id:entity.id)}" label="label.view" icon="arrowthick-1-ne" /></td>
					</tr>
				</g:each>
				</tbody>
			</table>

			<div class="paginatebuttons">
				<g:paginate total="${entityTotal}" />
			</div>
		</section>
	</body>
</html>