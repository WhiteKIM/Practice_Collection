package whitekim.practice.member.dto.request;

import whitekim.practice.member.entity.Member;

public record JoinMember(
        String memberName,
        String memberPassword,
        String memberEmail
) {
    public Member toEntity() {
        return new Member(memberName, memberPassword, memberEmail);
    }
}
