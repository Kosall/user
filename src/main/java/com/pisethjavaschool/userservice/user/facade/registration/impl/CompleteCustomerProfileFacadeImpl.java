package com.pisethjavaschool.userservice.user.facade.registration.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.dto.CustomerProfileRequest;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileResponse;
import com.pisethjavaschool.userservice.user.facade.registration.CompleteCustomerProfileFacade;
import com.pisethjavaschool.userservice.user.mapper.CustomerProfileResponseMapper;
import com.pisethjavaschool.userservice.user.service.CustomerProfileService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.UserAccountStateService;
import com.pisethjavaschool.userservice.user.validation.UserAccountRegistrationValidator;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CompleteCustomerProfileFacadeImpl implements CompleteCustomerProfileFacade {
	private final UserAccountFinder userAccountFinder;
	private final UserAccountRegistrationValidator userAccountRegistrationValidator;
	private final CustomerProfileService customerProfileService;
	private final UserAccountStateService userAccountStateService;
	private final CustomerProfileResponseMapper customerProfileResponseMapper;

	@Override
	public Mono<CustomerProfileResponse> execute(UUID userAccountId, CustomerProfileRequest request) {
		return userAccountFinder.findRequiredById(userAccountId)
				.flatMap(account -> userAccountRegistrationValidator.validateCanCompleteCustomerProfile(account)
						.then(customerProfileService.upsert(account.getId(), request))
						.flatMap(profile -> userAccountStateService.markProfileCompleted(account).thenReturn(profile)))
				.map(customerProfileResponseMapper::toResponse);
	}

}
