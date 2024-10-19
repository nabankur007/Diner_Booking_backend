package com.diner.backend.service.serviceimpl;

import com.diner.backend.enitiy.Users;
import com.diner.backend.repository.UsersRepo;
import com.diner.backend.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    UsersRepo usersRepo;

    @Override
    public void saveUser(Users user) {
        usersRepo.save(user);
    }
}
