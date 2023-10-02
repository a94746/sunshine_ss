package com.vindie.sunshine_ss.security.service;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.dto.Cread;
import com.vindie.sunshine_ss.account.dto.Device;
import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.account.repo.CreadRepo;
import com.vindie.sunshine_ss.account.repo.DeviceRepo;
import com.vindie.sunshine_ss.location.LocationRepo;
import com.vindie.sunshine_ss.security.record.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.util.StringUtils.hasLength;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AccountRepo accountRepo;
    private final LocationRepo locationRepo;
    private final DeviceRepo deviceRepo;
    private final CreadRepo creadRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var account = Account.builder()
                .name(request.name)
                .lastPresence(LocalDateTime.now())
                .description(request.description)
                .gender(request.gender)
                .bday(request.bday)
                .lang(request.lang)
                .location(locationRepo.findByName(request.locationName).orElseThrow())
                .deleted(false)
                .likes(0)
                .views(0)
                .rating(50D)
                .matchesNum((byte) 0)
                .premMatchesNum(null)
                .premTill(null)
                .filter(null)
                .devices(new ArrayList<>())
                .build();

        var cread = Cread.builder()
                .owner(account)
                .email(request.email)
                .pass(passwordEncoder.encode(request.getPassword()))
                .build();
        account.setCread(cread);

        Device device = new Device();
        var deviceOpt = deviceRepo.findFirstByUniqueId(request.uniqueId);
        if (deviceOpt.isPresent())
            device = deviceOpt.get();
        device.setOwner(account);
        device.setUniqueId(request.uniqueId);
        device.setImei(request.imei);
        device.setWifiMac(request.wifiMac);
        device.setPhoneNum(request.phoneNum);
        device.setAppVersion(request.appVersion);
        account.getDevices().add(device);

        account = accountRepo.save(account);
        var user = new User(account.getId(), account.getName(), account.getCread().getEmail(), account.getCread().getPass(),
                account.getLang(), account.getGender(), account.getPremTill());
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        User user = null;
        if (hasLength(request.uniqueId)) {
            user = deviceRepo.findUserByUniqueId(request.uniqueId)
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid uniqueId"));
            if (user == null)
                throw new UsernameNotFoundException("user == null");
        } else {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email, request.pass));
            user = accountRepo.findUserByEmail(request.email)
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));
        }

        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    @Transactional
    public void recoveryPassword(PasswordRecovery request) {
        var cread = creadRepo.findFirstByEmail(request.email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email"));
        cread.setPass(passwordEncoder.encode(request.newPassword));
        creadRepo.save(cread);
    }

    @Override
    @Transactional
    public void logout(User user) {
        deviceRepo.logoutDevicesByOwnerId(user.getId());
    }
}
