package com.mpx.minipx.framework.config;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class QuillSanitizer {

  private static final PolicyFactory POLICY = new HtmlPolicyBuilder()
      // 허용 태그
      .allowElements("p", "br", "strong", "b", "em", "i", "u", "s",
                    "ol", "ul", "li",
                    "h1", "h2", "h3",
                    "blockquote", "pre", "code",
                    "span", "div",
                    "a", "img")
      // a 링크 허용 속성
      .allowAttributes("href", "target", "rel").onElements("a")
      .allowUrlProtocols("http", "https", "mailto")
      // img 허용 속성 (필요 최소만)
      .allowAttributes("src", "alt", "title").onElements("img")
      .allowUrlProtocols("http", "https", "data") // data: 허용 여부는 정책에 맞게 선택
      // class 허용 (정렬/사이즈 등)
      .allowAttributes("class").onElements("p", "div", "span", "img")
      // 보안상 권장: a 태그에 rel 강제(원하면)
      .toFactory();

  public static String sanitize(String dirtyHtml) {
    if (dirtyHtml == null) return "";
    return POLICY.sanitize(dirtyHtml);
  }
}