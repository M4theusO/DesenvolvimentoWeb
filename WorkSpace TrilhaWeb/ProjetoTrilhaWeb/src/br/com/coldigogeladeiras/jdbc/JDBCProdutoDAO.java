package br.com.coldigogeladeiras.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; //PERGUNTAR
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import br.com.coldigogeladeiras.jdbcinterface.ProdutoDAO;
import br.com.coldigogeladeiras.modelo.Produto;

public class JDBCProdutoDAO implements ProdutoDAO {
	
	private Connection conexao;
	
	public JDBCProdutoDAO(Connection conexao) { 
		this.conexao = conexao;
	}
	
	public boolean inserir(Produto produto) {		
		
		String comando = "INSERT INTO produtos " + "(id, categoria, modelo, capacidade, valor, marcas_id)" + "VALUES (?,?,?,?,?,?)";
		
		PreparedStatement p;
		
		try {
			
			//Prepara o comando para a execução no BD em que nos conectamos
			p = this.conexao.prepareStatement(comando);
			
			//Substitui no comando os "?" pelos valores do produto 
			//indica a posição da interrogação que deve ser substituída e qual é o valor que a substituirá.
			p.setInt(1, produto.getId());
			p.setString(2, produto.getCategoria());
			p.setString(3, produto.getModelo());
			p.setInt(4, produto.getCapacidade());
			p.setFloat(5, produto.getValor());
			p.setInt(6, produto.getMarcaId());
			//Executa o comando no BD
			p.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public List<JsonObject> buscarPorNome(String nome){
		
		//Inicia criação do comando SQL de busca
		String comando = "SELECT produtos.*, marcas.nome as marca FROM produtos "
				+ "INNER JOIN marcas ON produtos.marcas_id = marcas.id ";
		//Se o nome não estiver vazio...
		if(!nome.equals("")) {
			//concatena no comando o WHERE buscando no nome do produto
			//o texto da variável nome
			comando += "WHERE modelo LIKE '%" + nome + "%'";
		}
		//Finaliza o comando ordenado alfabeticamente por
		//categoria, marca e depois modelo.
		comando += "ORDER BY categoria ASC, marcas.nome ASC, modelo ASC";
		
		List<JsonObject> listaProdutos = new ArrayList<JsonObject>();
		JsonObject produto = null;
		
		try {
			Statement stmt = conexao.createStatement();
			ResultSet rs = stmt.executeQuery(comando);
			
			while(rs.next()) {
				
				int id = rs.getInt("id");
				String categoria = rs.getString("categoria");
				String modelo = rs.getString("modelo");
				int capacidade = rs.getInt("capacidade");
				float valor = rs.getFloat("valor");
				String marcaNome = rs.getString("marca");
				
				if(categoria.equals("1")) {
					categoria = "Geladeira";
				}else if(categoria.equals("2")) {
					categoria = "Freezer";
				}
				
				produto = new JsonObject();
				produto.addProperty("id", id);
				produto.addProperty("categoria", categoria);
				produto.addProperty("modelo", modelo);
				produto.addProperty("capacidade", capacidade);
				produto.addProperty("valor", valor);
				produto.addProperty("marcaNome", marcaNome);
				
				listaProdutos.add(produto);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return listaProdutos;
	}
	
	public boolean deletar(int id) {
		String comando = "DELETE FROM produtos WHERE id = ?";
		PreparedStatement p;
		
		try {
			p = this.conexao.prepareStatement(comando);
			p.setInt(1, id);
			p.execute();
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Produto buscarPorId(int id) {
		String comando = "SELECT * FROM produtos WHERE produtos.id = ?";
		Produto produto = new Produto();
		
		try {
			PreparedStatement p = this.conexao.prepareStatement(comando);
			p.setInt(1, id);
			ResultSet rs = p.executeQuery(); //PQ EXECUTEQUERY E NÃO SÓ EXECUTE???
			while(rs.next()) {
				
				String categoria = rs.getString("categoria");
				String modelo = rs.getString("modelo");
				int capacidade = rs.getInt("capacidade");
				float valor = rs.getFloat("valor");
				int marcaId = rs.getInt("marcas_id");
				
				produto.setId(id);
				produto.setCategoria(categoria);
				produto.setMarcaId(marcaId);
				produto.setModelo(modelo);
				produto.setCapacidade(capacidade);
				produto.setValor(valor);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return produto;
	}
	
	public boolean alterar(Produto produto) {
		
		String comando = "UPDATE produtos "
				+ "SET categoria=?, modelo=?, capacidade=?, valor=?, marcas_id=?"
				+ " WHERE id=?";
		PreparedStatement p;
		
		try {
			p = this.conexao.prepareStatement(comando);
			p.setString(1, produto.getCategoria());
			p.setString(2, produto.getModelo());
			p.setInt(3, produto.getCapacidade());
			p.setFloat(4, produto.getValor());
			p.setInt(5, produto.getMarcaId());
			p.setInt(6, produto.getId());
			p.executeUpdate();
	
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public int validaProduto(Produto produto) {	
	
		int retorno=-1;

		retorno = produto.getMarcaId(); //substitui
		System.out.println(retorno);

		return retorno; //retorno -1 caso não exista a marca ou então o id_marca
	}
	
	public boolean validaMarca(int id) {
		
		String comando = "SELECT * FROM marcas WHERE marcas.id = ?";
		int marcaId=-2;
		int status=-1;
		boolean retorno = false;

		try {
			PreparedStatement m = this.conexao.prepareStatement(comando);
			m.setInt(1, id);
			ResultSet rs = m.executeQuery(); 
			
			while(rs.next()) {

				marcaId = rs.getInt("id"); //substitui o -2 pelo id caso exista a marca
				status = rs.getInt("status");//substitui o -1 pelo status 
			}
			
			if(status==1 && marcaId!=-2) { //se a marca existir e estiver ativa 
				retorno = true;
			}

		}catch(Exception e) {
			e.printStackTrace();		
		}
		return retorno;
	}
	
	public int verificaProduto(int id) {
		
		String comando = "SELECT * FROM produtos WHERE produtos.id = ?"; //tabela marcas
		int retorno=-1;
		//System.out.println(retorno+"ela é o retorno1!!!");
	
		try {
			PreparedStatement m = this.conexao.prepareStatement(comando);
			m.setInt(1, id);
			ResultSet rs = m.executeQuery(); 

			while(rs.next()) {
					
				retorno = rs.getInt("id"); //substitui o -1 pelo id caso exista o produto		
			}

		}catch(Exception e) {
			e.printStackTrace();		
		}
		return retorno;
	}
	
	public boolean verificaProdutoIgual(Produto produto) {
		
		String comando = "SELECT * FROM produtos WHERE produtos.modelo = ? and produtos.categoria = ? and produtos.marcas_Id = ?"; //tabela marcas
		int id = 0;
		String modelo="";
		String categoria="";
		int marca=0;
		boolean retorno=true;
	
		try {
			PreparedStatement m = this.conexao.prepareStatement(comando);
			m.setString(1, produto.getModelo());
			m.setString(2, produto.getCategoria());
			m.setInt(3, produto.getMarcaId());
			ResultSet rs = m.executeQuery(); 

			while(rs.next()) {
					
				id = rs.getInt("id");
				modelo = rs.getString("modelo");
				categoria = rs.getString("categoria");
				marca = rs.getInt("marcas_Id");
			}
			
			if(produto.getId() != id && marca!=0 && modelo!="" && categoria!="") {
				retorno = false;
			}

		}catch(Exception e) {
			e.printStackTrace();		
		}
		return retorno;
	}
	
	public List<JsonObject> buscarParaVenda(int idMarca, int categoria){
		
		String comando = "SELECT id, modelo FROM produtos "
				+ "WHERE marcas_id = "+ idMarca + " AND categoria = " + categoria
				+ " ORDER BY produtos.modelo ASC";
		System.out.println(comando);
		List<JsonObject> listaProdutos = new ArrayList<JsonObject>();
		JsonObject produto = null;
		
		try {
			Statement stmt = conexao.createStatement();
			ResultSet rs = stmt.executeQuery(comando);
			
			while(rs.next()) {
				
				int id = rs.getInt("id");
				String modelo = rs.getString("modelo");
				
				produto = new JsonObject(); //PQ JSON?
				produto.addProperty("id", id);
				produto.addProperty("modelo", modelo);
				
				listaProdutos.add(produto);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return listaProdutos;
	}

}
