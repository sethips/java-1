package de.benjaminborbe.gallery.gui.servlet;

import com.google.inject.Provider;
import de.benjaminborbe.api.ValidationError;
import de.benjaminborbe.api.ValidationErrorSimple;
import de.benjaminborbe.api.ValidationException;
import de.benjaminborbe.authentication.api.AuthenticationService;
import de.benjaminborbe.authentication.api.AuthenticationServiceException;
import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authorization.api.AuthorizationService;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.cache.api.CacheService;
import de.benjaminborbe.gallery.api.GalleryGroup;
import de.benjaminborbe.gallery.api.GalleryGroupDto;
import de.benjaminborbe.gallery.api.GalleryGroupIdentifier;
import de.benjaminborbe.gallery.api.GalleryService;
import de.benjaminborbe.gallery.api.GalleryServiceException;
import de.benjaminborbe.gallery.gui.GalleryGuiConstants;
import de.benjaminborbe.gallery.gui.util.GalleryGuiLinkFactory;
import de.benjaminborbe.gallery.gui.util.GalleryGuiWebsiteHtmlServlet;
import de.benjaminborbe.html.api.HttpContext;
import de.benjaminborbe.html.api.Widget;
import de.benjaminborbe.lib.validation.ValidationResultImpl;
import de.benjaminborbe.navigation.api.NavigationWidget;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.date.TimeZoneUtil;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.tools.util.ParseException;
import de.benjaminborbe.tools.util.ParseUtil;
import de.benjaminborbe.website.form.FormInputHiddenWidget;
import de.benjaminborbe.website.form.FormInputSubmitWidget;
import de.benjaminborbe.website.form.FormInputTextWidget;
import de.benjaminborbe.website.form.FormWidget;
import de.benjaminborbe.website.servlet.RedirectException;
import de.benjaminborbe.website.util.ExceptionWidget;
import de.benjaminborbe.website.util.H1Widget;
import de.benjaminborbe.website.util.ListWidget;
import de.benjaminborbe.website.widget.ValidationExceptionWidget;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class GalleryGuiGroupUpdateServlet extends GalleryGuiWebsiteHtmlServlet {

	private static final long serialVersionUID = -5013723680643328782L;

	private static final String TITLE = "Gallery - Create";

	private final GalleryService galleryService;

	private final Logger logger;

	private final GalleryGuiLinkFactory galleryGuiLinkFactory;

	private final AuthenticationService authenticationService;

	private final ParseUtil parseUtil;

	@Inject
	public GalleryGuiGroupUpdateServlet(
		final Logger logger,
		final CalendarUtil calendarUtil,
		final TimeZoneUtil timeZoneUtil,
		final ParseUtil parseUtil,
		final NavigationWidget navigationWidget,
		final AuthenticationService authenticationService,
		final Provider<HttpContext> httpContextProvider,
		final UrlUtil urlUtil,
		final GalleryService galleryService,
		final AuthorizationService authorizationService,
		final GalleryGuiLinkFactory galleryGuiLinkFactory,
		final CacheService cacheService
	) {
		super(logger, calendarUtil, timeZoneUtil, parseUtil, navigationWidget, authenticationService, authorizationService, httpContextProvider, urlUtil, cacheService, galleryService);
		this.galleryService = galleryService;
		this.logger = logger;
		this.parseUtil = parseUtil;
		this.galleryGuiLinkFactory = galleryGuiLinkFactory;
		this.authenticationService = authenticationService;
	}

	@Override
	protected Widget createGalleryContentWidget(final HttpServletRequest request) throws IOException,
		PermissionDeniedException, RedirectException, LoginRequiredException {
		try {
			final ListWidget widgets = new ListWidget();
			widgets.add(new H1Widget(getTitle()));
			final String id = request.getParameter(GalleryGuiConstants.PARAMETER_GROUP_ID);
			final String name = request.getParameter(GalleryGuiConstants.PARAMETER_GROUP_NAME);
			final String shared = request.getParameter(GalleryGuiConstants.PARAMETER_GROUP_SHARED);
			final String referer = request.getParameter(GalleryGuiConstants.PARAMETER_REFERER);

			final String previewShortSideMinLength = request.getParameter(GalleryGuiConstants.PARAMETER_PREVIEW_SHORT_SIDE_MIN_LENGTH);
			final String previewShortSideMaxLength = request.getParameter(GalleryGuiConstants.PARAMETER_PREVIEW_SHORT_SIDE_MAX_LENGTH);
			final String previewLongSideMinLength = request.getParameter(GalleryGuiConstants.PARAMETER_PREVIEW_LONG_SIDE_MIN_LENGTH);
			final String previewLongSideMaxLength = request.getParameter(GalleryGuiConstants.PARAMETER_PREVIEW_LONG_SIDE_MAX_LENGTH);

			final String shortSideMinLength = request.getParameter(GalleryGuiConstants.PARAMETER_SHORT_SIDE_MIN_LENGTH);
			final String shortSideMaxLength = request.getParameter(GalleryGuiConstants.PARAMETER_SHORT_SIDE_MAX_LENGTH);
			final String longSideMinLength = request.getParameter(GalleryGuiConstants.PARAMETER_LONG_SIDE_MIN_LENGTH);
			final String longSideMaxLength = request.getParameter(GalleryGuiConstants.PARAMETER_LONG_SIDE_MAX_LENGTH);

			final GalleryGroupIdentifier galleryGroupIdentifier = galleryService.createGroupIdentifier(id);

			final SessionIdentifier sessionIdentifier = authenticationService.createSessionIdentifier(request);
			final GalleryGroup galleryGroup = galleryService.getGroup(sessionIdentifier, galleryGroupIdentifier);

			if (id != null && name != null && shared != null && shortSideMinLength != null && shortSideMaxLength != null && longSideMinLength != null && longSideMaxLength != null && previewShortSideMinLength != null && previewShortSideMaxLength != null && previewLongSideMinLength != null && previewLongSideMaxLength != null
				) {
				try {
					updateGroup(sessionIdentifier, galleryGroupIdentifier, name, shared,
						previewShortSideMinLength, previewShortSideMaxLength, previewLongSideMinLength, previewLongSideMaxLength,
						shortSideMinLength, shortSideMaxLength, longSideMinLength, longSideMaxLength);

					if (referer != null) {
						throw new RedirectException(referer);
					} else {
						throw new RedirectException(galleryGuiLinkFactory.collectionListUrl(request, galleryGroupIdentifier));
					}
				} catch (final ValidationException e) {
					widgets.add("update collection => failed");
					widgets.add(new ValidationExceptionWidget(e));
				}
			}
			final FormWidget formWidget = new FormWidget();
			formWidget.addFormInputWidget(new FormInputHiddenWidget(GalleryGuiConstants.PARAMETER_REFERER).addDefaultValue(buildRefererUrl(request)));
			formWidget.addFormInputWidget(new FormInputHiddenWidget(GalleryGuiConstants.PARAMETER_GROUP_ID).addValue(galleryGroup.getId()));
			formWidget.addFormInputWidget(new FormInputTextWidget(GalleryGuiConstants.PARAMETER_GROUP_NAME).addLabel("Name:").addDefaultValue(galleryGroup.getName()));
			formWidget.addFormInputWidget(new FormInputTextWidget(GalleryGuiConstants.PARAMETER_GROUP_SHARED).addLabel("Shared:").addPlaceholder("shared...").addDefaultValue(galleryGroup.getShared()));

			formWidget.addFormInputWidget(new FormInputTextWidget(GalleryGuiConstants.PARAMETER_PREVIEW_SHORT_SIDE_MIN_LENGTH).addLabel("PreviewShortSideMinLength:").addDefaultValue(galleryGroup.getPreviewShortSideMinLength()));
			formWidget.addFormInputWidget(new FormInputTextWidget(GalleryGuiConstants.PARAMETER_PREVIEW_SHORT_SIDE_MAX_LENGTH).addLabel("PreviewShortSideMaxLength:").addDefaultValue(galleryGroup.getPreviewShortSideMaxLength()));
			formWidget.addFormInputWidget(new FormInputTextWidget(GalleryGuiConstants.PARAMETER_PREVIEW_LONG_SIDE_MIN_LENGTH).addLabel("PreviewLongSideMinLength:").addDefaultValue(galleryGroup.getPreviewLongSideMinLength()));
			formWidget.addFormInputWidget(new FormInputTextWidget(GalleryGuiConstants.PARAMETER_PREVIEW_LONG_SIDE_MAX_LENGTH).addLabel("PreviewLongSideMaxLength:").addDefaultValue(galleryGroup.getPreviewLongSideMaxLength()));

			formWidget.addFormInputWidget(new FormInputTextWidget(GalleryGuiConstants.PARAMETER_SHORT_SIDE_MIN_LENGTH).addLabel("ShortSideMinLength:").addDefaultValue(galleryGroup.getShortSideMinLength()));
			formWidget.addFormInputWidget(new FormInputTextWidget(GalleryGuiConstants.PARAMETER_SHORT_SIDE_MAX_LENGTH).addLabel("ShortSideMaxLength:").addDefaultValue(galleryGroup.getShortSideMaxLength()));
			formWidget.addFormInputWidget(new FormInputTextWidget(GalleryGuiConstants.PARAMETER_LONG_SIDE_MIN_LENGTH).addLabel("LongSideMinLength:").addDefaultValue(galleryGroup.getLongSideMinLength()));
			formWidget.addFormInputWidget(new FormInputTextWidget(GalleryGuiConstants.PARAMETER_LONG_SIDE_MAX_LENGTH).addLabel("LongSideMaxLength:").addDefaultValue(galleryGroup.getLongSideMaxLength()));

			formWidget.addFormInputWidget(new FormInputSubmitWidget("update"));
			widgets.add(formWidget);
			return widgets;
		} catch (final GalleryServiceException e) {
			logger.debug(e.getClass().getName(), e);
			return new ExceptionWidget(e);
		} catch (final AuthenticationServiceException e) {
			logger.debug(e.getClass().getName(), e);
			return new ExceptionWidget(e);
		}
	}

	private void updateGroup(
		final SessionIdentifier sessionIdentifier,
		final GalleryGroupIdentifier galleryGroupIdentifier,
		final String name,
		final String sharedString,
		final String previewShortSideMinLength,
		final String previewShortSideMaxLength,
		final String previewLongSideMinLength,
		final String previewLongSideMaxLength,
		final String shortSideMinLength,
		final String shortSideMaxLength,
		final String longSideMinLength,
		final String longSideMaxLength
	)
		throws GalleryServiceException, LoginRequiredException, PermissionDeniedException, ValidationException {
		final GalleryGroupDto galleryGroupDto = new GalleryGroupDto();
		galleryGroupDto.setId(galleryGroupIdentifier);
		galleryGroupDto.setName(name);

		galleryGroupDto.setPreviewShortSideMinLength(parseUtil.parseInt(previewShortSideMinLength, null));
		galleryGroupDto.setPreviewShortSideMaxLength(parseUtil.parseInt(previewShortSideMaxLength, null));
		galleryGroupDto.setPreviewLongSideMinLength(parseUtil.parseInt(previewLongSideMinLength, null));
		galleryGroupDto.setPreviewLongSideMaxLength(parseUtil.parseInt(previewLongSideMaxLength, null));

		galleryGroupDto.setShortSideMinLength(parseUtil.parseInt(shortSideMinLength, null));
		galleryGroupDto.setShortSideMaxLength(parseUtil.parseInt(shortSideMaxLength, null));
		galleryGroupDto.setLongSideMinLength(parseUtil.parseInt(longSideMinLength, null));
		galleryGroupDto.setLongSideMaxLength(parseUtil.parseInt(longSideMaxLength, null));

		final List<ValidationError> errors = new ArrayList<ValidationError>();
		{
			try {
				galleryGroupDto.setShared(parseUtil.parseBoolean(sharedString));
			} catch (final ParseException e) {
				errors.add(new ValidationErrorSimple("illegal shared"));
			}
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(new ValidationResultImpl(errors));
		} else {
			galleryService.updateGroup(sessionIdentifier, galleryGroupDto);
		}
	}

	@Override
	protected String getTitle() {
		return TITLE;
	}
}
