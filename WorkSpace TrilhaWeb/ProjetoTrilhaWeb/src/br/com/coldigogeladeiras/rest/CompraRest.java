package br.com.coldigogeladeiras.rest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.coldigogeladeiras.bd.Conexao;
import br.com.coldigogeladeiras.jdbc.JDBCCompraDAO;
import br.com.coldigogeladeiras.modelo.Compra;
import br.com.coldigogeladeiras.modelo.ProdutoCompra;

@Path("compra")
public class CompraRest extends UtilRest{

	@POST
	@Path("/inserir")
	@Consumes("application/*")
	public Response inserir(String compraParam) {
		
		try {
			System.out.println(compraParam);
			Compra compra = new Gson().fromJson(compraParam, Compra.class);
		
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			
			JDBCCompraDAO jdbcCompra = new JDBCCompraDAO(conexao);
			
			boolean retorno = jdbcCompra.inserir(compra);
			
			String msg = "Erro ao cadastrar compra.";
			if(retorno) {
				msg = "Compra cadastrada com sucesso!";
			}
			
			conec.fecharConexao();
			
			return this.buildResponse(msg);
			
		/*	
			System.out.println(compra.getData());
			System.out.println(compra.getFornecedor());
			
			for(ProdutoCompra produto: compra.getProdutos()) {
				
				System.out.println("Produto:");
				System.out.println(produto.getIdProduto());
				System.out.println(produto.getQuantidade());
				System.out.println(produto.getValor());
								
			}
			
			return this.buildResponse("Sucesso!");
		
		*/	
		}catch(Exception e){
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
		
	}
	
	@GET
	@Path("/relatorio")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response gerarRelatorio() {
		
		try {
			List<JsonObject> listaCompras = new ArrayList<JsonObject>();
			
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCCompraDAO jdbcCompra = new JDBCCompraDAO(conexao);
			listaCompras = jdbcCompra.gerarRelatorio();
			System.out.println(listaCompras);
			conec.fecharConexao();
			String json = new Gson().toJson(listaCompras);
			
			return this.buildResponse(json);
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
}
