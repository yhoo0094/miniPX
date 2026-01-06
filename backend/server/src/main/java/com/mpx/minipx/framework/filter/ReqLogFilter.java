package com.mpx.minipx.framework.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.mpx.minipx.dto.common.ReqLog;
import com.mpx.minipx.framework.util.CommonUtil;
import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.service.admin.ReqLogService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ReqLogFilter extends OncePerRequestFilter {
	
    @Value("${jwt.secret}")
    private String jwtSecret;	

    @Autowired
    private ReqLogService reqLogService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 이미 래핑된 경우 중복 래핑 방지
        ContentCachingRequestWrapper wrapped =
                (request instanceof ContentCachingRequestWrapper)
                        ? (ContentCachingRequestWrapper) request
                        : new ContentCachingRequestWrapper(request);

        try {
            filterChain.doFilter(wrapped, response);
        } finally {
            // ===== 여기서 DB 저장 =====
            ReqLog log = new ReqLog();
            String uri = wrapped.getRequestURI(); 
            String reqTypeCode = getReqTypeCode(uri);
            log.setIp(CommonUtil.getClientIp(wrapped));
            log.setUri(uri);
            log.setReqTypeCode(reqTypeCode);
            log.setUserSeq(extractUserSeqSafely(wrapped)); // 프로젝트 방식에 맞게 구현
            log.setParam(buildParamText(wrapped));
            if(!isExclusiveUri(uri)) {
            	reqLogService.saveReqLog(log);
        	}
        }
    }

    private String buildParamText(ContentCachingRequestWrapper req) {
        // query parameters
        StringBuilder sb = new StringBuilder();

        Map<String, String[]> pm = req.getParameterMap();
        if (pm != null && !pm.isEmpty()) {
            sb.append("queryParams=").append(pmToString(pm)).append("\n");
        }

        // body (주의: multipart는 제외 권장)
        String contentType = req.getContentType();
        if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
            sb.append("body=<multipart omitted>");
            return limit(sb.toString(), 10000);
        }

        byte[] body = req.getContentAsByteArray();
        if (body != null && body.length > 0) {
            Charset cs = (req.getCharacterEncoding() != null)
                    ? Charset.forName(req.getCharacterEncoding())
                    : StandardCharsets.UTF_8;
            sb.append("body=").append(new String(body, cs));
        }

        String raw = limit(sb.toString(), 10000); // 너무 길면 DB/성능 문제 → 적당히 컷
        return maskSensitiveText(raw);	//민감정보 마스킹 
    }

    private String pmToString(Map<String, String[]> pm) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String[]> e : pm.entrySet()) {
            if (!first) sb.append(", ");
            first = false;
            sb.append(e.getKey()).append("=");
            String[] v = e.getValue();
            if (v == null) sb.append("null");
            else if (v.length == 1) sb.append(v[0]);
            else sb.append(java.util.Arrays.toString(v));
        }
        sb.append("}");
        return sb.toString();
    }

    //userSeq 추출
    private Long extractUserSeqSafely(HttpServletRequest request) {
        // 1) 쿠키에서 accessToken 꺼내기 (없으면 비로그인)
        String accessToken = JwtUtil.extractTokenFromCookies(request, "accessToken");
        if (accessToken == null || accessToken.trim().isEmpty()) {
            return null;
        }

        try {
            // 2) 토큰 검증/파싱 (실패하면 비로그인 처리)
            Claims claims = JwtUtil.validateToken(accessToken, jwtSecret);

            // 3) userSeq 안전 변환
            Object v = claims.get("userSeq");
            if (v == null) return null;

            if (v instanceof Long) return (Long) v;
            if (v instanceof Integer) return ((Integer) v).longValue();
            if (v instanceof Number) return ((Number) v).longValue();
            if (v instanceof String) {
                String s = ((String) v).trim();
                if (s.isEmpty()) return null;
                return Long.parseLong(s);
            }

            return null;
        } catch (Exception e) {
            // 만료/위조/파싱 실패 등 → 요청은 정상 진행, 로그만 익명 처리
            return null;
        }
    }

    private String limit(String s, int max) {
        if (s == null) return null;
        return (s.length() <= max) ? s : s.substring(0, max);
    }
    
    //파라미터에서 민감정보 마스킹 
    private String maskSensitiveText(String text) {
        if (text == null || text.isEmpty()) return text;

        String masked = text;

        for (String key : SENSITIVE_KEYS) {
            // 1) query/form 스타일: key=VALUE  (예: password=abc123)
            Pattern p1 = Pattern.compile("(?i)(\\b" + Pattern.quote(key) + "\\b\\s*=\\s*)([^&\\s\\n\\r]+)");
            masked = p1.matcher(masked).replaceAll("$1***");

            // 2) JSON 스타일: "key":"VALUE" 또는 "key": "VALUE"
            Pattern p2 = Pattern.compile("(?i)(\""+ Pattern.quote(key) +"\"\\s*:\\s*\")([^\"]*)(\")");
            masked = p2.matcher(masked).replaceAll("$1***$3");

            // 3) JSON 숫자/불린/null 등: "key": 123
            Pattern p3 = Pattern.compile("(?i)(\""+ Pattern.quote(key) +"\"\\s*:\\s*)([^,}\\n\\r]+)");
            masked = p3.matcher(masked).replaceAll("$1***");
        }

        return masked;
    }   
    
    //민감정보 key
    private static final String[] SENSITIVE_KEYS = {
	    "password", "passwd", "pw", "userpw",
	    "accessToken", "refreshToken", "token",
	    "authorization", "secret", "apiKey", "apikey",
	    "jwt", "session", "cookie"
	};    
    
    //reqTypeCode 분류
    private String getReqTypeCode(String uri) {
        if (uri == null) return "";
        uri = uri.toLowerCase();
        String reqTypeCode;
        if(uri.contains("/item/".toLowerCase())){
        	reqTypeCode = "market";
        } else if(uri.contains("/basket/".toLowerCase())) {
        	reqTypeCode = "market";
        } else if(uri.contains("/order/".toLowerCase())) {
        	reqTypeCode = "market";
        } else if(uri.contains("/itemNew/".toLowerCase())) {
        	reqTypeCode = "market";
        } else if(uri.contains("/itemDetail/".toLowerCase())) {
        	reqTypeCode = "market";
        } else if(uri.contains("/mngOrder/".toLowerCase())) {
        	reqTypeCode = "admin";
        } else if(uri.contains("/mngUser/".toLowerCase())) {
        	reqTypeCode = "admin";
        } else if(uri.contains("/loginLog/".toLowerCase())) {
        	reqTypeCode = "admin";
        } else if(uri.contains("/reqLog/".toLowerCase())) {
        	reqTypeCode = "admin";
        } else {
        	reqTypeCode = "etc";
        }
        return reqTypeCode;
    }      
    
    //저장 제외 uri 목록
    private boolean isExclusiveUri(String uri) {
        if (uri == null) return false;
        uri = uri.toLowerCase();

        return uri.equals("/api/item/getItemImage".toLowerCase())
            || uri.equals("/api/common/getCodeList".toLowerCase())
            || uri.equals("/actuator/health/liveness".toLowerCase())
            || uri.equals("/api/auth/check".toLowerCase())
            ;
    }   
}
