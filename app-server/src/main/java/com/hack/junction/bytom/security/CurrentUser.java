package com.hack.junction.bytom.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

/*
 * Wrapper around @AuthenticationPrincipal annotation provided by Spring Security, in order
 * to avoid tying up with Spring related annotations in our project.
 */

@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {
}
