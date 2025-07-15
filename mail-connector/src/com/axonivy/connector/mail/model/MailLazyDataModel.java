package com.axonivy.connector.mail.model;

import com.axonivy.connector.mail.businessData.Mail;

import ch.ivyteam.ivy.business.data.store.search.Filter;
import ch.ivyteam.ivy.business.data.store.search.Query;

public class MailLazyDataModel extends AbstractBusinessDataLazyDataModel<Mail> {

	private static final long serialVersionUID = -501730717404259085L;
	private String caseId = "";

	public MailLazyDataModel() {
		super(Mail.class);
	}

	public MailLazyDataModel(String caseId) {
		super(Mail.class);
		this.caseId = caseId;
	}

	@Override
	protected Filter<Mail> filter(Query<Mail> query) {
		return query.textField("caseId").containsAllWordPatterns(caseId == null ? "" : caseId);
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

}
