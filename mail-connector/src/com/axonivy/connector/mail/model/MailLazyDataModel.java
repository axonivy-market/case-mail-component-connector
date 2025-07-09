package com.axonivy.connector.mail.model;

import com.axonivy.connector.mail.businessData.Mail;

import ch.ivyteam.ivy.business.data.store.search.Filter;
import ch.ivyteam.ivy.business.data.store.search.Query;

public class MailLazyDataModel extends AbstractBusinessDataLazyDataModel<Mail> {

	private static final long serialVersionUID = -501730717404259085L;
	private String wordsFilter = "";

	public MailLazyDataModel() {
		super(Mail.class);
	}

	@Override
	protected Filter<Mail> filter(Query<Mail> query) {
		return query.allFields().containsAllWordPatterns(wordsFilter);
	}

	public String getWordsFilter() {
		return wordsFilter;
	}

	public void setWordsFilter(String wordsFilter) {
		this.wordsFilter = wordsFilter;
	}

}
