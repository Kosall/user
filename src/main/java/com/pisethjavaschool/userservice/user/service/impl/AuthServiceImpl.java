package com.pisethjavaschool.userservice.user.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pisethjavaschool.userservice.user.dto.ForgotPinRequest;
import com.pisethjavaschool.userservice.user.dto.LoginRequest;
import com.pisethjavaschool.userservice.user.dto.LoginResponse;
import com.pisethjavaschool.userservice.user.dto.ResetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.facade.auth.LoginFacade;
import com.pisethjavaschool.userservice.user.facade.auth.RequestForgotPinOtpFacade;
import com.pisethjavaschool.userservice.user.facade.auth.ResetPinFacade;
import com.pisethjavaschool.userservice.user.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final LoginFacade loginFacade;
	private final ResetPinFacade resetPinFacade;
	private final RequestForgotPinOtpFacade requestForgotPinOtpFacade;

	/*
	 * @Override public Mono<LoginResponse> login(LoginRequest request) {
	 * NormalizedPhone phone = normalizePhone(request.countryCode(),
	 * request.phoneNumber());
	 * 
	 * return findLoginAccount(phone, request.userType()) .flatMap(account ->
	 * keycloakAuthClient.login( phoneNormalizer.toUsername(phone.countryCode(),
	 * phone.phoneNumber()), request.pin() )); }
	 */

	@Override
	public Mono<LoginResponse> login(LoginRequest request) {
		return loginFacade.execute(request);
	}

	/*
	 * @Override public Mono<Void> requestForgotPinOtp(ForgotPinRequest request) {
	 * NormalizedPhone phone = normalizePhone(request.countryCode(),
	 * request.phoneNumber());
	 * 
	 * return findLoginAccount(phone, request.userType()) .then(otpService.sendOtp(
	 * phone.countryCode(), phone.phoneNumber(), OtpPurpose.FORGOT_PIN )); }
	 */

	@Override
	public Mono<Void> requestForgotPinOtp(ForgotPinRequest request) {
		
		return requestForgotPinOtpFacade.execute(request);
	}
		

	@Override
	@Transactional
	public Mono<UserAccountResponse> resetPin(ResetPinRequest request) {
		

		return resetPinFacade.execute(request);

		
	}

}