package com.axonivy.connector.mail.service;


public abstract class AbstractEmailHandlerCreator {

	/**
	 * Retrieve Mail
	 *
	 */
	public void retrieveEmails() {
		final AbstractEmailHandler emailHandler = getEmailHandler();

		emailHandler.handleMail();
	}

	protected AbstractEmailHandler getEmailHandler() {
		return new DefaultEmailHandler();
	}
}
