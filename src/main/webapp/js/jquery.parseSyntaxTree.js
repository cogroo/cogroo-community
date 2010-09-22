;(function($) {
	

$.fn.parseSyntaxTree = function(str) {  

	function parseSyntaxTree(str) {
	  var c;
	  var pos=0;
	  var pilha = new Array();
	  var word = '';
	  var node;
          var tag ='';
	  while(pos < str.length){
	    c = str.charAt(pos);

	    switch(c) {

	      case '[':

		var tag = str.substr(pos+1,str.length-pos);
		var shift = tag.indexOf(" ");
		if(shift < 0){
		  shift = tag.indexOf("]");
		} 
		tag = tag.substr(0,shift);
		pos = pos + shift;

		var node = {};
		node.op = tag;
		node.child = new Array();
		if( pilha.length != 0 ){
		  pilha[pilha.length-1].child.push(node);
		  node.pai = pilha[pilha.length-1];
		}
		pilha.push(node);

	      break;

	      case ']':
		if(word.length != 0 ){
		  var nodeValue = {};
		  nodeValue.value = word;
		  pilha[pilha.length-1].child.push(nodeValue);
		  nodeValue.pai = pilha[pilha.length-1];
          nodeValue.hide = tag ;
		  word = '';
		}
		var node = pilha.pop();

		if(pilha.length == 0 ) {
		  return node;
		}
	      break;

	      case '{':
		var tag = str.substr(pos+1,str.length-pos);
		var shift = tag.indexOf("}");
		tag = tag.substr(0,shift);
		pos = pos + shift+1;
		//node.hide = tag ;
	
	      break;

	      default:
		word = word + str.substr(pos,1);
	    }
	    pos++;
	  }
	  //  alert("saindo return");
	  return node;
	}

/*
return this.each(function() {
  parseSyntaxTree($(this).html());
});
*/

var syntax = $(this).html();
syntax = syntax.replace(/ \]/g, ']');
syntax = syntax.replace(/\] \[/g, '][');
syntax = syntax.replace(/\} \]/g, '}]');
return parseSyntaxTree( syntax );


}; // makeTree

})(jQuery);