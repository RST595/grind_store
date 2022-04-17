package com.bmxstore.grind_store.data.repository;


import com.bmxstore.grind_store.data.entity.user.UserEntity;
import com.bmxstore.grind_store.dto.user.UserPage;
import com.bmxstore.grind_store.dto.user.UserResponse;
import com.bmxstore.grind_store.dto.user.UserSearchCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class UserCriteriaRepo {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    private final ObjectMapper objectMapper;

    public UserCriteriaRepo(EntityManager entityManager, ObjectMapper objectMapper) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
        this.objectMapper = objectMapper;
    }

    public Page<UserResponse> findAllWithFilters(UserPage userPage, UserSearchCriteria userSearchCriteria){
        CriteriaQuery<UserEntity> criteriaQuery = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> userRoot = criteriaQuery.from(UserEntity.class);
        Predicate predicate = getPredicate(userSearchCriteria, userRoot);
        criteriaQuery.where(predicate);
        setOrderForResult(userPage, criteriaQuery, userRoot);

        TypedQuery<UserEntity> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(userPage.getPageNumber() * userPage.getPageSize());
        typedQuery.setMaxResults(userPage.getPageSize());

        Pageable pageable = getPageable(userPage);

        long usersCount = getUsersCount(predicate);

        List<UserResponse> users = new ArrayList<>();
        for(UserEntity user : typedQuery.getResultList()){
            UserResponse userResponse = objectMapper.convertValue(user, UserResponse.class);
            users.add(userResponse);
        }
        return new PageImpl<>(users, pageable, usersCount);
    }

    private Predicate getPredicate(UserSearchCriteria userSearchCriteria, Root<UserEntity> userRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(userSearchCriteria.getFirstName())){
            predicates.add(
                    criteriaBuilder.like(userRoot.get("firstName"), "%" + userSearchCriteria.getFirstName() + "%")
            );
        }
        if (Objects.nonNull(userSearchCriteria.getLastName())){
            predicates.add(
                    criteriaBuilder.like(userRoot.get("lastName"), "%" + userSearchCriteria.getLastName() + "%")
            );
        }
        if (Objects.nonNull(userSearchCriteria.getEmail())){
            predicates.add(
                    criteriaBuilder.like(userRoot.get("email"), "%" + userSearchCriteria.getEmail() + "%")
            );
        }
        if (Objects.nonNull(userSearchCriteria.getAddress())){
            predicates.add(
                    criteriaBuilder.like(userRoot.get("address"), "%" + userSearchCriteria.getAddress() + "%")
            );
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrderForResult(UserPage userPage, CriteriaQuery<UserEntity> criteriaQuery, Root<UserEntity> userRoot) {
        if (userPage.getSortDirection().equals(Sort.Direction.ASC)) {
            criteriaQuery.orderBy(criteriaBuilder.asc(userRoot.get(userPage.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(userRoot.get(userPage.getSortBy())));
        }
    }

    private Pageable getPageable(UserPage userPage) {
        Sort sort = Sort.by(userPage.getSortDirection(), userPage.getSortBy());
        return PageRequest.of(userPage.getPageNumber(), userPage.getPageSize(), sort);
    }

    private long getUsersCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<UserEntity> countRoot = countQuery.from(UserEntity.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
