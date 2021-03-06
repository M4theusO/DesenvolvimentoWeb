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
import br.com.coldigogeladeiras.jdbc.JDBCProdutoDAO;
import br.com.coldigogeladeiras.modelo.Produto;

@Path("produto")
public class ProdutoRest extends UtilRest{
	
	@POST
	@Path("/inserir")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response inserir(String produtoParam) {
		
		String msg="";
		
		try {
			Produto produto = new Gson().fromJson(produtoParam, Produto.class); //perguntar
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao); //passa a conexao com o BD pro método JDBCProdutoDAO
			
			int idMarca = produto.getMarcaId();
			boolean retornoMarca = jdbcProduto.validaMarca(idMarca); //true, caso a marca exista e esteja ativa
			boolean retornoProdutoIgual = jdbcProduto.verificaProdutoIgual(produto); //modelo que o usuario informou pra ver se existe no BD
			
			if(retornoProdutoIgual == true) { //caso não exista, pode cadastrar
			
				if(retornoMarca == true) { //caso a marca exista e esteja ativa
				
					boolean retorno = jdbcProduto.inserir(produto);
					conec.fecharConexao();
					
					if(retorno) { 
						msg = "Produto Cadastrado!";
					}else {
						msg = "Erro ao cadastrar produto!";
						return this.buildErrorResponse(msg);
					}
				}else {
					conec.fecharConexao();
					msg = "A marca selecionada não existe ou está inativa, recarregue a página!";
					return this.buildErrorResponse(msg);
				}	
			}else {
				conec.fecharConexao();
				msg = "Este produto já existe!";
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
	@Path("/buscar")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscarPorNome(@QueryParam("valorBusca") String nome) {
		
		try {
			
			List<JsonObject> listaProdutos = new ArrayList<JsonObject>();
			
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			listaProdutos = jdbcProduto.buscarPorNome(nome);
			conec.fecharConexao();
			String json = new Gson().toJson(listaProdutos);
			
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
		
		String msg = "";
		
		try {
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			
			int retornoP = jdbcProduto.verificaProduto(id);
			
			if(retornoP!=-1) {
				
				boolean retorno = jdbcProduto.deletar(id);
				conec.fecharConexao();
				
				if(retorno) {
					msg = "Produto excluído com sucesso!";
				}else {
					msg = "Erro ao excluir produto, recarregue a página.";
					return this.buildErrorResponse(msg);
				}
			}else {
				conec.fecharConexao();
				msg = "Esse produto não existe, recarregue a página!";
				return this.buildErrorResponse(msg);
			}		
			
			return this.buildResponse(msg);
			
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
			Produto produto = new Produto();
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			
			produto = jdbcProduto.buscarPorId(id);
			
			//está correto fazer essa validação?
			int idProduto = produto.getId();
			conec.fecharConexao();
			
			String msg = "";
			
			if(idProduto == 0) { //se a marca não existir/excluida
				msg = "Esse produto não existe, recarregue a página!"; // valida na hora de editar um produto já excluido
				return this.buildErrorResponse(msg);
			}
			
			return this.buildResponse(produto);
			
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
			Produto produto = new Gson().fromJson(produtoParam, Produto.class);
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			
			int idMarca = produto.getMarcaId(); //pego o id da marca
			boolean test = jdbcProduto.validaMarca(idMarca); //true, caso a marca exista e esteja ativa
			int retornoP = jdbcProduto.verificaProduto(produto.getId());
			boolean retornoProdutoIgual = jdbcProduto.verificaProdutoIgual(produto); //produto que o usuario informou pra ver se já existe no BD
			
			String msg = "";
			
			if(retornoP!=-1) {
			
				if(retornoProdutoIgual == true) { //caso não exista, pode cadastrar
			
					if(test == true) { //caso a marca exista e esteja ativa
				
						boolean retorno = jdbcProduto.alterar(produto);
						conec.fecharConexao();
			
						if(retorno) {
							msg = "Produto alterado com sucesso!";
						}else {
							msg = "Erro ao alterar produto.";
							return this.buildErrorResponse(msg);
						}
					}else {
						conec.fecharConexao();
						msg = "A marca selecionada não existe ou está inativa, recarregue a página!";
						return this.buildErrorResponse(msg);
					}
				}else {
					conec.fecharConexao();
					msg = "Já existe um produto igual a esse!";
					return this.buildErrorResponse(msg);
				}
			}else {
				conec.fecharConexao();
				msg = "Esse produto não existe, recarregue a página!";
				return this.buildErrorResponse(msg);
			}
			
			return this.buildResponse(msg);
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@GET
	@Path("/buscarParaVenda")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscarParaVenda(@QueryParam("idMarca") int idMarca, @QueryParam("categoria") int categoria) {
		
		try {
			List<JsonObject> listaProdutos = new ArrayList<JsonObject>();
			
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			listaProdutos = jdbcProduto.buscarParaVenda(idMarca, categoria);
			conec.fecharConexao();
			String json = new Gson().toJson(listaProdutos);
			System.out.println(json);
			return this.buildResponse(json);
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}		
	}
	
	
}//fecha a classe ProdutoRest
