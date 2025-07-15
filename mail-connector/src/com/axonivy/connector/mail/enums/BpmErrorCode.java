package com.axonivy.connector.mail.enums;

import java.util.Arrays;

import ch.ivyteam.ivy.environment.Ivy;

public enum BpmErrorCode {
	ERROR_MAIL_NOT_SENT("com:axonivy:connector:mail:mailNotSent","/Errors/mailNotSent");

	private final String code;
	private final String cmsPath;

	private BpmErrorCode(String code, String cmsPath) {
		this.cmsPath = cmsPath;
		this.code = code;
	}

	/**
	 * Return the message entry of the instance.
	 *
	 * @return
	 */
	public String getCmsMessage(Object... params) {
		return Ivy.cms().co(cmsPath, Arrays.asList(params));
	}

	public String getCmsPath() {
		return cmsPath;
	}

	public String getCode() {
		return code;
	}
}
