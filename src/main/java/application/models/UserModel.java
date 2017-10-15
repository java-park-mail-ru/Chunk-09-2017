package application.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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


	public void updateProfile(final UserModel newProfile) {

		if (!newProfile.username.isEmpty()) {
			this.username = newProfile.username;
		}

		if (!newProfile.email.isEmpty()) {
			this.email = newProfile.email;
		}

		if (!newProfile.password.isEmpty()) {
			this.password = newProfile.password;
		}
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

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
