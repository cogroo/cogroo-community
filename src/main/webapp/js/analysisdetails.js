
var oTables = new Array();


/* Formating function for row details */
function fnFormatDetails ( table, nTr )
{
	var iIndex = table.fnGetPosition( nTr );
	var aData = table.fnSettings().aoData[iIndex]._aData;
	
	return '<div class="reportlist_details">'+aData[4]+'</div>';
}

$(document).ready(function() {
	
	$('table[id^="analysisTable_"]').each(function() {
		var id = $(this).attr('id');
		oTables[id] = $(this).dataTable( {
			"oLanguage": {
				"sLengthMenu": "Exibir _MENU_ entradas por página",
				"sSearch": "Filtrar entradas:",
				"sFirst": "Primeira página",
				"sLast": "Última página",
				"sNext": "Próxima página",
				"sPrevious": "Página anterior",
				"sZeroRecords": "Desculpe, nada encontrado.",
				"sInfo": "Exibindo de _START_ até _END_ de um total de _TOTAL_ entradas",
				"sInfoEmpty": "Exibindo de 0 até 0 de um total de 0 entradas",
				"sInfoFiltered": "(filtrados de um total de _MAX_ entradas)"
			},
			"bFilter": false,
			"bInfo": false,
			"bPaginate": false,
			"bDestroy": true,
			"aoColumns": [
				{ "bSortable": false },
				{ "bSortable": false }, 
				{ "bSortable": false },
				{ "bSortable": false },
				{ "bVisible": false }
			]
		} );
	});

	/* Add click event handler for user interaction */
	$('.analysisTableDetails' ).each( function () {
		$(this).click( function () {
			var id = this.parentNode.parentNode.parentNode.parentNode.id;
			var nTr = this.parentNode.parentNode;
			if ( this.src.match('details_close') )
			{
				/* This row is already open - close it */
				this.src = "./images/details_open.png";
				oTables[id].fnClose( nTr );
			}
			else
			{
				/* Open this row */
				this.src = "./images/details_close.png";
				oTables[id].fnOpen( nTr, fnFormatDetails(oTables[id], nTr), 'details' );
			}
		} );
	} );
	
	$('.hidden_div').hide();

} );

$(function() {
	
	$('.iframe').click(function(e) {
		var currentId = e.target.id;
		
		e.preventDefault();
		var $this = $(this);
		var horizontalPadding = 30;
		var verticalPadding = 30;
		/*$("#form" + currentId ).submit();*/
		$('#externalSite').remove();

		$('<iframe id="externalSite" src="about:blank"/>').dialog({
			    title: 'Agrupamentos sintáticos',
			    autoOpen: true,
			    width: 730,
			    height: 280,
			    modal: true,
			    resizable: true,
		            autoResize: true,
			    overlay: {
				opacity: 0.5,
				background: "black"
			    }
			}).width(730 - horizontalPadding).height(280 - verticalPadding);

		$.post("/cogroo/phpsyntaxtree/cogroo.php?", $("#form" + currentId).serialize(), function(data, textStatus, XMLHttpRequest) {
			
	    	  var d = $("#externalSite")[0].contentWindow.document; // contentWindow works in IE7 and FF
			  d.open(); d.close(); // must open and close document object to start using it!

			  // now start doing normal jQuery:
			  $("body", d).append(data);

			
		}, 'html');
	});
});
