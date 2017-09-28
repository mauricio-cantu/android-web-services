package exemplo.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by mauriciocantu on 27/07/17.
 */

@Entity
public class Usuario implements Serializable{
	
    private String nome;
    @Column(unique=true)
    private String login;
    private String senha;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;      
    
	public Usuario(String nome, String login, String senha) {
		super();
		this.nome = nome;
		this.login = login;
		this.senha = senha;
	}
	
	public Usuario() {
		
	}

	public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	} 
    
}
