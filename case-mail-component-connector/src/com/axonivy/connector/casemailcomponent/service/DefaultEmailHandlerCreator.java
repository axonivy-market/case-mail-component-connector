package com.axonivy.connector.casemailcomponent.service;

public class DefaultEmailHandlerCreator extends AbstractEmailHandlerCreator {

	@Override
	public AbstractEmailHandler getEmailHandler(String storeName) {
		return new DefaultEmailHandler(storeName);
	}

}
