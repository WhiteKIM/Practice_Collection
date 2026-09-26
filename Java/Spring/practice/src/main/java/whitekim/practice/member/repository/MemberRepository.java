package whitekim.practice.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import whitekim.practice.member.dto.response.RespMemberInfo;
import whitekim.practice.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
