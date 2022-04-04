function validaInscricao(){
	//alert("Preencha o campo nome.");
	var nome = document.frminscricao.txtnome.value;
	var expRegNome = new RegExp("^[A-zÀ-ü]{3,}([ ]{1}[A-zÀ-ü]{2,})+$");
	
	if(!expRegNome.test(nome)){
		alert("Preencha o campo Nome corretamente.");
		document.frminscricao.txtnome.focus();
		return false;
	}
	
	//Validação do Sexo
	var opcaoSexo = document.frminscricao.sexo;
	var valido = false;
	
	for(var i = 0; i < opcaoSexo.length; i++){
		if(opcaoSexo[i].checked == true){
			valido = true;
		}
	}
	if(valido==false){
		alert("Preencha o campo sexo.");
		return false;
	}
	
	//Validação do Nascimento OBS.: expressão regular
	var dataNasci = document.frminscricao.datanascimento.value;
	var expRegDatanasci = new RegExp("^(([0-2]{1}[0-9]{1})|([3]{1}[0-1]{1}))\/[0-1]{1}[1-9]{1}\/[1-2]{1}[0-9]{3}");
	
	if(!expRegDatanasci.test(dataNasci)){
		alert("Preencha o campo Data Nascimento corretamente.");
		document.frminscricao.datanascimento.focus();
		return false;
	}
	
	//if(document.frminscricao.datanascimento.value==""){
	//	alert("Preencha o campo nascimento.");
	//	document.frminscricao.datanascimento.focus();
	//	return false;
	//}
	
	//Validação do E-mail
	var email = document.frminscricao.txtemail.value;
	var expRegEmail = new RegExp("^[A-z0-9._]{3,}[@]{1}[A-z]{3,}([.]{1}[A-z]{2,})+$");
	
	if(!expRegEmail.test(email)){
		alert("Preencha o campo E-mail corretamente.");
		document.frminscricao.txtemail.focus();
		return false;
	}
	
	//if(document.frminscricao.txtemail.value==""){
	//	alert("Preencha o campo e-mail.");
	//	document.frminscricao.txtemail.focus();
	//	return false;
	//}
	
	//Validação do Telefone
	var fone = document.frminscricao.txtfone.value;
	var expRegFone = new RegExp("^[(]{1}[1-9]{2}[)]{1}[0-9]{4,5}[-]{1}[0-9]{4}$");

	if(!expRegFone.test(fone)){
		alert("Preencha o campo Telefone corretamente.");
		document.frminscricao.txtfone.focus();
		return false;
	}
	
	//Validação do Raio
	var opcaoRaio = document.frminscricao.corraio;
	var valido = false;
	
	for(var i = 0; i < opcaoRaio.length; i++){
		if(opcaoRaio[i].checked == true){
			valido = true;
		}
	}
	if(valido==false){
		alert("Preencha o campo raio.");
		return false;
	}
	return true;
}

//function verificaParticipacao(){
//	var elemento = document.getElementById("enviar");
//	
//	if(document.frminscricao.participar.checked==true){	
//		var button = document.createElement("button");
//		button.setAttribute("type", "submit");
//		var texto = document.createTextNode("Enviar");
//		button.appendChild(texto);
//		elemento.appendChild(button);
//	}else{
//		if(elemento.firstChild){
//			elemento.removeChild(elemento.firstChild);
//		}
//	}
//}

function verificaParticipacao(){

	if(document.frminscricao.participar.checked==true){	
		 //habilita o botão
	      document.getElementById("botao").disabled = false;
	}else{
		 //desabilita o botão se o conteúdo do input ficar em branco
	      document.getElementById("botao").disabled = true;
	}
}

//Assim q o documento HTML for carregado por completo...
$(document).ready(function(){
	//Carrega cabeçalho, menu e rodapé aos respectivos locais
	$("header").load("/ProjetoFaClube/pages/site/general/cabecalho.html");
	$("nav").load("/ProjetoFaClube/pages/site/general/menu.html");
	$("footer").load("/ProjetoFaClube/pages/site/general/rodape.html");
});