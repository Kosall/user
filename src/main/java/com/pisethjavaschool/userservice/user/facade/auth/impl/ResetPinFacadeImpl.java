package com.pisethjavaschool.userservice.user.facade.auth.impl;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.dto.ResetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.facade.auth.ResetPinFacade;
import com.pisethjavaschool.userservice.user.mapper.UserAccountMapper;
import com.pisethjavaschool.userservice.user.repository.UserAccountRepository;
import com.pisethjavaschool.userservice.user.service.OtpService;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.keycloak.KeycloakAdminClient;
import com.pisethjavaschool.userservice.user.service.keycloak.dto.KeycloakResetPasswordRequest;
import com.pisethjavaschool.userservice.user.validation.LoginValidator;
import com.pisethjavaschool.userservice.user.validation.PinValidator;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ResetPinFacadeImpl implements ResetPinFacade {
	private final UserAccountFinder userAccountFinder;
	private final LoginValidator loginValidator;
	private final PinValidator pinValidator;
	private final UserAccountMapper userAccountMapper;
	private final KeycloakAdminClient keycloakAdminClient;
	private final PhoneNumberService phoneNumberService;
	private final UserAccountRepository userAccountRepository;
	private final OtpService otpService;

	@Override
	public Mono<UserAccountResponse> execute(ResetPinRequest request) {
		pinValidator.validateMatched(request.pin(), request.confirmPin());
		NormalizedPhone phone = normalizePhone(request.countryCode(), request.phoneNumber());
		return findLoginAccount(phone, request.userType()).flatMap(account -> otpService
				.verifyOtp(phone.countryCode(), phone.phoneNumber(), OtpPurpose.FORGOT_PIN, request.otpCode())
				.then(keycloakAdminClient.resetPassword(toResetPasswordRequest(account, request.pin())))
				.then(updateLastModifiedAt(account))).map(userAccountMapper::toResponse);
	}

	private Mono<UserAccount> findLoginAccount(NormalizedPhone phone, UserType userType) {
		return userAccountFinder.findRequiredByPhoneAndUserType(phone, userType)
				.flatMap(account -> loginValidator.validateCanLogin(account).thenReturn(account));
	}

	private KeycloakResetPasswordRequest toResetPasswordRequest(UserAccount account, String pin) {
		return new KeycloakResetPasswordRequest(account.getKeycloakUserId(), pin, false);
	}

	private NormalizedPhone normalizePhone(String countryCode, String phoneNumber) {
		return phoneNumberService.normalize(countryCode, phoneNumber);
	}

	private Mono<UserAccount> updateLastModifiedAt(UserAccount account) {
		account.setUpdatedAt(Instant.now());

		return userAccountRepository.save(account);
	}

}
