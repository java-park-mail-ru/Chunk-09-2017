package application.entities;

import application.models.UserModel;

import javax.persistence.*;


@Entity
@Table(name = "users")
public class UserEntity {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "jpa_seq_generator")
	@SequenceGenerator(name = "jpa_seq_generator", sequenceName = "users_id_seq")
	private Long id;

	@Column(name = "login", unique = true, nullable = false, length = 40)
	private String login;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "email", unique = true, nullable = false, length = 50)
	private String email;

	public UserEntity() {}

	public UserEntity(UserModel userModel) {
		this.login = userModel.getUsername();
		this.password = userModel.getPassword();
		this.email = userModel.getEmail();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
