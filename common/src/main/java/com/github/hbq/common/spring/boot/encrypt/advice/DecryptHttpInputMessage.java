package com.github.hbq.common.spring.boot.encrypt.advice;

import com.github.hbq.common.spring.boot.encrypt.utils.AESUtil;
import com.github.hbq.common.spring.boot.encrypt.utils.Base64Util;
import com.github.hbq.common.spring.boot.encrypt.utils.JsonUtils;
import com.github.hbq.common.spring.boot.encrypt.utils.RSAUtil;
import com.github.hbq.common.spring.boot.encrypt.config.AESConfig;
import com.github.hbq.common.spring.boot.encrypt.config.Algorithm;
import com.github.hbq.common.spring.boot.encrypt.config.Decrypt;
import com.github.hbq.common.spring.boot.encrypt.config.RSAConfig;
import com.github.hbq.common.spring.boot.encrypt.exception.EncryptRequestException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

/**
 * @author hbq
 **/
@Slf4j
public class DecryptHttpInputMessage implements HttpInputMessage {

  private HttpHeaders headers;

  private InputStream body;


  public DecryptHttpInputMessage(HttpInputMessage inputMessage, RSAConfig rsaConfig, AESConfig aesConfig, Decrypt decrypt) throws Exception {

    if (aesConfig.isEnable() && decrypt.algorithm() == Algorithm.AES) {
      decryptWithAES(inputMessage, aesConfig);
    }

    if (rsaConfig.isEnable() && decrypt.algorithm() == Algorithm.RSA) {
      decryptWithRSA(inputMessage, rsaConfig, decrypt);
    }

  }

  private void decryptWithRSA(HttpInputMessage inputMessage, RSAConfig rsaConfig, Decrypt decrypt) throws Exception {
    String privateKey = rsaConfig.getPrivateKey();
    String charset = rsaConfig.getCharset();
    boolean showLog = rsaConfig.isShowLog();
    boolean timestampCheck = rsaConfig.isTimestampCheck();

    if (StringUtils.isEmpty(privateKey)) {
      throw new IllegalArgumentException("rsa????????????");
    }
    if (StringUtils.isBlank(charset)) {
      charset = "utf-8";
    }

    this.headers = inputMessage.getHeaders();
    String content = new BufferedReader(new InputStreamReader(inputMessage.getBody()))
        .lines().collect(Collectors.joining(System.lineSeparator()));
    String decryptBody;
    // ???????????????
    if (content.startsWith("{")) {
      // ????????????
      if (decrypt.required()) {
        log.error("????????????????????????: {}", content);
        throw new EncryptRequestException("????????????????????????");
      }
      decryptBody = content;
    } else {
      StringBuilder json = new StringBuilder();
      content = content.replaceAll(" ", "+");

      if (!StringUtils.isEmpty(content)) {
        String[] contents = content.split("\\|");
        for (String value : contents) {
          value = new String(RSAUtil.decrypt(Base64Util.decode(value), privateKey), charset);
          json.append(value);
        }
      }
      decryptBody = json.toString();
      if (showLog) {
        log.info("????????????, rsa????????????{}, ????????????{}", content, decryptBody);
      }
    }

    // ?????????????????????
    if (timestampCheck) {
      // ????????????????????????
      long toleranceTime = System.currentTimeMillis() - decrypt.timeout();
      long requestTime = JsonUtils.getNode(decryptBody, "timestamp").asLong();
      // ????????????????????????????????????????????????, ???????????????
      if (requestTime < toleranceTime) {
        log.error("??????????????????, ????????????: {}, ??????????????????: {}, ?????????????????????: {}, ??????????????????{}", toleranceTime, requestTime, decrypt.timeout(), decryptBody);
        throw new EncryptRequestException("??????????????????????????????");
      }
    }

    this.body = new ByteArrayInputStream(decryptBody.getBytes());
  }

  private void decryptWithAES(HttpInputMessage inputMessage, AESConfig aesConfig) throws IOException {
    String key = aesConfig.getKey();
    String charset = aesConfig.getCharset();
    boolean showLog = aesConfig.isShowLog();

    if (StringUtils.isBlank(key)) {
      throw new IllegalArgumentException("aes??????????????????");
    }
    this.headers = inputMessage.getHeaders();
    String content = IOUtils.toString(inputMessage.getBody(), Charset.forName(charset));
    if (StringUtils.isBlank(charset)) {
      charset = "utf-8";
    }
    String decryptBody = AESUtil.decrypt(content, key, Charset.forName(charset));
    if (showLog) {
      log.info("????????????, aes????????????{}, ????????????{}", content, decryptBody);
    }
    this.body = new ByteArrayInputStream(decryptBody.getBytes());
  }

  @Override
  public InputStream getBody() {
    return body;
  }

  @Override
  public HttpHeaders getHeaders() {
    return headers;
  }
}
