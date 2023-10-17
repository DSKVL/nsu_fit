var penpotFlags = "enable-audit-log enable-audit-log-archive enable-audit-log-gc enable-user-feedback enable-smtp disable-log-emails disable-backend-asserts disable-demo-users disable-telemetry enable-terms-and-privacy-checkbox enable-login-with-google enable-login-with-github enable-login-with-gitlab enable-newsletter-subscription enable-quotes enable-soft-quotes enable-webhooks enable-urepl-server enable-prepl-server enable-access-tokens";

var penpotTermsOfServiceURI = "https://penpot.app/terms";
var penpotPrivacyPolicyURI = "https://penpot.app/privacy";

var penpotOnboardingQuestionsFormId = "165598796955069817"

var _paq = window._paq || [];
_paq.push(["setDoNotTrack", true]);
_paq.push(["disableCookies"]);
_paq.push(['trackPageView']);
(function() {
  var u="https://matomo.kaleidos.net/";
  _paq.push(['setTrackerUrl', u+'matomo.php']);
  _paq.push(['setSiteId', '6']);
  var d=document, g=d.createElement('script'), s=d.getElementsByTagName('script')[0];
  g.type='text/javascript'; g.async=true; g.defer=true; g.src=u+'matomo.js'; s.parentNode.insertBefore(g,s);
})();
