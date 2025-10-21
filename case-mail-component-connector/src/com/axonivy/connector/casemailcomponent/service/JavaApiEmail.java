package com.axonivy.connector.casemailcomponent.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;

import com.axonivy.connector.casemailcomponent.Constants;
import com.axonivy.connector.casemailcomponent.businessData.Mail;
import com.axonivy.connector.casemailcomponent.enums.BpmErrorCode;
import com.axonivy.connector.casemailcomponent.utils.FileUtils;

import ch.ivyteam.ivy.bpm.error.BpmError;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.mail.Attachment;
import ch.ivyteam.ivy.mail.MailClient;
import ch.ivyteam.ivy.mail.MailMessage;

/**
 * Class accepts object of type {@link Mail}, creates {@link MailMessage} from
 * it using the ivy email client and sends it.<br />
 * Important feature is that it transforms base64 inline attachments to cid
 * reference attachments.<br />
 * Any problem is wrapped to {@link BpmError} and thrown to be distributed to
 * calling ivy process, e.g. SendMailAsync.p.json.
 *
 * @see BpmError
 */
public class JavaApiEmail {
	private final static String MAIL_CID_INLINE_IMAGES_MODE = "cidInlineImagesMode";

	private MailMessage.Builder ivyMessageBuilder;

	private JavaApiEmail() {
		super();
	}

	/**
	 * Instantiation of the object using {@link Mail} object. <br />
	 * Creates {@link MailMessage.Builder} and initializes the basic email
	 * properties: <br />
	 * subject, recipient, cc, bcc, and sender<br />
	 * Created object is stored to property {@link #ivyMessageBuilder}.
	 *
	 * @param mail
	 * @return
	 */
	public static JavaApiEmail getInstance(Mail mail) {
		final JavaApiEmail jaMail = new JavaApiEmail();
		try {
			// mandatory email properties
			final MailMessage.Builder mBuilder = MailMessage.create().subject(mail.getSubject())
					.to(MailService.extractEmails(mail.getRecipient())).from(mail.getSender());

			// optional cc
			if (StringUtils.isNotBlank(mail.getRecipientCC())) {
				mBuilder.toMailMessage().cc().addAll(MailService.extractInternetAddresses(mail.getRecipientCC()));
			}

			jaMail.setIvyMessageBuilder(mBuilder);
		} catch (final Exception e) {
			BpmErrorService.get().throwBpmErrorSimplified(BpmErrorCode.ERROR_JAVA_API_MAIL_NOT_SENT, e);
		}
		return jaMail;
	}

	/**
	 * @return the local {@link #ivyMessageBuilder} initialized in
	 *         {@link #getInstance(Mail)}
	 */
	public MailMessage.Builder getIvyMessageBuilder() {
		return ivyMessageBuilder;
	}

	/**
	 * @param ivyMessageBuilder the ivyMessageBuilder to set
	 */
	public void setIvyMessageBuilder(MailMessage.Builder ivyMessageBuilder) {
		this.ivyMessageBuilder = ivyMessageBuilder;
	}

	/**
	 * @return true if variable {@link #MAIL_CID_INLINE_IMAGES_MODE} is set to true,
	 *         false otherwise
	 */
	public static boolean useJavaApiEmail() {
		return Boolean.parseBoolean(Ivy.var().get(MAIL_CID_INLINE_IMAGES_MODE));
	}

	/**
	 * Internal helper class that contains a processed html and the extracted
	 * attachments that are referenced from the html using cid.
	 */
	private static class HtmlAndImages {
		public final String html;
		public final List<Attachment> imageParts;

		public HtmlAndImages(String html, List<Attachment> imageParts) {
			this.html = html;
			this.imageParts = imageParts;
		}
	}

	/**
	 * Crucial method which transforms inline base64 attachments of passed html body
	 * to proper cid referenced attachments.<br />
	 * Result is stored to {@link #ivyMessageBuilder}.
	 *
	 * @uses extractImagesAndReplaceWithCid(String originalHtml)
	 * @param mailHtmlBody
	 * @throws BpmError
	 */
	public void prepareBody(String mailHtmlBody) throws BpmError {
		try {
			// extract images and turn into inline attachments
			final HtmlAndImages result = extractImagesAndReplaceWithCid(mailHtmlBody);

			// prepare builder that contains the inline attachments and the updated html
			// with proper <img src="cid:imageid"> tags
			getIvyMessageBuilder().htmlContent(result.html);
			getIvyMessageBuilder().attachments(result.imageParts);
		} catch (final Exception e) {
			BpmErrorService.get().throwBpmErrorSimplified(BpmErrorCode.ERROR_JAVA_API_MAIL_NOT_SENT, e);
		}
	}

	/**
	 * Extracts the base64 images from the provided html and transforms them into
	 * inline attachments.
	 *
	 * @param originalHtml
	 * @return wrapper that contains the inline attachments and the updated html
	 * @throws Exception
	 */
	private HtmlAndImages extractImagesAndReplaceWithCid(String originalHtml) throws Exception {
		final List<Attachment> imageParts = new ArrayList<>();
		final StringBuilder htmlBuilder = new StringBuilder(originalHtml);
		// pattern to find base64 images
		final Pattern pattern = Pattern.compile("data:image/(\\w+);base64,([A-Za-z0-9+/=]+)");
		final Matcher matcher = pattern.matcher(originalHtml);

		int imageIndex = 1;
		int offset = 0;

		// find and process all base64 images
		while (matcher.find()) {
			final String imageType = matcher.group(1);
			final String base64Data = matcher.group(2);
			final String cid = "image" + imageIndex;

			// Decode image
			final byte[] imageBytes = Base64.getDecoder().decode(base64Data);
			final InputStream imageStream = new ByteArrayInputStream(imageBytes);
			final DataSource ds = new ByteArrayDataSource(imageStream, "image/" + imageType);
			// transform to ivy inline mail attachment and
			final Attachment imagePart = Attachment.create().dataSource(ds).filename("inline" + cid).contentId(cid)
					.dispositionInline().toAttachment();
			imageParts.add(imagePart);

			// Replace base64 in hmtl with CID reference
			final int start = matcher.start() + offset;
			final int end = matcher.end() + offset;
			final String replacement = Constants.CID + cid;
			htmlBuilder.replace(start, end, replacement);
			offset += replacement.length() - (end - start);
			imageIndex++;
		}
		return new HtmlAndImages(htmlBuilder.toString(), imageParts);
	}

	/**
	 * Transforms all passed {@link Attachment} objects to email attachments. <br />
	 * Result is stored to {@link #ivyMessageBuilder}.
	 *
	 * @param files
	 * @throws BpmError
	 */
	public void prepareAttachments(List<com.axonivy.connector.casemailcomponent.businessData.Attachment> files)
			throws BpmError {
		try {
			Attachment properMailAtt;
			DataSource ds;
			String contentType;

			for (final com.axonivy.connector.casemailcomponent.businessData.Attachment file : files) {
				contentType = FileUtils.getContentType(file.getName());
				ds = new ByteArrayDataSource(file.getContent(), contentType);
				properMailAtt = Attachment.create().dataSource(ds).filename(file.getName())
						.contentId(file.getContentId()).toAttachment();

				getIvyMessageBuilder().attachments(properMailAtt);
			}
		} catch (final Exception e) {
			BpmErrorService.get().throwBpmErrorSimplified(BpmErrorCode.ERROR_JAVA_API_MAIL_NOT_SENT, e);
		}
	}

	/**
	 * Sends the email in {@link #ivyMessageBuilder}
	 *
	 * @throws BpmError
	 */
	public void sendEmail() throws BpmError {
		try {
			MailClient.newMailClient().send(getIvyMessageBuilder().toMailMessage());
		} catch (final Exception e) {
			BpmErrorService.get().throwBpmErrorSimplified(BpmErrorCode.ERROR_JAVA_API_MAIL_NOT_SENT, e);
		}
	}
}
