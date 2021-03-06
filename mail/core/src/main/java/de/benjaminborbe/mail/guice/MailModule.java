package de.benjaminborbe.mail.guice;

import com.google.inject.AbstractModule;
import de.benjaminborbe.mail.api.MailService;
import de.benjaminborbe.mail.service.MailServiceImpl;
import de.benjaminborbe.mail.util.MailSender;
import de.benjaminborbe.mail.util.MailSenderUTF8;
import de.benjaminborbe.mail.util.MailSessionFactory;
import de.benjaminborbe.mail.util.MailSessionFactoryImpl;
import de.benjaminborbe.tools.log.LoggerSlf4Provider;
import org.slf4j.Logger;

import javax.inject.Singleton;

public class MailModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(MailSender.class).to(MailSenderUTF8.class).in(Singleton.class);
		bind(MailService.class).to(MailServiceImpl.class).in(Singleton.class);
		bind(MailSessionFactory.class).to(MailSessionFactoryImpl.class).in(Singleton.class);
		bind(Logger.class).toProvider(LoggerSlf4Provider.class).in(Singleton.class);
	}
}
