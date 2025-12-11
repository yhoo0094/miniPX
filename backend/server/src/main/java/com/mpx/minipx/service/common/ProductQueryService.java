package com.mpx.minipx.service.common;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * LLM이 생성한 SQL을 실제로 DB에 실행하는 서비스
 */
@Service
public class ProductQueryService {

    private final JdbcTemplate jdbcTemplate;

    public ProductQueryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> runReadOnlyQuery(String sql) {

        String lower = sql.toLowerCase();

        // 매우 기초적인 방어 (원하면 더 강화 가능)
        if (!lower.startsWith("select")) {
            throw new IllegalArgumentException("SELECT 문만 허용됩니다.");
        }
        if (lower.contains("delete") || lower.contains("update") || lower.contains("insert")
                || lower.contains("alter") || lower.contains("drop") || lower.contains(";")) {
            throw new IllegalArgumentException("위험한 SQL은 허용되지 않습니다.");
        }

        return jdbcTemplate.queryForList(sql);
    }
}
