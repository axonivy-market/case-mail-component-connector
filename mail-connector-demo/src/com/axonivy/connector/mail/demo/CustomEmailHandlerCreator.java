package com.axonivy.connector.mail.demo;

import com.axonivy.connector.mail.service.AbstractEmailHandler;
import com.axonivy.connector.mail.service.AbstractEmailHandlerCreator;

public class CustomEmailHandlerCreator extends AbstractEmailHandlerCreator {

	@Override
	public AbstractEmailHandler getEmailHandler() {
		return new CustomEmailHandler();
	}

}
