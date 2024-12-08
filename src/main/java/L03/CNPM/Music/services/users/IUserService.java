package L03.CNPM.Music.services.users;

import L03.CNPM.Music.DTOS.user.CreateUserDTO;
import L03.CNPM.Music.DTOS.user.ResetPasswordDTO;
import L03.CNPM.Music.DTOS.user.UserLoginDTO;
import L03.CNPM.Music.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IUserService {
    Map<String, Object> AdminDashboard() throws Exception;

    Page<User> findAll(String keyword, Pageable pageable);

    List<User> GetByIDs(List<Long> userIds) throws Exception;

    String Login(UserLoginDTO userLoginDTO) throws Exception;

    User Create(CreateUserDTO createUserDTO) throws Exception;

    User Detail(Long userId) throws Exception;

    User UpdateToArtist(Long userId) throws Exception;

    User GetUserDetailByToken(String token) throws Exception;

    User ResetPassword(Long userId, ResetPasswordDTO resetPasswordDTO) throws Exception;

    User BlockOrEnable(Long userId, Boolean active) throws Exception;

    User UpdateImageProfile(Long userId, MultipartFile file) throws Exception;
}