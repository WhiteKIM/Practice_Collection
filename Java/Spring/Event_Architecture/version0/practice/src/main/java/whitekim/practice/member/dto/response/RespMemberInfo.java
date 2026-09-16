package whitekim.practice.member.dto.response;

import whitekim.practice.member.entity.Member;

public record RespMemberInfo(
        String memberName,
        String memberEmail
) {
    public static RespMemberInfo toDto(Member member) {
        return new RespMemberInfo(member.getMemberName(), member.getMemberEmail());
    }
}
