package com.axonivy.connector.mail.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.ivyteam.ivy.environment.Ivy;

public class DefaultEmailHandler extends AbstractEmailHandler {
	private static final String CASE_REFERENCE_REGEX_VAR = "caseReferenceRegex";

	public DefaultEmailHandler() {
		super();
	}

	@Override
	public String getCaseReference(String subject) {
		final Pattern pattern = Pattern.compile(Ivy.var().get(CASE_REFERENCE_REGEX_VAR), Pattern.CASE_INSENSITIVE);
		final Matcher matcher = pattern.matcher(subject);

		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

}
