
<div class="description">
	<h4 id="totalstitle"></h4>
	<p><g:message code="fedreg.templates.reports.identityprovider.totals.description"/></p>

	<div class="reportrefinement">
		<form id='totalsrefinement' class="reportrefinementinput loadhide">
			<h5><g:message code="fedreg.templates.reports.identityprovider.totals.refinement.title"/> ( <a href="#" onClick="$('#totalscomponents :unchecked').attr('checked', true); return false;"><g:message code="label.addallchecks" /></a> | <a href="#" onClick="$('#totalscomponents :checked').attr('checked', false); return false;"><g:message code="label.removeallchecks" /></a> )</h5>
			<input type="hidden" name='activesp' value='0'/>
			
			<span id="totalscomponents" class="reportrefinementcomponents">
			</span>
			
			<h5><g:message code="fedreg.templates.reports.identityprovider.totals.refinement.maxmin.title"/> ( <a href="#" onClick="$('#.reportrefinementval :input').val(''); return false;"><g:message code="label.clear" /></a>  )</h5>
			<div class="reportrefinementval">
				<label><g:message code="label.min" /><input name="min" size="4" value="" class="number"/><fr:tooltip code='fedreg.help.report.min' /></label>
				<label><g:message code="label.max" /><input name="max" size="4" value="" class="number"/><fr:tooltip code='fedreg.help.report.max' /></label>
			</div>
			<div class="buttons">
				<a href="#" onClick="fedreg.refineIdPReport($('#totalsrefinement')); return false;" class="update-button"><g:message code="label.update" /></a>
				<a href="#" onClick="$('.reportrefinementinput').slideUp(); $('.reportrefinementopen').show(); return false;" class="close-button"><g:message code="label.close" /></a>
			</div>
		</form>
		<div class="reportrefinementopen">
			<a href="#" onClick="$('.reportrefinementopen').hide(); $('.reportrefinementinput').slideDown(); return false;" class="download-button"><g:message code="label.refine" /></a>
		</div>
	</div>
</div>

<div id="totalsdata">
</div>

<script type="text/javascript+protovis">
	fedreg.renderIdPTotals = function(data, refine) {
		$('#totalsdata').empty();
		$('#totalstitle').html(data.title);
		
		if(!refine) {
			$('#totalscomponents').empty();
			$.each( data.services, function(index, sp) {
				if(sp.rendered)
					$("#totalscomponents").append("<label class='choice'><input type='checkbox' checked='checked' name='activesp' value='"+sp.id+"'></input>"+sp.name+"</label>");
				else
					$("#totalscomponents").append("<label class='choice'><input type='checkbox' name='activesp' value='"+sp.id+"'></input>"+sp.name+"</label>");
			});
		}
		
		var canvas = document.createElement("div");
		$('#totalsdata').append(canvas);

		/* Sizing and scales. */
		var w = 900,
		    h = 500,
		    x = pv.Scale.linear(0, data.maxlogins).range(0, w),
		    y = pv.Scale.ordinal(pv.range(data.servicecount + 1)).splitBanded(0, h, 4/5);

		/* The root panel. */
		var vis = new pv.Panel()
			.canvas(canvas)
		    .width(w)
		    .height(h)
		    .bottom(20)
		    .left(200)
		    .right(10)
		    .top(5);

		/* The bars. */
		var bar = vis.add(pv.Bar)
		    .data(data.bars)
		    .top(function() y(this.index))
		    .height(y.range().band)
		    .left(0)
		    .width(x);

		/* The value label. */
		bar.anchor("right").add(pv.Label)
			.textStyle("white");

		/* The variable label. */
		bar.anchor("left").add(pv.Label)
		    .textMargin(5)
		    .textAlign("right")
		    .text(function(d) data.barlabels[this.index]);

		/* X-axis ticks. */
		vis.add(pv.Rule)
		    .data(x.ticks(10))
		    .left(x)
		    .strokeStyle(function(d) d ? "rgba(255,255,255,.3)" : "#000")
		  .add(pv.Rule)
		    .bottom(0)
		    .height(5)
		    .strokeStyle("#000")
		  .anchor("bottom").add(pv.Label)
		    .text(x.tickFormat);

		vis.render();
	};
</script>