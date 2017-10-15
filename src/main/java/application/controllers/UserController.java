package application.controllers;

import application.dao.user.UserDao;
import application.dao.user.UserDaoImpl;
import application.models.UserModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceContext;


@RestController
@RequestMapping(path = "/user")
@CrossOrigin(origins = "https://tower-defense.herokuapp.com")
public class UserController {

	private final UserDao userDao;

	UserController(UserDaoImpl userDaoImpl) {
		this.userDao = userDaoImpl;
	}

	@GetMapping(path = "/helloworld")
	public ResponseEntity hello(
			@PathVariable(name = "login") String login,
			@PathVariable(name = "password") String password,
			@PathVariable(name = "email") String email) {
		UserModel userModel = new UserModel();
		userModel.setEmail(email);
		userModel.setPassword(password);
		userModel.setUsername(login);
		userDao.createUser(userModel);

		return new ResponseEntity<>(
				userModel,
				HttpStatus.CREATED
		);
	}

//	private UserService users = new UserService();
//
//
//	@GetMapping(path = "/whoisit")
//	public ResponseEntity<SuccessResponse> whoisit(HttpSession httpSession) {
//
//		final Long id = (Long) httpSession.getAttribute("ID");
//		if (id == null) {
//			return new ResponseEntity<>(
//					HttpStatus.UNAUTHORIZED
//			);
//		}
//
//		final UserModel currentUser = users.findUserById(id);
//		if (currentUser == null) {
//			return new ResponseEntity<>(
//					HttpStatus.FORBIDDEN
//			);
//		}
//		return new ResponseEntity<SuccessResponse>(
//				new SuccessResponse(currentUser),
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
//			return new ResponseEntity<FailResponse>(
//					new FailResponse("Необходима авторизация"),
//					HttpStatus.UNAUTHORIZED
//			);
//		}
//
//		final UserModel currentUser = users.findUserById(id);
//		if (currentUser == null) {
//			return new ResponseEntity<FailResponse>(
//					new FailResponse("Профиль не найден"),
//					HttpStatus.GONE
//			);
//		}
//		if (!currentUser.getPassword().equals(parseBody.getOldPassword())) {
//			return new ResponseEntity<FailResponse>(
//					new FailResponse("Неверный пароль"),
//					HttpStatus.FORBIDDEN
//			);
//		}
//		if (!currentUser.getUsername().equals(parseBody.getUsername())) {
//			if (users.findUserByUsername(parseBody.getUsername()) != null) {
//				return new ResponseEntity<FailResponse>(
//						new FailResponse("Пользователь с таким именем уже существует!"),
//						HttpStatus.FORBIDDEN
//				);
//			}
//		}
//		if (!currentUser.getEmail().equals(parseBody.getEmail())) {
//			if (users.findUserByEmail(parseBody.getEmail()) != null) {
//				return new ResponseEntity<FailResponse>(
//						new FailResponse("Пользователь с такой почтой уже существует!"),
//						HttpStatus.FORBIDDEN
//				);
//			}
//		}
//		currentUser.updateProfile(parseBody);
//		return new ResponseEntity<SuccessResponse>(
//				new SuccessResponse(currentUser),
//				HttpStatus.OK
//		);
//	}
//
//	@PostMapping(path = "/sign_up", consumes = "application/json")
//	public ResponseEntity<?> signUp(
//			@RequestBody UserModel parseBody,
//			HttpSession httpSession) {
//
//
//		final String errorMessage = UserService.userValidation(parseBody);
//		if (errorMessage != null) {
//			return new ResponseEntity<FailResponse>(
//					new FailResponse(errorMessage),
//					HttpStatus.BAD_REQUEST
//			);
//		}
//		if (users.findUserByUsername(parseBody.getUsername()) != null) {
//			return new ResponseEntity<FailResponse>(
//					new FailResponse("Пользователь с таким логином уже существует"),
//					HttpStatus.BAD_REQUEST
//			);
//		}
//		if (users.findUserByEmail(parseBody.getEmail()) != null) {
//			return new ResponseEntity<FailResponse>(
//					new FailResponse("Пользователь с такой почтой уже существует"),
//					HttpStatus.BAD_REQUEST
//			);
//		}
//
//		httpSession.setAttribute("ID", users.addUser(parseBody));
//
//		return new ResponseEntity<SuccessResponse>(
//				new SuccessResponse(parseBody),
//				HttpStatus.CREATED
//		);
//	}
//
//	@PostMapping(path = "/sign_in", consumes = "application/json")
//	public ResponseEntity<?> signIn(
//			@RequestBody SignInUser parseBody,
//			HttpSession httpSession) {
//
//		final UserModel user = users.findUserByLogin(parseBody.getLogin());
//
//		if (user == null) {
//			httpSession.invalidate();
//			return new ResponseEntity<FailResponse>(
//					new FailResponse("Пользователя с таким логином не существует"),
//					HttpStatus.FORBIDDEN
//			);
//		}
//		if (!parseBody.getPassword().equals(user.getPassword())) {
//			httpSession.invalidate();
//			return new ResponseEntity<FailResponse>(
//					new FailResponse("Неверный логин или пароль"),
//					HttpStatus.FORBIDDEN
//			);
//		}
//		httpSession.setAttribute("ID", user.getId());
//		return new ResponseEntity<SuccessResponse>(
//				new SuccessResponse(user),
//				HttpStatus.OK
//		);
//	}

}
