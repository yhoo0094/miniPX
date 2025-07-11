package com.mpx.minipx.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RedisPermissionRepository {

    private final HashOperations<String, String, String> hashOperations;

    @Autowired
    public RedisPermissionRepository(RedisTemplate<String, String> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    // 권한 저장
    public void savePermission(String userId, String path, int authGrade) {
        String key = getKey(userId);
        hashOperations.put(key, path, String.valueOf(authGrade));
    }

    // 권한 조회
    public Optional<Integer> getPermission(String userId, String path) {
        String key = getKey(userId);
        String value = hashOperations.get(key, path);
        return Optional.ofNullable(value).map(Integer::valueOf);
    }

    // 전체 권한 삭제 (ex. 로그아웃 또는 권한 변경 시)
    public void deleteAllPermissions(String userId) {
        String key = getKey(userId);
        hashOperations.getOperations().delete(key);
    }

    private String getKey(String userId) {
        return "perm:" + userId;
    }
}


	