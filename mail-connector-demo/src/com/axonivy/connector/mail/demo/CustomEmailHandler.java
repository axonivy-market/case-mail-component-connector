package com.axonivy.connector.mail.demo;

import com.axonivy.connector.mail.service.AbstractEmailHandler;

public class CustomEmailHandler extends AbstractEmailHandler {

	@Override
	protected String getCaseReference(String subject) {
		return super.getCaseReference(subject);
	}

	
}
