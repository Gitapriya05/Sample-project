package com.kickstarter.services;

import android.net.Uri;

import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.utils.extensions.UriExt;

import org.junit.Test;

public final class KSUriTest extends KSRobolectricTestCase {
  private final Uri checkoutUri = Uri.parse("https://www.ksr.com/projects/creator/project/pledge");
  private final Uri deepLinkMarketingHttps = Uri.parse("https://www.kickstarter.com/settings/notify_mobile_of_marketing_update/true");
  private final Uri deepLinkMarketingHttpsStaging = Uri.parse("https://staging.kickstarter.com/settings/notify_mobile_of_marketing_update/true");
  private final Uri deepLinkMarketingKsr = Uri.parse("ksr://www.kickstarter.com/settings/notify_mobile_of_marketing_update/true");
  private final Uri deepLinkMarketingKsrStaging = Uri.parse("ksr://staging.kickstarter.com/settings/notify_mobile_of_marketing_update/true");
  private final Uri discoverCategoriesUri = Uri.parse("https://www.ksr.com/discover/categories/art");
  private final Uri discoverScopeUri = Uri.parse("https://www.kickstarter.com/discover/ending-soon");
  private final Uri discoverPlacesUri = Uri.parse("https://www.ksr.com/discover/places/newest");
  private final Uri newGuestCheckoutUri = Uri.parse("https://www.ksr.com/checkouts/1/guest/new");
  private final Uri projectUri = Uri.parse("https://www.ksr.com/projects/creator/project");
  private final Uri projectPreviewUri = Uri.parse("https://www.ksr.com/projects/creator/project?token=token");
  private final Uri projectSurveyUri = Uri.parse("https://www.ksr.com/projects/creator/project/surveys/survey-param");
  private final Uri updatesUri = Uri.parse("https://www.ksr.com/projects/creator/project/posts");
  private final Uri updateUri = Uri.parse("https://www.ksr.com/projects/creator/project/posts/id");
  private final Uri userSurveyUri = Uri.parse("https://www.ksr.com/users/user-param/surveys/survey-id");
  private final String webEndpoint = "https://www.ksr.com";

  @Test
  public void testKSUri_isCheckoutUri() {
    assertTrue(KSUri.isCheckoutUri(this.checkoutUri, this.webEndpoint));
  }

  @Test
  public void testKSUri_isSettingsURI() {
    assertTrue(KSUri.isSettingsUrl(this.deepLinkMarketingHttps));
    assertTrue(KSUri.isSettingsUrl(this.deepLinkMarketingKsr));
    assertTrue(KSUri.isSettingsUrl(this.deepLinkMarketingHttpsStaging));
    assertTrue(KSUri.isSettingsUrl(this.deepLinkMarketingKsrStaging));
  }

  @Test
  public void testKSUri_isDiscoverCategoriesPath() {
    assertTrue(KSUri.isDiscoverCategoriesPath(this.discoverCategoriesUri.getPath()));
    assertFalse(KSUri.isDiscoverCategoriesPath(this.discoverPlacesUri.getPath()));
  }

  @Test
  public void testKSUri_isDiscoverPlacesPath() {
    assertTrue(KSUri.isDiscoverPlacesPath(this.discoverPlacesUri.getPath()));
    assertFalse(KSUri.isDiscoverPlacesPath(this.discoverCategoriesUri.getPath()));
  }

  @Test
  public void testKSUri_isDiscoverScopePath() {
    assertTrue(KSUri.isDiscoverScopePath(this.discoverScopeUri.getPath(), "ending-soon"));
  }

  @Test
  public void testKSUri_isKickstarterUri() {
    final Uri ksrUri = Uri.parse("https://www.ksr.com/discover");
    final Uri uri = Uri.parse("https://www.hello-world.org/goodbye");

    assertTrue(KSUri.isKickstarterUri(ksrUri, this.webEndpoint));
    assertFalse(KSUri.isKickstarterUri(uri, this.webEndpoint));
  }

  @Test
  public void testKSUri_isWebViewUri() {
    final Uri ksrUri = Uri.parse("https://www.ksr.com/project");
    final Uri uri = Uri.parse("https://www.hello-world.org/goodbye");
    final Uri ksrGraphUri = Uri.parse("https://www.ksr.com/graph");
    final Uri graphUri = Uri.parse("https://www.hello-world.org/graph");
    final Uri favIconUri = Uri.parse("https://www.ksr.com/favicon.ico");

    assertTrue(KSUri.isWebViewUri(ksrUri, this.webEndpoint));
    assertFalse(KSUri.isWebViewUri(uri, this.webEndpoint));
    assertTrue(KSUri.isWebViewUri(ksrGraphUri, this.webEndpoint));
    assertFalse(KSUri.isWebViewUri(graphUri, this.webEndpoint));
    assertFalse(KSUri.isWebViewUri(favIconUri, this.webEndpoint));
  }

  @Test
  public void testKSUri_isKSFavIcon() {
    final Uri ksrUri = Uri.parse("https://www.ksr.com/favicon.ico");
    final Uri uri = Uri.parse("https://www.hello-world.org/goodbye");

    assertTrue(KSUri.isKSFavIcon(ksrUri, this.webEndpoint));
    assertFalse(KSUri.isKSFavIcon(uri, this.webEndpoint));
  }

  @Test
  public void testKSUri_isModalUri() {
    final Uri modalUri = Uri.parse("https://www.ksr.com/project?modal=true");

    assertTrue(KSUri.isModalUri(modalUri, this.webEndpoint));
    assertFalse(KSUri.isModalUri(this.projectUri, this.webEndpoint));
  }

  @Test
  public void testKSUri_isNewGuestCheckoutUri() {
    assertTrue(KSUri.isNewGuestCheckoutUri(this.newGuestCheckoutUri, this.webEndpoint));
  }

  @Test
  public void testKSUri_isProjectSurveyUri() {
    assertTrue(KSUri.isProjectSurveyUri(this.projectSurveyUri, this.webEndpoint));
    assertFalse(KSUri.isProjectSurveyUri(this.userSurveyUri, this.webEndpoint));
  }

  @Test
  public void testKSUri_isProjectUpdateCommentsUri() {
    final Uri updateCommentsUri = Uri.parse("https://www.ksr.com/projects/creator/project/posts/id/comments");

    assertTrue(KSUri.isProjectUpdateCommentsUri(updateCommentsUri, this.webEndpoint));
    assertFalse(KSUri.isProjectUpdateCommentsUri(this.updatesUri, this.webEndpoint));
  }

  @Test
  public void testKSUri_isProjectUpdateUri() {
    assertTrue(KSUri.isProjectUpdateUri(this.updateUri, this.webEndpoint));
    assertFalse(KSUri.isProjectUpdateUri(this.updatesUri, this.webEndpoint));
  }

  @Test
  public void testKSUri_isProjectUpdatesUri() {
    assertTrue(KSUri.isProjectUpdatesUri(this.updatesUri, this.webEndpoint));
    assertFalse(KSUri.isProjectUpdatesUri(this.updateUri, this.webEndpoint));
  }

  @Test
  public void testKSUri_isProjectUri() {
    assertTrue(KSUri.isProjectUri(this.projectUri, this.webEndpoint));
    assertTrue(KSUri.isProjectUri(this.projectPreviewUri, this.webEndpoint));
    assertFalse(KSUri.isProjectUri(this.updateUri, this.webEndpoint));
  }

  @Test
  public void testKSUri_isProjectPreviewUri() {
    assertTrue(KSUri.isProjectPreviewUri(this.projectPreviewUri, this.webEndpoint));
    assertFalse(KSUri.isProjectPreviewUri(this.projectUri, this.webEndpoint));
  }

  @Test
  public void testKSuri_isUserSurveyUri() {
    assertTrue(KSUri.isUserSurveyUri(this.userSurveyUri, this.webEndpoint));
    assertFalse(KSUri.isUserSurveyUri(this.projectSurveyUri, this.webEndpoint));
  }

  @Test
  public void testUri_isEmailSubdomainUri() {
    final Uri emails = Uri.parse("https://emails.kickstarter.com/ss/c/jbhlvoU_4ViWFpZVbUjED0OpkQHNg7x7JtaKmK7tAlf5HiOJFJwm9QPPHsJjrM5f9t9VpxDuveQHeHob1bSqGauk2heWob9Nvf5D1AgqasWyutgs_WwtPIUhUkX5M3H7U6NCGGIfeY9CvX4ft9BfkMkCE8G15l8dEz1PdFQRek_DMr5D5qq0dR4Qq0kRPKN6snTdVcVJxKhGn6x8t0hegsNVS046-eMTInsXrYvLawE/3c0/o96ZyfWsS1aWJ3l5HgGPcw/h1/07F-qb88bQjr9FC9pH6j4r-zri95lNQI5hMZvl7Z7WM");
    final Uri brazeClicks = Uri.parse("https://clicks.kickstarter.com/f/a/Hs4EAU85CJvgLr-uBBByog~~/AAQRxQA~/RgRiXE13P0TUaHR0cHM6Ly93d3cua2lja3N0YXJ0ZXIuY29tL3Byb2plY3RzL21zdDNrL21ha2Vtb3JlbXN0M2s_cmVmPU5ld3NBcHIxNjIxLWVuLWdsb2JhbC1hbGwmdXRtX21lZGl1bT1lbWFpbC1tZ2ImdXRtX3NvdXJjZT1wd2xuZXdzbGV0dGVyJnV0bV9jYW1wYWlnbj1wcm9qZWN0c3dlbG92ZS0wNDE2MjAyMSZ1dG1fY29udGVudD1pbWFnZSZiYW5uZXI9ZmlsbS1uZXdzbGV0dGVyMDFXA3NwY0IKYHh3yHlgkYIOrFIYbGl6YmxhaXJAa2lja3N0YXJ0ZXIuY29tWAQAAABU");
    final Uri brazeEa = Uri.parse("https://me.kickstarter.com/f/a/Hs4EAU85CJvgLr-uBBByog~~/AAQRxQA~/RgRiXE13P0TUaHR0cHM6Ly93d3cua2lja3N0YXJ0ZXIuY29tL3Byb2plY3RzL21zdDNrL21ha2Vtb3JlbXN0M2s_cmVmPU5ld3NBcHIxNjIxLWVuLWdsb2JhbC1hbGwmdXRtX21lZGl1bT1lbWFpbC1tZ2ImdXRtX3NvdXJjZT1wd2xuZXdzbGV0dGVyJnV0bV9jYW1wYWlnbj1wcm9qZWN0c3dlbG92ZS0wNDE2MjAyMSZ1dG1fY29udGVudD1pbWFnZSZiYW5uZXI9ZmlsbS1uZXdzbGV0dGVyMDFXA3NwY0IKYHh3yHlgkYIOrFIYbGl6YmxhaXJAa2lja3N0YXJ0ZXIuY29tWAQAAABU");
    final Uri brazeMe = Uri.parse("https://ea.kickstarter.com/f/a/Hs4EAU85CJvgLr-uBBByog~~/AAQRxQA~/RgRiXE13P0TUaHR0cHM6Ly93d3cua2lja3N0YXJ0ZXIuY29tL3Byb2plY3RzL21zdDNrL21ha2Vtb3JlbXN0M2s_cmVmPU5ld3NBcHIxNjIxLWVuLWdsb2JhbC1hbGwmdXRtX21lZGl1bT1lbWFpbC1tZ2ImdXRtX3NvdXJjZT1wd2xuZXdzbGV0dGVyJnV0bV9jYW1wYWlnbj1wcm9qZWN0c3dlbG92ZS0wNDE2MjAyMSZ1dG1fY29udGVudD1pbWFnZSZiYW5uZXI9ZmlsbS1uZXdzbGV0dGVyMDFXA3NwY0IKYHh3yHlgkYIOrFIYbGl6YmxhaXJAa2lja3N0YXJ0ZXIuY29tWAQAAABU");

    assertTrue(UriExt.isEmailSubdomain(emails));
    assertTrue(UriExt.isEmailSubdomain(brazeClicks));
    assertTrue(UriExt.isEmailSubdomain(brazeEa));
    assertTrue(UriExt.isEmailSubdomain(brazeMe));
  }
}
