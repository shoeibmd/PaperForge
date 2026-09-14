package com.paperforge.repository;

import com.paperforge.model.ApiKey;
import com.paperforge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    List<ApiKey> findByUserOrderByCreatedAtDesc(User user);

    List<ApiKey> findByKeyPrefixAndRevokedFalse(String keyPrefix);

    List<ApiKey> findAllByOrderByCreatedAtDesc();
}
