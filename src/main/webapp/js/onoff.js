// JavaScript Document
// display ON/OFF

// appear or disappear
function onOff (idElem) {
    if (document.getElementById(idElem).style.display == 'none') {
        document.getElementById(idElem).style.display = 'block';
    }
    else {
        document.getElementById(idElem).style.display = 'none';
    }
}

function on (idElem) {
    if (document.getElementById(idElem)) {
        document.getElementById(idElem).style.display = 'block';
    }
}
function off (idElem) {
    if (document.getElementById(idElem)) {
        document.getElementById(idElem).style.display = 'none';
    }
}