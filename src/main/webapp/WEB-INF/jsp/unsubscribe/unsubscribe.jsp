<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css"/>" />

<div class="specialframe">
	<h2>Olá ${user.name}, escolha as opções abaixo.</h2>
	<br/>
	
	<form action="<c:url value='/doUnsubscription'/>" method="post">
	<c:choose>
		<c:when test="${user.isReceiveEmail}">
    		<c:set var="checked" value="checked='CHECKED'"></c:set>
  		</c:when>
  		<c:otherwise>
    		<c:set var="checked" value=""></c:set>
  		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${user.isReceiveNewsMail}">
    		<c:set var="checkedNews" value="checked='CHECKED'"></c:set>
  		</c:when>
  		<c:otherwise>
    		<c:set var="checkedNews" value=""></c:set>
  		</c:otherwise>
	</c:choose>
	
	<input type="hidden" name="user.id" value="${ user.id }"/>
	<input type="hidden" name="codeRecover" value="${ user.emailOptOutCode }" />
	
	<input type="checkbox" name="isReceiveTransactionalMail" value="true" ${checked }/> Quero parar de receber e-mails do CoGrOO Comunidade<br/>
	<input type="checkbox" name="isReceiveNewsMail" value="true" ${checkedNews }/> Quero parar de receber atualizações do CoGrOO<br/>
	
	<p style="text-align: right;"><input type="submit" value=" Enviar &raquo; " class="button"></p> 
	</form>
</div>