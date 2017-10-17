package application.controllers;

import application.models.UserModel;
import application.services.UserServiceAbstract;
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

//	@GetMapping(path = "/whoisit")
//	public ResponseEntity<UserSuccess> whoisit(HttpSession httpSession) {
//
//		final Long id = (Long) httpSession.getAttribute("ID");
//		if (id == null) {
//			return new ResponseEntity<>(
//					HttpStatus.UNAUTHORIZED
//			);
//		}
//
//		final UserModel currentUser = users.getUserById(id);
//		if (currentUser == null) {
//			return new ResponseEntity<>(
//					HttpStatus.FORBIDDEN
//			);
//		}
//		return new ResponseEntity<UserSuccess>(
//				new UserSuccess(currentUser),
//				HttpStatus.OK
//		);
//	}
//
//	@GetMapping(path = "/exit")
//	public ResponseEntity exit(HttpSession httpSession) {
//
//		httpSession.invalidate();
//		return new ResponseEntity(HttpStatus.OK);
//	}
//
//	@PostMapping(path = "/update", consumes = "application/json")
//	public ResponseEntity<?> settings(
//			@RequestBody UpdateUser parseBody,
//			HttpSession httpSession) {
//
//		final Long id = (Long) httpSession.getAttribute("ID");
//		if (id == null) {
//			return new ResponseEntity<UserFail>(
//					new UserFail("Необходима авторизация"),
//					HttpStatus.UNAUTHORIZED
//			);
//		}
//
//		final UserModel currentUser = users.getUserById(id);
//		if (currentUser == null) {
//			return new ResponseEntity<UserFail>(
//					new UserFail("Профиль не найден"),
//					HttpStatus.GONE
//			);
//		}
//		if (!currentUser.getPassword().equals(parseBody.getOldPassword())) {
//			return new ResponseEntity<UserFail>(
//					new UserFail("Неверный пароль"),
//					HttpStatus.FORBIDDEN
//			);
//		}
//		if (!currentUser.getUsername().equals(parseBody.getUsername())) {
//			if (users.findUserByUsername(parseBody.getUsername()) != null) {
//				return new ResponseEntity<UserFail>(
//						new UserFail("Пользователь с таким именем уже существует!"),
//						HttpStatus.FORBIDDEN
//				);
//			}
//		}
//		if (!currentUser.getEmail().equals(parseBody.getEmail())) {
//			if (users.getUserByEmail(parseBody.getEmail()) != null) {
//				return new ResponseEntity<UserFail>(
//						new UserFail("Пользователь с такой почтой уже существует!"),
//						HttpStatus.FORBIDDEN
//				);
//			}
//		}
//		currentUser.updateProfile(parseBody);
//		return new ResponseEntity<UserSuccess>(
//				new UserSuccess(currentUser),
//				HttpStatus.OK
//		);
//	}
//

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
		catch (UserServiceAbstract.UserServiceException e) {
			e.printStackTrace();
			return new ResponseEntity<>(
					new UserFail(e.getErrorMessage()),
					e.getErrorCode()
			);
		}
	}

//	@PostMapping(path = "/sign_in", consumes = "application/json")
//	public ResponseEntity<?> signIn(
//			@RequestBody SignInUser parseBody,
//			HttpSession httpSession) {
//
//		final UserModel user = users.getUserByLogin(parseBody.getLogin());
//
//		if (user == null) {
//			httpSession.invalidate();
//			return new ResponseEntity<UserFail>(
//					new UserFail("Пользователя с таким логином не существует"),
//					HttpStatus.FORBIDDEN
//			);
//		}
//		if (!parseBody.getPassword().equals(user.getPassword())) {
//			httpSession.invalidate();
//			return new ResponseEntity<UserFail>(
//					new UserFail("Неверный логин или пароль"),
//					HttpStatus.FORBIDDEN
//			);
//		}
//		httpSession.setAttribute("ID", user.getId());
//		return new ResponseEntity<UserSuccess>(
//				new UserSuccess(user),
//				HttpStatus.OK
//		);
//	}

}
