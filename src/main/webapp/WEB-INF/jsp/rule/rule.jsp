<div class="report_details">
	<table class="attributes">
		<tbody>
			<tr>
			    <th>Tipo:</th><td>${rule.type}</td>
			</tr>
			<tr>
			    <th>Grupo:</th><td>${rule.group}</td>
			</tr>
			<tr>
			    <th>Mensagem curta:</th><td>${rule.shortMessage}</td>
			</tr>
			<tr>
			    <th>Mensagem:</th><td>${rule.message}</td>
			</tr>
			<tr>
			    <th>Exemplos:</th>
		    	<td>
	    			<ol>
		    			<c:forEach items="${exampleList}" var="example" >
							<li>
								<ul>
									<li><b>incorreto:</b> <br />
										<div class="analise_text">
											<p align="center"><c:out value="${example.a}" /></p>
										</div>
									</li>
									<li><b>correto:</b> <br />
										<div class="analise_text">
											<p align="center">${example.b}</p>
										</div>
									</li>
								</ul> 
							</li>
						</c:forEach> 
					</ol>
				</td>
			</tr>
		</tbody>
	</table>
</div>