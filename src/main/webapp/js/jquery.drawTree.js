;(function($) {
	
/*  Uma forma de fazer */
$.fn.drawTree = function(tree, options) {  

var defaults = {  
	nodeColor: '#FFFF',
	yStep : 24,
	widthMAX : 800,
	height : 480,
        border : 'solid',

	RED : "#ffc0c0",
	BLUE : "#c0c0ff"
};  

var options = $.extend(defaults, options);

function createTreeNode(node) {
  var val;
  if(node.op) {
    val = node.op;
    color = defaults.BLUE;
  } else if(node.value) {
    val = node.value;
    color = defaults.RED;
  } else
    val = "?";

  var nodeElement = $('<div>');
  nodeElement.css( 'position', "absolute");

  nodeElement.css( 'fontSize', 10);

  nodeElement.css( 'border', "solid");
  nodeElement.css( 'borderWidth', 1);
  nodeElement.css( 'backgroundColor', color);
  nodeElement.append(val );
  nodeElement.attr('title',node.hide);
  nodeElement.tooltip();
  
  $container.append(nodeElement);

  node.element = nodeElement;
  return nodeElement;
}


function renderLink(x1, y1, x2, y2)
{
  var left = Math.min(x1,x2);
  var top = Math.min(y1,y2);

  var width = 1+Math.abs(x2-x1);
  var height = 1+Math.abs(y2-y1);

  var svg = document.createElementNS(svgNS, "svg");
  svg.setAttribute("x", left);
  svg.setAttribute("y",  top);
  svg.setAttribute("width", width );
  svg.setAttribute("height", height );

  var line = document.createElementNS(svgNS,"line");

  line.setAttribute("x1", (x1 - left) );
  line.setAttribute("x2", (x2 - left) );
  line.setAttribute("y1", (y1 - top) );
  line.setAttribute("y2", (y2 - top) );
  line.setAttribute("stroke-width",  "1");
  line.setAttribute("stroke",  "black");
  svg.appendChild(line);



  var $div = $('<div>');
  $div.width( width);
  $div.height( height);
  $div.css( 'position', "absolute");
  var offset = $div.offset();
  offset.left = left;
  offset.top = top;
  $div.offset(offset)

  $div.append(svg);
  $container.append($div);

}

//var arrayPosFilhos = new Array();
function drawTree(y, node, height)
{
  if(height < 1.5 * defaults.yStep)
     height = 1.5 * defaults.yStep;

  var newY = y + height;
  var widthPadrao = 100;

  var newNode = createTreeNode(node);

  var offset = newNode.offset();
  offset.top = y;
  if(node.child) {

    // Criando sub-arvores
    for(var i = 0; i < node.child.length; i++){
      var child = drawTree( newY, node.child[i], height/2);
    }

    // Posicionando nó corrente baseado no deslocamento criado pelos descendentes.
    offset.left = ESQUERDA - (widthPadrao * node.child.length /2) - ((widthPadrao/2) + newNode.width()/2);

    // Criando linhas, agora que já se tem o posicionamento dos pais e filhos
    for(var i = 0; i < node.child.length; i++){
      var child = node.child[i].element;
      renderLink( offset.left + newNode.width()/2, y + newNode.height() , child.offset().left + child.width()/2, newY );
    }
 
  } else {

    offset.left = ESQUERDA - newNode.width()/2;
    ESQUERDA = ESQUERDA + widthPadrao;

  }
  newNode.offset(offset);

  return newNode;
}


var svgNS = "http://www.w3.org/2000/svg";
var $this = $(this);

$container = $('<div>');

$container.width( defaults.widthMAX);
$container.height( defaults.height);
$container.css('border', defaults.border);
$this.append($container);

var ESQUERDA = $container.offset().left + 50;
drawTree( $container.offset().top + 10 , tree, 4* defaults.yStep );

};

})(jQuery);
