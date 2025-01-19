package com.zacharia.applicatiofybe.service;

import com.zacharia.applicatiofybe.dto.UpdateUserRequestDTO;
import com.zacharia.applicatiofybe.dto.UpdateUserResponseDTO;
import com.zacharia.applicatiofybe.entity.Account;
import com.zacharia.applicatiofybe.repository.AccountRepository;
import com.zacharia.applicatiofybe.util.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AccountService{

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    public UpdateUserResponseDTO updateUser(String username, UpdateUserRequestDTO updateUserRequestDTO){
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() ->  new RuntimeException("User not found."));

        if(updateUserRequestDTO.getFirstName() != null && !updateUserRequestDTO.getFirstName().isEmpty()){
            account.setFirstName(updateUserRequestDTO.getFirstName());
        }
        if(updateUserRequestDTO.getLastName() != null && !updateUserRequestDTO.getLastName().isEmpty()){
            account.setLastName(updateUserRequestDTO.getLastName());
        }

        if(updateUserRequestDTO.getPassword() != null && !updateUserRequestDTO.getPassword().isEmpty()){
            account.setPassword(passwordEncoder.encode(updateUserRequestDTO.getPassword()));
        }
        accountRepository.save(account);

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

        String token = jwtUtil.generateToken(userDetails);
        return new UpdateUserResponseDTO(account.getId(),account.getFirstName(),account.getLastName(),account.getUsername(),token);
    }

    public void deleteAccount(String username){
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        accountRepository.delete(account);
    }


}
