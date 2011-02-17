// JavaScript Document
// image OPEN/CLOSE

function openClose (idElem) {
	if (document.getElementById(idElem).src.match('details_close') )
	{
		document.getElementById(idElem).src = path + "/images/details_open.png";
	}
	else
	{
		document.getElementById(idElem).src = path + "/images/details_close.png";
	}
}