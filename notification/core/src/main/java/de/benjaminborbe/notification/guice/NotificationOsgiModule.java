package de.benjaminborbe.notification.guice;

import com.google.inject.AbstractModule;
import de.benjaminborbe.authentication.api.AuthenticationService;
import de.benjaminborbe.authorization.api.AuthorizationService;
import de.benjaminborbe.configuration.api.ConfigurationService;
import de.benjaminborbe.mail.api.MailService;
import de.benjaminborbe.storage.api.StorageService;
import de.benjaminborbe.xmpp.api.XmppService;
import org.apache.felix.http.api.ExtHttpService;
import org.osgi.service.log.LogService;

import static org.ops4j.peaberry.Peaberry.service;

public class NotificationOsgiModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(StorageService.class).toProvider(service(StorageService.class).single());
		bind(AuthorizationService.class).toProvider(service(AuthorizationService.class).single());
		bind(AuthenticationService.class).toProvider(service(AuthenticationService.class).single());
		bind(MailService.class).toProvider(service(MailService.class).single());
		bind(XmppService.class).toProvider(service(XmppService.class).single());
		bind(ConfigurationService.class).toProvider(service(ConfigurationService.class).single());
		bind(LogService.class).toProvider(service(LogService.class).single());
		bind(ExtHttpService.class).toProvider(service(ExtHttpService.class).single());
	}
}
