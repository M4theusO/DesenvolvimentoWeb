package br.com.coldigogeladeiras.rest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.coldigogeladeiras.bd.Conexao;
import br.com.coldigogeladeiras.jdbc.JDBCMarcaDAO;
import br.com.coldigogeladeiras.jdbc.JDBCProdutoDAO;
import br.com.coldigogeladeiras.modelo.Marca;
import br.com.coldigogeladeiras.modelo.Produto;

@Path("marca")
public class MarcaRest extends UtilRest{

	@GET
	@Path("/buscar")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscar() {
		
		try {
			List<Marca> listaMarcas = new ArrayList<Marca>();
		
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			listaMarcas = jdbcMarca.buscar();
			conec.fecharConexao();
			
			return this.buildResponse(listaMarcas);
		
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@POST
	@Path("/inserir")
	@Consumes("application/*")
	public Response inserir(String marcaParam) {

		try {
			Marca marca = new Gson().fromJson(marcaParam, Marca.class);
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao); //passa a conexao com o BD pro método JDBCProdutoDAO
			
			boolean verifica = jdbcMarca.verificaMarcaNome(marca); //verifica se a marca existe no BD
			//System.out.println(verifica+"AQUIII");
			String msg = "";
			
			if(verifica == true) { //caso a marca não exista, cadastra normalmente
				
				boolean retorno = jdbcMarca.inserir(marca); //true ou false
				conec.fecharConexao();
				
				if(retorno) {
					msg = "Marca cadastrada com sucesso!";
				}else {
					msg = "Erro ao cadastrar marca.";
					return this.buildErrorResponse(msg);
				}
			}else {
				conec.fecharConexao();
				msg = "Marca já existente!";
				return this.buildErrorResponse(msg);
			}
			
			return this.buildResponse(msg);
			
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());	
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@GET
	@Path("/buscarMarca")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscarPorNome(@QueryParam("valorBusca") String nome) {
		
		try {
			
			List<JsonObject> listaMarcas = new ArrayList<JsonObject>();
			
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			listaMarcas = jdbcMarca.buscarPorNome(nome);
			conec.fecharConexao();	
			String json = new Gson().toJson(listaMarcas);
			
			return this.buildResponse(json);
			
		}catch(Exception e){
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@DELETE
	@Path("/excluir/{id}")
	@Consumes("application/*")
	public Response excluir(@PathParam("id") int id) {
		
		String retorno = "";
		
		try {
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			
			int retornoM = jdbcMarca.verificaMarca(id);
			int retornoP = jdbcMarca.verificaProduto(id);
			
			if(retornoM != -1) { //caso o retorno do id de marca seja o id da marca
				
				if(retornoM == retornoP) { //se a marca a ser excluida existir nos produtos cadastrados
					conec.fecharConexao();
					retorno = "Essa marca possui produtos cadastrados!";
					return this.buildErrorResponse(retorno);
				
				}else {
					retorno = "Marca excluída com sucesso!";
					
					boolean retornoDeletar = jdbcMarca.deletar(id); //faz a exclusão
					conec.fecharConexao();
					
						if(retornoDeletar!=true) { //valida se deu excessao no metodo de deletar
							retorno = "Erro ao excluir marca!";
							return this.buildErrorResponse(retorno);
						}
				}
			}else { //se o retorno do id da marca não possuir o id da marca (ELA NÃO EXISTE DÃÃÃN)
				conec.fecharConexao();
				retorno = "Essa marca já foi excluída!";
				return this.buildErrorResponse(retorno);
			}
			
			return this.buildResponse(retorno);
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@GET
	@Path("/buscarPorId")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscarPorId(@QueryParam("id") int id) {
		
		try {
			Marca marca = new Marca();
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			
			marca = jdbcMarca.buscarPorId(id);
			
			conec.fecharConexao();
			
			//está correto fazer essa validação?
			int idMarca = marca.getId();
			
			String msg = "";
			
			if(idMarca == 0) { //se a marca não existir/excluida
				msg = "Essa marca não existe, recarregue a página!"; // valida na hora de editar um produto já excluido
				return this.buildErrorResponse(msg);
			}
			
			return this.buildResponse(marca);
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@PUT
	@Path("/alterar")
	@Consumes("application/*")
	public Response alterar(String produtoParam) {
		try {
			Marca marca = new Gson().fromJson(produtoParam, Marca.class);
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			
			int idMarca = marca.getId(); //pego o id da marca
			int test = jdbcMarca.verificaMarca(idMarca);
			
			String msg = "";
			
			boolean verifica = jdbcMarca.verificaMarcaNome(marca);
			
			if(test!=-1) {
				
				if(verifica == true) {
				
					boolean retorno = jdbcMarca.alterar(marca);
					conec.fecharConexao();
			
					if(retorno) {
						msg = "Marca alterada com sucesso!";
					}else {
						msg = "Erro ao alterar marca.";
						return this.buildErrorResponse(msg);
					}
				
				}else {
					conec.fecharConexao();
					msg = "Marca já existente";
					return this.buildErrorResponse(msg);
				}	
			}else {
				conec.fecharConexao();
				msg = "A marca selecionada não existe, recarregue a página!";
				return this.buildErrorResponse(msg);
			}
			
			return this.buildResponse(msg);
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@PUT
	@Path("/ativaMarca/{id}")
	@Consumes("application/*")
	public Response ativaMarca(@PathParam("id") int id) {
		
		try {
	
			Marca marca = new Marca();
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			
			marca = jdbcMarca.buscarPorId(id); //retorna um objeto marca com os dados da marca selecionada no form
			
			//VALIDAR RECEBIMENTO **************VALIDAR
			
			int idMarca = marca.getId();
			//System.out.println(idMarca+"passoooooooou");
			
			String msg = "";
			
			if(idMarca != 0) { //se essa marca existe
					
				boolean test = jdbcMarca.ativaMarca(marca);
				conec.fecharConexao();
			
				if(test) {
					msg = "Status da marca alterada com sucesso!";
				}else {
					msg = "Erro ao alterar status da marca.";
				return this.buildErrorResponse(msg);
				}
			}else {
				conec.fecharConexao();
				msg = "Essa marca foi excluída, recarregue a página!";
				return this.buildErrorResponse(msg);
			}
			
			return this.buildResponse(msg);
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
}
