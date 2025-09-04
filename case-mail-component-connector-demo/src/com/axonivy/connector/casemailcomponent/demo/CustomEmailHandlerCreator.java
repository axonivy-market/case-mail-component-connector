package com.axonivy.connector.casemailcomponent.demo;

import com.axonivy.connector.casemailcomponent.service.AbstractEmailHandler;
import com.axonivy.connector.casemailcomponent.service.AbstractEmailHandlerCreator;

public class CustomEmailHandlerCreator extends AbstractEmailHandlerCreator {

	@Override
	protected AbstractEmailHandler getEmailHandler(String storeName) {
		return new CustomEmailHandler(storeName);
	}

}
