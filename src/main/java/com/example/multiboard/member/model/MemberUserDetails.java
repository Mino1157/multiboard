package com.example.multiboard.member.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class MemberUserDetails extends User {
	
	private String userEmail;
	public MemberUserDetails(String username, String password,
			Collection<? extends GrantedAuthority> authorities, String userEmail) {
		super(username, password, authorities);
		this.userEmail = userEmail;
	}

	public String getUserEmail() {
		return this.userEmail;
	}
}
