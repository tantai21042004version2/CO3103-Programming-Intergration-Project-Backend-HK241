package L03.CNPM.Music.controllers;

import L03.CNPM.Music.DTOS.user.CreateUserDTO;
import L03.CNPM.Music.DTOS.user.ResetPasswordDTO;
import L03.CNPM.Music.DTOS.user.UserLoginDTO;
import L03.CNPM.Music.components.JwtTokenUtils;
import L03.CNPM.Music.components.LocalizationUtils;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.responses.ResponseObject;
import L03.CNPM.Music.responses.users.LoginResponse;
import L03.CNPM.Music.responses.users.UserDetailResponse;
import L03.CNPM.Music.responses.users.UserListResponse;
import L03.CNPM.Music.responses.users.UserResponse;
import L03.CNPM.Music.services.users.IUserService;
import L03.CNPM.Music.utils.MessageKeys;
import L03.CNPM.Music.utils.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
        private final LocalizationUtils localizationUtils;
        private final IUserService userService;
        private final ValidationUtils validationUtils;
        private final JwtTokenUtils jwtTokenUtils;

        // ENDPOINT: {{API_PREFIX}}/users [GET]
        // GET ALL USERS IN SYSTEM, USE BEFORE LOGIN
        // HEADERS: AUTHENTICATION: YES (ONLY ADMIN CAN ACCESS)
        // PARAMS:
        // keyword: String
        // page: int
        // limit: int
        /*
         * RESPONSE:
         * {
         * "message": "Get user list successfully",
         * "status": "200 OK",
         * "data": {
         * ...
         * }
         * }
         */
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @GetMapping("")
        public ResponseEntity<ResponseObject> Get(
                        @RequestParam(defaultValue = "", required = false) String keyword,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int limit) {
                if (page < 1) {
                        page = 1;
                }
                PageRequest pageRequest = PageRequest.of(
                                page - 1, limit,
                                Sort.by("id").ascending());
                Page<UserResponse> userPage = userService.findAll(keyword, pageRequest)
                                .map(UserResponse::fromUser);

                int totalPages = userPage.getTotalPages();

                int currentPage = userPage.getNumber() + 1;

                int itemsPerPage = userPage.getSize();
                List<UserResponse> userResponses = userPage.getContent();
                UserListResponse userListResponse = UserListResponse
                                .builder()
                                .users(userResponses)
                                .totalPages(totalPages)
                                .currentPage(currentPage)
                                .itemsPerPage(itemsPerPage)
                                .build();

                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("Get user list successfully")
                                .status(HttpStatus.OK)
                                .data(userListResponse)
                                .build());
        }

        // ENDPOINT: {{API_PREFIX}}/users/register [POST]
        // CREATE NEW USER
        // HEADERS: NO AUTHENTICATION
        // BODY:
        // createUserDTO: CreateUserDTO
        /*
         * RESPONSE:
         * {
         * "message": "Create user successfully",
         * "status": "200 OK",
         * "data": {
         * id, email, username, role, createdAt, updatedAt
         * }
         * }
         */
        @PostMapping("/register")
        public ResponseEntity<ResponseObject> Create(
                        @Valid @RequestBody CreateUserDTO createUserDTO,
                        BindingResult result) throws Exception {
                if (result.hasErrors()) {
                        List<String> errorMessages = result.getFieldErrors()
                                        .stream()
                                        .map(FieldError::getDefaultMessage)
                                        .toList();

                        return ResponseEntity.badRequest().body(ResponseObject.builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .message(errorMessages.toString())
                                        .build());
                }

                if (createUserDTO.getEmail() == null || createUserDTO.getEmail().trim().isBlank()) {
                        return ResponseEntity.badRequest().body(ResponseObject.builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .message("Email is required")
                                        .build());
                } else {
                        if (!validationUtils.isValidEmail(createUserDTO.getEmail())) {
                                throw new Exception("Invalid email format");
                        }
                }

                if (createUserDTO.getUsername() == null || createUserDTO.getUsername().trim().isBlank()
                                || createUserDTO.getUsername().trim().contains(" ")) {
                        return ResponseEntity.badRequest().body(ResponseObject.builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .message("Username is required")
                                        .build());
                }

                if (!createUserDTO.getPassword().equals(createUserDTO.getRetypePassword())) {
                        return ResponseEntity.badRequest().body(ResponseObject.builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                                        .build());
                }

                User newUser = userService.Create(createUserDTO);
                return ResponseEntity.ok(ResponseObject.builder()
                                .status(HttpStatus.CREATED)
                                .data(UserDetailResponse.fromUser(newUser))
                                .message(MessageKeys.REGISTER_SUCCESSFULLY)
                                .build());
        }

        // ENDPOINT: {{API_PREFIX}}/users/login [POST]
        // LOGIN USER
        // HEADERS: NO AUTHENTICATION
        // PARAMS:
        // userLoginDTO: UserLoginDTO
        /*
         * RESPONSE:
         * {
         * "message": "Login successfully",
         * "status": "200 OK",
         * "data": {
         * token, tokenType
         * }
         * }
         */
        @PostMapping("/login")
        public ResponseEntity<ResponseObject> Login(
                        @Valid @RequestBody UserLoginDTO userLoginDTO,
                        HttpServletRequest request) throws Exception {
                try {
                        String token = userService.Login(userLoginDTO);

                        LoginResponse loginResponse = LoginResponse.builder()
                                        .token(token)
                                        .tokenType("Bearer")
                                        .build();

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Login successfully")
                                        .data(loginResponse)
                                        .status(HttpStatus.OK)
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(ResponseObject.builder()
                                        .message("Invalid username or password")
                                        .status(HttpStatus.BAD_REQUEST)
                                        .build());
                }
        }

        // ENDPOINT: {{API_PREFIX}}/users/details [GET]
        // GET USER DETAILS
        // HEADERS: AUTHENTICATION: YES
        /*
         * RESPONSE:
         * {
         * "message": "Get user's detail successfully",
         * "status": "200 OK",
         * "data": {
         * id, email, username, role, createdAt, updatedAt
         * }
         * }
         */
        @GetMapping("/details")
        @PreAuthorize("hasRole('ROLE_LISTENER') or hasRole('ROLE_ARTIST') or hasRole('ROLE_ADMIN')")
        public ResponseEntity<ResponseObject> Detail(
                        @RequestHeader("Authorization") String authorizationHeader) throws Exception {
                try {
                        String extractedToken = authorizationHeader.substring(7);
                        User user = userService.GetUserDetailByToken(extractedToken);

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Get user's detail successfully")
                                        .data(UserDetailResponse.fromUser(user))
                                        .status(HttpStatus.OK)
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(ResponseObject.builder()
                                        .message("Unauthorized")
                                        .status(HttpStatus.UNAUTHORIZED)
                                        .build());
                }
        }

        // ENDPOINT: {{API_PREFIX}}/users/upload-profile-image [POST]
        // UPLOAD PROFILE IMAGE
        // HEADERS: AUTHENTICATION: YES
        // PARAMS:
        // file: MultipartFile
        /*
         * RESPONSE:
         * {
         * "message": "Update profile image successfully",
         * "status": "200 OK",
         * "data": {
         * id, email, username, role, createdAt, updatedAt
         * }
         * }
         */
        @PreAuthorize("hasRole('ROLE_LISTENER') or hasRole('ROLE_ARTIST') or hasRole('ROLE_ADMIN')")
        @PostMapping(value = "/upload-profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ResponseObject> UploadProfileImageUser(
                        @RequestParam("file") MultipartFile file,
                        @RequestHeader("Authorization") String authorizationHeader) throws Exception {
                try {
                        String extractedToken = authorizationHeader.substring(7);
                        Long userId = Long.parseLong(jwtTokenUtils.getUserId(extractedToken));

                        if (file == null || file.isEmpty()) {
                                return ResponseEntity.badRequest().body(ResponseObject.builder()
                                                .message(localizationUtils.getLocalizedMessage(
                                                                MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE))
                                                .build());
                        }

                        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                                return ResponseEntity.badRequest().body(ResponseObject.builder()
                                                .message(localizationUtils.getLocalizedMessage(
                                                                MessageKeys.UPLOAD_IMAGES_FILE_LARGE))
                                                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                                                .build());
                        }

                        if (!isImageFile(file)) {
                                return ResponseEntity.badRequest().body(ResponseObject.builder()
                                                .message("Uploaded file must be an image.")
                                                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                                .build());
                        }

                        User updatedUser = userService.UpdateImageProfile(userId, file);
                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Update profile image successfully")
                                        .data(UserDetailResponse.fromUser(updatedUser))
                                        .status(HttpStatus.OK)
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(ResponseObject.builder()
                                        .message("Unauthorized")
                                        .status(HttpStatus.UNAUTHORIZED)
                                        .build());
                }
        }

        // ENDPOINT: {{API_PREFIX}}/users/reset-password [PATCH]
        // RESET PASSWORD
        // HEADERS: AUTHENTICATION: YES
        // PARAMS:
        // resetPasswordDTO: ResetPasswordDTO
        /*
         * RESPONSE:
         * {
         * "message": "Update user detail successfully",
         * "status": "200 OK",
         * "data": {
         * id, email, username, role, createdAt, updatedAt
         * }
         * }
         */
        @PreAuthorize("hasRole('ROLE_LISTENER') or hasRole('ROLE_ARTIST')")
        @PatchMapping("/reset-password")
        public ResponseEntity<ResponseObject> ResetPassword(
                        @Valid @RequestBody ResetPasswordDTO resetPasswordDTO,
                        @RequestHeader("Authorization") String authorizationHeader) throws Exception {
                try {
                        String extractedToken = authorizationHeader.substring(7);
                        Long userId = Long.parseLong(jwtTokenUtils.getUserId(extractedToken));

                        User updatedUser = userService.ResetPassword(userId, resetPasswordDTO);
                        return ResponseEntity.ok().body(
                                        ResponseObject.builder()
                                                        .message("Update user detail successfully")
                                                        .data(UserDetailResponse.fromUser(updatedUser))
                                                        .status(HttpStatus.OK)
                                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(ResponseObject.builder()
                                        .message("Unauthorized")
                                        .status(HttpStatus.UNAUTHORIZED)
                                        .build());
                }
        }

        // ENDPOINT: {{API_PREFIX}}/users/update-role/{userId} [PATCH]
        // UPDATE USER ROLE TO ARTIST
        // HEADERS: AUTHENTICATION: YES (ONLY ADMIN CAN ACCESS)
        // PARAMS:
        // userId: long
        /*
         * RESPONSE:
         * {
         * "message": "Update user to artist successfully",
         * "status": "200 OK",
         * "data": {
         * id, email, username, role, createdAt, updatedAt
         * }
         * }
         */
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @PatchMapping("/update-role/{userId}")
        public ResponseEntity<ResponseObject> UpdateToArtist(
                        @Valid @PathVariable long userId) throws Exception {
                try {
                        User updatedUser = userService.UpdateToArtist(userId);
                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Update user to artist successfully")
                                        .status(HttpStatus.OK)
                                        .data(UserDetailResponse.fromUser(updatedUser))
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(ResponseObject.builder()
                                        .message("Unauthorized")
                                        .status(HttpStatus.UNAUTHORIZED)
                                        .build());
                }
        }

        // ENDPOINT: {{API_PREFIX}}/users/block/{userId}/{active} [PATCH]
        // BLOCK OR ENABLE USER
        // HEADERS: AUTHENTICATION: YES (ONLY ADMIN CAN ACCESS)
        // PARAMS:
        // userId: long
        // active: int
        /*
         * RESPONSE:
         * {
         * "message": "Successfully enabled the user.",
         * "status": "200 OK",
         * "data": {
         * id, email, username, role, createdAt, updatedAt
         * }
         * }
         */
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @PatchMapping("/block/{userId}/{active}")
        public ResponseEntity<ResponseObject> BlockOrEnable(
                        @Valid @PathVariable long userId,
                        @Valid @PathVariable int active) throws Exception {
                User updatedUser = userService.BlockOrEnable(userId, active > 0);
                String message = active > 0 ? "Successfully enabled the user." : "Successfully blocked the user.";
                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message(message)
                                .status(HttpStatus.OK)
                                .data(UserResponse.fromUser(updatedUser))
                                .build());
        }

        public static boolean isImageFile(MultipartFile file) {
                return true;
                /*
                 * String contentType = file.getContentType();
                 * return contentType != null && contentType.startsWith("image/");
                 */
                /*
                 * AutoDetectParser parser = new AutoDetectParser();
                 * Detector detector = parser.getDetector();
                 * try {
                 * Metadata metadata = new Metadata();
                 * TikaInputStream stream = TikaInputStream.get(file.getInputStream());
                 * MediaType mediaType = detector.detect(stream, metadata);
                 * String mimeType = mediaType.toString();
                 * } catch (IOException e) {
                 * return false;
                 * }
                 */
        }
}