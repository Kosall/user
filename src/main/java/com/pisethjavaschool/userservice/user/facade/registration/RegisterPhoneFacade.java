package com.pisethjavaschool.userservice.user.facade.registration;

import java.lang.management.MonitorInfo;

import com.pisethjavaschool.userservice.user.dto.RegisterPhoneRequest;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneResponse;

import reactor.core.publisher.Mono;

public interface RegisterPhoneFacade {
	Mono<RegisterPhoneResponse> execute(RegisterPhoneRequest request);

}
