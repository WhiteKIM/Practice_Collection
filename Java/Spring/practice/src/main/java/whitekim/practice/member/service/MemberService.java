package whitekim.practice.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whitekim.practice.member.dto.request.JoinMember;
import whitekim.practice.member.dto.response.RespMemberInfo;
import whitekim.practice.member.entity.Member;
import whitekim.practice.member.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    public RespMemberInfo findByMemberInfo(Long memberId) {
        Optional<Member> optMember = memberRepository.findById(memberId);

        if(optMember.isEmpty()) {
            throw new RuntimeException();
        }

        return RespMemberInfo.toDto(optMember.get());
    }

    public void joinMember(JoinMember joinMember) {
        //String rawPassword = joinMember.memberPassword();
        // String encPassword;

        memberRepository.save(joinMember.toEntity());
    }

    public boolean existMemberById(Long memberId) {
        return memberRepository.existsById(memberId);
    }
}
