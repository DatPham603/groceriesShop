package com.project.shopapp.Services;

import com.project.shopapp.Components.JwtUtils;
import com.project.shopapp.DTO.UserDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Exception.PermissionDeniedException;
import com.project.shopapp.Models.Role;
import com.project.shopapp.Models.User;
import com.project.shopapp.Repositories.RoleRepository;
import com.project.shopapp.Repositories.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    @Override
    public User createUser(UserDTO userDTO) throws Exception{
        String phoneNumber = userDTO.getPhoneNumber();
        if(userRepository.existsByPhoneNumber(phoneNumber)){
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        if(!userDTO.getRetypePassword().equals(userDTO.getPassword())){
            throw new DataIntegrityViolationException("Password does not match");
        }
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();

            Role role = roleRepository.findById(userDTO.getRoleId()).orElseThrow(() -> new DataNotFoundException("Data not found"));
            if(role.getName().equals(Role.ADMIN)){
                throw new PermissionDeniedException("You cannot create Admin account");
            }
            newUser.setRole(role);
        if(userDTO.getGoogleAccountId() == 0 && userDTO.getFacebookAccountId() == 0){
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password) throws Exception {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if(user.isEmpty()){
            throw new DataNotFoundException("Invalid phone number/password");
        }
        if(user.get().getGoogleAccountId() == 0 && user.get().getFacebookAccountId() == 0){
            if(!passwordEncoder.matches(password,user.get().getPassword())){
                throw new BadCredentialsException("Wrong phone number / password");
            }
        }
        //UsernamePasswordAuthenticationToken là một lớp trong Spring Security,
        // đại diện cho thông tin xác thực của người dùng khi đăng nhập vào hệ thống.
//trc khi xác thực :nó được tạo ra và được truyền vào AuthenticationManager
// để xác thực.(chỉ chứa username và password-còn gọi là principal và credentials)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber,password,user.get().getAuthorities()
                //getAuthorities()trả về một Collection<? extends GrantedAuthority>, chứa các quyền của người dùng.
        );
        authenticationManager.authenticate(authenticationToken);
//AuthenticationManager chịu trách nhiệm xác thực người dùng khi họ đăng nhập,
//Sau khi xác thực: Sau khi xác thực thành công,UsernamePasswordAuthenticationToken sẽ chứa thêm
// các thông tin về quyền (authorities) của người dùng,mật khẩu (credentials) có thể bị bỏ trống vì người dùng đã được xác thực
        return jwtUtils.generateToken(user.get()); // trả về token
    }

    @Override
    public User getUserDetailFromToken(String token) throws Exception {
        if(jwtUtils.tokenExpired(token)){
            throw new Exception("Token expired");
        }
        String phoneNumber = jwtUtils.extractClaims(token, Claims::getSubject);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if(user.isPresent()){
            return user.get();
        }else {
            throw new Exception("User not found");
        }
    }
}
