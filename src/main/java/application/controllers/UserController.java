package application.controllers;

import application.models.SignInModel;
import application.models.UpdateUser;
import application.models.UserModel;
import application.services.UserServiceAbstract;
import application.services.UserServiceAbstract.UserServiceException;
import application.services.UserServiceJpa;
import application.views.UserFail;
import application.views.UserSuccess;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@RestController
@RequestMapping(path = "/user")
@CrossOrigin(origins = "*")
public class UserController {

	private final UserServiceAbstract service;

	UserController(UserServiceJpa service) {
		this.service = service;
	}

	@GetMapping(path = "/whoisit")
	public ResponseEntity whoisit(HttpSession httpSession) {
		final Long id = (Long) httpSession.getAttribute("ID");
		if (id == null) {
			return new ResponseEntity<>(
					new UserFail("Need to sign up"),
					HttpStatus.UNAUTHORIZED
			);
		}
		try {
			final UserModel currentUser = service.getUserById(id);
			return new ResponseEntity<>(
					new UserSuccess(currentUser),
					HttpStatus.OK
			);
		}
		catch (UserServiceException e) {
			e.printStackTrace();
			return new ResponseEntity<>(
					new UserFail(e.getErrorMessage()),
					e.getErrorCode()
			);
		}
	}

	@GetMapping(path = "/exit")
	public ResponseEntity exit(HttpSession httpSession) {
		httpSession.invalidate();
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping(path = "/update", consumes = "application/json")
	public ResponseEntity settings(
			@RequestBody UpdateUser userUpdate,
			HttpSession httpSession) {

		final Long id = (Long) httpSession.getAttribute("ID");
		if (id == null) {
			return new ResponseEntity<>(
					new UserFail("Need to sign up"),
					HttpStatus.UNAUTHORIZED
			);
		}
		try {
			final UserModel userUpdated = service.updateUserProfile(userUpdate, id);
			httpSession.setAttribute("ID", userUpdated.getId());
			return new ResponseEntity<>(
					new UserSuccess(userUpdated),
					HttpStatus.OK
			);
		}
		catch (UserServiceException e) {
			e.printStackTrace();
			return new ResponseEntity<>(
					new UserFail(e.getErrorMessage()),
					e.getErrorCode()
			);
		}
	}

	@PostMapping(path = "/sign_up", consumes = "application/json")
	public ResponseEntity signUp(
			@RequestBody UserModel user,
			HttpSession httpSession) {

		try {
			httpSession.setAttribute("ID", service.addUser(user));
			return new ResponseEntity<>(
					new UserSuccess(user),
					HttpStatus.CREATED
			);
		}
		catch (UserServiceException e) {
			e.printStackTrace();
			return new ResponseEntity<>(
					new UserFail(e.getErrorMessage()),
					e.getErrorCode()
			);
		}
	}

	@PostMapping(path = "/sign_in", consumes = "application/json")
	public ResponseEntity signIn(
			@RequestBody SignInModel parseBody,
			HttpSession httpSession) {

		try {
			final UserModel user = service.signInByLogin(
					parseBody.getLogin(),
					parseBody.getPassword()
			);
			httpSession.setAttribute("ID", user.getId());
			return new ResponseEntity<>(
					new UserSuccess(user),
					HttpStatus.OK
			);
		} catch (UserServiceException e) {
			e.printStackTrace();
			return new ResponseEntity<>(
					new UserFail(e.getErrorMessage()),
					e.getErrorCode()
			);
		}
	}

}
