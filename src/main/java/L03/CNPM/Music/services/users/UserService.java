package L03.CNPM.Music.services.users;

import L03.CNPM.Music.DTOS.user.CreateUserDTO;
import L03.CNPM.Music.DTOS.user.ResetPasswordDTO;
import L03.CNPM.Music.DTOS.user.UserLoginDTO;
import L03.CNPM.Music.components.JwtTokenUtils;
import L03.CNPM.Music.components.LocalizationUtils;
import L03.CNPM.Music.exceptions.DataNotFoundException;
import L03.CNPM.Music.exceptions.ExpiredTokenException;
import L03.CNPM.Music.exceptions.PermissionDenyException;
import L03.CNPM.Music.exceptions.UploadCloudinaryException;
import L03.CNPM.Music.models.Role;
import L03.CNPM.Music.models.User;
import L03.CNPM.Music.repositories.UserRepository;
import L03.CNPM.Music.repositories.RoleRepository;
import L03.CNPM.Music.utils.MessageKeys;
import L03.CNPM.Music.utils.ValidationUtils;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final LocalizationUtils localizationUtils;
    private final JwtTokenUtils jwtTokenUtil;

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final Cloudinary cloudinary;
    private final ValidationUtils validationUtils;

    @Override
    public Page<User> findAll(String keyword, Pageable pageable) {
        return userRepository.findAll(keyword, pageable);
    }

    @Override
    public List<User> GetByIDs(List<Long> userIds) throws Exception {
        return (List<User>) userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public User Create(CreateUserDTO createUserDTO) throws Exception {
        if (!createUserDTO.getEmail().isBlank() && userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new DataIntegrityViolationException(
                    localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_EXISTED));
        }

        Role role = roleRepository.findByName(Role.LISTENER)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));
        if (role.getName().equalsIgnoreCase(Role.ADMIN)) {
            throw new PermissionDenyException("Registering admin accounts is not allowed");
        }

        String password = createUserDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(password);

        User newUser = User.builder()
                .email(createUserDTO.getEmail())
                .username(createUserDTO.getUsername())
                .password(encodedPassword)
                .country(validationUtils.convertAndUpperCase(createUserDTO.getCountry()))
                .dateOfBirth(Date.valueOf(createUserDTO.getDateOfBirth()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .role(role)
                .build();

        return userRepository.save(newUser);
    }

    @Override
    public String Login(UserLoginDTO userLoginDTO) throws Exception {
        Role role = roleRepository.findById(userLoginDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));

        Optional<User> optionalUser = Optional.empty();
        String subject = null;

        if (userLoginDTO.getEmail() != null) {
            optionalUser = userRepository.findByEmail(userLoginDTO.getEmail());
            subject = userLoginDTO.getEmail();
        }
        if (optionalUser.isEmpty() && userLoginDTO.getUsername() != null) {
            optionalUser = userRepository.findByUsername(userLoginDTO.getUsername());
            subject = userLoginDTO.getUsername();
        }
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_EMAIL));
        }

        User existingUser = optionalUser.get();
        if (!existingUser.getIsActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }

        if (!role.getId().equals(existingUser.getRole().getId())) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_EMAIL));
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                subject,
                userLoginDTO.isPasswordBlank() ? "" : userLoginDTO.getPassword(),
                existingUser.getAuthorities());

        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public User Detail(Long userId) throws Exception {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_DOES_NOT_EXISTS)));
    }

    @Override
    @Transactional
    public User UpdateToArtist(Long userId) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_DOES_NOT_EXISTS)));
        existingUser.setRole(roleRepository.findByName(Role.ARTIST)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS))));
        return userRepository.save(existingUser);
    }

    @Override
    public User GetUserDetailByToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException(localizationUtils.getLocalizedMessage(MessageKeys.TOKEN_EXPIRED));
        }

        String subject = jwtTokenUtil.getSubject(token);
        Optional<User> user;
        user = userRepository.findByEmail(subject);
        if (user.isEmpty()) {
            user = userRepository.findByUsername(subject);
        }
        return user.orElseThrow(() -> new Exception(localizationUtils.getLocalizedMessage(MessageKeys.USER_EXISTED)));
    }

    @Override
    public User ResetPassword(Long userId, ResetPasswordDTO resetPasswordDTO) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if (resetPasswordDTO.getCurrentPassword() != null
                && !resetPasswordDTO.getCurrentPassword().isEmpty()) {
            if (!passwordEncoder.matches(resetPasswordDTO.getCurrentPassword(), existingUser.getPassword())) {
                throw new DataNotFoundException("Current password is incorrect");
            }
        }

        if (resetPasswordDTO.getNewPassword() != null
                && !resetPasswordDTO.getNewPassword().isEmpty()) {
            if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getRetypeNewPassword())) {
                throw new DataNotFoundException("Password and retype password not the same");
            }
            String newPassword = resetPasswordDTO.getNewPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        existingUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public User BlockOrEnable(Long userId, Boolean active) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_DOES_NOT_EXISTS)));
        existingUser.setIsActive(active);
        return userRepository.save(existingUser);
    }

    @SuppressWarnings("unchecked")
    @Override
    public User UpdateImageProfile(Long userId, MultipartFile file) throws Exception {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_EXISTED));
        }

        User existingUser = optionalUser.get();
        String oldFileId = existingUser.getImageUrl();

        Map<String, Object> response;
        try {
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "profileimages");

            response = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        } catch (Exception e) {
            throw new UploadCloudinaryException(
                    localizationUtils.getLocalizedMessage(MessageKeys.CLOUDINARY_UPLOAD_FAIL) + ": " + e.getMessage());
        }

        if (response == null || response.isEmpty()) {
            throw new UploadCloudinaryException(
                    localizationUtils.getLocalizedMessage(MessageKeys.CLOUDINARY_UPLOAD_FAIL));
        }

        existingUser.setImageUrl((String) response.get("url"));
        userRepository.save(existingUser);

        if (oldFileId != null && !oldFileId.isEmpty()) {
            cloudinary.uploader().destroy(oldFileId, ObjectUtils.asMap("resource_type", "image"));
        }

        return existingUser;
    }
}
