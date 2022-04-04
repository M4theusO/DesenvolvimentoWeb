package br.com.coldigogeladeiras.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import br.com.coldigogeladeiras.jdbcinterface.MarcaDAO;
import br.com.coldigogeladeiras.modelo.Marca;
import br.com.coldigogeladeiras.modelo.Produto;

public class JDBCMarcaDAO implements MarcaDAO{
	
	private Connection conexao;
	
	public JDBCMarcaDAO(Connection conexao) {
		this.conexao = conexao;
	}
	
	public List<Marca> buscar(){
		//Criação da instrução SQL para busca de todas as marcas
		String comando = "SELECT * FROM marcas ORDER BY marcas.nome ASC";
		
		//Criação de uma lista para armazenar cada marca encontrada
		List<Marca> listMarcas = new ArrayList<Marca>();
		
		//Criação do objeto marca com valor null (ou seja, sem instanciá-lo)
		Marca marca = null;
		
		//Abertura do try-catch
		try {
			//Uso da conexão do banco para prepara-lo para uma instrução SQL
			Statement stmt = conexao.createStatement();
			
			//Execução da instrução criada previamente
			//e armazenamento do resultado no objeto rs
			ResultSet rs = stmt.executeQuery(comando);
			
			//Enquanto houver uma próxima linha no resultado
			while(rs.next()) {
				
				//Criação de intância da classe Marca
				marca = new Marca();
				
				//Recebimento dos 2 dados retornados do BD para cada linha encontrada
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				int status = rs.getInt("status");
				
				//Setando no objeto marca os valores encontrados
				marca.setId(id);
				marca.setNome(nome);
				marca.setStatus(status);
				
				//Adição da instância contida no objeto Marca na lista de marcas
				listMarcas.add(marca);
			}
			
		//Caso alguma Exception seja gerada no try, recebe-a no objeto "ex"
		}catch(Exception ex) {
			//Exibe a exceção na console
			ex.printStackTrace();
		}
		
		//Retorna para quem chamou o método a lista criada
		return listMarcas;
	}
	
	public boolean inserir(Marca marca) {

		String comando = "INSERT INTO marcas " + "(id, nome)" + "VALUES (?,?)";
		
		PreparedStatement m;

		try {
			
			//Prepara o comando para a execução no BD em que nos conectamos
			m = this.conexao.prepareStatement(comando);
			
			//Substitui no comando os "?" pelos valores do produto 
			//indica a posição da interrogação que deve ser substituída e qual é o valor que a substituirá.
			m.setInt(1, marca.getId());
			m.setString(2, marca.getNome());

			//Executa o comando no BD
			m.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public List<JsonObject> buscarPorNome(String nome){
		
		//Inicia criação do comando SQL de busca
		String comando = "SELECT marcas.* FROM marcas ";
		//Se o nome não estiver vazio...
		if(!nome.equals("")) {
			//concatena no comando o WHERE buscando no nome da marca
			//o texto da variável nome
			comando += "WHERE nome LIKE '%" + nome + "%'";
		}
		//Finaliza o comando ordenado alfabeticamente por
		//categoria, marca e depois modelo.
		comando += "ORDER BY marcas.nome ASC";
		
		List<JsonObject> listaMarcas = new ArrayList<JsonObject>();
		JsonObject marca = null;
		
		try {
			Statement stmt = conexao.createStatement();
			ResultSet rs = stmt.executeQuery(comando);
			
			while(rs.next()) {
				
				int id = rs.getInt("id");
				String nomeMarca = rs.getString("nome");
				int status = rs.getInt("status"); //ALTERADO
				
				marca = new JsonObject();
				marca.addProperty("id", id);
				marca.addProperty("nome", nomeMarca);
				marca.addProperty("status", status); //ALTERADO OT18
				
				listaMarcas.add(marca);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return listaMarcas;
	}

	public boolean deletar(int id) {
		
		String comando = "DELETE FROM marcas WHERE id = ?";
		PreparedStatement m;
	
		try {
			m = this.conexao.prepareStatement(comando);
			m.setInt(1, id);
		//	System.out.println(id);
			m.execute();
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Marca buscarPorId(int id) {
		String comando = "SELECT * FROM marcas WHERE marcas.id = ?";
		Marca marca = new Marca();
	
		try {
			PreparedStatement m = this.conexao.prepareStatement(comando); //PQ criamos dentro do try e não fora como em delete
			m.setInt(1, id);
			ResultSet rs = m.executeQuery(); //PQ EXECUTEQUERY E NÃO SÓ EXECUTE???
			while(rs.next()) {
			
				String nome = rs.getString("nome");
				int status = rs.getInt("status");
				
				marca.setId(id);
				marca.setNome(nome);
				marca.setStatus(status);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return marca;
	}
	
	public boolean alterar(Marca marca) {
		
		String comando = "UPDATE marcas "
				+ "SET nome=?"
				+ " WHERE id=?";
		PreparedStatement m;
		try {
			m = this.conexao.prepareStatement(comando);
			m.setString(1, marca.getNome());
			m.setInt(2, marca.getId());
			m.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public int verificaMarca(int id) {
	
		String comando = "SELECT * FROM marcas WHERE marcas.id = ?"; //tabela marcas
		int retorno=-1;
		//System.out.println(retorno+"ela é o retorno1!!!");
	
		try {
			PreparedStatement m = this.conexao.prepareStatement(comando);
			m.setInt(1, id);
			ResultSet rs = m.executeQuery(); 

			while(rs.next()) {
					
				retorno = rs.getInt("id"); //substitui o -1 pelo id caso exista a marca		
			}

		}catch(Exception e) {
			e.printStackTrace();		
		}
		return retorno;
	}

	public int verificaProduto(int id) {
	
		String comando = "SELECT * FROM produtos WHERE produtos.marcas_id = ?"; //tabela produtos
		int retorno=-1;

		try {
		
			PreparedStatement m = this.conexao.prepareStatement(comando);
			m.setInt(1, id);
			ResultSet rs = m.executeQuery();

			while(rs.next()) {
					
				retorno = rs.getInt("marcas_id"); //substitui o -2 pelo id caso exista a marca
			}
		
		}catch(Exception e) {
			e.printStackTrace();		
		}
		return retorno;
	}
	
	public boolean ativaMarca(Marca marca) {
		
		String comando = "UPDATE marcas "
				+ "SET status=?"
				+ " WHERE id=?";
		PreparedStatement m;
		
		int status = marca.getStatus(); //recebe o status da marca
	
		if(status==0) { //verifica com qual status a marca está e muda
			status = 1;
		}else {
			status = 0;
		}
		
		try {
			m = this.conexao.prepareStatement(comando);
			m.setInt(1, status);
			m.setInt(2, marca.getId());
			m.executeUpdate(); //implementa as alterações
		
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean verificaMarcaNome(Marca marca) {
		
		String comando = "SELECT * FROM marcas WHERE marcas.nome = ?";
		//String nome="";
		int id = 0;
		boolean retorno = true;

		try {
		
			PreparedStatement m = this.conexao.prepareStatement(comando);
			m.setString(1, marca.getNome());
			ResultSet rs = m.executeQuery();

			while(rs.next()) {
				
				//nome = rs.getString("nome"); 
				id = rs.getInt("id");
				
			}
			//System.out.println(id+"teste marcaNome");
			if(marca.getId() != id && id != 0) {//verifica se a marca selecionada existe no BD e se ela é a mesma já cadastrada (não alterou só clicou em salvar)
				retorno = false;
			}
		
		}catch(Exception e) {
			e.printStackTrace();	
		}
		return retorno;
	}
	
}

