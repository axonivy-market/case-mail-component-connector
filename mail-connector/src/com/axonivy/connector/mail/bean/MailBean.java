package com.axonivy.connector.mail.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.text.StringEscapeUtils;

import com.axonivy.connector.mail.Constants;
import com.axonivy.connector.mail.businessData.Mail;
import com.axonivy.connector.mail.enums.MailStatus;
import com.axonivy.connector.mail.enums.ResponseAction;
import com.axonivy.connector.mail.model.MailLazyDataModel;
import com.axonivy.connector.mail.service.MailService;

@ManagedBean
@ViewScoped
public class MailBean {
	private Mail mail;
	private Mail selectedMail;
	private MailLazyDataModel mailModel;
	private MailService mailService;
	private String caseId;
	
	@PostConstruct
    public void init() {
		mailService = new MailService();
    }

	public void initMail() {
		mailModel = new MailLazyDataModel(caseId);
		mail = new Mail();
		mail.setCaseId(caseId);
	}

	public void handleCloseDialog() {
		initMail();
	}

	public void prepareMail(String actionType) {
		ResponseAction type = ResponseAction.valueOf(actionType);
		mail.setCaseId(caseId);
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

	/**
	 * Get mail body with embedded images
	 */
	public String getMailBodyWithEmbeddedImages() {

//		if (CollectionUtils.isNotEmpty(inlineAtachments)) {
//			for (final File file : inlineAtachments) {
//				final String content = Base64.getEncoder().encodeToString(file.getContent());
//				final StringBuilder base64Content = new StringBuilder().append("data:image/")
//						.append(file.getType().getDefaultExtension()).append(";base64,").append(content);
//				email2CaseSelected.getEmail().setBody(
//						email2CaseSelected.getEmail().getBody().replace("cid:" + file.getContentId(), base64Content));
//			}
//		}

		// Detect if it's HTML or plain text
		if (!isHtml(selectedMail.getBody())) {
			// Escape HTML and wrap in <pre> to preserve formatting
			selectedMail.setBody("<pre>" + StringEscapeUtils.escapeHtml4(selectedMail.getBody()) + "</pre>");
		}

		return selectedMail.getBody();
	}

	/**
	 * Checks whether the given text contains HTML tags.
	 * 
	 * @param text the input string to check
	 * @return {@code true} if the input contains HTML tags; {@code false} otherwise
	 */
	private static boolean isHtml(String text) {
		return text != null && text.trim().matches(Constants.HTML_REGEX);
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

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

}
