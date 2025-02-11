package com.example.movies_recommendation_API.users;

import com.example.movies_recommendation_API.Jwt.JwtService;
import com.example.movies_recommendation_API.oauth2.GoogleAuthService;
import com.example.movies_recommendation_API.response.LoginResponseSuccess;
import com.example.movies_recommendation_API.response.ResponseError;
import com.example.movies_recommendation_API.response.ResponseSuccess;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private GoogleAuthService googleAuthService;

    @GetMapping("")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<?> postRegisterUser(@Valid @RequestBody UserCreateDTO user) {
        return userService.createUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> postLogin(@RequestBody User loginRequest) {
        try {
            // Xác thực thông tin đăng nhập
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            // Tải thông tin người dùng
            User user = userService.getUserByUsername(loginRequest.getUsername());

            // Tạo JWT token
            String token = jwtService.generateToken(user);
            LoginResponseSuccess res = new LoginResponseSuccess();
            res.setUsername(user.getUsername());
            res.setToken(token);
            return ResponseEntity.ok().body(res);
        } catch (AuthenticationException e) {
            ResponseError error = new ResponseError(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            // Lấy thông tin từ SecurityContext
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ResponseSuccess res = new ResponseSuccess(user);

            // Trả về thông tin người dùng
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            ResponseError error = new ResponseError(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> postRequestSendOTP (@RequestBody Map<String, String> body) throws MessagingException {
        String email = body.get("email");
        return userService.createAndSendOTP(email);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<?>  postRequestChangePassword (@RequestBody Map<String, String> body) {
        if (body.get("email") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được email.")
            );
        }
        if (body.get("password") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được password.")
            );
        }
        if (body.get("otp") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được OTP.")
            );
        }
        return userService.validateOtpAndResetPassword(body.get("email"), body.get("otp"), body.get("password"));
    }

    @PatchMapping("/active-account")
    public ResponseEntity<?>  postRequestActiveUser (@RequestBody Map<String, String> body) {
        if (body.get("email") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được email.")
            );
        }
        if (body.get("password") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được password.")
            );
        }
        if (body.get("otp") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseError("Không nhận được OTP.")
            );
        }
        return userService.validateOtpAndActiveUser(body.get("email"), body.get("password"), body.get("otp"));
    }
}
