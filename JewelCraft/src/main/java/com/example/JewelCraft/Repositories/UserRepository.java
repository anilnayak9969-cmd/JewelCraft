package com.example.JewelCraft.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.JewelCraft.Entites.User;
import com.example.JewelCraft.Enum.ApprovalStatus;
import com.example.JewelCraft.Enum.RoleName;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);

	long countByRole_Name(RoleName role);

	List<User> findByRole_NameAndEnabledTrueAndApprovalStatusOrderByFirstNameAsc(RoleName role, ApprovalStatus approvalStatus);

	/** pattern is already lower-cased and wrapped in %...%, or null for no search. */
	@Query("""
			select u from User u
			where (:role is null or u.role.name = :role)
			  and (:approval is null or u.approvalStatus = :approval)
			  and (:pattern is null
			       or lower(u.email) like :pattern
			       or lower(u.firstName) like :pattern
			       or lower(u.lastName) like :pattern
			       or u.phone like :pattern)
			""")
	Page<User> search(@Param("role") RoleName role, @Param("approval") ApprovalStatus approval,
			@Param("pattern") String pattern, Pageable pageable);

	long countByApprovalStatus(ApprovalStatus approvalStatus);

	@Query("select u.role.name, count(u) from User u group by u.role.name")
	List<Object[]> countGroupedByRole();
}
