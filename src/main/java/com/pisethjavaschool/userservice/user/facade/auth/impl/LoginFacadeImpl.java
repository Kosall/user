package com.pisethjavaschool.userservice.user.facade.auth.impl;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.LoginRequest;
import com.pisethjavaschool.userservice.user.dto.LoginResponse;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.exception.InvalidPinException;
import com.pisethjavaschool.userservice.user.facade.auth.LoginFacade;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.keycloak.KeycloakAuthClient;
import com.pisethjavaschool.userservice.user.util.LogMasker;
import com.pisethjavaschool.userservice.user.util.PhoneNormalizer;
import com.pisethjavaschool.userservice.user.validation.LoginValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginFacadeImpl implements LoginFacade {
	private final PhoneNormalizer phoneNormalizer;
	private final UserAccountFinder userAccountFinder;
	private final LoginValidator loginValidator;
	private final PhoneNumberService phoneNumberService;
	private final KeycloakAuthClient keycloakAuthClient;

	@Override
	public Mono<LoginResponse> execute(LoginRequest request) {
		NormalizedPhone phone = normalizePhone(request.countryCode(), request.phoneNumber());

//		NormalizedPhone phone = normalizePhone(request.countryCode(), request.phoneNumber());

		log.info("Login requested. userType={}, phone={}", request.userType(),
				LogMasker.maskPhone(phone.phoneNumber()));

		return findLoginAccount(phone, request.userType())
				.flatMap(account -> keycloakAuthClient
						.login(phoneNormalizer.toUsername(phone.countryCode(), phone.phoneNumber()), request.pin()))
				.doOnSuccess(response -> log.info("Login successful. userType={}, phone={}", request.userType(),
						LogMasker.maskPhone(phone.phoneNumber())))
//                        .doOnError(error -> log.warn(
//                        "Loging failed. userType={}, phone={}, reason={}",
//                        request.userType(),
//                        LogMasker.maskPhone(phone.phoneNumber()),
//                        error.getMessage()
//                ));
//               
				.onErrorMap(err -> new InvalidPinException(err.getMessage()));
	}

	private Mono<UserAccount> findLoginAccount(NormalizedPhone phone, UserType userType) {
		return userAccountFinder.findRequiredByPhoneAndUserType(phone, userType)
				.flatMap(account -> loginValidator.validateCanLogin(account).thenReturn(account));
	}
	
	private NormalizedPhone normalizePhone(String countryCode, String phoneNumber) {
		return phoneNumberService.normalize(countryCode, phoneNumber);
	}


}
