package com.pisethjavaschool.userservice.user.facade.auth.impl;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.ForgotPinRequest;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.facade.auth.RequestForgotPinOtpFacade;
import com.pisethjavaschool.userservice.user.service.OtpService;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.util.LogMasker;
import com.pisethjavaschool.userservice.user.validation.LoginValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
@Component
@RequiredArgsConstructor
@Slf4j
public class RequestForgotPinOtpFacadeImpl implements RequestForgotPinOtpFacade {
		private final UserAccountFinder userAccountFinder;
		private LoginValidator loginValidator;
		private final PhoneNumberService phoneNumberService;
		private final OtpService otpService;

	@Override
	public Mono<Void> execute(ForgotPinRequest request) {
		NormalizedPhone phone = normalizePhone(request.countryCode(), request.phoneNumber());

		log.info("Forgot PIN OTP requested. userType={}, phone={}", request.userType(),
				LogMasker.maskPhone(phone.phoneNumber()));

		return findLoginAccount(phone, request.userType())
				.then(otpService.sendOtp(phone.countryCode(), phone.phoneNumber(), OtpPurpose.FORGOT_PIN))
				.doOnSuccess(ignored -> log.info("Forgot PIN OTP sent. userType={}, phone={}", request.userType(),
						LogMasker.maskPhone(phone.phoneNumber())))
				.doOnError(error -> log.warn("Forgot PIN OTP request failed. userType={}, phone={}, reason={}",
						request.userType(), LogMasker.maskPhone(phone.phoneNumber()), error.getMessage()));
	}
	
	
	private Mono<UserAccount> findLoginAccount(NormalizedPhone phone, UserType userType) {
		return userAccountFinder.findRequiredByPhoneAndUserType(phone, userType)
				.flatMap(account -> loginValidator.validateCanLogin(account).thenReturn(account));
	}
	
	private NormalizedPhone normalizePhone(String countryCode, String phoneNumber) {
		return phoneNumberService.normalize(countryCode, phoneNumber);
	}

}
