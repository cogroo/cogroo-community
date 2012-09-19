// http://www.datatables.net/plug-ins/sorting 

	jQuery.fn.dataTableExt.oSort['num-html-asc']  = function(a,b) {
		var x = a.replace( /<.*?>/g, "" );
		var y = b.replace( /<.*?>/g, "" );
		x = parseFloat( x );
		y = parseFloat( y );
		return ((x < y) ? -1 : ((x > y) ?  1 : 0));
	};

	jQuery.fn.dataTableExt.oSort['num-html-desc'] = function(a,b) {
		var x = a.replace( /<.*?>/g, "" );
		var y = b.replace( /<.*?>/g, "" );
		x = parseFloat( x );
		y = parseFloat( y );
		return ((x < y) ?  1 : ((x > y) ? -1 : 0));
	};
	
	jQuery.fn.dataTableExt.oSort['title-string-asc']  = function(a,b) {
		var x = a.match(/title="(.*?)"/)[1].toLowerCase();
		var y = b.match(/title="(.*?)"/)[1].toLowerCase();
		return ((x < y) ? -1 : ((x > y) ?  1 : 0));
	};

	jQuery.fn.dataTableExt.oSort['title-string-desc'] = function(a,b) {
		var x = a.match(/title="(.*?)"/)[1].toLowerCase();
		var y = b.match(/title="(.*?)"/)[1].toLowerCase();
		return ((x < y) ?  1 : ((x > y) ? -1 : 0));
	};
	
	jQuery.fn.dataTableExt.oSort['title-numeric-asc']  = function(a,b) {
		var x = parseFloat(a.match(/title="(.*?)"/)[1]);
		var y = parseFloat(b.match(/title="(.*?)"/)[1]);
		return ((x < y) ? -1 : ((x > y) ?  1 : 0));
	};

	jQuery.fn.dataTableExt.oSort['title-numeric-desc'] = function(a,b) {
		var x = parseFloat(a.match(/title="(.*?)"/)[1]);
		var y = parseFloat(b.match(/title="(.*?)"/)[1]);
		return ((x < y) ?  1 : ((x > y) ? -1 : 0));
	};
	
	