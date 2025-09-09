package com.si.mindhealth.repositories;

import com.si.mindhealth.entities.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("""
                select u from User u
                where lower(u.username) like lower(concat('%', :kw, '%'))
                   or lower(u.email)    like lower(concat('%', :kw, '%'))
                   or lower(u.firstName) like lower(concat('%', :kw, '%'))
                   or lower(u.lastName)  like lower(concat('%', :kw, '%'))
            """)
    Page<User> search(@Param("kw") String kw, Pageable pageable);
}
