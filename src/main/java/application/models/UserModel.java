package application.models;

import application.entities.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;


@Service
public class UserModel {

	@JsonProperty("username")
	private String username;

	@JsonProperty("email")
	private String email;

	@JsonProperty(value = "password")
	private String password;

	private Long id;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UserModel)) return false;

		final UserModel userModel = (UserModel) o;
		return (
				username.equals(userModel.username) &&
				password.equals(userModel.password) &&
				email.equals(userModel.email)
		);
	}

	@Override
	public int hashCode() {
		int result = username != null ? username.hashCode() : 0;
		result = 31 * result + (email != null ? email.hashCode() : 0);
		result = 31 * result + (password != null ? password.hashCode() : 0);
		result = 31 * result + (id != null ? id.hashCode() : 0);
		return result;
	}

	public UserModel() {}

	public UserModel(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public UserModel(UserEntity userEntity) {
		this.username = userEntity.getLogin();
		this.email = userEntity.getEmail();
		this.password = userEntity.getPassword();
		this.id = userEntity.getId();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
