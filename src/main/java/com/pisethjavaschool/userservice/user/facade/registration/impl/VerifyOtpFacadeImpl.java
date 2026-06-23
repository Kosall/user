package com.pisethjavaschool.userservice.user.facade.registration.impl;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;
import com.pisethjavaschool.userservice.user.dto.VerifyOtpRequest;
import com.pisethjavaschool.userservice.user.facade.registration.VerifyOtpFacade;
import com.pisethjavaschool.userservice.user.mapper.RegistrationStatusMapper;
import com.pisethjavaschool.userservice.user.mapper.UserAccountMapper;
import com.pisethjavaschool.userservice.user.service.OtpService;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.UserAccountStateService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
@Component
@RequiredArgsConstructor
public class VerifyOtpFacadeImpl implements VerifyOtpFacade{

	private final PhoneNumberService phoneNumberService;
	private final UserAccountFinder userAccountFinder;
	private final OtpService otpService;
	private final UserAccountStateService userAccountStateService;
	private final RegistrationStatusMapper registrationStatusMapper;
	@Override
	public Mono<RegistrationStatusResponse> execute(VerifyOtpRequest request) {
		NormalizedPhone phone = phoneNumberService.normalize(
                request.countryCode(),
                request.phoneNumber()
        );

        return userAccountFinder.findRequiredByPhoneAndUserType(phone, request.userType())
                .flatMap(account -> otpService.verifyOtp(
                                phone.countryCode(),
                                phone.phoneNumber(),
                                OtpPurpose.REGISTRATION,
                                request.otpCode()
                        )
                        .then(userAccountStateService.markOtpVerified(account)))
                .map(registrationStatusMapper::toResponse);
	}

}
