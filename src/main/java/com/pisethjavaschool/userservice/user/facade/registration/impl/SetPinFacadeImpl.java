package com.pisethjavaschool.userservice.user.facade.registration.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.dto.SetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.facade.registration.SetPinFacade;
import com.pisethjavaschool.userservice.user.mapper.UserAccountMapper;
import com.pisethjavaschool.userservice.user.service.CustomerProfileService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.UserAccountStateService;
import com.pisethjavaschool.userservice.user.service.UserIdentityProvisioningService;
import com.pisethjavaschool.userservice.user.validation.PinValidator;
import com.pisethjavaschool.userservice.user.validation.UserAccountRegistrationValidator;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SetPinFacadeImpl implements SetPinFacade {
	private final PinValidator pinValidator;
	private final UserAccountFinder userAccountFinder;
	private final UserAccountRegistrationValidator userAccountRegistrationValidator;
	private final CustomerProfileService customerProfileService;
	private final UserIdentityProvisioningService userIdentityProvisioningService;
	private final UserAccountStateService userAccountStateService;
	private final UserAccountMapper userAccountMapper;

	@Override
	public Mono<UserAccountResponse> execute(UUID userAccountId, SetPinRequest request) {
		pinValidator.validateMatched(request.pin(), request.confirmPin());

		return userAccountFinder.findRequiredById(userAccountId)
				.flatMap(account -> userAccountRegistrationValidator.validateCanSetPin(account)
						.then(customerProfileService.findRequiredByUserAccountId(account.getId()))
						.flatMap(profile -> userIdentityProvisioningService.provisionOrResetPin(account, profile,
								request.pin())))
				.flatMap(userAccountStateService::activate).map(userAccountMapper::toResponse);
	}

}
