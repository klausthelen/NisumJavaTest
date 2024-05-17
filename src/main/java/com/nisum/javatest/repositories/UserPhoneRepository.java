package com.nisum.javatest.repositories;

import com.nisum.javatest.models.User;
import com.nisum.javatest.models.UserPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPhoneRepository extends JpaRepository<UserPhone, Long> {
    List<UserPhone> findAllByUser(final User user);
}