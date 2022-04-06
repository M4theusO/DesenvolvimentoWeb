COLDIGO.compra = new Object();

$(document).ready(function(){
	
	/*******************************
	 * 	Funções do mestre-detalhe 
	 *******************************/
	
	/* FUNÇÃO PARA CARREGAR AS MARCAS NOS CAMPOS DO FORMULÁRIO */

	//Carrega as marcas registradas no BD no select do formulário, usando o id para fazer isso no campo certo
	COLDIGO.compra.carregarMarcas = function(id){
		
		//Recebe os valores de todos os campos de marca no formulário e armazena em camposMarcas
		var camposMarcas = document.getElementsByName('selMarca[]');
		
		//Inicia o Ajax que busca no BD as marcas cadastradas
		$.ajax({
			type: "GET",
			url: COLDIGO.PATH + "marca/buscar",
			//Caso dê certo
			success: function(marcas){
				//Esvazia o select de marca cujo id foi recebido
				$(camposMarcas[id]).html("");
				
				//Se houver mais de uma marca nesse objeto
				if(marcas.length){
					//Cria uma opção vazia, para validarmos o campo depois
					var option = document.createElement("option");
					option.setAttribute("value", "");
					option.innerHTML = ("Escolha");
					//Coloca o campo no select correto
					$(camposMarcas[id]).append(option);
					//Para cada marca...
					for(var i = 0; i < marcas.length; i++){
						
						//Cria uma opção com o valor do id e o nome da marca
						var option = document.createElement("option");
						option.setAttribute("value", marcas[i].id);
						option.innerHTML = (marcas[i].nome);
						//Coloca essa opção no select
						$(camposMarcas[id]).append(option);
					}
				//senão tiverem marcas no BD
				}else{
					//Cria uma opção destacando que deve-se criar uma marca primeiro
					var option = document.createElement("option");
					option.setAttribute("value", "");
					option.innerHTML = ("Cadastre uma marca primeiro!");
					//Adiciona uma classe que destaca essa opção em vermelho
					$(camposMarcas[id]).addClass("aviso");
					//Coloca a opção no select
					$(camposMarcas[id]).append(option);				
				}
			//Fim do SUCCESS
			},
			//Se der erro no processo de busca de marcas
			error: function(info){
				//Exibe aviso
				COLDIGO.exibirAviso("Erro ao buscar as marcas: "+ info.status + " - " + info.statusText);
				//Cria opção de erro e a coloca no select, adicionando uma classe que destaca a opção em vermelho
				$(camposMarcas[id]).html("");
				var option = document.createElement("option");
				option.setAttribute("value", "");
				option.innerHTML = ("Erro ao carregar marcas!");
				$(camposMarcas[id]).addClass("Aviso");
				$(camposMarcas[id]).append(option);			
			}			
		});		
		
	}
	
	//Executa a função ao carregar a página, afetando a única opção de marca disponivel (0)
	COLDIGO.compra.carregarMarcas(0);
	
	/*FUNÇÃO PARA CRIAR UMA NOVA LINHA DE DETALHE NO FORMULÁRIO*/
	
	//Ao clicar no botão botaoAdd:
	$(".botaoAdd").click(function(){
		
		//Cria um clone da primeira linha de detalhe (veja a classe 'detalhes') e salva na variável novoCampo
		var novoCampo = $("tr.detalhes:first").clone();
		//Esvazia os valores de todos os inputs do clone
		novoCampo.find("input").val("");
		//Insere o clone na página, logo após a última linha já existente
		novoCampo.insertAfter("tr.detalhes:last");
		
	});
	
	/* FUNÇÃO PARA REMOVER UMA LINHA DE DETALHE NO FORMULÁRIO */
	
	//Cria a função, recebendo o botão clicado por parâmetro
	COLDIGO.compra.removeCampo = function(botao){
		//Se houver mais de uma linha no mestre-detalhe
		if($("tr.detalhes").length > 1){
			//Remove a linha que contém o botão de excluir clicado. Para endender, pense na estrutura HTML:
			//botao seria o botão, o primeiro parent é a CÉLULA onde está o botão,
			//e o segundo parent é a linha onde está o botão
			$(botao).parent().parent().remove();
		//senão, é porque só tem uma linha, então...					
		}else{
			//...avisa que a linha não pode ser removida
			COLDIGO.exibirAviso("A última linha não pode ser removida.");
		}
	//fecha a função removeCampo
	}
	
});