package whitekim.practice.member.api;

/**
 * 사용자 존재여부 확인 API
 */
public interface MemberExistApi {
    boolean existMemberById(Long memberId);
}
