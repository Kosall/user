package com.pisethjavaschool.userservice.user.facade.registration;

import java.util.UUID;

import com.pisethjavaschool.userservice.user.dto.SetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;

import reactor.core.publisher.Mono;

public interface SetPinFacade {
	Mono<UserAccountResponse> execute(UUID userAccountId, SetPinRequest request);
}
