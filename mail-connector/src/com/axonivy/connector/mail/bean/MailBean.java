package com.axonivy.connector.mail.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.axonivy.connector.mail.businessData.Mail;
import com.axonivy.connector.mail.enums.MailStatus;
import com.axonivy.connector.mail.enums.ResponseAction;
import com.axonivy.connector.mail.model.MailLazyDataModel;
import com.axonivy.connector.mail.service.MailService;

@ManagedBean
@ViewScoped
public class MailBean {
	private Mail mail = new Mail();
	private Mail selectedMail;
	private MailLazyDataModel mailModel = new MailLazyDataModel();
	private MailService mailService = new MailService();

	public void resetMail() {
		mailModel = new MailLazyDataModel();
		mail = new Mail();
	}

	public void handleCloseDialog() {
		resetMail();
	}

	public void prepareMail(String actionType) {
		ResponseAction type = ResponseAction.valueOf(actionType);
		switch (type) {
		case RESEND:
			setMail(mailService.setUpResendMail(selectedMail));
			break;
		case FORWARD:
			setMail(mailService.setUpForwardMail(selectedMail));
			break;
		case REPLY:
			setMail(mailService.setUpReplyMail(selectedMail));
			break;
		default:
			break;
		}
	}

	/**
	 * Check if mail is Sent
	 *
	 * @return boolean
	 */
	public boolean isSent() {
		return selectedMail.getStatus().equals(MailStatus.SENT);
	}

	public String getConfirmMessage() {
		return mailService.setUpMessageConfirm(selectedMail, ResponseAction.RESEND.toString());
	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}

	public MailLazyDataModel getMailModel() {
		return mailModel;
	}

	public void setMailModel(MailLazyDataModel mailModel) {
		this.mailModel = mailModel;
	}

	public Mail getSelectedMail() {
		return selectedMail;
	}

	public void setSelectedMail(Mail selectedMail) {
		this.selectedMail = selectedMail;
	}

}
