package com.vindie.sunshine_ss.security.service;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.dto.Cread;
import com.vindie.sunshine_ss.account.dto.Device;
import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.security.record.JwtAuthenticationResponse;
import com.vindie.sunshine_ss.security.record.SignUpRequest;
import com.vindie.sunshine_ss.security.record.SigninRequest;
import com.vindie.sunshine_ss.security.record.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AccountRepo accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var account = Account.builder()
                .name(request.name)
                .lastPresence(LocalDateTime.now())
                .description(request.description)
                .gender(request.gender)
                .bday(request.bday)
                .lang(request.lang)
                .location(request.location)
                .deleted(false)
                .likes(0)
                .views(0)
                .rating(50D)
                .matchesNum(null)
                .premTill(null)
                .filter(null)
                .build();

        var cread = Cread.builder()
                .owner(account)
                .email(request.email)
                .pass(passwordEncoder.encode(request.getPassword()))
                .build();
        account.setCread(cread);

        var device = Device.builder()
                .owner(account)
                .uniqueId(request.uniqueId)
                .imei(request.imei)
                .wifiMac(request.wifiMac)
                .phoneNum(request.phoneNum)
                .appVersion(request.appVersion)
                .build();
        account.getDevices().add(device);

        account = accountRepo.save(account);

        var user = new User(account.getId(), account.getName(), account.getCread().getEmail(), account.getLang(),
                account.getGender(), account.getPremTill());
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email, request.pass));
        var user = accountRepo.findUserByEmail(request.email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }
}
