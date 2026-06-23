package com.pisethjavaschool.userservice.user.facade.registration.impl;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneRequest;
import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;
import com.pisethjavaschool.userservice.user.facade.registration.CheckRegistrationFacade;
import com.pisethjavaschool.userservice.user.mapper.RegistrationStatusMapper;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
@Component
@RequiredArgsConstructor
public class CheckRegistrationFacadeImpl implements CheckRegistrationFacade {
	private final PhoneNumberService phoneNumberService;
	private final UserAccountFinder userAccountFinder;
	private final RegistrationStatusMapper registrationStatusMapper;

	@Override
	public Mono<RegistrationStatusResponse> execute(RegisterPhoneRequest request) {
		NormalizedPhone phone = phoneNumberService.normalize(
                request.countryCode(),
                request.phoneNumber()
        );

        return userAccountFinder.findByPhoneAndUserType(phone, request.userType())
                .map(registrationStatusMapper::toResponse)
                .defaultIfEmpty(registrationStatusMapper.notRegistered(request.userType()));
	}

}
