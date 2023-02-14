package com.github.hbq.manage.chatgpt;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

/**
 * @author hbq
 */
public class Demo {

  public static void main(String[] args) throws Exception {
    List<String> list=FileUtils.readLines(new File("d://vocab.bpe"), Charset.forName("utf-8"));
    StringBuilder sb = new StringBuilder(200000);
    for (String s : list) {
      sb.append(",").append('"').append(s).append('"');
    }
    System.out.println(sb.substring(1));
  }
}
