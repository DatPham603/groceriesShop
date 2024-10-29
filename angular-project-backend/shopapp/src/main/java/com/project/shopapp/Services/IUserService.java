package com.project.shopapp.Services;

import com.project.shopapp.DTO.UserDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Models.User;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password) throws Exception;
    User getUserDetailFromToken(String token) throws Exception;
}
