
<html>
	<head>
		
		<meta name="layout" content="workflow" />
		<title><g:message code="fedreg.view.workflow.process.edit.title" /></title>
		
		<script src="${request.contextPath}/js/codemirror/js/codemirror.js" type="text/javascript" charset="utf-8"></script>
	</head>
	<body>
		<section>
			<h2><g:message code="fedreg.view.workflow.process.edit.heading" args="[process.name]"/></h2>
		
			<g:if test="${process.hasErrors()}">
				<div class="error">
				<ul>
				<g:eachError bean="${process}">
				    <li>${it}</li>
				</g:eachError>
				</ul>
				</div>
			</g:if>
		
			<g:form action="update" id="${process.id}">
				<g:textArea name="code" value="${process.definition.encodeAsHTML()}" rows="5" cols="40"/>
				<br>
				<button type="submit" class="button icon icon_accept"/><g:message code="label.update" /></button>
			</g:form>
		
			<script type="text/javascript">
				 var textarea = $("#code");
				  var editor = CodeMirror.fromTextArea('code',  {
		            height: "300px",
		            content: textarea.value,
		            parserfile: ["tokenizegroovy.js", "parsegroovy.js"],
		            stylesheet: "${request.contextPath}/js/codemirror/css/groovycolors.css",
		            path: "${request.contextPath}/js/codemirror/js/",
		            autoMatchParens: true,
		            disableSpellcheck: true,
		            lineNumbers: true,
		            tabMode: 'shift'
		         });
			</script>
		</section>
	</body>
</html>