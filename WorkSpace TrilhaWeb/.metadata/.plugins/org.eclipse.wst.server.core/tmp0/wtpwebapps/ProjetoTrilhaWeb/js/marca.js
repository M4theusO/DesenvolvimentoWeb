/* Esse arquivo será responsável no frontend pelas funções que realmente farão 
 * as coisas acontecerem, desde a validação de dados dos formulários até 
 * inserção, exclusão, busca, alteração…
 */

COLDIGO.marca = new Object();

$(document).ready(function(){
	
	//Cadastra no BD a marca informada
	COLDIGO.marca.cadastrar = function(){
		
		var marca = new Object();
		marca.nome = document.frmAddMarca.marca.value;

		if(marca.nome==""){
			COLDIGO.exibirAviso("Preencha todos os campos!");	
		}else{
			
			$.ajax({
				
				type: "POST",
				url: COLDIGO.PATH + "marca/inserir",
				data: JSON.stringify(marca),
				success: function(msg){
					COLDIGO.exibirAviso(msg);
					$("#addMarca").trigger("reset"); //reseta o formulário
					COLDIGO.marca.buscar();
				},
				error: function(msg){
					console.log(msg);
					COLDIGO.exibirAviso("Erro ao cadastrar uma nova marca: "+ msg.status + " - " + msg.responseText);
					//COLDIGO.exibirAviso(msg.responseText);
				}
			});
		}
	}
	
	//Busca no BD e exibe na página as marcas que atendam à solicitação do usuário
	COLDIGO.marca.buscar = function(){
		
		var valorBusca = $("#campoBuscaMarca").val();
		
		$.ajax({
			type: "GET",
			url: COLDIGO.PATH + "marca/buscarMarca",
			data: "valorBusca=" + valorBusca,
			success: function(dados){
				console.log(dados);
				dados = JSON.parse(dados);
				
				$("#listaMarcas").html(COLDIGO.marca.exibir(dados));
				
			},
			error: function(info){
				COLDIGO.exibirAviso("Erro ao consultar os contatos: "+ info.status + " - " + info.responseText);
				//COLDIGO.exibirAviso(info.responseText);
			}
		});
		
	};
	
	//Transforma os dados das marcas recebidas do servidor em uma tabela HTML
	COLDIGO.marca.exibir = function(listaDeMarcas){
	
		var tabela = "<table>" +
		"<tr>" + 
		"<th>Marca</th>" +
		"<th>Ativação/Inativação</th>"+
		"<th class='acoes'>Ações</th>" +
		"</tr>";
		
		if (listaDeMarcas != undefined && listaDeMarcas.length > 0){
			
			for(var i=0; i<listaDeMarcas.length; i++){
				
				var status ="";
				
				if(listaDeMarcas[i].status==1){
					var status = "checked";
				}
							
				tabela += "<tr>" +
						"<td>"+listaDeMarcas[i].nome+"</td>" +
						"<td>"+
						//"<div class='switch'>"+
						"<input type='checkbox' name='status' id='selStatus' onclick=\"COLDIGO.marca.ativaMarca('"+listaDeMarcas[i].id+"',(this))\" "+ status +">" + 
						//"</div>"+                                       
						"</td>"+
						"<td>" +
							"<a onclick=\"COLDIGO.marca.exibirEdicao('"+listaDeMarcas[i].id+"')\"><img src='../../imgs/edit.png' alt='Editar registro'></a> " +
							"<a onclick=\"COLDIGO.marca.confirmaExclusao('"+listaDeMarcas[i].id+"')\"><img src='../../imgs/delete.png' alt='Excluir registro'></a>" +
						"</td>" +
						"</tr>"	
			}
						
		} else if(listaDeMarcas == ""){
			tabela += "<tr><td colspan='3'>Nenhum registro encontrado</td></tr>";
		} 
		tabela += "</table>";
		
		return tabela;
	};
		
	//Executa a função de busca ao carregar a página
	COLDIGO.marca.buscar();
	
	//Exclui a marca selecionada
	COLDIGO.marca.excluir = function(id){
		
		$.ajax({
			type: "DELETE",
			url: COLDIGO.PATH + "marca/excluir/"+id,
			success: function(msg){	
				
				COLDIGO.exibirAviso(msg);
				COLDIGO.marca.buscar();
			},
			error: function(info){
				COLDIGO.exibirAviso("Erro ao excluir marca: "+ info.status + " - " + info.responseText);
				//COLDIGO.exibirAviso(info.responseText);
			}
		
		});
		
	};
	
	//Carrega no BD os dados da marca selecionada para alteração e coloca-a no formulário de alteração
	COLDIGO.marca.exibirEdicao = function(id){
		
		$.ajax({
			type: "GET",
			url: COLDIGO.PATH + "marca/buscarPorId",
			data: "id="+id, //leva ao metodo
			success: function(marca){
				
				document.frmEditaMarca.idMarca.value = marca.id; //coloca os dados da marca no formulario
				document.frmEditaMarca.marca.value = marca.nome;
				
				var modalEditaMarca = {
						title: "Editar Marca",
						height: 200,
						width: 350,
						modal: true,
						buttons:{
							"Salvar": function(){
								$(this).dialog("close");
								COLDIGO.marca.editar();
							},
							"Cancelar": function(){
								$(this).dialog("close");
							}
						},
						close: function(){
							//caso o usuário simplesmente feche a caixa de edição
							//não deve acontecer nada
						}
				};
				
				$("#modalEditaMarca").dialog(modalEditaMarca);
			},
			error: function(info){
				COLDIGO.exibirAviso("Erro ao buscar marca para edição: "+ info.status + " - " + info.responseText);
				//COLDIGO.exibirAviso(info.responseText);
			}
		});
	};
	
	//Realiza a edição dos dados no BD
	COLDIGO.marca.editar = function(){
		
		var marca = new Object();
		marca.id = document.frmEditaMarca.idMarca.value; //coloca os dados do formulário nos dados de marca 
		marca.nome = document.frmEditaMarca.marca.value;
		
		$.ajax({
			type: "PUT",
			url: COLDIGO.PATH + "marca/alterar",
			data: JSON.stringify(marca), //dados a serem enviados
			success: function(msg){
				COLDIGO.exibirAviso(msg);
				COLDIGO.marca.buscar();
				$("#modalEditaMarca").dialog("close");
			},
			error: function(info){
				COLDIGO.exibirAviso("Erro ao editar marca: "+ info.status + " - " + info.responseText);
				//COLDIGO.exibirAviso(info.responseText);
			}
		});
	};
	
	COLDIGO.marca.confirmaExclusao = function(id){
		
		var modalExclusao = {
				title: "Exclusão",
				height: 175,
				width: 400,
				modal: true,
				buttons:{
					"OK": function(){
						COLDIGO.marca.excluir(id);
					},
					"Cancelar": function(){
						$(this).dialog("close");
					}
				},
				close: function(){
					//caso o usuário simplesmente feche a caixa de edição
					//não deve acontecer nada
				}
		};
		$("#modalAviso").html("Você realmente deseja excluir essa marca?");
		$("#modalAviso").dialog(modalExclusao);
		
	};
	
	COLDIGO.marca.ativaMarca = function(id, checkbox){
		
		$.ajax({
			type: "PUT",
			url: COLDIGO.PATH + "marca/ativaMarca/"+id,
			success: function(msg){	
				
				COLDIGO.exibirAviso(msg);			
				//COLDIGO.marca.buscar();
			},
			error: function(info){
				COLDIGO.exibirAviso("Erro ao ativar marca: "+ info.status + " - " + info.responseText);
				//COLDIGO.exibirAviso(info.responseText);
				checkbox.checked=!checkbox.checked;
				//console.log(info);
			}
		
		});
		
	};
	
});