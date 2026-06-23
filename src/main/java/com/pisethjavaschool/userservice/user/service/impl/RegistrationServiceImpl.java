package com.pisethjavaschool.userservice.user.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pisethjavaschool.userservice.user.dto.CustomerProfileRequest;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileResponse;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneRequest;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneResponse;
import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;
import com.pisethjavaschool.userservice.user.dto.SetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.dto.VerifyOtpRequest;
import com.pisethjavaschool.userservice.user.facade.registration.CheckRegistrationFacade;
import com.pisethjavaschool.userservice.user.facade.registration.CompleteCustomerProfileFacade;
import com.pisethjavaschool.userservice.user.facade.registration.RegisterPhoneFacade;
import com.pisethjavaschool.userservice.user.facade.registration.SetPinFacade;
import com.pisethjavaschool.userservice.user.facade.registration.VerifyOtpFacade;
import com.pisethjavaschool.userservice.user.service.RegistrationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

	private final RegisterPhoneFacade registerPhonefacade;
	private final VerifyOtpFacade otpFacade;
	private final CompleteCustomerProfileFacade completeCustomerProfileFacade;
	private final SetPinFacade setPinFacade;
	private final CheckRegistrationFacade registrationFacade;

	@Override
	@Transactional
	public Mono<RegisterPhoneResponse> registerPhone(RegisterPhoneRequest request) {
		return registerPhonefacade.execute(request);

	}

	@Override
	@Transactional
	public Mono<RegistrationStatusResponse> verifyOtp(VerifyOtpRequest request) {
		return otpFacade.execute(request);
	}

	@Override
	@Transactional
	public Mono<CustomerProfileResponse> completeCustomerProfile(UUID userAccountId, CustomerProfileRequest request) {
		return completeCustomerProfileFacade.execute(userAccountId, request);
	}

	@Override
	@Transactional
	public Mono<UserAccountResponse> setPin(UUID userAccountId, SetPinRequest request) {
		return setPinFacade.execute(userAccountId, request);
	}

	@Override
	public Mono<RegistrationStatusResponse> checkRegistration(RegisterPhoneRequest request) {

		return registrationFacade.execute(request);
	}

}