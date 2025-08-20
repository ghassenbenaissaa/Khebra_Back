package com.example.Khebra.service;

import com.example.Khebra.dto.ExpertDto;
import com.example.Khebra.entity.Expert;
import com.example.Khebra.entity.User;

public interface IUserService {

    User getUserEntityByEmail(String email);
    void BanUser(int userId);
    void UnBanUser(int userId);
}
