package com.example.cachingbyehcache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings("unchecked")
@CacheConfig(cacheNames = "users")
public interface UserRepository extends JpaRepository<User, Long> {

    @Cacheable
    User findByName(String name);

    /*
     * @see
     * org.springframework.data.repository.CrudRepository#save(java.lang.Object)
     * Type safety: The return type User for save(User) from the type UserRepository
     * needs unchecked conversion to conform to S from the type CrudRepository<T,ID>
     */
    @CachePut
    User save(User user);

}
