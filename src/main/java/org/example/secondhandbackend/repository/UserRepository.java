package org.example.secondhandbackend.repository;

import org.example.secondhandbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/*this file is only responsible for connecting and accesing our database.Every UserRepository obeject
is a data base which saves datas about our users
 */

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
}
