package com.dongxuexidu.douban4j.provider;

import com.dongxuexidu.douban4j.constants.DefaultConfigs;
import com.dongxuexidu.douban4j.model.AccessToken;
import com.dongxuexidu.douban4j.model.DoubanException;
import com.dongxuexidu.douban4j.model.RequestGrantScope;
import com.dongxuexidu.douban4j.utils.Converters;
import com.dongxuexidu.douban4j.utils.ErrorHandler;
import com.dongxuexidu.douban4j.utils.HttpManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.NameValuePair;

/**
 *
 * @author Zhibo Wei <uglytroll@dongxuexidu.com>
 */
public class OAuthDoubanProvider {
  
  private String apiKey = DefaultConfigs.API_KEY;
  private String secretKey = DefaultConfigs.SECRET_KEY;
  private String authUrl = DefaultConfigs.AUTH_URL;
  private String redirectUrl = null;
  private String responseType = "code";
  private String grantType = "authorization_code";
  
  private List<RequestGrantScope> scopes = new ArrayList<RequestGrantScope>();
  
  static final private Logger logger = Logger.getLogger(OAuthDoubanProvider.class.getName());

  /**
   * @return the apiKey
   */
  public String getApiKey() {
    return apiKey;
  }

  /**
   * @param apiKey the apiKey to set
   */
  public OAuthDoubanProvider setApiKey(String apiKey) {
    this.apiKey = apiKey;
    return this;
  }

  /**
   * @return the secretKey
   */
  public String getSecretKey() {
    return secretKey;
  }

  /**
   * @param secretKey the secretKey to set
   */
  public OAuthDoubanProvider setSecretKey(String secretKey) {
    this.secretKey = secretKey;
    return this;
  }

  /**
   * @return the authUrl
   */
  public String getAuthUrl() {
    return authUrl;
  }

  /**
   * @param authUrl the authUrl to set
   */
  public OAuthDoubanProvider setAuthUrl(String authUrl) {
    this.authUrl = authUrl;
    return this;
  }

  /**
   * @return the redirectUrl
   */
  public String getRedirectUrl() {
    return redirectUrl;
  }

  /**
   * @param redirectUrl the redirectUrl to set
   */
  public OAuthDoubanProvider setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
    return this;
  }

  /**
   * @return the type
   */
  public String getResponseType() {
    return responseType;
  }

  /**
   * @param type the type to set
   */
  public OAuthDoubanProvider setResponseType(String type) {
    this.responseType = type;
    return this;
  }

  /**
   * @return the grantType
   */
  public String getGrantType() {
    return grantType;
  }

  /**
   * @param grantType the grantType to set
   */
  public OAuthDoubanProvider setGrantType(String grantType) {
    this.grantType = grantType;
    return this;
  }
  
  public OAuthDoubanProvider addScope (RequestGrantScope scope) {
    this.scopes.add(scope);
    return this;
  }
  
  public String getGetCodeRedirectUrl () {
    if (this.redirectUrl == null || this.redirectUrl.isEmpty()) {
      logger.warning("Redirect url cannot be null or empty, did you forget to set it?");
      return null;
    }
    StringBuilder getCodeUrl = new StringBuilder(this.authUrl);
    getCodeUrl.append("?client_id=").append(this.apiKey).append("&redirect_uri=").append(this.redirectUrl).append("&response_type=").append(this.responseType);
    if (!this.scopes.isEmpty()) {
      getCodeUrl.append("&scope=").append(generateScopeString());
    }
    return getCodeUrl.toString();
  }
  
  public AccessToken tradeAccessTokenWithCode (String code) throws DoubanException {
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new NameValuePair("client_id", this.apiKey));
    params.add(new NameValuePair("client_secret", this.secretKey));
    params.add(new NameValuePair("redirect_uri", DefaultConfigs.ACCESS_TOKEN_REDIRECT_URL));
    params.add(new NameValuePair("grant_type", "authorization_code"));
    params.add(new NameValuePair("code", code));
    String responseStr = new HttpManager().postResponse(DefaultConfigs.ACCESS_TOKEN_URL, null, false);
    return Converters.stringToAccessToken(responseStr);
  }
  
  private String generateScopeString() {
    if (this.scopes == null || this.scopes.isEmpty()) {
      return "";
    } else {
      StringBuilder scopeStr = new StringBuilder();
      for (RequestGrantScope sco : this.scopes) {
        scopeStr.append(sco.getValue()).append(",");
      }
      scopeStr.deleteCharAt(scopeStr.length() - 1); // Get rid of the last comma
      return scopeStr.toString();
    }
  }
  
}
