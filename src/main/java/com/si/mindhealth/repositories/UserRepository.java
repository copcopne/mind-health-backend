package com.si.mindhealth.repositories;

import com.si.mindhealth.dtos.response.UserStatsResponseDTO;
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
          where lower(u.username)  like lower(concat('%', :kw, '%'))
             or lower(u.email)     like lower(concat('%', :kw, '%'))
             or lower(u.firstName) like lower(concat('%', :kw, '%'))
             or lower(u.lastName)  like lower(concat('%', :kw, '%'))
      """)
  Page<User> search(@Param("kw") String kw, Pageable pageable);

  @Query("""
          select new com.si.mindhealth.dtos.response.UserStatsResponseDTO(
              concat(:time_type, :time_value),
              count(u.id),
              (select count(all_u.id) from User all_u)
          )
          from User u
          where function('YEAR', u.createdAt) = :year
            and (
                 (:time_type = 'MONTH'   and function('MONTH', u.createdAt)   = :time_value)
              or (:time_type = 'QUARTER' and function('QUARTER', u.createdAt) = :time_value)
              or (:time_type = 'YEAR')
            )
      """)
  UserStatsResponseDTO aggregateByTime(
      @Param("time_type") String timeType,
      @Param("time_value") int timeValue,
      @Param("year") int year);

}
